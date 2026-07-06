package org.example.rougevolley.scene;

import com.almasb.fxgl.logging.Logger;

import java.util.*;

/**
 * 场景栈管理器 —— 基于栈模型管理场景生命周期
 * 支持 push / pop 操作，最大深度不超过 {@link org.example.rougevolley.core.GameConfig#SCENE_STACK_MAX_DEPTH}
 */
public class SceneManager {

    private static final Logger log = Logger.get(SceneManager.class);

    private final Deque<GameScene> sceneStack = new ArrayDeque<>();
    private final int maxDepth;

    public SceneManager() {
        this(org.example.rougevolley.core.GameConfig.SCENE_STACK_MAX_DEPTH);
    }

    public SceneManager(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * 将场景推入栈顶
     * @throws IllegalStateException 如果栈已满
     */
    public void push(GameScene scene) {
        Objects.requireNonNull(scene, "Scene must not be null");

        if (sceneStack.size() >= maxDepth) {
            throw new IllegalStateException(
                    "Scene stack overflow: max depth " + maxDepth + " exceeded");
        }

        // 暂停当前场景
        if (!sceneStack.isEmpty()) {
            GameScene current = sceneStack.peek();
            current.onExit();
            current.setActive(false);
        }

        // 初始化新场景（仅首次）
        if (!scene.isInitialized()) {
            scene.onInit();
            scene.setInitialized(true);
        }

        sceneStack.push(scene);
        scene.onEnter();
        scene.setActive(true);

        log.debug("Scene pushed: " + scene.getClass().getSimpleName()
                + " (stack depth: " + sceneStack.size() + ")");
    }

    /**
     * 弹出栈顶场景
     * @throws IllegalStateException 如果栈为空
     */
    public void pop() {
        if (sceneStack.isEmpty()) {
            throw new IllegalStateException("Cannot pop from empty scene stack");
        }

        GameScene exiting = sceneStack.pop();
        exiting.onExit();
        exiting.setActive(false);

        // 恢复上一个场景
        if (!sceneStack.isEmpty()) {
            GameScene previous = sceneStack.peek();
            previous.onEnter();
            previous.setActive(true);
        }

        log.debug("Scene popped: " + exiting.getClass().getSimpleName()
                + " (stack depth: " + sceneStack.size() + ")");
    }

    /** 替换栈顶场景（pop + push 原子操作） */
    public void replace(GameScene scene) {
        if (!sceneStack.isEmpty()) {
            pop(); // 触发完整生命周期 (onExit)
        }
        push(scene);
    }

    /** 更新当前活动场景 */
    public void updateActiveScene(double dt) {
        if (!sceneStack.isEmpty()) {
            GameScene current = sceneStack.peek();
            if (current.isActive()) {
                current.onUpdate(dt);
            }
        }
    }

    public GameScene getCurrentScene() {
        return sceneStack.peek();
    }

    public int size() {
        return sceneStack.size();
    }

    public boolean isEmpty() {
        return sceneStack.isEmpty();
    }

    /** 清空所有场景 */
    public void clear() {
        while (!sceneStack.isEmpty()) {
            pop();
        }
    }
}
