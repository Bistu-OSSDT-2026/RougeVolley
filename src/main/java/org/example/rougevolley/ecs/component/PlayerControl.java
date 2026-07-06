package org.example.rougevolley.ecs.component;

import org.example.rougevolley.ecs.Component;
import org.example.rougevolley.ecs.Entity;
import javafx.geometry.Point2D;

/**
 * 玩家控制组件 —— 处理键盘输入与移动
 */
public class PlayerControl implements Component {

    private double speed = 200.0; // 像素/秒

    @Override
    public void onAttach(Entity owner) {
        // 注册输入监听
    }

    @Override
    public void onUpdate(Entity owner, double dt) {
        // 移动逻辑由 FXGL 输入系统处理，此处仅预留
        // 实际通过 FXGL Input 读取方向并更新位置
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }
}
