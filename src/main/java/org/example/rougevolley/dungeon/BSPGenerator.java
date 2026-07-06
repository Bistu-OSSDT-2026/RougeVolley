package org.example.rougevolley.dungeon;

import com.almasb.fxgl.logging.Logger;

import java.util.*;

/**
 * BSP 地牢生成器
 * 递归分割空间 → 叶节点生成房间 → L 形走廊连接 → 后处理优化
 * 支持种子控制，保证同种子产生一致输出
 */
public class BSPGenerator {

    private static final Logger log = Logger.get(BSPGenerator.class);

    private final Random rng;
    private final BSPNodePool nodePool = new BSPNodePool();

    public BSPGenerator(long seed) {
        this.rng = new Random(seed);
    }

    /**
     * 生成地牢
     * @param depth 递归深度 (1-6)
     * @param mapWidth  地图宽度
     * @param mapHeight 地图高度
     */
    public DungeonData generate(int depth, int mapWidth, int mapHeight) {
        depth = clamp(depth, 1, 6);
        double minSize = 40.0;

        Rect rootRect = new Rect(0, 0, mapWidth, mapHeight);
        BSPNode root = split(rootRect, depth, minSize);

        List<Room> rooms = new ArrayList<>();
        collectRooms(root, rooms);

        List<Corridor> corridors = new ArrayList<>();
        connectRooms(root, corridors);

        // 后处理：清理过短/冗余走廊
        postProcess(corridors);

        log.info("BSP generated: depth=" + depth
                + ", rooms=" + rooms.size()
                + ", corridors=" + corridors.size()
                + ", pool.created=" + nodePool.getTotalCreated());

        return new DungeonData(root, rooms, corridors, mapWidth, mapHeight, rng.nextLong());
    }

    /**
     * 递归分割
     */
    private BSPNode split(Rect rect, int depth, double minSize) {
        if (depth <= 0 || shouldStop(rect, minSize)) {
            BSPNode leaf = nodePool.obtain(rect);
            Rect roomBounds = generateRoomBounds(rect);
            leaf.setRoom(new Room(roomBounds));
            return leaf;
        }

        // 优先切割长边
        boolean splitHorizontal = rect.w() < rect.h();
        double splitPos = getSplitPosition(rect, splitHorizontal, minSize);

        Rect rectLeft, rectRight;
        if (splitHorizontal) {
            rectLeft  = new Rect(rect.x(), rect.y(), rect.w(), splitPos);
            rectRight = new Rect(rect.x(), rect.y() + splitPos, rect.w(), rect.h() - splitPos);
        } else {
            rectLeft  = new Rect(rect.x(), rect.y(), splitPos, rect.h());
            rectRight = new Rect(rect.x() + splitPos, rect.y(), rect.w() - splitPos, rect.h());
        }

        BSPNode node = nodePool.obtain(rect);
        node.setLeft(split(rectLeft,  depth - 1, minSize));
        node.setRight(split(rectRight, depth - 1, minSize));
        return node;
    }

    private boolean shouldStop(Rect rect, double minSize) {
        return rect.w() < minSize * 2 || rect.h() < minSize * 2;
    }

    /**
     * 确定分割位置（在 40%-60% 之间随机）
     */
    private double getSplitPosition(Rect rect, boolean horizontal, double minSize) {
        double maxDim = horizontal ? rect.h() : rect.w();
        double minPos = minSize;
        double maxPos = maxDim - minSize;
        if (maxPos <= minPos) return maxDim / 2.0;

        double range = maxPos - minPos;
        return minPos + rng.nextDouble() * range * 0.4 + range * 0.1;
    }

    /**
     * 在叶节点内生成房间（尺寸为节点的 60%-90%）
     */
    private Rect generateRoomBounds(Rect nodeRect) {
        double ratio = 0.6 + rng.nextDouble() * 0.3; // [0.6, 0.9]
        double rw = nodeRect.w() * ratio;
        double rh = nodeRect.h() * ratio;
        double rx = nodeRect.x() + (nodeRect.w() - rw) * rng.nextDouble();
        double ry = nodeRect.y() + (nodeRect.h() - rh) * rng.nextDouble();
        return new Rect(rx, ry, rw, rh);
    }

    /**
     * 递归收集所有叶节点的房间
     */
    private void collectRooms(BSPNode node, List<Room> rooms) {
        if (node == null) return;
        if (node.isLeaf()) {
            if (node.getRoom() != null) {
                rooms.add(node.getRoom());
            }
            return;
        }
        collectRooms(node.getLeft(), rooms);
        collectRooms(node.getRight(), rooms);
    }

    /**
     * 连接兄弟节点房间（L 形走廊）
     */
    private void connectRooms(BSPNode node, List<Corridor> corridors) {
        if (node == null || node.isLeaf()) return;

        connectRooms(node.getLeft(), corridors);
        connectRooms(node.getRight(), corridors);

        // 获取左右子树中的所有房间
        List<Room> leftRooms  = new ArrayList<>();
        List<Room> rightRooms = new ArrayList<>();
        collectRooms(node.getLeft(),  leftRooms);
        collectRooms(node.getRight(), rightRooms);

        if (leftRooms.isEmpty() || rightRooms.isEmpty()) return;

        // 连接最近的两房间
        Room bestA = null, bestB = null;
        double bestDist = Double.MAX_VALUE;
        for (Room a : leftRooms) {
            for (Room b : rightRooms) {
                double dist = dist(a.centerX(), a.centerY(), b.centerX(), b.centerY());
                if (dist < bestDist) {
                    bestDist = dist;
                    bestA = a;
                    bestB = b;
                }
            }
        }

        if (bestA != null && bestB != null) {
            Corridor corridor = createLShapeCorridor(bestA, bestB);
            corridors.add(corridor);
            bestA.addCorridor(corridor);
            bestB.addCorridor(corridor);
        }
    }

    /**
     * 创建 L 形走廊路径
     */
    private Corridor createLShapeCorridor(Room a, Room b) {
        Corridor corridor = new Corridor(a, b);

        double ax = a.centerX(), ay = a.centerY();
        double bx = b.centerX(), by = b.centerY();

        // 随机选择先水平再垂直 或 先垂直再水平
        if (rng.nextBoolean()) {
            corridor.addWaypoint(ax, ay);
            corridor.addWaypoint(bx, ay);
            corridor.addWaypoint(bx, by);
        } else {
            corridor.addWaypoint(ax, ay);
            corridor.addWaypoint(ax, by);
            corridor.addWaypoint(bx, by);
        }

        return corridor;
    }

    /**
     * 后处理：清理冗余走廊
     */
    private void postProcess(List<Corridor> corridors) {
        corridors.removeIf(c -> c.getWaypoints().size() < 2);
    }

    private double dist(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
