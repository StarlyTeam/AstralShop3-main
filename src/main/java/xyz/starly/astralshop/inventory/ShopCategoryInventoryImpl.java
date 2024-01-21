package xyz.starly.astralshop.inventory;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.AstralShop;

public class ShopCategoryInventoryImpl extends ShopInventory implements Listener {

    private final String title;
    private final int rows;
    private final JavaPlugin plugin;

    @Getter
    private Inventory inventory;

    public ShopCategoryInventoryImpl() {
        this.plugin = AstralShop.getInstance();

        FileConfiguration config = plugin.getConfig();
        this.title = config.getString("category_inventory.title");
        this.rows = config.getInt("category_inventory.rows");
    }

    @Override
    public void open(Player player) {
        if (player != null || player.isOnline()) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                createInventory();
                player.openInventory(inventory);
            }, 1L);
        }
    }

    private void createInventory() {
        inventory = plugin.getServer().createInventory(this, rows * 9, title);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {

    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {

    }
}