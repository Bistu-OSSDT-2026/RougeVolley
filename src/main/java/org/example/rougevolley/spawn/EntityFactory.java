package org.example.rougevolley.spawn;

import javafx.geometry.Point2D;
import org.example.rougevolley.ecs.Entity;
import org.example.rougevolley.ecs.EntityType;

/**
 * 实体工厂 —— 统一创建游戏实体
 * 所有实体分配唯一 UUID，按类型自动装配组件
 */
public final class EntityFactory {

    private EntityFactory() {}

    /**
     * 创建实体
     * @param type     实体类型
     * @param position 世界坐标位置（非 null）
     * @return 已装配基础组件的实体
     * @throws IllegalArgumentException 如果 position 为 null
     */
    public static Entity createEntity(EntityType type, Point2D position) {
        if (position == null) {
            throw new IllegalArgumentException("Entity position must not be null");
        }

        Entity entity = new Entity(type, position);

        // 按类型装配组件
        switch (type) {
            case PLAYER -> {
                entity.addComponent(new org.example.rougevolley.ecs.component.PlayerControl());
                entity.addComponent(new org.example.rougevolley.ecs.component.CameraFollow());
                entity.addComponent(new org.example.rougevolley.ecs.component.Collidable(28, 32));
                entity.addComponent(new org.example.rougevolley.ecs.component.Health(100));
            }
            case ENEMY_NORMAL, ENEMY_ELITE, ENEMY_BOSS -> {
                entity.addComponent(new org.example.rougevolley.ecs.component.AIControl());
                entity.addComponent(new org.example.rougevolley.ecs.component.Collidable(32, 32));
                entity.addComponent(new org.example.rougevolley.ecs.component.PhysicsComponent());
            }
            case ITEM_COMMON, ITEM_RARE, ITEM_LEGEND -> {
                entity.addComponent(new org.example.rougevolley.ecs.component.Collidable(16, 16));
            }
            case PROJECTILE -> {
                entity.addComponent(new org.example.rougevolley.ecs.component.PhysicsComponent());
                entity.addComponent(new org.example.rougevolley.ecs.component.Collidable(8, 8));
            }
            case TRAP, DOOR, CHEST -> {
                entity.addComponent(new org.example.rougevolley.ecs.component.Collidable(32, 32));
            }
        }

        return entity;
    }
}
