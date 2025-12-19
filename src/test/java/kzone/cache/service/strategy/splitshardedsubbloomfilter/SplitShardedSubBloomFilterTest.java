package kzone.cache.service.strategy.splitshardedsubbloomfilter;

import kzone.cache.service.strategy.splitshardedbloomfilter.SplitShardedBloomFilter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class SplitShardedSubBloomFilterTest {

    @Test
    void create() {
        SplitShardedSubBloomFilter splitShardedSubBloomFilter = SplitShardedSubBloomFilter.create("testId", 1000, 0.01, 4);

        System.out.println("splitShardedSubBloomFilter = " + splitShardedSubBloomFilter);
        assertThat(splitShardedSubBloomFilter.getId()).isEqualTo("testId");
    }
    
    @Test
    void findSubFilter() {
        // given
        SplitShardedSubBloomFilter splitShardedSubBloomFilter = SplitShardedSubBloomFilter.create("testId", 1000, 0.01, 4);

        SplitShardedBloomFilter subFilter0 = splitShardedSubBloomFilter.findSubFilter(0);
        SplitShardedBloomFilter subFilter1 = splitShardedSubBloomFilter.findSubFilter(1);
        SplitShardedBloomFilter subFilter2 = splitShardedSubBloomFilter.findSubFilter(2);

        System.out.println("subFilter0 = " + subFilter0);
        System.out.println("subFilter1 = " + subFilter1);
        System.out.println("subFilter2 = " + subFilter2);
        
        assertThat(subFilter0.getId()).isEqualTo(splitShardedSubBloomFilter.getId() + ":sub:0");
        assertThat(subFilter0.getDataCount())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getDataCount() * 2);
        assertThat(subFilter0.getFalsePositiveRate())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getFalsePositiveRate() / 2);
        assertThat(subFilter0.getShardCount())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getShardCount());

        assertThat(subFilter1.getId()).isEqualTo(splitShardedSubBloomFilter.getId() + ":sub:1");
        assertThat(subFilter1.getDataCount())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getDataCount() * 4);
        assertThat(subFilter1.getFalsePositiveRate())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getFalsePositiveRate() / 4);
        assertThat(subFilter1.getShardCount())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getShardCount());

        assertThat(subFilter2.getId()).isEqualTo(splitShardedSubBloomFilter.getId() + ":sub:2");
        assertThat(subFilter2.getDataCount())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getDataCount() * 8);
        assertThat(subFilter2.getFalsePositiveRate())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getFalsePositiveRate() / 8);
        assertThat(subFilter2.getShardCount())
                .isEqualTo(splitShardedSubBloomFilter.getSplitShardedBloomFilter().getShardCount());
    }
            
}