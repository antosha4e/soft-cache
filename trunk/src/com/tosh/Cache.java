package com.tosh;

/**
 * User: arsentyev
 * Date: 30.10.12
 */
public interface Cache<K, V> {
    void cache(K key, V value);
    V getCached(K key);
}