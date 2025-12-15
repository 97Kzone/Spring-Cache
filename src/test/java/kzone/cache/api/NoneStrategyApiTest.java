package kzone.cache.api;


import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.response.ItemPageResponse;
import kzone.cache.service.response.ItemResponse;
import org.junit.jupiter.api.Test;

public class NoneStrategyApiTest {

    static final CacheStrategy CACHE_STRATEGY = CacheStrategy.NONE;

    @Test
    void createAndReadUpdateAndDelete() {
        ItemResponse created = ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data"));
        System.out.println("created: " + created);

        ItemResponse read1 = ItemApiTestUtil.read(CACHE_STRATEGY, created.itemId());
        System.out.println("read1: " + read1);

        ItemResponse updated = ItemApiTestUtil.update(CACHE_STRATEGY, read1.itemId(), new ItemUpdateRequest("updatedData"));
        System.out.println("updated2: " + updated);

        ItemResponse read2 = ItemApiTestUtil.read(CACHE_STRATEGY, updated.itemId());
        System.out.println("read2: " + read2);

        ItemApiTestUtil.delete(CACHE_STRATEGY, read1.itemId());

        ItemResponse read3 = ItemApiTestUtil.read(CACHE_STRATEGY, read1.itemId());
        System.out.println("read3: " + read3);
    }

    @Test
    void readAll() {
        for (int i = 0; i < 3; i++) {
            ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data" + i));
        }

        ItemPageResponse itemPage1 = ItemApiTestUtil.readAll(CACHE_STRATEGY, 1L, 2L);
        System.out.println("itemPage1: " + itemPage1);

        ItemPageResponse itemPage2 = ItemApiTestUtil.readAll(CACHE_STRATEGY, 2L, 2L);
        System.out.println("itemPage2: " + itemPage2);
    }

    @Test
    void readAllInfiniteScroll() {
        for (int i = 0; i < 3; i++) {
            ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data" + i));
        }

        ItemPageResponse itemPage1 = ItemApiTestUtil.readAllInfiniteScroll(CACHE_STRATEGY, null, 2L);
        System.out.println("itemPage1: " + itemPage1);

        ItemPageResponse itemPage2 = ItemApiTestUtil.readAllInfiniteScroll(CACHE_STRATEGY, itemPage1.items().getLast().itemId(), 2L);
        System.out.println("itemPage2: " + itemPage2);
    }
}
