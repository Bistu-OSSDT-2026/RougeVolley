package org.example.rougevolley.util;

import org.example.rougevolley.ecs.Entity;
import org.example.rougevolley.ecs.component.Collidable;

/**
 * 碰撞检测工具 —— AABB 碰撞判定
 */
public final class CollisionUtils {

    private CollisionUtils() {}

    /**
     * 检测两个实体是否发生碰撞
     */
    public static boolean checkCollision(Entity a, Entity b) {
        var ca = a.getComponent(Collidable.class);
        var cb = b.getComponent(Collidable.class);
        if (ca.isEmpty() || cb.isEmpty()) return false;

        Collidable colA = ca.get();
        Collidable colB = cb.get();

        double ax = a.getPosition().getX();
        double ay = a.getPosition().getY();
        double bx = b.getPosition().getX();
        double by = b.getPosition().getY();

        return ax < bx + colB.getHitboxWidth()
                && ax + colA.getHitboxWidth() > bx
                && ay < by + colB.getHitboxHeight()
                && ay + colA.getHitboxHeight() > by;
    }

    /**
     * 检测点是否在实体的碰撞盒内
     */
    public static boolean pointInEntity(double px, double py, Entity entity) {
        var comp = entity.getComponent(Collidable.class);
        if (comp.isEmpty()) return false;

        Collidable col = comp.get();
        double ex = entity.getPosition().getX();
        double ey = entity.getPosition().getY();

        return px >= ex && px <= ex + col.getHitboxWidth()
                && py >= ey && py <= ey + col.getHitboxHeight();
    }
}
