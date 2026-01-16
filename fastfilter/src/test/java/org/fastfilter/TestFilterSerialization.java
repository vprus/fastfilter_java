package org.fastfilter;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.util.function.Function;

import org.fastfilter.bloom.Bloom;
import org.fastfilter.bloom.BlockedBloom;
import org.fastfilter.bloom.count.CountingBloom;
import org.fastfilter.cuckoo.Cuckoo8;
import org.fastfilter.cuckoo.Cuckoo16;
import org.fastfilter.cuckoo.CuckooPlus8;
import org.fastfilter.cuckoo.CuckooPlus16;
import org.fastfilter.xor.Xor8;
import org.fastfilter.xor.Xor16;
import org.fastfilter.xor.XorBinaryFuse8;
import org.fastfilter.xor.XorBinaryFuse16;
import org.fastfilter.xor.XorBinaryFuse32;
import org.fastfilter.xorplus.XorPlus8;
import org.fastfilter.utils.Hash;
import org.fastfilter.utils.RandomGenerator;
import org.junit.Test;

public class TestFilterSerialization {

    /**
     * Generic test method that tests serialization/deserialization for any filter.
     * 
     * @param original the original filter
     * @param readFrom function to deserialize the filter from a ByteBuffer
     * @param keys the keys that were added to the filter
     */
    private <F extends Filter> void testFilterSerialization(F original, Function<ByteBuffer, F> readFrom, long[] keys) {
        // Serialize
        ByteBuffer buffer = ByteBuffer.allocate(10000000);
        original.writeTo(buffer);
        
        // Deserialize
        buffer.flip();
        F restored = readFrom.apply(buffer);
        
        // Verify all keys are still found
        for (long key : keys) {
            assertTrue("Key " + key + " should be found", restored.mayContain(key));
        }
        
        // Verify bit count is the same
        assertEquals(original.getBitCount(), restored.getBitCount());
    }

    @Test
    public void testBloomSerialization() {
        Hash.setSeed(1);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        Bloom original = Bloom.construct(keys, 10);
        testFilterSerialization(original, Bloom::readFrom, keys);
    }

    @Test
    public void testBlockedBloomSerialization() {
        Hash.setSeed(2);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        BlockedBloom original = BlockedBloom.construct(keys, 10);
        testFilterSerialization(original, BlockedBloom::readFrom, keys);
    }

    @Test
    public void testXor8Serialization() {
        Hash.setSeed(3);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        Xor8 original = Xor8.construct(keys);
        testFilterSerialization(original, Xor8::readFrom, keys);
    }

    @Test
    public void testXor16Serialization() {
        Hash.setSeed(4);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        Xor16 original = Xor16.construct(keys);
        testFilterSerialization(original, Xor16::readFrom, keys);
    }

    @Test
    public void testCuckoo8Serialization() {
        Hash.setSeed(5);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        Cuckoo8 original = Cuckoo8.construct(keys);
        testFilterSerialization(original, Cuckoo8::readFrom, keys);
    }

    @Test
    public void testCuckoo16Serialization() {
        Hash.setSeed(6);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        Cuckoo16 original = Cuckoo16.construct(keys);
        testFilterSerialization(original, Cuckoo16::readFrom, keys);
    }

    @Test
    public void testCuckooPlus8Serialization() {
        Hash.setSeed(7);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        CuckooPlus8 original = CuckooPlus8.construct(keys);
        testFilterSerialization(original, CuckooPlus8::readFrom, keys);
    }

    @Test
    public void testCuckooPlus16Serialization() {
        Hash.setSeed(8);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        CuckooPlus16 original = CuckooPlus16.construct(keys);
        testFilterSerialization(original, CuckooPlus16::readFrom, keys);
    }

    @Test
    public void testXorBinaryFuse8Serialization() {
        Hash.setSeed(9);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        XorBinaryFuse8 original = XorBinaryFuse8.construct(keys);
        testFilterSerialization(original, XorBinaryFuse8::readFrom, keys);
    }

    @Test
    public void testXorBinaryFuse16Serialization() {
        Hash.setSeed(10);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        XorBinaryFuse16 original = XorBinaryFuse16.construct(keys);
        testFilterSerialization(original, XorBinaryFuse16::readFrom, keys);
    }

    @Test
    public void testXorBinaryFuse32Serialization() {
        Hash.setSeed(11);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        XorBinaryFuse32 original = XorBinaryFuse32.construct(keys);
        testFilterSerialization(original, XorBinaryFuse32::readFrom, keys);
    }

    @Test
    public void testXorPlus8Serialization() {
        Hash.setSeed(12);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        XorPlus8 original = XorPlus8.construct(keys);
        testFilterSerialization(original, XorPlus8::readFrom, keys);
    }

    @Test
    public void testCountingBloomSerialization() {
        Hash.setSeed(13);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        CountingBloom original = CountingBloom.construct(keys, 10);
        testFilterSerialization(original, CountingBloom::readFrom, keys);
    }
}
