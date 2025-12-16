package kzone.cache.service.strategy.bloomfilter;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.ItemCacheService;
import kzone.cache.service.ItemService;
import kzone.cache.service.response.ItemPageResponse;
import kzone.cache.service.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemBloomFilterCacheService implements ItemCacheService {
    private final ItemService itemService;
    private final BloomFilterRedisHandler bloomFilterRedisHandler;

    private static final BloomFilter bloomfilter = BloomFilter.create(
            "item-bloom-filter",
            1000,
            0.01
    );

    @Override
    public ItemResponse read(Long itemId) {
        boolean result = bloomFilterRedisHandler.mightContain(bloomfilter, String.valueOf(itemId));
        if (!result) {
            return null;
        }

        return itemService.read(itemId);
    }

    @Override
    public ItemPageResponse readAll(Long page, Long pageSize) {
        return itemService.readAll(page, pageSize);
    }

    @Override
    public ItemPageResponse readAllInfiniteScroll(Long lastItemId, Long pageSize) {
        return itemService.readAllInfiniteScroll(lastItemId, pageSize);
    }

    @Override
    public ItemResponse crete(ItemCreateRequest request) {
        ItemResponse itemResponse = itemService.creat(request);
        bloomFilterRedisHandler.add(bloomfilter, String.valueOf(itemResponse.itemId()));

        return itemResponse;
    }

    @Override
    public ItemResponse update(Long itemId, ItemUpdateRequest request) {
        return itemService.update(itemId, request);
    }

    @Override
    public void delete(Long itemId) {
        itemService.delete(itemId);
    }

    @Override
    public boolean supports(CacheStrategy cacheStrategy) {
        return CacheStrategy.BLOOM_FILTER == cacheStrategy;
    }
}
