package kzone.cache.api;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.response.ItemPageResponse;
import kzone.cache.service.response.ItemResponse;
import org.junit.jupiter.api.Test;

public class WriteThroughStrategyApiTest {
    static final CacheStrategy CACHE_STRATEGY = CacheStrategy.WRITE_THROUGH;

    @Test
    void test() {
        for (int i = 0; i < 120; i++) {
            ItemResponse item = ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data" + i));
            ItemResponse response = ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId());
            System.out.println("response = " + response);
        }

        ItemPageResponse readAllPage1 = ItemApiTestUtil.readAll(CACHE_STRATEGY, 1, 60);
        ItemPageResponse readAllPage2 = ItemApiTestUtil.readAll(CACHE_STRATEGY, 2, 60);
        System.out.println("readAllPage1.items().size() = " + readAllPage1.items().size());
        System.out.println("readAllPage2.items().size() = " + readAllPage2.items().size());

        ItemPageResponse readAllInfiniteScrollPage1 = ItemApiTestUtil.readAllInfiniteScroll(CACHE_STRATEGY, null, 60);
        ItemPageResponse readAllInfiniteScrollPage2 = ItemApiTestUtil.readAllInfiniteScroll(CACHE_STRATEGY,
                readAllInfiniteScrollPage1.items().getLast().itemId(), 60);
        System.out.println("readAllInfiniteScrollPage1.items().size() = " + readAllInfiniteScrollPage1.items().size());
        System.out.println("readAllInfiniteScrollPage2.items().size() = " + readAllInfiniteScrollPage2.items().size());

        ItemResponse item = readAllPage1.items().getFirst();
        ItemApiTestUtil.update(CACHE_STRATEGY, item.itemId(), new ItemUpdateRequest("updated"));
        ItemResponse updated = ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId());
        System.out.println("updated = " + updated);

        ItemApiTestUtil.delete(CACHE_STRATEGY, item.itemId());
        ItemResponse deleted = ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId());
        System.out.println("deleted = " + deleted);

        readAllPage1 = ItemApiTestUtil.readAll(CACHE_STRATEGY, 1, 60);
        System.out.println("readAllPage1.items().getFirst() = " + readAllPage1.items().getFirst());

        readAllInfiniteScrollPage1 = ItemApiTestUtil.readAllInfiniteScroll(CACHE_STRATEGY, null, 60);
        System.out.println("readAllInfiniteScrollPage1.items().getFirst() = " + readAllInfiniteScrollPage1.items().getFirst());
    }
}
