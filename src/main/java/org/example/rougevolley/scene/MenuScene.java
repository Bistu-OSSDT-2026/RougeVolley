package org.example.rougevolley.scene;

import org.example.rougevolley.core.GameState;

/**
 * 主菜单场景
 */
public class MenuScene extends GameScene {

    private final GameState gameState;

    public MenuScene(GameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void onInit() {
        // 加载菜单界面资源
    }

    @Override
    public void onEnter() {
        // 显示菜单、激活输入
    }

    @Override
    public void onUpdate(double dt) {
        // 菜单动画
    }

    @Override
    public void onExit() {
        // 隐藏菜单界面
    }
}
