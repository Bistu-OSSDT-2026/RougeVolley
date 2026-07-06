package org.example.rougevolley.dungeon;

import java.util.*;

/**
 * 走廊 —— 连接两个房间的 L 形路径
 */
public class Corridor {

    private final Room roomA;
    private final Room roomB;
    private final List<javafx.geometry.Point2D> waypoints = new ArrayList<>();

    public Corridor(Room roomA, Room roomB) {
        this.roomA = roomA;
        this.roomB = roomB;
    }

    public void addWaypoint(double x, double y) {
        waypoints.add(new javafx.geometry.Point2D(x, y));
    }

    public void addWaypoints(List<javafx.geometry.Point2D> points) {
        waypoints.addAll(points);
    }

    public Room getRoomA() {
        return roomA;
    }

    public Room getRoomB() {
        return roomB;
    }

    public List<javafx.geometry.Point2D> getWaypoints() {
        return Collections.unmodifiableList(waypoints);
    }
}
