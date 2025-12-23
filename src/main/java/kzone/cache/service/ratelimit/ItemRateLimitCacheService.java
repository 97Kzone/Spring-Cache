package kzone.cache.service.ratelimit;

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
public class ItemRateLimitCacheService implements ItemCacheService {

    private final ItemService itemService;
    private final RateLimiter rateLimiter;

    private static final String  RATE_LIMIT_ID = "itemRead";
    private static final long RATE_LIMIT_COUNT = 100;
    private static final long RATE_LIMIT_PER_SECONDS = 1;


    @Override
    public ItemResponse read(Long itemId) {
        boolean allowed = rateLimiter.isAllowed(RATE_LIMIT_ID, RATE_LIMIT_COUNT, RATE_LIMIT_PER_SECONDS);
        if (allowed) {
            return itemService.read(itemId);
        }
        throw new RuntimeException("item read rate limit exception");
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
    public ItemResponse update(Long itemId, ItemUpdateRequest request) {
        return itemService.update(itemId, request);
    }

    @Override
    public void delete(Long itemId) {
        itemService.delete(itemId);
    }

    @Override
    public boolean supports(CacheStrategy cacheStrategy) {
        return CacheStrategy.RATE_LIMIT == cacheStrategy;
    }
}
