package org.example.rougevolley.ecs;

import javafx.geometry.Point2D;

import java.util.*;

/**
 * 游戏实体基类 —— 组合式组件容器，全局唯一 UUID 标识
 */
public class Entity {

    private final String uuid;
    private final EntityType type;
    private final Map<Class<?>, Component> components = new HashMap<>();

    private Point2D position;
    private boolean active = true;

    public Entity(EntityType type, Point2D position) {
        if (position == null) {
            throw new IllegalArgumentException("Entity position must not be null");
        }
        this.uuid = UUID.randomUUID().toString();
        this.type = type;
        this.position = position;
    }

    // ── 组件管理 ──

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
        component.onAttach(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> Optional<T> getComponent(Class<T> clazz) {
        return Optional.ofNullable((T) components.get(clazz));
    }

    public <T extends Component> boolean hasComponent(Class<T> clazz) {
        return components.containsKey(clazz);
    }

    public void removeComponent(Class<? extends Component> clazz) {
        Component comp = components.remove(clazz);
        if (comp != null) comp.onDetach(this);
    }

    public Collection<Component> getAllComponents() {
        return Collections.unmodifiableCollection(components.values());
    }

    // ── 生命周期 ──

    public void onUpdate(double dt) {
        if (!active) return;
        for (Component comp : components.values()) {
            comp.onUpdate(this, dt);
        }
    }

    public void destroy() {
        active = false;
        components.clear();
    }

    // ── Getters / Setters ──

    public String getUuid() {
        return uuid;
    }

    public EntityType getType() {
        return type;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity entity)) return false;
        return uuid.equals(entity.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}
