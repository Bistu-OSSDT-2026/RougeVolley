package org.example.rougevolley;

import javafx.geometry.Point2D;
import org.example.rougevolley.ai.AStarPathfinder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * A* 寻路算法单元测试
 */
class AStarPathfinderTest {

    private boolean[][] createEmptyMap(int w, int h) {
        boolean[][] map = new boolean[h][w];
        for (int y = 0; y < h; y++)
            for (int x = 0; x < w; x++)
                map[y][x] = true;
        return map;
    }

    @Test
    void testStraightLinePath() {
        boolean[][] map = createEmptyMap(10, 10);
        AStarPathfinder pf = new AStarPathfinder(map, 10, 10);

        List<Point2D> path = pf.findPath(0, 0, 5, 0);
        assertFalse(path.isEmpty());
        assertEquals(0, path.getFirst().getX());
        assertEquals(0, path.getFirst().getY());
        assertTrue(path.getLast().getX() >= 4.5);
    }

    @Test
    void testUnreachableDestination() {
        boolean[][] map = createEmptyMap(10, 10);
        map[5][5] = false; // 阻塞目标点
        AStarPathfinder pf = new AStarPathfinder(map, 10, 10);

        List<Point2D> path = pf.findPath(0, 0, 5, 5);
        assertTrue(path.isEmpty(), "Path should be empty when destination is blocked");
    }

    @Test
    void testPathAroundObstacle() {
        boolean[][] map = createEmptyMap(10, 10);
        // 障碍物墙
        for (int x = 3; x <= 6; x++) map[3][x] = false;
        AStarPathfinder pf = new AStarPathfinder(map, 10, 10);

        List<Point2D> path = pf.findPath(0, 2, 9, 2);
        assertFalse(path.isEmpty(), "Path should go around obstacle");
    }

    @Test
    void testOutOfBoundsStart() {
        boolean[][] map = createEmptyMap(10, 10);
        AStarPathfinder pf = new AStarPathfinder(map, 10, 10);
        assertTrue(pf.findPath(-1, 0, 5, 5).isEmpty());
    }

    @Test
    void testSameStartAndEnd() {
        boolean[][] map = createEmptyMap(10, 10);
        AStarPathfinder pf = new AStarPathfinder(map, 10, 10);
        List<Point2D> path = pf.findPath(5, 5, 5, 5);
        assertEquals(1, path.size(), "Path from point to itself should have one element");
        assertEquals(5.0, path.getFirst().getX(), 0.01);
        assertEquals(5.0, path.getFirst().getY(), 0.01);
    }

    @Test
    void testAsyncPathfinding() {
        boolean[][] map = createEmptyMap(10, 10);
        AStarPathfinder pf = new AStarPathfinder(map, 10, 10);

        var future = pf.findPathAsync(0, 0, 9, 9);
        List<Point2D> path = future.join(); // 同步等待
        assertFalse(path.isEmpty());
    }
}
