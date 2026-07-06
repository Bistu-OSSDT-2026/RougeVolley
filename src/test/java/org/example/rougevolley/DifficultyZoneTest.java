package org.example.rougevolley;

import org.example.rougevolley.difficulty.DifficultyZone;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 难度区域划分单元测试
 */
class DifficultyZoneTest {

    @Test
    void testSafeZone() {
        assertEquals(DifficultyZone.SAFE, DifficultyZone.fromDistance(0));
        assertEquals(DifficultyZone.SAFE, DifficultyZone.fromDistance(10));
        assertEquals(DifficultyZone.SAFE, DifficultyZone.fromDistance(19.9));
    }

    @Test
    void testNormalZone() {
        assertEquals(DifficultyZone.NORMAL, DifficultyZone.fromDistance(20));
        assertEquals(DifficultyZone.NORMAL, DifficultyZone.fromDistance(30));
        assertEquals(DifficultyZone.NORMAL, DifficultyZone.fromDistance(39.9));
    }

    @Test
    void testDangerZone() {
        assertEquals(DifficultyZone.DANGER, DifficultyZone.fromDistance(40));
        assertEquals(DifficultyZone.DANGER, DifficultyZone.fromDistance(50));
        assertEquals(DifficultyZone.DANGER, DifficultyZone.fromDistance(59.9));
    }

    @Test
    void testExtremeZone() {
        assertEquals(DifficultyZone.EXTREME, DifficultyZone.fromDistance(60));
        assertEquals(DifficultyZone.EXTREME, DifficultyZone.fromDistance(100));
        assertEquals(DifficultyZone.EXTREME, DifficultyZone.fromDistance(Double.MAX_VALUE));
    }

    @Test
    void testZoneProperties() {
        assertEquals("安全区", DifficultyZone.SAFE.label());
        assertEquals("极限区", DifficultyZone.EXTREME.label());

        assertTrue(DifficultyZone.EXTREME.difficultyBonus() > DifficultyZone.SAFE.difficultyBonus());
        assertTrue(DifficultyZone.EXTREME.tier() > DifficultyZone.SAFE.tier());
    }
}
