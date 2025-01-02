package io.josemmo.bukkit.plugin.renderer;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBundle;
import io.josemmo.bukkit.plugin.YamipaPlugin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class FakeEntity {

    /**
     * Try to sleep
     * <p>
     * NOTE: Will wait synchronously, blocking the invoker thread
     * @param ms Delay in milliseconds
     */
    protected static void tryToSleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception __) {
            // Fail silently
        }
    }

    /**
     * Try to send packet
     * @param player Player who will receive the packet
     * @param packet Packet to send
     */
    protected static void tryToSendPacket(@NotNull Player player, @NotNull PacketWrapper<?> packet) {
        PacketEvents.getAPI().getProtocolManager().sendPacket(player, packet);
    }

    /**
     * Try to send several packets
     * @param player  Player who will receive the packets
     * @param packets Packets to send
     */
    protected static void tryToSendPackets(@NotNull Player player, @NotNull Iterable<PacketWrapper<?>> packets) {
        tryToSendPackets(player, packets, true);
    }

    /**
     * Try to send several packets
     * @param player  Player who will receive the packets
     * @param packets Packets to send
     * @param bundle Whether to bundle packets
     */
    @SuppressWarnings("SameParameterValue")
    protected static void tryToSendPackets(@NotNull Player player, @NotNull Iterable<PacketWrapper<?>> packets, boolean bundle) {
        if (bundle) {
            tryToSendPacket(player, new WrapperPlayServerBundle());
        }

        for (PacketWrapper<?> packet : packets) {
            tryToSendPacket(player, packet);
        }

        if (bundle) {
            tryToSendPacket(player, new WrapperPlayServerBundle());
        }
    }

    /**
     * Try to run asynchronous task
     * @param callback Callback to execute
     */
    protected static void tryToRunAsyncTask(@NotNull Runnable callback) {
        YamipaPlugin.getInstance().getScheduler().execute(callback);
    }
}
