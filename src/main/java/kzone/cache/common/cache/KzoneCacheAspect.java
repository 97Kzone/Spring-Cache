package kzone.cache.common.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class KzoneCacheAspect {
    private final List<KzoneCacheHandler> kzoneCacheHandlers;
    private final KzoneCacheKeyGenerator kzoneCacheKeyGenerator;

    @Around("@annotation(kzoneCacheable)")
    public Object handleCacheable(ProceedingJoinPoint joinPoint, KzoneCacheable kzoneCacheable) throws Throwable {
        CacheStrategy cacheStrategy = kzoneCacheable.cacheStrategy();
        KzoneCacheHandler cacheHandler = findCacheHandler(cacheStrategy);

        String key = kzoneCacheKeyGenerator.genKey(joinPoint, cacheStrategy, kzoneCacheable.cacheName(), kzoneCacheable.key());
        Duration ttl = Duration.ofSeconds(kzoneCacheable.ttlSeconds());
        Supplier<Object> dataSourceSupplier = createDataSourceSupplier(joinPoint);
        Class returnType = findReturnType(joinPoint);

        try {
            log.info("[KzoneCacheAspect.handleCacheable] key={}", key);
            return cacheHandler.fetch(
                    key,
                    ttl,
                    dataSourceSupplier,
                    returnType
            );
        } catch (Exception e) {
            log.error("[KzoneCacheAspect.handleCacheable] key={}", key, e);
            return dataSourceSupplier.get();
        }


    }

    private KzoneCacheHandler findCacheHandler(CacheStrategy cacheStrategy) {
        return kzoneCacheHandlers.stream()
                .filter(handler -> handler.supports(cacheStrategy))
                .findFirst()
                .orElseThrow();
    }

    private Supplier<Object> createDataSourceSupplier(ProceedingJoinPoint joinPoint) {
        return () -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Class findReturnType(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;

        return methodSignature.getReturnType();
    }

    @AfterReturning(pointcut = "@annotation(kzoneCachePut)", returning = "result")
    private void handleCachePut(JoinPoint joinPoint, KzoneCachePut kzoneCachePut, Object result) {
        CacheStrategy cacheStrategy = kzoneCachePut.cacheStrategy();
        KzoneCacheHandler cacheHandler = findCacheHandler(cacheStrategy);
        String key = kzoneCacheKeyGenerator.genKey(joinPoint, cacheStrategy, kzoneCachePut.cacheName(), kzoneCachePut.key());
        log.info("[KzoneCacheAspect.handleCachePut] key={}", kzoneCachePut.key());

        cacheHandler.put(key, Duration.ofSeconds(kzoneCachePut.ttlSeconds()), result);
    }

    @AfterReturning(pointcut = "@annotation(kzoneCacheEvict)")
    private void handleCacheEvict(JoinPoint joinPoint, KzoneCacheEvict kzoneCacheEvict) {
        CacheStrategy cacheStrategy = kzoneCacheEvict.cacheStrategy();
        KzoneCacheHandler cacheHandler = findCacheHandler(cacheStrategy);
        String key = kzoneCacheKeyGenerator.genKey(joinPoint, cacheStrategy, kzoneCacheEvict.cacheName(), kzoneCacheEvict.key());
        log.info("[KzoneCacheAspect.kzoneCacheEvict] key={}", kzoneCacheEvict.key());

        cacheHandler.evict(key);
    }
}
