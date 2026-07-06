package org.example.rougevolley.dungeon;

import java.util.*;

/**
 * 地牢生成结果 —— 包含所有房间、走廊和 BSP 树根节点
 */
public record DungeonData(
        BSPNode root,
        List<Room> rooms,
        List<Corridor> corridors,
        int width,
        int height,
        long seed
) {

    /** 获取最大面积房间（通常用于 Boss 或起点） */
    public Optional<Room> getLargestRoom() {
        return rooms.stream()
                .max(Comparator.comparingDouble(r -> r.getBounds().area()));
    }

    /** 根据坐标查找所在房间 */
    public Optional<Room> findRoomAt(double x, double y) {
        return rooms.stream()
                .filter(r -> r.getBounds().contains(x, y))
                .findFirst();
    }
}
