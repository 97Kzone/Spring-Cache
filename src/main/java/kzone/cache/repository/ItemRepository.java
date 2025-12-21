package kzone.cache.repository;

import kzone.cache.model.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;

/**
 * Data Source
 * DB / 외부 API / 무거운 연산
 */

@Slf4j
@Repository
public class ItemRepository {
    private final ConcurrentSkipListMap<Long, Item> database = new ConcurrentSkipListMap<>(Comparator.reverseOrder());

    public Optional<Item> read(Long itemId) {
        log.info("[ItemRepository.read] itemId={}", itemId);
        return Optional.ofNullable(database.get(itemId));
    }

    public List<Item> readAll(Long page, Long pageSize) {
        log.info("[ItemRepository.readAll] page={} pageSize={}", page, pageSize);
        return database.values().stream()
                .skip((page - 1) * pageSize)
                .limit(pageSize)
                .toList();
    }

    public List<Item> readAllInfiniteScroll(Long lastItemId, Long pageSize) {
        log.info("[ItemRepository.infiniteScroll] lastItemId={}, pageSize={}", lastItemId, pageSize);
        if(lastItemId == null) {
            return database.values().stream()
                    .limit(pageSize)
                    .toList();
        }

        return database.tailMap(lastItemId, false).values().stream()
                .limit(pageSize)
                .toList();
    }

    public Item create(Item item) {
        log.info("[ItemRepository.create] item={}", item);
        database.put(item.getItemId(), item);

        return item;
    }

    public Item update(Item item) {
        log.info("[ItemRepository.update] item={}", item);
        database.put(item.getItemId(), item);

        return item;
    }

    public void delete(Item item) {
        log.info("[ItemRepository.delete] item={}", item);
        database.remove(item.getItemId());
    }

    public long count() {
        log.info("[ItemRepository.count] count");
        return database.size();
    }
}
