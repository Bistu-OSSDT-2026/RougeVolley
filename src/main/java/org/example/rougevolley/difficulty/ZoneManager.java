package org.example.rougevolley.difficulty;

import com.almasb.fxgl.logging.Logger;
import org.example.rougevolley.core.GameState;
import org.example.rougevolley.ecs.Entity;

/**
 * 难度区域管理器 —— 根据玩家距起点距离动态调整挑战等级
 */
public class ZoneManager {

    private static final Logger log = Logger.get(ZoneManager.class);

    private final GameState gameState;
    private DifficultyZone currentZone = DifficultyZone.SAFE;

    public ZoneManager(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * 每帧更新：检测玩家所在区域并触发事件
     */
    public void update(double dt) {
        Entity player = gameState.getPlayer();
        if (player == null) return;

        // 计算距起点(0,0)距离
        double dist = player.getPosition().distance(0, 0);

        DifficultyZone newZone = DifficultyZone.fromDistance(dist);
        if (newZone != currentZone) {
            onZoneChanged(currentZone, newZone);
            currentZone = newZone;
        }
    }

    private void onZoneChanged(DifficultyZone oldZone, DifficultyZone newZone) {
        log.info("Zone changed: " + oldZone.label()
                + " -> " + newZone.label()
                + " (tier " + oldZone.tier() + " -> " + newZone.tier() + ")");

        // 通过 EventBus 发布区域变更事件
        // FXGL.getEventBus().fireEvent(new Event(GameEvent.ZONE_CHANGED, newZone));
    }

    /**
     * 根据区域计算敌人属性倍率
     */
    public double getEnemyHealthMultiplier() {
        return 1.0 + currentZone.tier() * 0.5;
    }

    public double getEnemyDamageMultiplier() {
        return 1.0 + currentZone.difficultyBonus();
    }

    public double getLootQualityBonus() {
        return currentZone.tier() * 0.1;
    }

    public DifficultyZone getCurrentZone() {
        return currentZone;
    }
}
