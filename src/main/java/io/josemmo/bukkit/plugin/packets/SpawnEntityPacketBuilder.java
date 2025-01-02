package io.josemmo.bukkit.plugin.packets;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;

import java.util.UUID;

public class SpawnEntityPacketBuilder {
    private int entityId;
    private final UUID uuid;
    private EntityType entityType;
    private final Location location;
    private int data;

    public SpawnEntityPacketBuilder() {
        uuid = UUID.randomUUID();
        location = new Location(Vector3d.zero(), 0, 0);
    }

    public SpawnEntityPacketBuilder setId(int id) {
        this.entityId = id;
        return this;
    }

    public SpawnEntityPacketBuilder setEntityType(EntityType entityType) {
        this.entityType = entityType;
        return this;
    }

    public SpawnEntityPacketBuilder setRotation(int pitch, int yaw) {
        this.location.setPitch(pitch);
        this.location.setYaw(yaw);
        return this;
    }

    public SpawnEntityPacketBuilder setData(int data) {
        this.data = data;
        return this;
    }

    public SpawnEntityPacketBuilder setPosition(double x, double y, double z) {
        this.location.setPosition(new Vector3d(x, y, z));
        return this;
    }

    public WrapperPlayServerSpawnEntity build() {
        return new WrapperPlayServerSpawnEntity(
            entityId,
            uuid,
            entityType,
            location,
            0,
            data,
            null
        );
    }
}
