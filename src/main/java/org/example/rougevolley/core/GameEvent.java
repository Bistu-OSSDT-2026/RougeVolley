package org.example.rougevolley.core;

/**
 * 游戏事件类型定义 —— 通过 FXGL EventBus 发布/订阅
 * 所有跨模块通信必须使用此处定义的事件标识
 */
public final class GameEvent {

    private GameEvent() {}

    /* ───────── 通用事件 ───────── */
    public static final String ENGINE_INIT    = "engine.init";
    public static final String ENGINE_STOP    = "engine.stop";

    /* ───────── 场景事件 ───────── */
    public static final String SCENE_CHANGED  = "scene.changed";

    /* ───────── 实体事件 ───────── */
    public static final String ENTITY_SPAWNED = "entity.spawned";
    public static final String ENTITY_DESTROYED = "entity.destroyed";
    public static final String PLAYER_MOVED   = "player.moved";

    /* ───────── 战斗事件 ───────── */
    public static final String DAMAGE_DEALT   = "combat.damage";
    public static final String ENTITY_KILLED  = "combat.kill";

    /* ───────── 地牢事件 ───────── */
    public static final String DUNGEON_GENERATED = "dungeon.generated";
    public static final String ROOM_ENTERED    = "dungeon.room.entered";
    public static final String ZONE_CHANGED    = "dungeon.zone.changed";

    /* ───────── 物品事件 ───────── */
    public static final String ITEM_PICKED_UP  = "item.pickup";
}
