package org.example.rougevolley.dungeon;

import com.almasb.fxgl.logging.Logger;
import javafx.geometry.Point2D;
import org.example.rougevolley.config.GameConfig;

import java.util.*;

/**
 * 传送门映射系统 —— 为每个门分配唯一的目标房间。
 * <p>
 * 规则：
 * - 走廊门（room_corridor_h / room_corridor_v）只能传送到房间型房间
 * - 房间门（crossroad / lshape / hall）只能传送到走廊型房间
 * - 同一个房间的每个门传送到不同的目标房间
 * <p>
 * 映射使用种子 RNG 生成，确保同一种子产生相同的传送网络。
 */
public class TeleportMapping {

    private static final Logger log = Logger.get(TeleportMapping.class);

    /** 传送目标：目标房间 + 到达方向 */
    public static class TeleportTarget {
        private final Room destinationRoom;
        private final String arrivalDirection; // N/S/E/W, 可为 null

        public TeleportTarget(Room destinationRoom, String arrivalDirection) {
            this.destinationRoom = Objects.requireNonNull(destinationRoom);
            this.arrivalDirection = arrivalDirection;
        }

        public Room getDestinationRoom() { return destinationRoom; }
        public String getArrivalDirection() { return arrivalDirection; }
    }

    // roomId → (direction → TeleportTarget)
    private final Map<String, Map<String, TeleportTarget>> mapping;

    private TeleportMapping(Map<String, Map<String, TeleportTarget>> mapping) {
        this.mapping = Collections.unmodifiableMap(mapping);
    }

    // ============================================================
    //  公共 API
    // ============================================================

    /**
     * 查询门的传送目标。返回 null 表示该门无传送目标（断开）。
     */
    public TeleportTarget getTarget(String roomId, String direction) {
        Map<String, TeleportTarget> doorMap = mapping.get(roomId);
        if (doorMap == null) return null;
        return doorMap.get(direction);
    }

    /**
     * 指定房间的指定门是否有有效的传送目标。
     */
    public boolean isDoorConnected(String roomId, String direction) {
        TeleportTarget target = getTarget(roomId, direction);
        return target != null && target.getDestinationRoom() != null;
    }

    /**
     * 计算玩家到达目标房间时应放置的位置。
     * <p>
     * 若指定了到达方向，则在对应门的内侧放置玩家；
     * 否则使用模板的玩家生成点或房间中心作为回退。
     */
    public static Point2D getArrivalPosition(Room room, String arrivalDirection) {
        if (arrivalDirection != null) {
            var doorBounds = room.getDoorWorldBounds(arrivalDirection);
            if (doorBounds != null) {
                // 门中心
                double cx = doorBounds.getMinX() + doorBounds.getWidth() / 2.0;
                double cy = doorBounds.getMinY() + doorBounds.getHeight() / 2.0;
                // 向房间内侧推入，防止立即重新触发门碰撞
                double pushIn = GameConfig.PLAYER_SIZE * 2 + GameConfig.TILE_SIZE;
                switch (arrivalDirection) {
                    case "N" -> cy += pushIn;
                    case "S" -> cy -= pushIn;
                    case "W" -> cx += pushIn;
                    case "E" -> cx -= pushIn;
                }
                return new Point2D(cx, cy);
            }
        }

        // 回退 1：玩家生成点
        RoomTemplate template = room.getTemplate();
        Point2D playerSpawn = template.getPlayerSpawn();
        if (playerSpawn != null) {
            return new Point2D(room.getWorldX() + playerSpawn.getX(),
                               room.getWorldY() + playerSpawn.getY());
        }

        // 回退 2：房间中心
        return new Point2D(
            room.getWorldX() + template.getWidthPixels() / 2.0,
            room.getWorldY() + template.getHeightPixels() / 2.0
        );
    }

    // ============================================================
    //  工厂方法 —— 构建映射
    // ============================================================

