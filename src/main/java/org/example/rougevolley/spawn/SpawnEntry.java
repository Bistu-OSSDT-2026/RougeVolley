package org.example.rougevolley.spawn;

import org.example.rougevolley.ecs.EntityType;

/**
 * 生成条目 —— 记录类型、权重与位置约束
 */
public record SpawnEntry(
        EntityType type,
        int weight,
        SpawnConstraint constraint
) {

    @FunctionalInterface
    public interface SpawnConstraint {
        /**
         * @param roomCenterX  房间中心 X
         * @param roomCenterY  房间中心 Y
         * @param roomWidth    房间宽度
         * @param roomHeight   房间高度
         * @param rng          随机数生成器
         * @return 具体生成位置 (x, y)，或 null 表示此房间不生成
         */
        java.util.Optional<javafx.geometry.Point2D> resolvePosition(
                double roomCenterX, double roomCenterY,
                double roomWidth, double roomHeight,
                java.util.Random rng);
    }
}
