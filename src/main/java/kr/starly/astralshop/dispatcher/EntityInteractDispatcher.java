package kr.starly.astralshop.dispatcher;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class EntityInteractDispatcher implements Listener {

    private EntityInteractDispatcher() {}

    public static void register(JavaPlugin plugin) {
        EntityInteractDispatcher dispatcher = new EntityInteractDispatcher();
        plugin.getServer().getPluginManager().registerEvents(dispatcher, plugin);
    }

    private static final Map<UUID, Consumer<String>> callbacks = new HashMap<>();

    public static void attachConsumer(UUID target, Consumer<String> callback) {
        callbacks.put(target, callback);
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        Consumer<String> callback = callbacks.remove(player.getUniqueId());
        if (callback != null) {
            event.setCancelled(true);

            callback.accept(event.getRightClicked().getCustomName());
        }
    }
}