package org.example.rougevolley.dungeon;

/**
 * BSP 树节点 —— 递归分割空间
 * 叶节点（left/right == null）包含具体房间
 * rect 非 final 以支持对象池复用
 */
public class BSPNode {

    private Rect rect;
    private BSPNode left;
    private BSPNode right;
    private Room room;   // 仅叶节点持有

    // ── 对象池复用字段 ──
    private boolean pooled = false;

    public BSPNode() {} // 用于对象池

    public BSPNode(Rect rect) {
        this.rect = rect;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }

    // ── Getters / Setters ──

    public BSPNode getLeft() {
        return left;
    }

    public void setLeft(BSPNode left) {
        this.left = left;
    }

    public BSPNode getRight() {
        return right;
    }

    public void setRight(BSPNode right) {
        this.right = right;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    // ── 对象池支持 ──

    public boolean isPooled() {
        return pooled;
    }

    public void markPooled(boolean pooled) {
        this.pooled = pooled;
    }

    /** 重置节点状态以回收到对象池 */
    public void reset() {
        this.rect = null;
        this.left = null;
        this.right = null;
        this.room = null;
    }
}
