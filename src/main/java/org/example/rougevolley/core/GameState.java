package org.example.rougevolley.core;

import org.example.rougevolley.dungeon.DungeonData;
import org.example.rougevolley.ecs.Entity;
import org.example.rougevolley.scene.SceneManager;

import java.util.*;

/**
 * 全局游戏状态
 * 管理玩家、地牢、场景等核心运行时数据
 */
public class GameState {

    private final SceneManager sceneManager = new SceneManager();
    private final Map<String, Entity> entityRegistry = new HashMap<>();

    private Entity player;
    private DungeonData currentDungeon;
    private long seed;
    private int depth = 1;

    public GameState() {
        this.seed = System.nanoTime();
    }

    public GameState(long seed) {
        this.seed = seed;
    }

    // ── 实体注册 ──

    public void registerEntity(Entity entity) {
        entityRegistry.put(entity.getUuid(), entity);
    }

    public void unregisterEntity(Entity entity) {
        entityRegistry.remove(entity.getUuid());
    }

    public Optional<Entity> getEntity(String uuid) {
        return Optional.ofNullable(entityRegistry.get(uuid));
    }

    public Collection<Entity> getAllEntities() {
        return Collections.unmodifiableCollection(entityRegistry.values());
    }

    // ── Getters / Setters ──

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public Entity getPlayer() {
        return player;
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public DungeonData getCurrentDungeon() {
        return currentDungeon;
    }

    public void setCurrentDungeon(DungeonData dungeon) {
        this.currentDungeon = dungeon;
    }

    public long getSeed() {
        return seed;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = Math.max(1, depth);
    }
}
