package com.tosh;

import java.lang.ref.SoftReference;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * User: arsentyev
 * Date: 30.07.12
 */
public class SoftList<T> extends AbstractList<T> {
    private final ArrayList<SoftReference<T>> list = new ArrayList<SoftReference<T>>();

    public int size() {
        return list.size();
    }

    public boolean add(T value) {
        if(value == null) {
            return false;
        }
        return list.add(new SoftReference<T>(value));
    }

    public boolean addAll(Collection<? extends T> collection) {
        if(collection == null || collection.isEmpty()) {
            return false;
        }
        for(T t: collection) {
            add(t);
        }
        return true;
    }

    public boolean addAll(int index, Collection<? extends T> collection) {
        if(collection == null || collection.isEmpty()) {
            return false;
        }
        for(T t: collection) {
            add(index++, t);
        }
        return true;
    }

    public void add(int index, T value) {
        if(value == null) {
            return;
        }
        list.add(index, new SoftReference<T>(value));
    }

    public T set(int index, T value) {
        if(value == null) {
            return null;
        }
        SoftReference<T> old = list.set(index, new SoftReference<T>(value));
        if(old != null) {
            return old.get();
        }
        return null;
    }

    public T get(int index) {
        SoftReference<T> value = list.get(index);
        if(value != null) {
            return value.get();
        }
        return null;
    }

    public void clear() {
        list.clear();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Iterator<SoftReference<T>> iterator = list.iterator();

            public boolean hasNext() {
                return iterator.hasNext();
            }

            public T next() {
                SoftReference<T> value = iterator.next();
                return value == null ? null : value.get();
            }

            public void remove() {
                iterator.remove();
            }
        };
    }

    public ListIterator<T> listIterator() {
        return new ListItr<T>(list.listIterator());
    }

    public ListIterator<T> listIterator(final int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        return new ListItr<T>(list.listIterator(index));
    }

    public boolean contains(Object obj) {
        if(obj == null) {
            return false;
        }

        for (SoftReference<T> aList : list) {
            if (obj.equals(aList.get())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsAll(Collection<?> collection) {
        if(collection == null || collection.isEmpty()) {
            return false;
        }

        for (Object obj: collection) {
            if (!contains(obj)) {
                return false;
            }
        }
        return true;
    }

    public int indexOf(Object obj) {
        if(obj == null) {
            return -1;
        }

        for(int i = 0; i < size(); i++) {
            if(obj.equals(get(i))) {
                return i;
            }
        }

        return -1;
    }

    public int lastIndexOf(Object obj) {
        if(obj == null) {
            return -1;
        }

        for(int i = size() - 1; i >= 0; i--) {
            if(obj.equals(get(i))) {
                return i;
            }
        }

        return -1;
    }

    public Object[] toArray() {
        Object[] objects = list.toArray();
        SoftReference<T> value = null;

        for(int i = 0; i < objects.length; i++) {
            value = (SoftReference<T>)objects[i];
            if(value != null) {
                objects[i] = value.get();
            }
        }
        return objects;
    }

    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    public T remove(int index) {
        SoftReference<T> value = list.remove(index);
        if(value != null) {
            return value.get();
        }
        return null;
    }

    public boolean remove(Object obj) {
        if (obj == null) {
            return false;
        }
        Iterator<T> e = iterator();
        while (e.hasNext()) {
            if (obj.equals(e.next())) {
                e.remove();
                return true;
            }
        }
        return false;
    }

    public boolean removeAll(Collection<?> collection) {
        if(collection == null || collection.isEmpty()) {
            return false;
        }
        boolean modified = false;

        for(Object obj: collection) {
            remove(obj);
            modified = true;
        }
        return modified;
    }

    public List<T> subList(int fromIndex, int toIndex, boolean notNull) {
        throw new UnsupportedOperationException();
    }

    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> collection) {
        boolean modified = false;
        Iterator<T> e = iterator();
        while (e.hasNext()) {
            if (!collection.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    private static final class ListItr<T> implements ListIterator<T> {
        private final ListIterator<SoftReference<T>> iterator;

        ListItr(ListIterator<SoftReference<T>> iterator) {
            this.iterator = iterator;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public T next() {
            SoftReference<T> value = iterator.next();
            return value == null ? null : value.get();
        }

        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        public T previous() {
            SoftReference<T> value = iterator.previous();
            return value == null ? null : value.get();
        }

        public int nextIndex() {
            return iterator.nextIndex();
        }

        public int previousIndex() {
            return iterator.previousIndex();
        }

        public void remove() {
            iterator.remove();
        }

        public void set(T t) {
            if (t == null) {
                return;
            }
            iterator.set(new SoftReference<T>(t));
        }

        public void add(T t) {
            if (t == null) {
                return;
            }
            iterator.add(new SoftReference<T>(t));
        }
    }
}