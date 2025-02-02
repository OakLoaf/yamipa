package io.josemmo.bukkit.plugin.renderer;

import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.josemmo.bukkit.plugin.packets.EntityMetadataPacketBuilder;
import io.josemmo.bukkit.plugin.packets.SpawnEntityPacketBuilder;
import io.josemmo.bukkit.plugin.utils.Internals;
import io.josemmo.bukkit.plugin.utils.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeItemFrame extends FakeEntity {
    public static final int MIN_FRAME_ID = Integer.MAX_VALUE / 4;
    public static final int MAX_FRAME_ID = Integer.MAX_VALUE;
    private static final boolean SUPPORTS_GLOWING = Internals.MINECRAFT_VERSION >= 17;
    private static final Logger LOGGER = Logger.getLogger("FakeItemFrame");
    private static final AtomicInteger LAST_FRAME_ID = new AtomicInteger(MAX_FRAME_ID);
    private final int id;
    private final Location location;
    private final BlockFace face;
    private final Rotation rotation;
    private final boolean glowing;
    private final FakeMap[] maps;

    /**
     * Get next unused item frame ID
     * @return Next unused item frame ID
     */
    private static int getNextId() {
        return LAST_FRAME_ID.updateAndGet(lastId -> {
            if (lastId == MAX_FRAME_ID) {
                return MIN_FRAME_ID;
            }
            return lastId + 1;
        });
    }

    /**
     * Class constructor
     * @param location Frame location
     * @param face     Block face
     * @param rotation Frame rotation
     * @param glowing  Whether is glowing or regular frame
     * @param maps     Fake maps to animate
     */
    public FakeItemFrame(
        @NotNull Location location,
        @NotNull BlockFace face,
        @NotNull Rotation rotation,
        boolean glowing,
        @NotNull FakeMap[] maps
    ) {
        this.id = getNextId();
        this.location = location;
        this.face = face;
        this.rotation = rotation;
        this.glowing = glowing;
        this.maps = maps;
        LOGGER.fine("Created FakeItemFrame#" + this.id + " using " + this.maps.length + " FakeMap(s)");
    }

    /**
     * Get frame ID
     * @return Frame ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get entity spawn packet
     * @return Spawn packet
     */
    public @NotNull WrapperPlayServerSpawnEntity getSpawnPacket() {
        // Calculate frame position in relation to target block
        double x = location.getBlockX();
        double y = location.getBlockY();
        double z = location.getBlockZ();
        int pitch = 0;
        int yaw = 0;
        int orientation = 3;
        switch (face) {
            case UP:
                ++y;
                pitch = -64;
                orientation = 1;
                break;
            case DOWN:
                --y;
                pitch = 64;
                orientation = 0;
                break;
            case EAST:
                ++x;
                yaw = -64;
                orientation = 5;
                break;
            case WEST:
                --x;
                yaw = 64;
                orientation = 4;
                break;
            case NORTH:
                --z;
                yaw = 128;
                orientation = 2;
                break;
            case SOUTH:
                ++z;
        }

        // Create item frame entity
        return new SpawnEntityPacketBuilder()
            .setId(id)
            .setEntityType((glowing && SUPPORTS_GLOWING) ? EntityTypes.GLOW_ITEM_FRAME : EntityTypes.ITEM_FRAME)
            .setPosition(x, y, z)
            .setRotation(pitch, yaw)
            .setData(orientation)
            .build();
    }

    /**
     * Get frame of animation packets
     * @param player Player who is expected to receive packets (for caching reasons)
     * @param step   Map step
     */
    @SuppressWarnings("deprecation")
    public @NotNull List<PacketWrapper<?>> getRenderPackets(@NotNull Player player, int step) {
        List<PacketWrapper<?>> packets = new ArrayList<>(2);

        // Enqueue map pixels packet (if needed)
        boolean mustSendPixels = maps[step].requestResend(player);
        if (mustSendPixels) {
            packets.add(maps[step].getPixelsPacket());
        }

        // Create and attach filled map
        ItemStack itemStack = new com.github.retrooper.packetevents.protocol.item.ItemStack.Builder()
            .type(ItemTypes.FILLED_MAP)
            .component(ComponentTypes.MAP_ID, maps[step].getId())
            .build();

        packets.add(new EntityMetadataPacketBuilder()
            .setId(id)
            .setInvisible(true)
            .setItem(itemStack)
            .setRotation(rotation)
            .build());

        return packets;
    }

    /**
     * Get destroy item frame packet
     * @return Destroy packet
     */
    public @NotNull WrapperPlayServerDestroyEntities getDestroyPacket() {
        return new WrapperPlayServerDestroyEntities(id);
    }
}
