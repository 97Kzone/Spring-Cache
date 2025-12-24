package kzone.cache.api;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.response.ItemResponse;
import org.junit.jupiter.api.Test;

public class ApplicationLevelShardingReplicationStrategyApiTest {
    static final CacheStrategy CACHE_STRATEGY = CacheStrategy.APPLICATION_LEVEL_SHARDING_REPLICATION;

    @Test
    void test() {
        ItemResponse item = ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data"));
        for (int i = 0; i < 3; i++) {
            ItemResponse read = ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId());
            System.out.println("read = " + read);
        }

        ItemApiTestUtil.update(CACHE_STRATEGY, item.itemId(), new ItemUpdateRequest("updated"));
        ItemResponse updated = ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId());
        System.out.println("updated = " + updated);

        ItemApiTestUtil.delete(CACHE_STRATEGY, item.itemId());
        ItemResponse deleted = ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId());
        System.out.println("deleted = " + deleted);
    }
}
