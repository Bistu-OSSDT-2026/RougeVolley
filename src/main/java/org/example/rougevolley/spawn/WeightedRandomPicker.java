package org.example.rougevolley.spawn;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加权随机选择器 —— 根据权重选择条目
 */
public class WeightedRandomPicker<T> {

    private final List<WeightedEntry<T>> entries = new ArrayList<>();
    private int totalWeight = 0;

    public void add(T item, int weight) {
        if (weight <= 0) throw new IllegalArgumentException("Weight must be positive: " + weight);
        entries.add(new WeightedEntry<>(item, weight));
        totalWeight += weight;
    }

    /**
     * 根据权重随机选择一个条目
     * @throws IllegalStateException 如果没有条目
     */
    public T pick(Random rng) {
        if (entries.isEmpty()) {
            throw new IllegalStateException("No entries in picker");
        }
        int roll = rng.nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedEntry<T> entry : entries) {
            cumulative += entry.weight;
            if (roll < cumulative) {
                return entry.item;
            }
        }
        // 兜底：返回最后一个
        return entries.getLast().item;
    }

    public T pick() {
        return pick(ThreadLocalRandom.current());
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public int size() {
        return entries.size();
    }

    private record WeightedEntry<T>(T item, int weight) {}
}
