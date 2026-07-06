package org.example.rougevolley;

import javafx.geometry.Point2D;
import org.example.rougevolley.ecs.Entity;
import org.example.rougevolley.ecs.EntityType;
import org.example.rougevolley.ecs.component.*;
import org.example.rougevolley.spawn.EntityFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * EntityFactory 单元测试
 */
class EntityFactoryTest {

    @Test
    void testCreatePlayer() {
        Entity player = EntityFactory.createEntity(EntityType.PLAYER, new Point2D(100, 200));
        assertEquals(EntityType.PLAYER, player.getType());
        assertEquals(100, player.getPosition().getX());
        assertEquals(200, player.getPosition().getY());
        assertTrue(player.hasComponent(PlayerControl.class));
        assertTrue(player.hasComponent(Collidable.class));
        assertTrue(player.hasComponent(Health.class));
    }

    @Test
    void testCreateEnemy() {
        Entity enemy = EntityFactory.createEntity(EntityType.ENEMY_NORMAL, new Point2D(50, 50));
        assertTrue(enemy.hasComponent(AIControl.class));
        assertTrue(enemy.hasComponent(Collidable.class));
        assertTrue(enemy.hasComponent(PhysicsComponent.class));
    }

    @Test
    void testCreateItem() {
        Entity item = EntityFactory.createEntity(EntityType.ITEM_COMMON, new Point2D(0, 0));
        assertTrue(item.hasComponent(Collidable.class));
    }

    @Test
    void testNullPositionThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> EntityFactory.createEntity(EntityType.PLAYER, null));
    }

    @Test
    void testUniqueUuid() {
        Entity a = EntityFactory.createEntity(EntityType.PLAYER, new Point2D(0, 0));
        Entity b = EntityFactory.createEntity(EntityType.PLAYER, new Point2D(0, 0));
        assertNotNull(a.getUuid());
        assertNotNull(b.getUuid());
        assertNotEquals(a.getUuid(), b.getUuid());
    }

    @Test
    void testAllEntityTypesCanBeCreated() {
        for (EntityType type : EntityType.values()) {
            Entity e = EntityFactory.createEntity(type, new Point2D(10, 10));
            assertNotNull(e);
            assertEquals(type, e.getType());
            assertTrue(e.isActive());
        }
    }
}
