package org.example.rougevolley.ecs.component;

import org.example.rougevolley.ecs.Component;
import org.example.rougevolley.ecs.Entity;

/**
 * 掉落物组件 —— 物品可拾取
 */
public class Pickup implements Component {

    public enum ItemRarity {
        COMMON, RARE, LEGEND
    }

    private final String itemId;
    private final ItemRarity rarity;
    private boolean pickedUp = false;

    public Pickup(String itemId, ItemRarity rarity) {
        this.itemId = itemId;
        this.rarity = rarity;
    }

    @Override
    public void onUpdate(Entity owner, double dt) {
        // 拾取检测由场景统一处理
    }

    public String getItemId() {
        return itemId;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }
}
