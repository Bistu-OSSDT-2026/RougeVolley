package org.example.rougevolley;

import org.example.rougevolley.ecs.Entity;
import org.example.rougevolley.ecs.EntityType;
import org.example.rougevolley.ecs.component.Health;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javafx.geometry.Point2D;

/**
 * 血量组件单元测试
 */
class HealthComponentTest {

    @Test
    void testInitialHealth() {
        Health health = new Health(100);
        assertEquals(100, health.getMaxHealth());
        assertEquals(100, health.getCurrentHealth());
        assertTrue(health.isAlive());
    }

    @Test
    void testTakeDamage() {
        Health health = new Health(100);
        boolean died = health.takeDamage(30);
        assertFalse(died);
        assertEquals(70, health.getCurrentHealth());
    }

    @Test
    void testTakeLethalDamage() {
        Health health = new Health(100);
        boolean died = health.takeDamage(200);
        assertTrue(died);
        assertEquals(0, health.getCurrentHealth());
        assertFalse(health.isAlive());
    }

    @Test
    void testHeal() {
        Health health = new Health(100);
        health.takeDamage(50);
        health.heal(20);
        assertEquals(70, health.getCurrentHealth());
    }

    @Test
    void testHealOverMax() {
        Health health = new Health(100);
        health.takeDamage(10);
        health.heal(50);
        assertEquals(100, health.getCurrentHealth(), "Should not exceed max health");
    }

    @Test
    void testHealthPercent() {
        Health health = new Health(100);
        health.takeDamage(25);
        assertEquals(0.75, health.getHealthPercent(), 0.001);
    }
}
