package org.example.rougevolley.scene;

/**
 * 场景基类 —— 定义生命周期：onInit → onEnter → onUpdate(每帧) → onExit
 */
public abstract class GameScene {

    private boolean initialized = false;
    private boolean active = false;

    /** 场景首次创建时调用——加载静态资源、初始化配置 */
    public abstract void onInit();

    /** 成为当前活动场景时调用——恢复状态、启动音乐、激活输入 */
    public abstract void onEnter();

    /** 每帧调用——处理逻辑、AI、碰撞 */
    public abstract void onUpdate(double dt);

    /** 切换至其他场景时调用——保存进度、释放临时资源 */
    public abstract void onExit();

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
