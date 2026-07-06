package org.example.rougevolley;

import com.almasb.fxgl.logging.Logger;
import org.example.rougevolley.core.GameLoop;
import org.example.rougevolley.core.GameState;
import org.example.rougevolley.difficulty.ZoneManager;

/**
 * 游戏模块注入器 —— 集中管理模块依赖
 * 遵循面向接口编程，暴露清晰契约
 */
public class GameModule {

    private static final Logger log = Logger.get(GameModule.class);

    private final GameState gameState;
    private final GameLoop gameLoop;
    private final ZoneManager zoneManager;

    public GameModule(long seed) {
        this.gameState = new GameState(seed);
        this.gameLoop = new GameLoop(gameState);
        this.zoneManager = new ZoneManager(gameState);

        log.info("GameModule initialized: seed=" + seed);
    }

    public void onInit() {
        gameLoop.onInit();
    }

    public void onUpdate(double tpf) {
        gameLoop.onUpdate(tpf);
        zoneManager.update(tpf);
    }

    public void onExit() {
        gameLoop.onExit();
    }

    public GameState getGameState() {
        return gameState;
    }

    public GameLoop getGameLoop() {
        return gameLoop;
    }

    public ZoneManager getZoneManager() {
        return zoneManager;
    }
}
