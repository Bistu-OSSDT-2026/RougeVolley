package org.example.rougevolley.ecs;

/**
 * 组件接口 —— 组合模式，为实体附加数据与行为
 * 所有具体组件必须实现此接口
 */
public interface Component {

    /**
     * 当组件被附加到实体时调用
     */
    default void onAttach(Entity owner) {}

    /**
     * 每帧更新
     * @param owner 所属实体
     * @param dt    固定时间步长 (1/60s)
     */
    default void onUpdate(Entity owner, double dt) {}

    /**
     * 当组件从实体分离时调用
     */
    default void onDetach(Entity owner) {}
}
