package android.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import dev.rikka.tools.refine.RefineAs;

/** @noinspection NullableProblems*/
@RefineAs(ArrayMap.class)
public class ArrayMapStub<K, V> implements Map<K, V> {
    @Override
    public int size() {
        throw new RuntimeException("STUB");
    }

    @Override
    public boolean isEmpty() {
        throw new RuntimeException("STUB");
    }

    @Override
    public boolean containsKey(Object key) {
        throw new RuntimeException("STUB");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new RuntimeException("STUB");
    }

    @Override
    public V get(Object key) {
        throw new RuntimeException("STUB");
    }

    @Override
    public V put(K key, V value) {
        throw new RuntimeException("STUB");
    }

    @Override
    public V remove(Object key) {
        throw new RuntimeException("STUB");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new RuntimeException("STUB");
    }

    @Override
    public void clear() {
        throw new RuntimeException("STUB");
    }

    @Override
    public Set<K> keySet() {
        throw new RuntimeException("STUB");
    }

    @Override
    public Collection<V> values() {
        throw new RuntimeException("STUB");
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new RuntimeException("STUB");
    }
}
