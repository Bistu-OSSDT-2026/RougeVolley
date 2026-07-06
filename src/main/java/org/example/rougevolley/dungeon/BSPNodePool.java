package org.example.rougevolley.dungeon;

import java.util.*;

/**
 * BSPNode 对象池 —— 减少 GC 压力
 * 支持 reuse 与 reset 模式
 */
public class BSPNodePool {

    private final Queue<BSPNode> pool = new ArrayDeque<>();
    private int created = 0;

    /**
     * 从池中获取或新建 BSPNode
     */
    public BSPNode obtain(Rect rect) {
        BSPNode node = pool.poll();
        if (node != null) {
            node.setRect(rect);
            node.markPooled(false);
            return node;
        }
        BSPNode newNode = new BSPNode(rect);
        created++;
        return newNode;
    }

    /**
     * 将节点回收到池中（递归回收子节点）
     */
    public void free(BSPNode node) {
        if (node == null || node.isPooled()) return;

        // 递归回收子节点
        if (node.getLeft() != null) free(node.getLeft());
        if (node.getRight() != null) free(node.getRight());

        node.reset();
        node.markPooled(true);
        pool.offer(node);
    }

    public int getPoolSize() {
        return pool.size();
    }

    public int getTotalCreated() {
        return created;
    }

    public void clear() {
        pool.clear();
    }
}
