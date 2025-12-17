package kzone.cache.service.strategy.splitbloomfilter;

import kzone.cache.RedisTestContainerSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SplitBloomFilterRedisHandlerTest extends RedisTestContainerSupport {

    @Autowired
    SplitBloomFilterRedisHandler splitBloomFilterRedisHandler;

    @Test
    void mightContain() {
        // given
        SplitBloomFilter splitBloomFilter = SplitBloomFilter.create("testID", 1000, 0.01);

        List<String> values = IntStream.range(0, 1000).mapToObj(idx -> "value" + idx).toList();
        for (String value: values) {
            splitBloomFilterRedisHandler.add(splitBloomFilter, value);
        }

        // when, then
        for (String value: values) {
            boolean result = splitBloomFilterRedisHandler.mightContain(splitBloomFilter, value);
            assertThat(result).isTrue();
        }

        for (int i = 0; i < 10000; i++) {
            String value = "notAddedValue" + i;
            boolean result = splitBloomFilterRedisHandler.mightContain(splitBloomFilter, value);
            if (result) {
                // false positive
                System.out.println("value = " + value);
            }
        }
    }

}