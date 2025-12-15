package kzone.cache.api;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.response.ItemPageResponse;
import kzone.cache.service.response.ItemResponse;
import org.junit.jupiter.api.Test;

public class SpringCacheAnnotationStrategyApiTest {

    static final CacheStrategy CACHE_STRATEGY = CacheStrategy.SPRING_CACHE_ANNOTATION;

    @Test
    void createAndReadAndReadAllAndUpdateAndDelete() {

        // create
        ItemResponse item1 = ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data1"));
        ItemResponse item2 = ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data2"));
        ItemResponse item3 = ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data3"));

        // read
        ItemResponse item1Read1 = ItemApiTestUtil.read(CACHE_STRATEGY, item1.itemId());
        ItemResponse item1Read2 = ItemApiTestUtil.read(CACHE_STRATEGY, item1.itemId());
        ItemResponse item1Read3 = ItemApiTestUtil.read(CACHE_STRATEGY, item1.itemId());
        System.out.println("item1Read1: " + item1Read1);
        System.out.println("item1Read2: " + item1Read2);
        System.out.println("item1Read3: " + item1Read3);

        ItemResponse item2Read1 = ItemApiTestUtil.read(CACHE_STRATEGY, item2.itemId());
        ItemResponse item2Read2 = ItemApiTestUtil.read(CACHE_STRATEGY, item2.itemId());
        ItemResponse item2Read3 = ItemApiTestUtil.read(CACHE_STRATEGY, item2.itemId());
        System.out.println("item2Read1: " + item2Read1);
        System.out.println("item2Read2: " + item2Read2);
        System.out.println("item2Read3: " + item2Read3);

        ItemResponse item3Read1 = ItemApiTestUtil.read(CACHE_STRATEGY, item3.itemId());
        ItemResponse item3Read2 = ItemApiTestUtil.read(CACHE_STRATEGY, item3.itemId());
        ItemResponse item3Read3 = ItemApiTestUtil.read(CACHE_STRATEGY, item3.itemId());
        System.out.println("item3Read1: " + item3Read1);
        System.out.println("item3Read2: " + item3Read2);
        System.out.println("item3Read3: " + item3Read3);

        // readAll
        ItemPageResponse itemPageReadAll1 = ItemApiTestUtil.readAll(CACHE_STRATEGY, 1L, 2L);
        ItemPageResponse itemPageReadAll2 = ItemApiTestUtil.readAll(CACHE_STRATEGY, 1L, 2L);
        System.out.println("itemPageReadAll1: " + itemPageReadAll1);
        System.out.println("itemPageReadAll2: " + itemPageReadAll2);

        // readAllInfiniteScroll
        ItemPageResponse itemPageReadAllInfiniteScroll1 = ItemApiTestUtil.readAllInfiniteScroll(CACHE_STRATEGY, null, 2L);
        ItemPageResponse itemPageReadAllInfiniteScroll2 = ItemApiTestUtil.readAllInfiniteScroll(CACHE_STRATEGY, null, 2L);
        System.out.println("itemPageReadAllInfiniteScroll1: " + itemPageReadAllInfiniteScroll1);
        System.out.println("itemPageReadAllInfiniteScroll2: " + itemPageReadAllInfiniteScroll2);

        // update
        ItemApiTestUtil.update(CACHE_STRATEGY, item1.itemId(), new ItemUpdateRequest("updateData"));
        ItemResponse updated = ItemApiTestUtil.read(CACHE_STRATEGY, item1.itemId());
        System.out.println("updated: " + updated);

        // delete
        ItemApiTestUtil.delete(CACHE_STRATEGY, item1.itemId());
        try {
            ItemApiTestUtil.read(CACHE_STRATEGY, item1.itemId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
