package org.example.rougevolley.core;

import com.almasb.fxgl.logging.Logger;

/**
 * 固定时间步长主循环（累加器模式）
 * 逻辑更新频率锁定为 60Hz，渲染帧独立插值，保障物理与判定的确定性
 */
public class FixedTimestepLoop {

    private static final Logger log = Logger.get(FixedTimestepLoop.class);

    private final double fixedTimestep;
    private final double maxAccumulator;
    private final Updatable updatable;

    private double accumulator = 0.0;
    private boolean running = false;

    @FunctionalInterface
    public interface Updatable {
        /** 每次固定步长逻辑更新时回调 */
        void update(double dt);
    }

    public FixedTimestepLoop(Updatable updatable) {
        this(updatable, GameConfig.FIXED_TIMESTEP, GameConfig.MAX_ACCUMULATOR);
    }

    public FixedTimestepLoop(Updatable updatable, double fixedTimestep, double maxAccumulator) {
        this.updatable = updatable;
        this.fixedTimestep = fixedTimestep;
        this.maxAccumulator = maxAccumulator;
    }

    /** 每帧由渲染循环调用 */
    public void tick(double deltaTime) {
        if (!running) return;

        accumulator += deltaTime;
        if (accumulator > maxAccumulator) {
            log.warning("Accumulator capped at " + maxAccumulator + " (was " + accumulator + ")");
            accumulator = maxAccumulator;
        }

        while (accumulator >= fixedTimestep) {
            updatable.update(fixedTimestep);
            accumulator -= fixedTimestep;
        }
    }

    /** 当前累积的时间比例 [0,1)，供渲染层插值使用 */
    public double getAlpha() {
        return accumulator / fixedTimestep;
    }

    public void start() {
        running = true;
        accumulator = 0.0;
    }

    public void stop() {
        running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public double getFixedTimestep() {
        return fixedTimestep;
    }
}
