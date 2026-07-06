package org.example.rougevolley;

import org.example.rougevolley.dungeon.Rect;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Rect 数据结构单元测试
 */
class RectTest {

    @Test
    void testCenter() {
        Rect r = new Rect(10, 20, 100, 200);
        assertEquals(60, r.centerX());
        assertEquals(120, r.centerY());
    }

    @Test
    void testContains() {
        Rect r = new Rect(0, 0, 100, 100);
        assertTrue(r.contains(50, 50));
        assertTrue(r.contains(0, 0));
        assertFalse(r.contains(-1, 50));
        assertFalse(r.contains(50, 101));
    }

    @Test
    void testIntersects() {
        Rect a = new Rect(0, 0, 50, 50);
        Rect b = new Rect(25, 25, 50, 50);
        Rect c = new Rect(100, 100, 50, 50);

        assertTrue(a.intersects(b));
        assertFalse(a.intersects(c));
    }

    @Test
    void testInset() {
        Rect r = new Rect(10, 10, 100, 100);
        Rect inset = r.inset(5);
        assertEquals(15, inset.x());
        assertEquals(15, inset.y());
        assertEquals(90, inset.w());
        assertEquals(90, inset.h());
    }

    @Test
    void testArea() {
        Rect r = new Rect(0, 0, 100, 200);
        assertEquals(20000, r.area());
    }
}
