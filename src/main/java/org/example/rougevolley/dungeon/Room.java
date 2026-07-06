package org.example.rougevolley.dungeon;

import java.util.*;

/**
 * 房间 —— 存储于 BSP 叶节点
 */
public class Room {

    private final Rect bounds;
    private final List<Corridor> connectedCorridors = new ArrayList<>();

    public Room(Rect bounds) {
        this.bounds = bounds;
    }

    public Rect getBounds() {
        return bounds;
    }

    public double centerX() {
        return bounds.centerX();
    }

    public double centerY() {
        return bounds.centerY();
    }

    public void addCorridor(Corridor corridor) {
        connectedCorridors.add(corridor);
    }

    public List<Corridor> getConnectedCorridors() {
        return Collections.unmodifiableList(connectedCorridors);
    }
}
