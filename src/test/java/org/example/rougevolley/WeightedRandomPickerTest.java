package org.example.rougevolley;

import org.example.rougevolley.spawn.WeightedRandomPicker;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * 加权随机选择器单元测试
 */
class WeightedRandomPickerTest {

    @Test
    void testPickReturnsValidItem() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>();
        picker.add("A", 10);
        picker.add("B", 20);

        String result = picker.pick(new Random(42));
        assertNotNull(result);
        assertTrue(result.equals("A") || result.equals("B"));
    }

    @Test
    void testWeightDistribution() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>();
        picker.add("heavy", 90);
        picker.add("light", 10);

        Random rng = new Random(123);
        int heavyCount = 0;
        int total = 10000;

        for (int i = 0; i < total; i++) {
            if ("heavy".equals(picker.pick(rng))) heavyCount++;
        }

        // "heavy" 应在 ~9000 左右（90% ± 5%）
        assertTrue(heavyCount > 8500 && heavyCount < 9500,
                "heavy count should be ~9000, got " + heavyCount);
    }

    @Test
    void testSingleEntry() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>();
        picker.add("only", 1);
        assertEquals("only", picker.pick());
    }

    @Test
    void testEmptyPickerThrows() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>();
        assertThrows(IllegalStateException.class, picker::pick);
    }

    @Test
    void testZeroWeightThrows() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>();
        assertThrows(IllegalArgumentException.class, () -> picker.add("zero", 0));
    }
}
