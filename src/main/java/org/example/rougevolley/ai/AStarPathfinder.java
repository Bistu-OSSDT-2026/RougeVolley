package org.example.rougevolley.ai;

import javafx.geometry.Point2D;

import java.util.*;
import java.util.concurrent.*;

/**
 * A* 寻路算法 —— 支持后台异步执行
 * 用于敌人追踪玩家、Boss 导航
 */
public class AStarPathfinder {

    private final boolean[][] walkable; // true = 可行走
    private final int width, height;

    public AStarPathfinder(boolean[][] walkable, int width, int height) {
        this.walkable = walkable;
        this.width = width;
        this.height = height;
    }

    /**
     * 同步寻路
     * @return 从起点到终点的路径点列表（含起点和终点），不可达时返回空列表
     */
    public List<Point2D> findPath(double startX, double startY, double endX, double endY) {
        int sx = (int) startX, sy = (int) startY;
        int ex = (int) endX, ey = (int) endY;

        if (!inBounds(sx, sy) || !inBounds(ex, ey)) return List.of();
        if (!walkable[sy][sx] || !walkable[ey][ex]) return List.of();

        // 优先级队列 (fScore)
        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.fScore));
        Set<Long> closed = new HashSet<>();
        Map<Long, Node> nodeMap = new HashMap<>();

        Node start = newNode(sx, sy, null, 0, heuristic(sx, sy, ex, ey));
        open.offer(start);
        nodeMap.put(key(sx, sy), start);

        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0},{1,1},{1,-1},{-1,1},{-1,-1}};

        while (!open.isEmpty()) {
            Node current = open.poll();
            long ck = key(current.x, current.y);

            if (current.x == ex && current.y == ey) {
                return reconstructPath(current);
            }

            if (closed.contains(ck)) continue;
            closed.add(ck);

            for (int[] d : dirs) {
                int nx = current.x + d[0];
                int ny = current.y + d[1];
                if (!inBounds(nx, ny) || !walkable[ny][nx]) continue;

                long nk = key(nx, ny);
                if (closed.contains(nk)) continue;

                double moveCost = (d[0] != 0 && d[1] != 0) ? 1.414 : 1.0;
                double tentativeG = current.gScore + moveCost;

                Node neighbor = nodeMap.get(nk);
                if (neighbor == null) {
                    neighbor = newNode(nx, ny, current, tentativeG, heuristic(nx, ny, ex, ey));
                    nodeMap.put(nk, neighbor);
                    open.offer(neighbor);
                } else if (tentativeG < neighbor.gScore) {
                    open.remove(neighbor);
                    neighbor.parent = current;
                    neighbor.gScore = tentativeG;
                    neighbor.fScore = tentativeG + neighbor.hScore;
                    open.offer(neighbor);
                }
            }
        }

        return List.of(); // 不可达
    }

    /**
     * 异步寻路 —— 在后台线程执行并返回 Future
     */
    public CompletableFuture<List<Point2D>> findPathAsync(double startX, double startY, double endX, double endY) {
        return CompletableFuture.supplyAsync(() -> findPath(startX, startY, endX, endY));
    }

    // ── 内部方法 ──

    private double heuristic(int x, int y, int ex, int ey) {
        // 八方向切比雪夫距离
        double dx = Math.abs(x - ex);
        double dy = Math.abs(y - ey);
        return Math.max(dx, dy) + (Math.sqrt(2) - 1) * Math.min(dx, dy);
    }

    private List<Point2D> reconstructPath(Node node) {
        LinkedList<Point2D> path = new LinkedList<>();
        while (node != null) {
            path.addFirst(new Point2D(node.x, node.y));
            node = node.parent;
        }
        return path;
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private long key(int x, int y) {
        return ((long) x << 32) | (y & 0xFFFFFFFFL);
    }

    private Node newNode(int x, int y, Node parent, double g, double h) {
        Node n = new Node();
        n.x = x;
        n.y = y;
        n.parent = parent;
        n.gScore = g;
        n.hScore = h;
        n.fScore = g + h;
        return n;
    }

    private static class Node {
        int x, y;
        Node parent;
        double gScore, hScore, fScore;
    }
}
