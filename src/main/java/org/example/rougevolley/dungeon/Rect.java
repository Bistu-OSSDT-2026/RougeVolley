package org.example.rougevolley.dungeon;

/**
 * 矩形数据结构 —— 表示空间区域或房间边界
 */
public record Rect(double x, double y, double w, double h) {

    public double centerX() {
        return x + w / 2.0;
    }

    public double centerY() {
        return y + h / 2.0;
    }

    public double area() {
        return w * h;
    }

    public boolean contains(double px, double py) {
        return px >= x && px < x + w && py >= y && py < y + h;
    }

    public boolean intersects(Rect other) {
        return x < other.x + other.w && x + w > other.x
                && y < other.y + other.h && y + h > other.y;
    }

    public Rect inset(double amount) {
        return new Rect(x + amount, y + amount, w - 2 * amount, h - 2 * amount);
    }
}
