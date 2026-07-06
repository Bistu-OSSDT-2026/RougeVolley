package org.example.rougevolley.core;

import com.almasb.fxgl.logging.Logger;

import java.util.concurrent.*;

/**
 * 游戏主循环服务
 * 集成固定时间步长 + 事件驱动 + 后台线程调度
 */
public class GameLoop {

    private static final Logger log = Logger.get(GameLoop.class);

    private FixedTimestepLoop loop;
    private final ExecutorService backgroundExecutor;
    private final GameState gameState;

    public GameLoop(GameState gameState) {
        this.gameState = gameState;
        this.backgroundExecutor = Executors.newFixedThreadPool(
                GameConfig.THREAD_POOL_SIZE,
                r -> {
                    Thread t = new Thread(r, "rougevolley-bg-" + r.hashCode());
                    t.setDaemon(true);
                    return t;
                }
        );
    }

    public void onInit() {
        log.info("GameLoop initializing, fixed timestep = " + GameConfig.FIXED_TIMESTEP);

        this.loop = new FixedTimestepLoop(dt -> {
            // 每次固定步长更新：驱动场景、AI、碰撞等逻辑
            gameState.getSceneManager().updateActiveScene(dt);
        });

        loop.start();
        log.info("GameLoop started");
    }

    /**
     * 每帧调用（由 FXGL onUpdate 驱动）
     */
    public void onUpdate(double tpf) {
        if (loop != null) {
            loop.tick(tpf);
        }
    }

    public void onExit() {
        if (loop != null) loop.stop();
        shutdownExecutor();
    }

    /** 提交异步任务，返回 Future */
    public <T> Future<T> submitAsync(Callable<T> task) {
        return backgroundExecutor.submit(task);
    }

    /** 提交异步 Runnable */
    public Future<?> submitAsync(Runnable task) {
        return backgroundExecutor.submit(task);
    }

    private void shutdownExecutor() {
        backgroundExecutor.shutdown();
        try {
            if (!backgroundExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                backgroundExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            backgroundExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public FixedTimestepLoop getFixedLoop() {
        return loop;
    }

    public GameState getGameState() {
        return gameState;
    }
}