    /**
     * 从房间列表构建传送映射（确定性，基于种子 RNG）。
     *
     * @param rooms 地牢中的所有房间
     * @param rng   游戏确定性随机源
     * @return 传送映射
     */
    public static TeleportMapping build(List<Room> rooms, Random rng) {
        // 1. 按房间类型分桶
        List<Room> corridorRooms = new ArrayList<>();
        List<Room> roomTypeRooms  = new ArrayList<>();

        for (Room room : rooms) {
            if (isCorridorType(room)) {
                corridorRooms.add(room);
            } else {
                roomTypeRooms.add(room);
            }
        }

        log.info("Teleport: " + corridorRooms.size() + " corridor rooms, "
                 + roomTypeRooms.size() + " room-type rooms");

        // 极端情况：一种类型完全缺失 → 所有门断开
        if (corridorRooms.isEmpty() || roomTypeRooms.isEmpty()) {
            log.warning("TeleportMapping: missing room type! corridors=" + corridorRooms.size()
                        + " rooms=" + roomTypeRooms.size() + ". All doors disconnected.");
            Map<String, Map<String, TeleportTarget>> fallback = new HashMap<>();
            for (Room room : rooms) {
                Map<String, TeleportTarget> empty = new HashMap<>();
                for (String dir : room.getDoorDirections()) {
                    empty.put(dir, null);
                }
                fallback.put(room.getId(), empty);
            }
            return new TeleportMapping(fallback);
        }

        // 2. 为每个房间的每个门分配唯一目标
        Map<String, Map<String, TeleportTarget>> mapping = new HashMap<>();

        for (Room sourceRoom : rooms) {
            boolean isSrcCorridor = isCorridorType(sourceRoom);
            List<Room> eligible = new ArrayList<>(
                isSrcCorridor ? roomTypeRooms : corridorRooms
            );
            // 洗牌：每个源房间使用独立的洗牌结果（由 rng 驱动，确定性）
            Collections.shuffle(eligible, rng);

            Map<String, TeleportTarget> doorMap = new LinkedHashMap<>();
            Set<String> usedDestIds = new HashSet<>();
            int eligibleIdx = 0;

            for (String dir : sourceRoom.getDoorDirections()) {
                // 在洗牌后的列表中寻找尚未使用的目标
                Room dest = null;
                for (int i = eligibleIdx; i < eligible.size(); i++) {
                    Room candidate = eligible.get(i);
                    if (!usedDestIds.contains(candidate.getId())) {
                        dest = candidate;
                        usedDestIds.add(candidate.getId());
                        // 交换到前面以保持迭代一致性
                        if (i != eligibleIdx) {
                            Collections.swap(eligible, i, eligibleIdx);
                        }
                        eligibleIdx++;
                        break;
                    }
                }

                if (dest != null) {
                    String arrivalDir = pickArrivalDirection(dest, rng);
                    doorMap.put(dir, new TeleportTarget(dest, arrivalDir));
                } else {
                    // 没有更多目标 → 该门断开
                    doorMap.put(dir, null);
                }
            }

            mapping.put(sourceRoom.getId(), doorMap);
        }

        // 3. 日志输出映射摘要
        int connectedDoors = 0;
        int totalDoors = 0;
        for (Map<String, TeleportTarget> dm : mapping.values()) {
            for (TeleportTarget t : dm.values()) {
                totalDoors++;
                if (t != null) connectedDoors++;
            }
        }
        log.info("Teleport mapping built: " + connectedDoors + "/" + totalDoors + " doors connected");

        return new TeleportMapping(mapping);
    }

    // ============================================================
    //  内部工具
    // ============================================================

    /**
     * 判断房间是否为走廊类型。
     * 匹配所有模板 ID 中包含 "corridor" 的房间（内置 + 外部模板均适用）。
     */
    private static boolean isCorridorType(Room room) {
        return room.getTemplate().getId().contains("corridor");
    }

    /**
     * 从目标房间的已连接门列表中随机选取一个作为到达方向。
     * 仅选取有有效传送目标的活跃门，避免玩家出生在不活跃的门前。
     */
    private static String pickArrivalDirection(Room destRoom, Random rng) {
        // 仅收集已连接（活跃）的门
        List<String> connectedDoors = new ArrayList<>();
        for (String dir : destRoom.getDoorDirections()) {
            if (destRoom.canPassThrough(dir)) {
                connectedDoors.add(dir);
            }
        }
        if (connectedDoors.isEmpty()) {
            // 回退：若所有门都断开，从所有门中随机选取
            List<String> allDoors = new ArrayList<>(destRoom.getDoorDirections());
            if (allDoors.isEmpty()) return null;
            return allDoors.get(rng.nextInt(allDoors.size()));
        }
        return connectedDoors.get(rng.nextInt(connectedDoors.size()));
    }
}