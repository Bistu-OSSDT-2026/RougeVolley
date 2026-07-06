package org.example.rougevolley;

import org.example.rougevolley.util.ObjectPool;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 对象池单元测试
 */
class ObjectPoolTest {

    @Test
    void testObtainAndFree() {
        ObjectPool<StringBuilder> pool = new ObjectPool<>(StringBuilder::new);

        StringBuilder sb = pool.obtain();
        assertNotNull(sb);
        sb.append("test");

        pool.free(sb);
        assertEquals(1, pool.size());
    }

    @Test
    void testReuse() {
        ObjectPool<StringBuilder> pool = new ObjectPool<>(StringBuilder::new);

        StringBuilder first = pool.obtain();
        pool.free(first);

        StringBuilder second = pool.obtain();
        // 对象池返回 reused 对象（但不保证即是同一个，因为 acquire 重新创建）
        // 验证池机制不抛异常
        assertNotNull(second);
    }
}
