package org.example.rougevolley.ecs.component;

import org.example.rougevolley.ecs.Component;
import org.example.rougevolley.ecs.Entity;

/**
 * 摄像机跟随组件 —— 附加于玩家，驱动镜头平滑跟随
 */
public class CameraFollow implements Component {

    private boolean enabled = true;
    private double lerpFactor = 0.1;

    @Override
    public void onUpdate(Entity owner, double dt) {
        if (!enabled) return;
        // FXGL 的摄像机由 FXGL.getGameScene().getViewport() 控制
        // 此处预留接口，实际跟随由 FXGL 层实现
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public double getLerpFactor() {
        return lerpFactor;
    }

    public void setLerpFactor(double lerpFactor) {
        this.lerpFactor = lerpFactor;
    }
}
