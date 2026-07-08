package org.example.rougevolley.ecs.components;

import org.example.rougevolley.ecs.Component;

/**
 * 可拾取道具组件 —— 携带道具类型、数值和拾取状态。
 */
public class PickupComponent implements Component {

    private final String pickupType;
    private final double value;
    private boolean collected;

    public PickupComponent(String pickupType, double value) {
        this.pickupType = pickupType;
        this.value = value;
    }

    public String getPickupType() { return pickupType; }
    public double getValue() { return value; }
    public boolean isCollected() { return collected; }
    public void markCollected() { this.collected = true; }
}
