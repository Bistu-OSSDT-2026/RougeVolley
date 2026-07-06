package org.example.rougevolley.ecs.component;

import org.example.rougevolley.ecs.Component;
import org.example.rougevolley.ecs.Entity;

/**
 * 血量组件 —— 管理实体生命值
 */
public class Health implements Component {

    private double maxHealth;
    private double currentHealth;

    public Health(double maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }

    @Override
    public void onUpdate(Entity owner, double dt) {
        // 状态效果（中毒、回血等）在此处理
    }

    /**
     * 造成伤害
     * @return true 如果实体死亡
     */
    public boolean takeDamage(double amount) {
        currentHealth = Math.max(0, currentHealth - amount);
        return currentHealth <= 0;
    }

    public void heal(double amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public boolean isAlive() {
        return currentHealth > 0;
    }

    public double getHealthPercent() {
        return currentHealth / maxHealth;
    }
}
