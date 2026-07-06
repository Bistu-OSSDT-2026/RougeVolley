package org.example.rougevolley.util;

import java.util.*;
import java.util.function.Supplier;

/**
 * 通用对象池 —— 减少高频对象的 GC 压力
 * @param <T> 池化对象类型
 */
public class ObjectPool<T> {

    private final Queue<T> pool = new ArrayDeque<>();
    private final Supplier<T> factory;
    private int created = 0;

    public ObjectPool(Supplier<T> factory) {
        this.factory = factory;
    }

    public T obtain() {
        T obj = pool.poll();
        if (obj == null) {
            obj = factory.get();
            created++;
        }
        return obj;
    }

    public void free(T obj) {
        pool.offer(obj);
    }

    public int size() {
        return pool.size();
    }

    public int getCreated() {
        return created;
    }

    public void clear() {
        pool.clear();
    }
}
