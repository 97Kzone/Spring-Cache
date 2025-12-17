package kzone.cache.api;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class SplitBloomFilterStrategyApiTest {

    static final CacheStrategy CACHE_STRATEGY = CacheStrategy.SPLIT_BLOOM_FILTER;

    @Test
    void test() {
        for (int i = 0; i < 1000; i++) {
            ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data" + i));
        }

        for (long itemId = 10000; itemId < 20000; itemId++) {
            ItemApiTestUtil.read(CACHE_STRATEGY, itemId);
        }
    }
}
