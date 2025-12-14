package kzone.cache.service;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.response.ItemPageResponse;
import kzone.cache.service.response.ItemResponse;

public interface ItemCacheService {
    ItemResponse read(Long itemId);

    ItemPageResponse readAll(Long page, Long pageSize);

    ItemPageResponse readAllInfiniteScroll(Long lastItemId, Long pageSize);

    ItemResponse crete(ItemCreateRequest request);

    ItemResponse update(Long itemId, ItemUpdateRequest request);

    void delete(Long itemId);

    boolean supports(CacheStrategy cacheStrategy);
}
