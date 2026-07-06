package org.example.rougevolley.ecs.component;

import org.example.rougevolley.ecs.Component;
import org.example.rougevolley.ecs.Entity;

/**
 * 碰撞组件 —— 标识实体参与碰撞检测
 */
public class Collidable implements Component {

    private double hitboxWidth = 32.0;
    private double hitboxHeight = 32.0;
    private boolean solid = true;

    public Collidable() {}

    public Collidable(double hitboxWidth, double hitboxHeight) {
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
    }

    @Override
    public void onUpdate(Entity owner, double dt) {
        // 碰撞检测由场景统一处理
    }

    public double getHitboxWidth() {
        return hitboxWidth;
    }

    public double getHitboxHeight() {
        return hitboxHeight;
    }

    public boolean isSolid() {
        return solid;
    }

    public void setSolid(boolean solid) {
        this.solid = solid;
    }
}
