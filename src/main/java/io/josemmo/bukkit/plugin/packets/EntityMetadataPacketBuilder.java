package io.josemmo.bukkit.plugin.packets;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.josemmo.bukkit.plugin.utils.Internals;
import org.bukkit.Rotation;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class EntityMetadataPacketBuilder {
    private static final int ITEM_INDEX;
    private static final int ROTATION_INDEX;

    static {
        ITEM_INDEX = (Internals.MINECRAFT_VERSION < 17) ? 7 : 8;
        ROTATION_INDEX = ITEM_INDEX + 1;
    }

    private int entityId;
    private final List<EntityData> metadata = new ArrayList<>();

    public @NotNull EntityMetadataPacketBuilder setId(int id) {
        this.entityId = id;
        return this;
    }

    public @NotNull EntityMetadataPacketBuilder setFlags(byte flags) {
        metadata.add(new EntityData(0, EntityDataTypes.BYTE, flags));
        return this;
    }

    public @NotNull EntityMetadataPacketBuilder setInvisible(boolean invisible) {
        int flags = invisible ? 0x20 : 0x00;
        return setFlags((byte) flags);
    }

    public @NotNull EntityMetadataPacketBuilder setItem(@NotNull ItemStack item) {
        metadata.add(new EntityData(ITEM_INDEX, EntityDataTypes.ITEMSTACK, item));
        return this;
    }

    public @NotNull EntityMetadataPacketBuilder setRotation(@NotNull Rotation rotation) {
        metadata.add(new EntityData(ROTATION_INDEX, EntityDataTypes.INT, rotation.ordinal()));
        return this;
    }

    public @NotNull WrapperPlayServerEntityMetadata build() {
        return new WrapperPlayServerEntityMetadata(entityId, metadata);
    }
}
