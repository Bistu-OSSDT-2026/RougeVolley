package org.example.rougevolley;

import javafx.geometry.Point2D;
import org.example.rougevolley.ecs.Entity;
import org.example.rougevolley.ecs.EntityType;
import org.example.rougevolley.ecs.component.Collidable;
import org.example.rougevolley.util.CollisionUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 碰撞检测工具单元测试
 */
class CollisionUtilsTest {

    @Test
    void testCollidingEntities() {
        Entity a = new Entity(EntityType.ENEMY_NORMAL, new Point2D(0, 0));
        a.addComponent(new Collidable(32, 32));

        Entity b = new Entity(EntityType.PLAYER, new Point2D(16, 16));
        b.addComponent(new Collidable(32, 32));

        assertTrue(CollisionUtils.checkCollision(a, b));
    }

    @Test
    void testNonCollidingEntities() {
        Entity a = new Entity(EntityType.ENEMY_NORMAL, new Point2D(0, 0));
        a.addComponent(new Collidable(32, 32));

        Entity b = new Entity(EntityType.PLAYER, new Point2D(100, 100));
        b.addComponent(new Collidable(32, 32));

        assertFalse(CollisionUtils.checkCollision(a, b));
    }

    @Test
    void testEntityWithoutCollidable() {
        Entity a = new Entity(EntityType.PLAYER, new Point2D(0, 0));
        // 不添加 Collidable 组件
        Entity b = new Entity(EntityType.PLAYER, new Point2D(0, 0));
        b.addComponent(new Collidable(32, 32));

        assertFalse(CollisionUtils.checkCollision(a, b));
    }

    @Test
    void testPointInEntity() {
        Entity e = new Entity(EntityType.PLAYER, new Point2D(10, 10));
        e.addComponent(new Collidable(32, 32));

        assertTrue(CollisionUtils.pointInEntity(20, 20, e));   // 内部
        assertFalse(CollisionUtils.pointInEntity(5, 5, e));    // 外部
        assertFalse(CollisionUtils.pointInEntity(50, 50, e));  // 外部
    }
}
