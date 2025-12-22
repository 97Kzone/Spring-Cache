package kzone.cache.service.requestcollepsing;

import kzone.cache.RedisTestContainerSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RequestCollapsingHandlerTest extends RedisTestContainerSupport {
    @Autowired
    RequestCollapsingHandler requestCollapsingHandler;

    @Test
    void put() {
        // given, when
        requestCollapsingHandler.put("testKey", Duration.ofSeconds(10), "data");

        // then
        String result = redisTemplate.opsForValue().get("testKey");
        assertThat(result).isNotNull();
        System.out.println("result = " + result);
    }

    @Test
    void evict() {
        // given
        requestCollapsingHandler.put("testKey", Duration.ofSeconds(10), "data");

        // when
        requestCollapsingHandler.evict("testKey");

        // then
        String result = redisTemplate.opsForValue().get("testKey");
        assertThat(result).isNull();
    }

    @Test
    void fetch_shouldSupplySourceDataOnlyOnce_whenMultiThreadAndRefreshSucceeded() throws InterruptedException {
        // given, when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger dataSourceExecCount = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                String result = requestCollapsingHandler.fetch(
                        "testKey",
                        Duration.ofSeconds(10),
                        () -> {
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                            }
                            dataSourceExecCount.incrementAndGet();
                            return "sourceData";
                        },
                        String.class
                );
                System.out.println("result = " + result);
                assertThat(result).isEqualTo("sourceData");
                latch.countDown();
            });
        }
        latch.await();

        // then
        assertThat(dataSourceExecCount.get()).isOne();
    }

    @Test
    void fetch_shouldRefreshDataSource_whenMultiThreadAndTimeOut() throws InterruptedException {
        // given, when
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger dataSourceExecCount = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                String result = requestCollapsingHandler.fetch(
                        "testKey",
                        Duration.ofSeconds(10),
                        () -> {
                            try {
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException e) {
                            }
                            dataSourceExecCount.incrementAndGet();
                            return "sourceData";
                        },
                        String.class
                );
                System.out.println("result = " + result);
                assertThat(result).isEqualTo("sourceData");
                latch.countDown();
            });
        }
        latch.await();

        // then
        assertThat(dataSourceExecCount.get()).isEqualTo(10);
    }
}