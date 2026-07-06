package org.example.rougevolley;

import org.example.rougevolley.scene.GameScene;
import org.example.rougevolley.scene.SceneManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * SceneManager 栈管理单元测试
 */
class SceneManagerTest {

    private static class TestScene extends GameScene {
        public boolean initCalled = false;
        public boolean enterCalled = false;
        public boolean exitCalled = false;
        public int updateCount = 0;

        @Override public void onInit() { initCalled = true; }
        @Override public void onEnter() { enterCalled = true; }
        @Override public void onUpdate(double dt) { updateCount++; }
        @Override public void onExit() { exitCalled = true; }
    }

    @Test
    void testPushAndPop() {
        SceneManager sm = new SceneManager(5);
        assertTrue(sm.isEmpty());

        TestScene scene = new TestScene();
        sm.push(scene);

        assertEquals(1, sm.size());
        assertSame(scene, sm.getCurrentScene());
        assertTrue(scene.initCalled);
        assertTrue(scene.enterCalled);

        sm.pop();
        assertTrue(sm.isEmpty());
        assertTrue(scene.exitCalled);
    }

    @Test
    void testReplace() {
        SceneManager sm = new SceneManager(5);
        TestScene a = new TestScene();
        TestScene b = new TestScene();

        sm.push(a);
        sm.replace(b);

        assertEquals(1, sm.size());
        assertSame(b, sm.getCurrentScene());
        assertTrue(a.exitCalled);
        assertTrue(b.initCalled);
        assertTrue(b.enterCalled);
    }

    @Test
    void testStackOverflow() {
        SceneManager sm = new SceneManager(2);
        sm.push(new TestScene());
        sm.push(new TestScene());
        assertThrows(IllegalStateException.class, () -> sm.push(new TestScene()));
    }

    @Test
    void testPopEmptyStack() {
        SceneManager sm = new SceneManager(3);
        assertThrows(IllegalStateException.class, sm::pop);
    }

    @Test
    void testUpdateActiveOnly() {
        SceneManager sm = new SceneManager(3);
        TestScene a = new TestScene();
        TestScene b = new TestScene();

        sm.push(a);
        sm.push(b); // a 被暂停

        sm.updateActiveScene(1.0);
        assertEquals(0, a.updateCount); // 不活跃
        assertEquals(1, b.updateCount); // 活跃

        sm.pop(); // b 退出，a 恢复
        sm.updateActiveScene(1.0);
        assertEquals(1, a.updateCount);
    }

    @Test
    void testClear() {
        SceneManager sm = new SceneManager(5);
        sm.push(new TestScene());
        sm.push(new TestScene());
        sm.push(new TestScene());
        sm.clear();
        assertTrue(sm.isEmpty());
    }
}
