package kzone.cache.service.strategy.splitshardedbloomfilter;

import kzone.cache.RedisTestContainerSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class SplitShardedBloomFilterRedisHandlerTest extends RedisTestContainerSupport {
    @Autowired
    SplitShardedBloomFilterRedisHandler splitShardedBloomFilterRedisHandler;

    @Test
    void mightContain() {
        // given
        SplitShardedBloomFilter splitShardedBloomFilter = SplitShardedBloomFilter.create(
                "testId", 1000, 0.01, 4
        );

        List<String> values = IntStream.range(0, 1000).mapToObj(idx -> "value" + idx).toList();
        for (String value : values) {
            splitShardedBloomFilterRedisHandler.add(splitShardedBloomFilter, value);
        }

        // when, then
        for (String value : values) {
            boolean result = splitShardedBloomFilterRedisHandler.mightContain(splitShardedBloomFilter, value);
            assertThat(result).isTrue();
        }

        for (int i = 0; i < 100000; i++) {
            String value = "notAddedValue" + i;
            boolean result = splitShardedBloomFilterRedisHandler.mightContain(splitShardedBloomFilter, value);
            if (result) {
                // false positive
                System.out.println("value = " + value);
            }
        }
    }

}