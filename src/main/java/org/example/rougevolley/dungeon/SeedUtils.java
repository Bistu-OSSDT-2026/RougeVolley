package org.example.rougevolley.dungeon;

import java.util.Random;

/**
 * 种子工具 —— 保证相同种子产生一致的地牢输出
 */
public final class SeedUtils {

    private SeedUtils() {}

    /**
     * 根据种子创建确定性随机数生成器
     */
    public static Random createRNG(long seed) {
        return new Random(seed);
    }

    /**
     * 从任意字符串生成稳定种子
     */
    public static long stringToSeed(String s) {
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L * hash + c;
        }
        return hash;
    }
}
