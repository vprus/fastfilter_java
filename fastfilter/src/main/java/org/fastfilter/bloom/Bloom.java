package org.fastfilter.bloom;

import java.nio.ByteBuffer;

import org.fastfilter.Filter;
import org.fastfilter.utils.Hash;

/**
 * A standard Bloom filter.
 *
 */
public class Bloom implements Filter {

    public static Bloom construct(long[] keys, double bitsPerKey) {
        long n = keys.length;
        int k = getBestK(bitsPerKey);
        Bloom f = new Bloom((int) n, bitsPerKey, k);
        for(long x : keys) {
            f.add(x);
        }
        return f;
    }

    private static int getBestK(double bitsPerKey) {
        return Math.max(1, (int) Math.round(bitsPerKey * Math.log(2)));
    }

    private final int k;
    private final long bits;
    private final long seed;
    private final int arraySize;
    private final long[] data;

    public long getBitCount() {
        return data.length * 64L;
    }

    Bloom(int entryCount, double bitsPerKey, int k) {
        entryCount = Math.max(1, entryCount);
        this.k = k;
        this.seed = Hash.randomSeed();
        this.bits = (long) (entryCount * bitsPerKey);
        arraySize = (int) ((bits + 63) / 64);
        data = new long[arraySize];
    }

    @Override
    public boolean supportsAdd() {
        return true;
    }

    @Override
    public void add(long key) {
        long hash = Hash.hash64(key, seed);
        long a = (hash >>> 32) | (hash << 32);
        long b = hash;
        for (int i = 0; i < k; i++) {
            data[Hash.reduce((int) (a >>> 32), arraySize)] |= 1L << a;
            a += b;
        }
    }

    @Override
    public boolean mayContain(long key) {
        long hash = Hash.hash64(key, seed);
        long a = (hash >>> 32) | (hash << 32);
        long b = hash;
        for (int i = 0; i < k; i++) {
            if ((data[Hash.reduce((int) (a >>> 32), arraySize)] & 1L << a) == 0) {
                return false;
            }
            a += b;
        }
        return true;
    }

    @Override
    public void writeTo(ByteBuffer buffer) {
        buffer.put((byte) 1); // version
        buffer.putInt(k);
        buffer.putLong(bits);
        buffer.putLong(seed);
        buffer.putInt(arraySize);
        for (long value : data) {
            buffer.putLong(value);
        }
    }

    public static Bloom readFrom(ByteBuffer buffer) {
        byte version = buffer.get();
        if (version != 1) {
            throw new IllegalArgumentException("Unsupported version: " + version);
        }
        int k = buffer.getInt();
        long bits = buffer.getLong();
        long seed = buffer.getLong();
        int arraySize = buffer.getInt();
        long[] data = new long[arraySize];
        for (int i = 0; i < arraySize; i++) {
            data[i] = buffer.getLong();
        }
        return new Bloom(k, bits, seed, arraySize, data);
    }

    private Bloom(int k, long bits, long seed, int arraySize, long[] data) {
        this.k = k;
        this.bits = bits;
        this.seed = seed;
        this.arraySize = arraySize;
        this.data = data;
    }

}
