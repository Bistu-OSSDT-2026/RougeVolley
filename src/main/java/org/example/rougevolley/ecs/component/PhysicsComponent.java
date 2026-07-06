package org.example.rougevolley.ecs.component;

import org.example.rougevolley.ecs.Component;
import org.example.rougevolley.ecs.Entity;

/**
 * 物理组件 —— 速度/加速度/摩擦力模拟
 */
public class PhysicsComponent implements Component {

    private double velocityX = 0.0;
    private double velocityY = 0.0;
    private double friction = 0.0;

    @Override
    public void onUpdate(Entity owner, double dt) {
        double px = owner.getPosition().getX() + velocityX * dt;
        double py = owner.getPosition().getY() + velocityY * dt;
        owner.setPosition(new javafx.geometry.Point2D(px, py));

        // 摩擦力减速
        if (friction > 0) {
            velocityX *= (1.0 - friction);
            velocityY *= (1.0 - friction);
            if (Math.abs(velocityX) < 0.1) velocityX = 0;
            if (Math.abs(velocityY) < 0.1) velocityY = 0;
        }
    }

    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getSpeed() {
        return Math.hypot(velocityX, velocityY);
    }

    public void setFriction(double friction) {
        this.friction = friction;
    }
}
