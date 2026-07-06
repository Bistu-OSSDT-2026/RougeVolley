package org.example.rougevolley;

import org.example.rougevolley.dungeon.*;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.*;

/**
 * BSP 地牢生成器单元测试
 * 覆盖：BSP 分割正确性、地图连通性、极端参数、性能统计
 */
class BSPGeneratorTest {

    @Test
    void testDefaultGeneration() {
        BSPGenerator gen = new BSPGenerator(42L);
        DungeonData data = gen.generate(4, 800, 600);

        assertNotNull(data);
        assertNotNull(data.root());
        assertFalse(data.rooms().isEmpty(), "Should generate at least 1 room");
        assertTrue(data.rooms().size() >= 1);
    }

    @Test
    void testSeedDeterminism() {
        BSPGenerator gen1 = new BSPGenerator(12345L);
        DungeonData data1 = gen1.generate(4, 800, 600);

        BSPGenerator gen2 = new BSPGenerator(12345L);
        DungeonData data2 = gen2.generate(4, 800, 600);

        // 相同种子应有相同数量的房间和走廊
        assertEquals(data1.rooms().size(), data2.rooms().size());
        assertEquals(data1.corridors().size(), data2.corridors().size());

        // 房间中心坐标应一致
        for (int i = 0; i < data1.rooms().size(); i++) {
            Room r1 = data1.rooms().get(i);
            Room r2 = data2.rooms().get(i);
            assertEquals(r1.centerX(), r2.centerX(), 1e-6);
            assertEquals(r1.centerY(), r2.centerY(), 1e-6);
        }
    }

    @Test
    void testDifferentSeedsProduceDifferentLayouts() {
        BSPGenerator gen1 = new BSPGenerator(1L);
        DungeonData data1 = gen1.generate(4, 800, 600);

        BSPGenerator gen2 = new BSPGenerator(2L);
        DungeonData data2 = gen2.generate(4, 800, 600);

        // 不同的种子高度不可能产生完全相同的房间数
        // 但可能偶然相同，所以这个测试是启发式的
        String rooms1 = data1.rooms().stream()
                .map(r -> r.centerX() + "," + r.centerY())
                .collect(Collectors.joining("|"));
        String rooms2 = data2.rooms().stream()
                .map(r -> r.centerX() + "," + r.centerY())
                .collect(Collectors.joining("|"));

        assertNotEquals(rooms1, rooms2, "Different seeds should produce different layouts (highly likely)");
    }

    @Test
    void testMinimumDepth() {
        BSPGenerator gen = new BSPGenerator(99L);
        // depth = 1 应仍能生成
        DungeonData data = gen.generate(1, 200, 200);
        assertNotNull(data);
        assertFalse(data.rooms().isEmpty());
    }

    @Test
    void testMaximumDepth() {
        BSPGenerator gen = new BSPGenerator(77L);
        DungeonData data = gen.generate(6, 800, 600);
        assertNotNull(data);
        assertFalse(data.rooms().isEmpty());
    }

    @Test
    void testDepthClamping() {
        BSPGenerator gen = new BSPGenerator(33L);
        // depth=0 会被 clamp 到 1
        DungeonData data = gen.generate(0, 400, 400);
        assertNotNull(data);
        assertFalse(data.rooms().isEmpty());
    }

    @Test
    void testRoomsWithinBounds() {
        BSPGenerator gen = new BSPGenerator(55L);
        DungeonData data = gen.generate(4, 500, 500);

        for (Room room : data.rooms()) {
            Rect b = room.getBounds();
            assertTrue(b.x() >= 0, "Room x should be >= 0");
            assertTrue(b.y() >= 0, "Room y should be >= 0");
            assertTrue(b.x() + b.w() <= 500, "Room right edge should be within map");
            assertTrue(b.y() + b.h() <= 500, "Room bottom edge should be within map");
        }
    }

