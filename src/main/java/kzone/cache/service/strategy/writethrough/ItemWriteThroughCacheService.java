package kzone.cache.service.strategy.writethrough;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.ItemCacheService;
import kzone.cache.service.ItemService;
import kzone.cache.service.response.ItemPageResponse;
import kzone.cache.service.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemWriteThroughCacheService implements ItemCacheService {

    private final ItemService itemService;
    private final RedisRepository redisRepository;

    private static final String LIST_ID = "itemList";
    private static final Duration TIME_TO_LIVE = Duration.ofMinutes(5);

    @Override
    public ItemResponse read(Long itemId) {
        ItemResponse item = redisRepository.read(genRedisId(itemId), ItemResponse.class);
        if (item != null) {
            return item;
        }

        return itemService.read(itemId);
    }

    @Override
    public ItemPageResponse readAll(Long page, Long pageSize) {
        List<ItemResponse> items = redisRepository.readAll(LIST_ID, page, pageSize, ItemResponse.class);
        if (items.size() < pageSize) {
            itemService.readAll(page, pageSize);
        }

        return ItemPageResponse.fromResponse(
                items, itemService.count()
        );
    }

    @Override
    public ItemPageResponse readAllInfiniteScroll(Long lastItemId, Long pageSize) {
        List<ItemResponse> items = redisRepository.readAllInfiniteScroll(LIST_ID, lastItemId, pageSize, ItemResponse.class);
        if (items.size() < pageSize) {
            itemService.readAllInfiniteScroll(lastItemId, pageSize);
        }

        return ItemPageResponse.fromResponse(
                items, itemService.count()
        );
    }

    @Override
    public ItemResponse crete(ItemCreateRequest request) {
        ItemResponse itemResponse = itemService.creat(request);
        redisRepository.add(
                LIST_ID,
                genRedisId(itemResponse.itemId()),
                itemResponse,
                TIME_TO_LIVE,
                itemResponse.itemId()
        );

        return itemResponse;
    }

    @Override
    public ItemResponse update(Long itemId, ItemUpdateRequest request) {
        ItemResponse itemResponse = itemService.update(itemId, request);
        redisRepository.add(
                LIST_ID,
                genRedisId(itemResponse.itemId()),
                itemResponse,
                TIME_TO_LIVE,
                itemResponse.itemId()
        );

        return itemResponse;
    }

    @Override
    public void delete(Long itemId) {
        itemService.delete(itemId);
        redisRepository.del(LIST_ID, genRedisId(itemId));
    }

    @Override
    public boolean supports(CacheStrategy cacheStrategy) {
        return CacheStrategy.WRITE_THROUGH == cacheStrategy;
    }

    private String genRedisId(Long itemId) {
        return "item:" + itemId;
    }
}
