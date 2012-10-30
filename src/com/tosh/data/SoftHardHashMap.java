package com.tosh.data;

import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * User: arsentyev
 * Date: 20.07.12
 */
public class SoftHardHashMap<K, V> extends AbstractMap<K, V> implements Serializable {
    /**
     * The internal HashMap that will hold the SoftReference.
     */
    private final Map<K, SoftReference<V>> hash = new HashMap<K, SoftReference<V>>();
    /**
     * The number of "hard" references to hold internally.
     */
    private final int HARD_SIZE;
    /**
     * The FIFO list of hard references, order of last access.
     */
    private final LinkedList<V> hardCache = new LinkedList<V>();
    /**
     * Reference queue for cleared SoftReference objects.
     */
    private final ReferenceQueue<V> queue = new ReferenceQueue<V>();

    public SoftHardHashMap() {
        this(100);
    }

    public SoftHardHashMap(int hardSize) {
        HARD_SIZE = hardSize;
    }

    public V get(Object key) {
        V result = null;
        // We get the SoftReference represented by that key
        SoftReference<V> soft_ref = (SoftReference<V>) hash.get(key);
        if (soft_ref != null) {
            // From the SoftReference we get the value, which can be
            // null if it was not in the map, or it was removed in
            // the processQueue() method defined below
            result = soft_ref.get();
            if (result == null) {
                // If the value has been garbage collected, remove the
                // entry from the HashMap.
                hash.remove(key);
            } else {
                // We now add this object to the beginning of the hard
                // reference queue.  One reference can occur more than
                // once, because lookups of the FIFO queue are slow, so
                // we don't want to search through it each time to remove
                // duplicates.
                hardCache.addFirst(result);
                if (hardCache.size() > HARD_SIZE) {
                    // Remove the last entry if list longer than HARD_SIZE
                    hardCache.removeLast();
                }
            }
        }
        return result;
    }

    /**
     * We define our own subclass of SoftReference which contains
     * not only the value but also the key to make it easier to find
     * the entry in the HashMap after it's been garbage collected.
     */
    private static class SoftValue<K, V> extends SoftReference<K> {
        private final K key;
        private final V value;

        private SoftValue(K key, V value, ReferenceQueue<K> q) {
            super(key, q);
            this.key = key;
            this.value = value;
        }
    }

    /**
     * Here we go through the ReferenceQueue and remove garbage
     * collected SoftValue objects from the HashMap by looking them
     * up using the SoftValue.key data member.
     */
    private void processQueue() {
        SoftValue sv;
        while ((sv = (SoftValue) queue.poll()) != null) {
            hash.remove(sv.key);
        }
    }

    /**
     * Here we put the key, value pair into the HashMap using a SoftValue object.
     */
    public V put(K key, V value) {
        processQueue(); // throw out garbage collected values first
        return hash.put(key, new SoftValue(key, value, queue)).get();
    }

    public V remove(Object key) {
        processQueue();
        return hash.remove(key).get();
    }

    public void clear() {
        hardCache.clear();
        processQueue();
        hash.clear();
    }

    public int size() {
        processQueue();
        return hash.size();
    }

//    public Set entrySet() {
//        throw new UnsupportedOperationException();
//    }

    /**
     * Returns a copy of the key/values in the map at the point of
     * calling.  However, setValue still sets the value in the
     * actual SoftHashMap.
     */
    public Set<Entry<K,V>> entrySet() {
        processQueue();
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
                        entry.setValue(new SoftReference<V>(v, queue));
                        return value;
                    }
                });
            }
        }
        return result;
    }
}