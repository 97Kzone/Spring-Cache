package kzone.cache.service.requestcollepsing;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.common.cache.KzoneCacheEvict;
import kzone.cache.common.cache.KzoneCachePut;
import kzone.cache.common.cache.KzoneCacheable;
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
public class ItemRequestCollapsingCacheService implements ItemCacheService {
    private final ItemService itemService;

    @Override
    @KzoneCacheable(
            cacheStrategy = CacheStrategy.REQUEST_COLLAPSING,
            cacheName = "item",
            key = "#item",
            ttlSeconds = 1
    )
    public ItemResponse read(Long itemId) {
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
        return itemService.creat(request);
    }

    @Override
    @KzoneCachePut(
            cacheStrategy = CacheStrategy.REQUEST_COLLAPSING,
            cacheName = "item",
            key = "#item",
            ttlSeconds = 1
    )
    public ItemResponse update(Long itemId, ItemUpdateRequest request) {
        return itemService.update(itemId, request);
    }

    @Override
    @KzoneCacheEvict(
            cacheStrategy = CacheStrategy.REQUEST_COLLAPSING,
            cacheName = "item",
            key = "#item"
    )
    public void delete(Long itemId) {
        itemService.delete(itemId);
    }

    @Override
    public boolean supports(CacheStrategy cacheStrategy) {
        return CacheStrategy.REQUEST_COLLAPSING == cacheStrategy;
    }
}
