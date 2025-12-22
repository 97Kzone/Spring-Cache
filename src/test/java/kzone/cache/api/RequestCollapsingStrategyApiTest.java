package kzone.cache.api;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.response.ItemResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RequestCollapsingStrategyApiTest {

    static final CacheStrategy CACHE_STRATEGY = CacheStrategy.REQUEST_COLLAPSING;

    @Test
    void test() throws InterruptedException {
        ItemResponse item = ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data"));

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        long start = System.nanoTime();
        while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
            for (int i = 0; i < 3; i++) {
                executorService.execute(() -> ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId()));
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }

        ItemApiTestUtil.update(CACHE_STRATEGY, item.itemId(), new ItemUpdateRequest("updated"));
        ItemResponse updated = ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId());
        System.out.println("updated = " + updated);

        ItemApiTestUtil.delete(CACHE_STRATEGY, item.itemId());
        ItemResponse deleted = ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId());
        System.out.println("deleted = " + deleted);
    }
}
