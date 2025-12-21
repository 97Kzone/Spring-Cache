package kzone.cache.api;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.response.ItemResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class JitterStrategyApiTest {

    static final CacheStrategy CACHE_STRATEGY = CacheStrategy.JITTER;

    @Test
    void test() throws InterruptedException {
        List<ItemResponse> items = List.of(
                ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data1")),
                ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data2")),
                ItemApiTestUtil.create(CACHE_STRATEGY, new ItemCreateRequest("data3"))
        );

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        long start = System.nanoTime();
        while (System.nanoTime() - start < TimeUnit.SECONDS.toNanos(20)) {
            for (ItemResponse item : items) {
                executorService.execute(() -> ItemApiTestUtil.read(CACHE_STRATEGY, item.itemId()));
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }

        ItemApiTestUtil.update(CACHE_STRATEGY, items.getFirst().itemId(), new ItemUpdateRequest("updated"));
        ItemResponse updated = ItemApiTestUtil.read(CACHE_STRATEGY, items.getFirst().itemId());
        System.out.println("updated = " + updated);

        ItemApiTestUtil.delete(CACHE_STRATEGY, items.getFirst().itemId());
        ItemResponse deleted = ItemApiTestUtil.read(CACHE_STRATEGY, items.getFirst().itemId());
        System.out.println("deleted = " + deleted);
    }
}
