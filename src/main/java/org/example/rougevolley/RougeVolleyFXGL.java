package org.example.rougevolley;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.logging.Logger;
import javafx.scene.input.KeyCode;
import org.example.rougevolley.core.*;
import org.example.rougevolley.difficulty.ZoneManager;
import org.example.rougevolley.scene.DungeonScene;
import org.example.rougevolley.scene.MenuScene;

import java.util.concurrent.ThreadLocalRandom;

/**
 * RougeVolley —— FXGL 地牢探索游戏入口
 * 架构组件：
 * - FixedTimestepLoop：固定 60Hz 逻辑更新
 * - SceneManager：栈式场景管理
 * - BSPGenerator：程序化地牢生成
 * - SpawnManager：加权随机敌人物品生成
 * - ZoneManager：动态难度分区
 * - AStarPathfinder：异步 A* 寻路
 */
public class RougeVolleyFXGL extends GameApplication {

    private static final Logger log = Logger.get(RougeVolleyFXGL.class);

    private GameState gameState;
    private GameLoop gameLoop;
    private ZoneManager zoneManager;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("RougeVolley — 地牢探险");
        settings.setVersion("0.1");
        settings.setWidth(GameConfig.VIEWPORT_WIDTH);
        settings.setHeight(GameConfig.VIEWPORT_HEIGHT);
        settings.setMainMenuEnabled(true);
        settings.setPreserveResizeRatio(true);
        settings.setGameMenuEnabled(true);
    }

    @Override
    protected void initInput() {
        FXGL.onKey(KeyCode.W, "Move up", () -> movePlayer(0, -1));
        FXGL.onKey(KeyCode.S, "Move down", () -> movePlayer(0, 1));
        FXGL.onKey(KeyCode.A, "Move left", () -> movePlayer(-1, 0));
        FXGL.onKey(KeyCode.D, "Move right", () -> movePlayer(1, 0));
        FXGL.onKey(KeyCode.SPACE, "Attack", this::playerAttack);
    }

    @Override
    protected void initGame() {
        log.info("RougeVolley initializing...");

        // 使用固定种子或随机种子
        long seed = ThreadLocalRandom.current().nextLong();

        gameState = new GameState(seed);

        // 初始化主循环
        gameLoop = new GameLoop(gameState);
        gameLoop.onInit();

        // 难度区域管理器
        zoneManager = new ZoneManager(gameState);

        // 构建场景栈
        var menuScene = new MenuScene(gameState);
        var dungeonScene = new DungeonScene(gameState);

        gameState.getSceneManager().push(menuScene);
        gameState.getSceneManager().push(dungeonScene);

        log.info("RougeVolley initialized. seed=" + seed);
    }

    @Override
    protected void onUpdate(double tpf) {
        // 主循环驱动（固定时间步长）
        if (gameLoop != null) {
            gameLoop.onUpdate(tpf);
        }

        // 更新难度区域
        if (zoneManager != null) {
            zoneManager.update(tpf);
        }
    }

    @Override
    protected void initUI() {
        // UI 层初始化 —— 与逻辑层完全解耦
    }

    private void movePlayer(int dx, int dy) {
        // 由 PlayerControl 组件处理具体移动
    }

    private void playerAttack() {
        // 触发攻击事件
    }

    public GameState getGameState() {
        return gameState;
    }
}
