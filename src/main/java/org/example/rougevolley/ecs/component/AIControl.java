package org.example.rougevolley.ecs.component;

import org.example.rougevolley.ecs.Component;
import org.example.rougevolley.ecs.Entity;

/**
 * AI 控制组件 —— 封装敌人行为逻辑
 */
public class AIControl implements Component {

    public enum AIState {
        IDLE, PATROL, CHASE, ATTACK, FLEE
    }

    private AIState state = AIState.IDLE;
    private double detectionRadius = 200.0;
    private double attackRadius = 40.0;
    private double speed = 80.0;
    private String targetUuid;

    @Override
    public void onUpdate(Entity owner, double dt) {
        // AI 状态机由场景更新驱动
    }

    // ── 状态切换 ──

    public void setState(AIState state) {
        this.state = state;
    }

    public AIState getState() {
        return state;
    }

    public void setTarget(String targetUuid) {
        this.targetUuid = targetUuid;
    }

    public String getTargetUuid() {
        return targetUuid;
    }

    // ── 参数 ──

    public double getDetectionRadius() {
        return detectionRadius;
    }

    public void setDetectionRadius(double radius) {
        this.detectionRadius = radius;
    }

    public double getAttackRadius() {
        return attackRadius;
    }

    public void setAttackRadius(double radius) {
        this.attackRadius = radius;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
