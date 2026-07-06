package org.example.rougevolley.scene;

import com.almasb.fxgl.logging.Logger;
import org.example.rougevolley.core.GameConfig;
import org.example.rougevolley.core.GameState;
import org.example.rougevolley.dungeon.BSPGenerator;
import org.example.rougevolley.dungeon.DungeonData;
import org.example.rougevolley.spawn.SpawnManager;

/**
 * 主游戏场景 —— 地牢探索核心玩法
 */
public class DungeonScene extends GameScene {

    private static final Logger log = Logger.get(DungeonScene.class);

    private final GameState gameState;
    private final BSPGenerator dungeonGenerator;
    private final SpawnManager spawnManager;
    private DungeonData dungeon;

    public DungeonScene(GameState gameState) {
        this.gameState = gameState;
        this.dungeonGenerator = new BSPGenerator(gameState.getSeed());
        this.spawnManager = new SpawnManager(gameState);
    }

    @Override
    public void onInit() {
        log.info("DungeonScene onInit");
    }

    @Override
    public void onEnter() {
        log.info("DungeonScene onEnter");
        // 生成或加载地牢
        if (dungeon == null) {
            generateDungeon();
        }
    }

    @Override
    public void onUpdate(double dt) {
        if (dungeon == null) return;

        // 更新所有活动实体
        for (var entity : gameState.getAllEntities()) {
            if (entity.isActive()) {
                entity.onUpdate(dt);
            }
        }
    }

    @Override
    public void onExit() {
        log.info("DungeonScene onExit");
        // 保存进度、释放临时资源
    }

    private void generateDungeon() {
        dungeon = dungeonGenerator.generate(
                GameConfig.BSP_DEFAULT_DEPTH,
                800, 600
        );
        gameState.setCurrentDungeon(dungeon);

        // 在地牢中生成敌人和物品
        spawnManager.populateDungeon(dungeon);

        log.info("Dungeon generated: " + dungeon.rooms().size()
                + " rooms, " + dungeon.corridors().size() + " corridors");
    }

    public DungeonData getDungeon() {
        return dungeon;
    }
}