    @Test
    void testMinimumRoomSize() {
        BSPGenerator gen = new BSPGenerator(66L);
        DungeonData data = gen.generate(4, 800, 600);

        for (Room room : data.rooms()) {
            Rect b = room.getBounds();
            assertTrue(b.w() >= 20, "Room width should be >= 20, got " + b.w());
            assertTrue(b.h() >= 20, "Room height should be >= 20, got " + b.h());
        }
    }

    @Test
    void testCorridorsConnectRooms() {
        BSPGenerator gen = new BSPGenerator(88L);
        DungeonData data = gen.generate(4, 800, 600);

        // 每个走廊连接两个不同房间
        for (Corridor corridor : data.corridors()) {
            assertNotNull(corridor.getRoomA());
            assertNotNull(corridor.getRoomB());
            assertNotSame(corridor.getRoomA(), corridor.getRoomB());
        }
    }

    @Test
    void testLargestRoomExists() {
        BSPGenerator gen = new BSPGenerator(44L);
        DungeonData data = gen.generate(4, 800, 600);

        assertTrue(data.getLargestRoom().isPresent());
        Room largest = data.getLargestRoom().get();
        assertTrue(largest.getBounds().area() > 0);
    }

    @Test
    void testFindRoomAt() {
        BSPGenerator gen = new BSPGenerator(22L);
        DungeonData data = gen.generate(4, 800, 600);

        // 在所有房间内测试
        for (Room room : data.rooms()) {
            double cx = room.centerX();
            double cy = room.centerY();
            var found = data.findRoomAt(cx, cy);
            assertTrue(found.isPresent(), "Should find room at its center");
        }
    }

    @Test
    void testRoomWithoutConflict() {
        BSPGenerator gen = new BSPGenerator(11L);
        DungeonData data = gen.generate(4, 800, 600);

        // 所有房间不相交（检查简单重叠——BSP 天然保证）
        List<Rect> bounds = data.rooms().stream()
                .map(Room::getBounds)
                .toList();

        for (int i = 0; i < bounds.size(); i++) {
            for (int j = i + 1; j < bounds.size(); j++) {
                assertFalse(bounds.get(i).intersects(bounds.get(j)),
                        "Rooms should not overlap");
            }
        }
    }

    @Test
    void testCorridorsHaveWaypoints() {
        BSPGenerator gen = new BSPGenerator(77L);
        DungeonData data = gen.generate(4, 800, 600);

        for (Corridor c : data.corridors()) {
            assertFalse(c.getWaypoints().isEmpty(), "Corridor should have waypoints");
            // L 形走廊应有 3 个航点 (起点 → 拐点 → 终点)
            assertTrue(c.getWaypoints().size() >= 2);
        }
    }

    @Test
    void testMapConnectivityViaAStar() {
        // 验证地图连通性：通过 BFS 确保所有房间可达
        BSPGenerator gen = new BSPGenerator(42L);
        DungeonData data = gen.generate(4, 800, 600);

        if (data.rooms().isEmpty()) return;

        // 构建设邻接表: 通过走廊连接的房间
        Map<Room, List<Room>> adjacency = new HashMap<>();
        for (Room r : data.rooms()) adjacency.put(r, new ArrayList<>());

        for (Corridor c : data.corridors()) {
            adjacency.get(c.getRoomA()).add(c.getRoomB());
            adjacency.get(c.getRoomB()).add(c.getRoomA());
        }

        // 从第一个房间 BFS
        Set<Room> visited = new HashSet<>();
        Queue<Room> queue = new ArrayDeque<>();
        Room start = data.rooms().get(0);
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            Room current = queue.poll();
            for (Room neighbor : adjacency.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // 验证所有房间都可达
        assertEquals(data.rooms().size(), visited.size(),
                "All rooms should be reachable via corridors");
    }

    @RepeatedTest(5)
    void testDungeonConsistencyAcrossRuns() {
        BSPGenerator gen = new BSPGenerator(999L);
        DungeonData data = gen.generate(4, 800, 600);

        assertNotNull(data);
        assertTrue(data.rooms().size() > 0);
        assertTrue(data.width() > 0);
        assertTrue(data.height() > 0);
    }
}
