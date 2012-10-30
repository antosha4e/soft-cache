package com.tosh.data;

import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: arsentyev
 * Date: 20.07.12
 */
public class SoftHashMap<K, V> extends AbstractMap<K, V> implements Serializable {
    private final Map<K, SoftReference<V>> hash = new HashMap<K, SoftReference<V>>();

    public V get(Object key) {
        V result = null;
        SoftReference<V> soft_ref = hash.get(key);
        if (soft_ref != null) {
            result = soft_ref.get();
            if (result == null) {
                hash.remove(key);
            }
        }
        return result;
    }

    public V put(K key, V value) {
        return hash.put(key, new SoftReference<V>(value)).get();
    }

    public V remove(Object key) {
        return hash.remove(key).get();
    }

    public void clear() {
        hash.clear();
    }

    public int size() {
        return hash.size();
    }

    public Set<Entry<K,V>> entrySet() {
        Set<Entry<K,V>> result = new LinkedHashSet<Entry<K, V>>();
        for (final Entry<K, SoftReference<V>> entry : hash.entrySet()) {
            final V value = entry.getValue().get();
            if (value != null) {
                result.add(new Entry<K, V>() {
                    public K getKey() {
                        return entry.getKey();
                    }
                    public V getValue() {
                        return value;
                    }
                    public V setValue(V v) {
                        entry.setValue(new SoftReference<V>(v));
                        return value;
                    }
                });
            }
        }
        return result;
    }
}