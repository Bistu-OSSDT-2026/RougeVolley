package org.example.rougevolley.core;

/**
 * 全局游戏配置常量
 * 所有可调参数集中管理，支持外部覆写
 */
public final class GameConfig {

    private GameConfig() {}

    // ── 主循环 ──
    /** 固定时间步长 (秒)，对应 60Hz */
    public static final double FIXED_TIMESTEP = 1.0 / 60.0;
    /** 最大累加器上限（防止螺旋式死循环） */
    public static final double MAX_ACCUMULATOR = 0.25;

    // ── 场景栈 ──
    /** 场景栈最大深度 */
    public static final int SCENE_STACK_MAX_DEPTH = 5;

    // ── BSP 地牢 ──
    /** 递归默认深度 */
    public static final int BSP_DEFAULT_DEPTH = 4;
    /** 最小递归深度 */
    public static final int BSP_MIN_DEPTH = 1;
    /** 最大递归深度 */
    public static final int BSP_MAX_DEPTH = 6;
    /** 最小房间尺寸（像素） */
    public static final double BSP_MIN_ROOM_SIZE = 40;
    /** 房间尺寸占叶节点比例下限 */
    public static final double BSP_ROOM_RATIO_MIN = 0.6;
    /** 房间尺寸占叶节点比例上限 */
    public static final double BSP_ROOM_RATIO_MAX = 0.9;

    // ── 难度分区 ──
    public static final double ZONE_SAFE_MAX   = 20;
    public static final double ZONE_NORMAL_MAX = 40;
    public static final double ZONE_DANGER_MAX = 60;

    // ── 生成权重 ──
    public static final int WEIGHT_ENEMY_NORMAL = 70;
    public static final int WEIGHT_ENEMY_ELITE  = 20;
    public static final int WEIGHT_ENEMY_BOSS    = 5;

    public static final int WEIGHT_ITEM_COMMON = 60;
    public static final int WEIGHT_ITEM_RARE   = 30;
    public static final int WEIGHT_ITEM_LEGEND = 10;

    // ── 视口 ──
    public static final int VIEWPORT_WIDTH  = 1280;
    public static final int VIEWPORT_HEIGHT = 720;

    // ── 线程池 ──
    /** 后台线程池大小 */
    public static final int THREAD_POOL_SIZE = 4;

}
