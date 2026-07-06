package org.example.rougevolley;

import org.example.rougevolley.core.FixedTimestepLoop;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * FixedTimestepLoop 单元测试
 */
class FixedTimestepLoopTest {

    @Test
    void testFixedTimestepAccumulator() {
        int[] callCount = {0};
        FixedTimestepLoop loop = new FixedTimestepLoop(dt -> callCount[0]++);
        loop.start();

        // 一次 tick 积累 2 个步长
        loop.tick(1.0 / 30.0); // ~0.0333s → 2 steps
        assertEquals(2, callCount[0]);
    }

    @Test
    void testNoUpdateWhenNotRunning() {
        int[] callCount = {0};
        FixedTimestepLoop loop = new FixedTimestepLoop(dt -> callCount[0]++);
        // not started
        loop.tick(1.0 / 60.0);
        assertEquals(0, callCount[0]);
    }

    @Test
    void testAccumulatorCapping() {
        int[] callCount = {0};
        FixedTimestepLoop loop = new FixedTimestepLoop(dt -> callCount[0]++, 1.0 / 60.0, 0.1);
        loop.start();

        // 巨大的 delta → 累计上限 0.1 → 最多 6 次
        loop.tick(10.0);
        assertTrue(callCount[0] <= 7, "Should cap at ~6 updates, got " + callCount[0]);
    }

    @Test
    void testAlphaBetweenZeroAndOne() {
        FixedTimestepLoop loop = new FixedTimestepLoop(dt -> {});
        loop.start();

        loop.tick(1.0 / 120.0); // 半个步长
        double alpha = loop.getAlpha();
        assertTrue(alpha >= 0.0 && alpha < 1.0, "Alpha should be in [0,1): " + alpha);
    }

    @Test
    void testStartStop() {
        int[] callCount = {0};
        FixedTimestepLoop loop = new FixedTimestepLoop(dt -> callCount[0]++);
        assertFalse(loop.isRunning());

        loop.start();
        assertTrue(loop.isRunning());

        loop.stop();
        assertFalse(loop.isRunning());

        loop.tick(1.0 / 60.0);
        assertEquals(0, callCount[0]);
    }
}
