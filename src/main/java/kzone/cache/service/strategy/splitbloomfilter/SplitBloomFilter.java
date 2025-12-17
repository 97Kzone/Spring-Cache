package kzone.cache.service.strategy.splitbloomfilter;

import kzone.cache.service.strategy.bloomfilter.BloomFilter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SplitBloomFilter {
    private String id;
    private BloomFilter bloomFilter;
    private long splitCount;

    // public static final long BIT_SPLIT_UNIT = 1L << 32;
    public static final long BIT_SPLIT_UNIT = 1L << 10; // 2^10

    public static SplitBloomFilter create(String id, long dataCount, double falsePositiveRate) {
        BloomFilter bloomFilter = BloomFilter.create(id, dataCount, falsePositiveRate);

        // 비트 사이즈 1024 -> (1024 - 1) / 1024 + 1 == 1개의 Split
        // 비트 사이즈 1025 -> (1025 - 1) / 1024 + 1 == 2개의 Split
        long splitCount = (bloomFilter.getBitSize() - 1) / BIT_SPLIT_UNIT + 1;
        SplitBloomFilter splitBloomFilter = new SplitBloomFilter();
        splitBloomFilter.id = id;
        splitBloomFilter.bloomFilter = bloomFilter;
        splitBloomFilter.splitCount = splitCount;

        return splitBloomFilter;
    }

    public long findSplitIndex(Long hashedIndex) {
        // hashedIndex == 1023 -> 0번째 split
        // hashedIndex == 1024 -> 1번째 split
        if (hashedIndex >= bloomFilter.getBitSize()) {
            throw new IllegalArgumentException("hashedIndex out of bounds");
        }
        return hashedIndex / BIT_SPLIT_UNIT;
    }

    public long calSplitBitSize(long splitIndex) {
        if (splitIndex == splitCount - 1) {
            return bloomFilter.getBitSize() - (BIT_SPLIT_UNIT * splitIndex);
        }

        return BIT_SPLIT_UNIT;
    }
}
