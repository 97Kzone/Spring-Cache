package kzone.cache.service.ratelimit;

import kzone.cache.RedisTestContainerSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RateLimiterTest extends RedisTestContainerSupport {
    @Autowired
    RateLimiter rateLimiter;

    @Test
    void isAllowed() throws InterruptedException {
        String id = "testId";
        long limit = 10;

        for (int i = 0; i < limit * 3; i++) {
            assertThat(rateLimiter.isAllowed(id, limit, 1)).isEqualTo(i < limit);
        }

        TimeUnit.SECONDS.sleep(2);

        for (int i = 0; i < limit * 3; i++) {
            assertThat(rateLimiter.isAllowed(id, limit, 1)).isEqualTo(i < limit);
        }
    }
}