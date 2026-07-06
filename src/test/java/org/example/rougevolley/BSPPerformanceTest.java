package org.example.rougevolley;

import org.example.rougevolley.dungeon.BSPGenerator;
import org.example.rougevolley.dungeon.DungeonData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BSP 生成性能测试 —— 1000 次生成平均耗时统计
 */
class BSPPerformanceTest {

    private static final int ITERATIONS = 1000;
    private static final long SEED = 123456L;

    @Test
    void testAverageGenerationTime() {
        BSPGenerator gen = new BSPGenerator(SEED);

        long start = System.nanoTime();

        DungeonData lastData = null;
        for (int i = 0; i < ITERATIONS; i++) {
            lastData = gen.generate(4, 800, 600);
            assertNotNull(lastData);
            assertFalse(lastData.rooms().isEmpty());
        }

        long elapsed = System.nanoTime() - start;
        double avgMs = elapsed / 1_000_000.0 / ITERATIONS;

        System.out.printf("BSPGenerator: %d generations, avg %.3f ms%n",
                ITERATIONS, avgMs);

        // 每次生成应在 50ms 以内（远低于此值）
        assertTrue(avgMs < 50.0,
                "Average generation time should be < 50ms, got " + avgMs + "ms");
    }

    @Test
    void testGenerationWithExtremeParameters() {
        BSPGenerator gen = new BSPGenerator(7777L);

        // 最小地图
        DungeonData small = gen.generate(1, 100, 100);
        assertNotNull(small);

        // 最大深度 + 大地图
        DungeonData large = gen.generate(6, 1600, 1200);
        assertNotNull(large);
        assertFalse(large.rooms().isEmpty());

        // 窄长地图
        DungeonData narrow = gen.generate(4, 2000, 200);
        assertNotNull(narrow);

        // 极小地图，depth=1
        DungeonData tiny = gen.generate(1, 50, 50);
        assertNotNull(tiny);
    }
}
