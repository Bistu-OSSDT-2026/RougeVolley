package org.example.rougevolley.combat;

import com.almasb.fxgl.logging.Logger;
import javafx.event.Event;
import org.example.rougevolley.config.GameConfig;
import org.example.rougevolley.core.GameEvent;
import org.example.rougevolley.core.GameState;
import org.example.rougevolley.ecs.Entity;
import org.example.rougevolley.ecs.components.EnemyComponent;
import org.example.rougevolley.ecs.components.HealthComponent;
import org.example.rougevolley.ecs.components.PlayerComponent;

/**
 * 伤害系统 —— 子弹与敌人的碰撞检测、伤害结算、击杀处理
 * <p>
 * 每帧由游戏主循环调用，遍历所有子弹-敌人对进行碰撞判定。
 * 碰撞后子弹销毁、敌人扣血；敌人血量归零时自动标记非活跃。
 */
public final class DamageSystem {

    private static final Logger log = Logger.get(DamageSystem.class);

    private DamageSystem() {}

    /**
     * 检测所有子弹与敌人之间的碰撞，执行伤害结算
     *
     * @param gameState 全局游戏状态（提供实体列表）
     */
    public static void checkBulletEnemyCollisions(GameState gameState) {
        for (Entity bullet : gameState.getEntities()) {
            // 子弹判断：有 MovementComponent、不是敌人、不是玩家（排除自爆）
            if (!bullet.isActive()) continue;
            if (bullet.hasComponent(EnemyComponent.class)) continue;
            if (bullet.hasComponent(PlayerComponent.class)) continue;

            // 取出子弹伤害（由 EntityFactory.createBullet 写入 userData）
            Object userData = bullet.getUserData();
            if (!(userData instanceof Double damage)) continue;

            double bulletX = bullet.getX();
            double bulletY = bullet.getY();
            double bulletRadius = GameConfig.BULLET_SIZE / 2.0;

            for (Entity enemy : gameState.getEntities()) {
                if (enemy == bullet || !enemy.isActive()) continue;
                if (!enemy.hasComponent(EnemyComponent.class)) continue;

                // 敌人碰撞尺寸
                EnemyComponent ec = enemy.getComponent(EnemyComponent.class).get();
                double enemySize = ec.getSize();

                // 圆形（子弹）vs AABB（敌人矩形，左上角 = position）
                if (circleIntersectsRect(bulletX, bulletY, bulletRadius,
                        enemy.getX(), enemy.getY(), enemySize, enemySize)) {

                    // ── 伤害结算 ──
                    HealthComponent health = enemy.getComponent(HealthComponent.class).orElse(null);
                    if (health != null) {
                        health.takeDamage(damage);
                    }

                    // 子弹命中后销毁
                    bullet.setActive(false);

                    // 触发伤害事件
                    com.almasb.fxgl.dsl.FXGL.getEventBus().fireEvent(new Event(GameEvent.DAMAGE_DEALT_EVENT));

                    // 敌人死亡
                    if (health != null && health.isDead()) {
                        log.info("Enemy destroyed");
                        com.almasb.fxgl.dsl.FXGL.getEventBus().fireEvent(new Event(GameEvent.ENTITY_KILLED_EVENT));
                    }

                    // 子弹已销毁，跳出内层循环
                    break;
                }
            }
        }
    }

    /**
     * 圆形与轴对齐矩形碰撞检测
     *
     * @param cx    圆心 X
     * @param cy    圆心 Y
     * @param r     圆半径
     * @param rx    矩形左上角 X
     * @param ry    矩形左上角 Y
     * @param rw    矩形宽度
     * @param rh    矩形高度
     * @return 是否相交
     */
    private static boolean circleIntersectsRect(double cx, double cy, double r,
                                                double rx, double ry, double rw, double rh) {
        // 矩形上离圆心最近的点
        double nearestX = Math.max(rx, Math.min(cx, rx + rw));
        double nearestY = Math.max(ry, Math.min(cy, ry + rh));

        double dx = cx - nearestX;
        double dy = cy - nearestY;

        return (dx * dx + dy * dy) <= (r * r);
    }
}
