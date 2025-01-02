package io.josemmo.bukkit.plugin.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerActionBar;
import io.josemmo.bukkit.plugin.YamipaPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class ActionBar {
    private static final Logger LOGGER = Logger.getLogger("ActionBar");
    private static final YamipaPlugin plugin = YamipaPlugin.getInstance();
    private final Player player;
    private String message;
    private BukkitTask task = null;

    private ActionBar(@NotNull Player player, @NotNull String message) {
        this.player = player;
        this.message = message;
    }

    /**
     * Send action bar message
     * @param player  Player who will receive the message
     * @param message Message to send
     */
    public static void send(@NotNull Player player, @NotNull String message) {
        ActionBar instance = new ActionBar(player, message);
        instance.sendOnce();
    }

    /**
     * Keep sending action bar message
     * @param  player  Player who will receive the message
     * @param  message Message to send
     * @return         New instance
     */
    public static @NotNull ActionBar repeat(@NotNull Player player, @NotNull String message) {
        ActionBar instance = new ActionBar(player, message);
        instance.start();
        return instance;
    }

    /**
     * Set message
     * @param  message Message
     * @return         This instance
     */
    public ActionBar setMessage(@NotNull String message) {
        this.message = message;
        return sendOnce();
    }

    /**
     * Clear message
     * @return This instance
     */
    public ActionBar clear() {
        message = "";
        stop();
        return sendOnce();
    }

    /**
     * Send message once
     * @return This instance
     */
    public ActionBar sendOnce() {
        WrapperPlayServerActionBar actionBarPacket = new WrapperPlayServerActionBar(
            Component.text(message)
        );

        PacketEvents.getAPI().getProtocolManager().sendPacket(player, actionBarPacket);
        return this;
    }

    /**
     * Start sending message indefinitely
     * @return This instance
     */
    public ActionBar start() {
        if (task == null) {
            task = Bukkit.getScheduler().runTaskTimer(plugin, this::sendOnce, 0L, 40L);
        }
        return this;
    }

    /**
     * Stop sending message indefinitely
     * @return This instance
     */
    public ActionBar stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        return this;
    }
}
