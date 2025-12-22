package kzone.cache.service.requestcollepsing;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.common.cache.KzoneCacheHandler;
import kzone.cache.common.distributedlock.DistributedLockProvider;
import kzone.cache.common.serde.DataSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RequestCollapsingHandler implements KzoneCacheHandler {

    private final StringRedisTemplate redisTemplate;
    private final DistributedLockProvider distributedLockProvider;

    private static final long POLLING_INTERVAL_MILLIS = 50;
    private static final long REFRESH_WAITING_TIMEOUT_MILLIS = 2000;

    @Override
    public <T> T fetch(String key, Duration ttl, Supplier<T> dataSourceSupplier, Class<T> clazz) {
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return DataSerializer.deserializeOrNull(cached, clazz);
        }

        String lockKey = genLockKey(key);
        if (distributedLockProvider.lock(lockKey, Duration.ofSeconds(3))) {
            try {
                return refresh(key, ttl, dataSourceSupplier);
            } finally {
                distributedLockProvider.unlock(lockKey);
            }
        }

        /*
        캐시가 갱신되면 루프는 끝날텐데, 왜 요청마다 Time Out을 이중으로 검사하는가?
        - 만약 요청마다 검사하지 않는다면
        1. 요청 1이 락을 획득
        2. 요청 2는 캐시가 갱신될 때까지 폴링
        3. 요청 1이 캐시 갱신 실패하고 락을 해제
        4. 요청 3이 다시 락을 획득
        5. 요청 2는 캐시가 갱신될 때까지 폴링 (갱신 실패 사실을 인지 못함, 락 유무 확인하더라도 부가적인 조회 비용 + sleep 시점 시 감지못함)
        -> 위처럼 특정 상황에 무한 대기 가능, 요청별 타임아웃으로 최소한의 종료 정책
         */
        long start = System.nanoTime();
        while (System.nanoTime() - start < TimeUnit.MILLISECONDS.toNanos(REFRESH_WAITING_TIMEOUT_MILLIS)) {
            cached = redisTemplate.opsForValue().get(key);

            if (cached != null) {
                return DataSerializer.deserializeOrNull(cached, clazz);
            }

            try {
              TimeUnit.MILLISECONDS.sleep(POLLING_INTERVAL_MILLIS);
            } catch (InterruptedException e) {
                break;
            }
        }

        return refresh(key, ttl, dataSourceSupplier);
    }

    private <T> T refresh(String key, Duration ttl, Supplier<T> dataSourceSupplier) {
        T sourceResult = dataSourceSupplier.get();
        put(key, ttl, sourceResult);
        return sourceResult;
    }

    private String genLockKey(String key) {
        return CacheStrategy.REQUEST_COLLAPSING + ":lock:" + key;
    }

    @Override
    public void put(String key, Duration ttl, Object value) {
        redisTemplate.opsForValue().set(key, DataSerializer.serializeOrException(value), ttl);
    }

    @Override
    public void evict(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean supports(CacheStrategy cacheStrategy) {
        return CacheStrategy.REQUEST_COLLAPSING == cacheStrategy;
    }
}
