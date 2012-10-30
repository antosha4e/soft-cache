package com.tosh;

import com.tosh.data.ConcurrentSoftHashMap;

/**
 * User: arsentyev
 * Date: 30.07.12
 */
public class SoftCache<K, V> implements Cache<K, V> {
    private final ConcurrentSoftHashMap<K, V> values = new ConcurrentSoftHashMap<K, V>();

    public void cache(K key, V value) {
        values.putIfAbsent(key, value);
    }

    public V getCached(K key) {
        return null;
    }
}