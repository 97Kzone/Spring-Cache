package kzone.cache.service.strategy.nullobjectpattern;

import kzone.cache.common.cache.CacheStrategy;
import kzone.cache.model.ItemCreateRequest;
import kzone.cache.model.ItemUpdateRequest;
import kzone.cache.service.ItemCacheService;
import kzone.cache.service.ItemService;
import kzone.cache.service.response.ItemPageResponse;
import kzone.cache.service.response.ItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemNullObjectPatternCacheService implements ItemCacheService {

    private final ItemService itemService;

    /**
     *  더욱 유연한 구조의 null object 만들 수 있다
     *  ex) 데이터가 정말 없는지, 또는 비공개 처리 되어 접근이 불가능한지 등 예외 정보도 함께 캐시해서 분기
     */
    private static final ItemResponse nullObject = new ItemResponse(null, null);

    @Override
    @Cacheable(cacheNames = "item", key = "#itemId")
    public ItemResponse read(Long itemId) {
        ItemResponse itemResponse = itemService.read(itemId);
        if (itemResponse == null) {
            return nullObject;
        }

        return itemResponse;
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
        return CacheStrategy.NULL_OBJECT_PATTERN == cacheStrategy;
    }
}
