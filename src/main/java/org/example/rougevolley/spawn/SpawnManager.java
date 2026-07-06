package org.example.rougevolley.spawn;

import com.almasb.fxgl.logging.Logger;
import javafx.geometry.Point2D;
import org.example.rougevolley.core.GameState;
import org.example.rougevolley.dungeon.BSPNode;
import org.example.rougevolley.dungeon.DungeonData;
import org.example.rougevolley.dungeon.Room;
import org.example.rougevolley.ecs.Entity;
import org.example.rougevolley.ecs.EntityType;
import org.example.rougevolley.ecs.component.*;
import org.example.rougevolley.ecs.component.Pickup.ItemRarity;

import java.util.*;

/**
 * 生成管理器 —— 依据权重与空间约束填充地牢
 */
public class SpawnManager {

    private static final Logger log = Logger.get(SpawnManager.class);

    private final GameState gameState;
    private final Random rng;

    public SpawnManager(GameState gameState) {
        this.gameState = gameState;
        this.rng = new Random(gameState.getSeed() ^ 0xDEADBEEFL);
    }

    /**
     * 在地牢中生成所有敌人和物品
     */
    public void populateDungeon(DungeonData dungeon) {
        var rooms = dungeon.rooms();
        if (rooms.isEmpty()) {
            log.warning("Cannot populate dungeon: no rooms");
            return;
        }

        // 找最大房间作为 Boss 房
        Room bossRoom = dungeon.getLargestRoom().orElse(null);
        List<Room> leafRooms = findLeafRooms(dungeon.root());

        for (Room room : rooms) {
            boolean isBossRoom = (bossRoom != null && room == bossRoom);
            boolean isLeaf = leafRooms.contains(room);

            if (isBossRoom) {
                // Boss 房间：Boss + 传说物品
                spawnBoss(room);
                spawnLegendItem(room);
            } else if (isLeaf) {
                // 叶节点房间：普通/精英敌人 + 物品
                spawnEnemyInRoom(room);
                spawnItemInRoom(room);
            }
            // 非叶节点房间只用于通道，不生成
        }

        log.info("Dungeon populated: " + rooms.size() + " rooms");
    }

    private void spawnBoss(Room room) {
        Entity boss = EntityFactory.createEntity(
                EntityType.ENEMY_BOSS,
                new Point2D(room.centerX(), room.centerY())
        );
        boss.addComponent(new Health(500));
        boss.addComponent(new AIControl());
        boss.addComponent(new Collidable(48, 48));
        gameState.registerEntity(boss);
    }

    private void spawnLegendItem(Room room) {
        Entity item = EntityFactory.createEntity(
                EntityType.ITEM_LEGEND,
                new Point2D(room.centerX() + 30, room.centerY())
        );
        item.addComponent(new Pickup("legend_sword", ItemRarity.LEGEND));
        gameState.registerEntity(item);
    }

    private void spawnEnemyInRoom(Room room) {
        // 加权选择敌人类型
        double roll = rng.nextDouble() * 100;

        EntityType enemyType;
        if (roll < 5) {
            enemyType = EntityType.ENEMY_BOSS;
        } else if (roll < 25) {
            enemyType = EntityType.ENEMY_ELITE;
        } else {
            enemyType = EntityType.ENEMY_NORMAL;
        }

        double x = room.centerX() + (rng.nextDouble() - 0.5) * room.getBounds().w() * 0.5;
        double y = room.centerY() + (rng.nextDouble() - 0.5) * room.getBounds().h() * 0.5;

        Entity enemy = EntityFactory.createEntity(enemyType, new Point2D(x, y));

        double hp = switch (enemyType) {
            case ENEMY_NORMAL -> 50;
            case ENEMY_ELITE  -> 150;
            case ENEMY_BOSS   -> 500;
            default -> 50;
        };

        enemy.addComponent(new Health(hp));
        enemy.addComponent(new AIControl());
        enemy.addComponent(new Collidable(32, 32));
        gameState.registerEntity(enemy);
    }

    private void spawnItemInRoom(Room room) {
        double roll = rng.nextDouble() * 100;

        EntityType itemType;
        ItemRarity rarity;
        if (roll < 10) { // 10% 传说
            itemType = EntityType.ITEM_LEGEND;
            rarity = ItemRarity.LEGEND;
        } else if (roll < 40) { // 30% 稀有
            itemType = EntityType.ITEM_RARE;
            rarity = ItemRarity.RARE;
        } else { // 60% 普通
            itemType = EntityType.ITEM_COMMON;
            rarity = ItemRarity.COMMON;
        }

        double x = room.centerX() + (rng.nextDouble() - 0.5) * room.getBounds().w() * 0.4;
        double y = room.centerY() + (rng.nextDouble() - 0.5) * room.getBounds().h() * 0.4;

        Entity item = EntityFactory.createEntity(itemType, new Point2D(x, y));
        item.addComponent(new Pickup("item_" + rarity.name().toLowerCase(), rarity));
        gameState.registerEntity(item);
    }

    private List<Room> findLeafRooms(BSPNode node) {
        List<Room> rooms = new ArrayList<>();
        collectLeafRooms(node, rooms);
        return rooms;
    }

    private void collectLeafRooms(BSPNode node, List<Room> rooms) {
        if (node == null) return;
        if (node.isLeaf()) {
            if (node.getRoom() != null) rooms.add(node.getRoom());
            return;
        }
        collectLeafRooms(node.getLeft(), rooms);
        collectLeafRooms(node.getRight(), rooms);
    }
}
