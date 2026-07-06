package org.example.rougevolley.util;

import java.util.*;

/**
 * 语义标注系统 —— 为房间/区域绑定语义标签（如"宝库"、"监狱"）
 * 支持装饰模板匹配
 */
public enum SemanticTag {
    TREASURY("宝库"),
    PRISON("监狱"),
    SHRINE("神殿"),
    LIBRARY("图书馆"),
    ARMORY("军械库"),
    KITCHEN("厨房"),
    THRONE("王座厅"),
    EMPTY("空室");

    private final String label;

    SemanticTag(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /** 根据距离和随机数分配语义标签 */
    public static SemanticTag assignTag(double distanceFromStart, java.util.Random rng) {
        if (distanceFromStart < 20) {
            return rng.nextDouble() < 0.3 ? SHRINE : EMPTY;
        } else if (distanceFromStart < 40) {
            double roll = rng.nextDouble();
            if (roll < 0.2) return LIBRARY;
            if (roll < 0.4) return KITCHEN;
            return EMPTY;
        } else if (distanceFromStart < 60) {
            double roll = rng.nextDouble();
            if (roll < 0.15) return TREASURY;
            if (roll < 0.3) return ARMORY;
            if (roll < 0.45) return PRISON;
            return EMPTY;
        } else {
            double roll = rng.nextDouble();
            if (roll < 0.25) return THRONE;
            if (roll < 0.4) return TREASURY;
            return EMPTY;
        }
    }
}
