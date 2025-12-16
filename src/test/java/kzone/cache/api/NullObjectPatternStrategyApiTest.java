package kzone.cache.api;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.service.response.ItemResponse;
import org.junit.jupiter.api.Test;

public class NullObjectPatternStrategyApiTest {
    static final CacheStrategy CACHE_STRATEGY = CacheStrategy.NULL_OBJECT_PATTERN;

    @Test
    void read() {
        for (int i = 0; i < 3; i++) {
            ItemResponse item = ItemApiTestUtil.read(CACHE_STRATEGY, 99999L);
            System.out.println("item = " + item);
        }
    }
}
