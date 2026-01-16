package org.fastfilter;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.fastfilter.bloom.Bloom;
import org.fastfilter.bloom.BlockedBloom;
import org.fastfilter.xor.Xor8;
import org.fastfilter.utils.Hash;
import org.fastfilter.utils.RandomGenerator;
import org.junit.Test;

public class TestFilterSerialization {

    @Test
    public void testBloomSerialization() {
        Hash.setSeed(1);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        Bloom original = Bloom.construct(keys, 10);
        
        // Serialize
        ByteBuffer buffer = ByteBuffer.allocate(100000);
        original.writeTo(buffer);
        
        // Deserialize
        buffer.flip();
        Bloom restored = Bloom.readFrom(buffer);
        
        // Verify all keys are still found
        for (long key : keys) {
            assertTrue("Key " + key + " should be found", restored.mayContain(key));
        }
        
        // Verify bit count is the same
        assertEquals(original.getBitCount(), restored.getBitCount());
    }

    @Test
    public void testBlockedBloomSerialization() {
        Hash.setSeed(2);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        BlockedBloom original = BlockedBloom.construct(keys, 10);
        
        // Serialize
        ByteBuffer buffer = ByteBuffer.allocate(100000);
        original.writeTo(buffer);
        
        // Deserialize
        buffer.flip();
        BlockedBloom restored = BlockedBloom.readFrom(buffer);
        
        // Verify all keys are still found
        for (long key : keys) {
            assertTrue("Key " + key + " should be found", restored.mayContain(key));
        }
        
        // Verify bit count is the same
        assertEquals(original.getBitCount(), restored.getBitCount());
    }

    @Test
    public void testXor8Serialization() {
        Hash.setSeed(3);
        long[] keys = new long[1000];
        RandomGenerator.createRandomUniqueListFast(keys, 1000);
        
        Xor8 original = Xor8.construct(keys);
        
        // Serialize
        ByteBuffer buffer = ByteBuffer.allocate(100000);
        original.writeTo(buffer);
        
        // Deserialize
        buffer.flip();
        Xor8 restored = Xor8.readFrom(buffer);
        
        // Verify all keys are still found
        for (long key : keys) {
            assertTrue("Key " + key + " should be found", restored.mayContain(key));
        }
        
        // Verify bit count is the same
        assertEquals(original.getBitCount(), restored.getBitCount());
    }
}
