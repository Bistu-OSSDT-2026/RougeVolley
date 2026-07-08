package org.example.rougevolley.ecs;

/**
 * 实体类型枚举 —— 用于批量过滤和语义化分类
 * <p>
 * 与组件系统互补：组件定义行为，EntityType 定义语义类别。
 * 例如可以通过 {@code entity.getType() == EntityType.ENEMY_NORMAL}
 * 快速区分普通敌人和精英/Boss，而不需要检查多个组件组合。
 */
public enum EntityType {
    PLAYER,
    ENEMY_NORMAL,
    ENEMY_ELITE,
    ENEMY_BOSS,
    ITEM_COMMON,
    ITEM_RARE,
    ITEM_LEGENDARY,
    BULLET,
    PICKUP
}
