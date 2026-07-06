package org.example.rougevolley.difficulty;

/**
 * 难度区域枚举
 */
public enum DifficultyZone {
    SAFE(0, 20, "安全区", 0, 0.0),
    NORMAL(20, 40, "普通区", 1, 0.1),
    DANGER(40, 60, "危险区", 2, 0.3),
    EXTREME(60, Double.MAX_VALUE, "极限区", 3, 0.5);

    private final double minDist;
    private final double maxDist;
    private final String label;
    private final int tier;
    private final double difficultyBonus; // 额外的伤害倍率

    DifficultyZone(double minDist, double maxDist, String label, int tier, double difficultyBonus) {
        this.minDist = minDist;
        this.maxDist = maxDist;
        this.label = label;
        this.tier = tier;
        this.difficultyBonus = difficultyBonus;
    }

    /**
     * 根据距起点距离计算所在区域
     */
    public static DifficultyZone fromDistance(double distance) {
        for (DifficultyZone zone : values()) {
            if (distance >= zone.minDist && distance < zone.maxDist) {
                return zone;
            }
        }
        return EXTREME;
    }

    public double minDist()  { return minDist; }
    public double maxDist()  { return maxDist; }
    public String label()    { return label; }
    public int tier()        { return tier; }
    public double difficultyBonus() { return difficultyBonus; }
}
