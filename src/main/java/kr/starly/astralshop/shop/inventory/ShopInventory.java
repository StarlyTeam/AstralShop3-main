package kr.starly.astralshop.shop.inventory;

import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.AstralShop;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ShopInventory implements InventoryHolder {

    protected final JavaPlugin plugin = AstralShop.getInstance();

    @Getter
    protected Inventory inventory;

    protected Shop shop;
    private final String title;
    private final int rows;
    private final boolean cancel;

    public ShopInventory(String title, int rows, boolean cancel) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.rows = rows;
        this.cancel = cancel;
    }

    public ShopInventory(Shop shop, String title, int rows, boolean cancel) {
        this.shop = shop;
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.rows = rows;
        this.cancel = cancel;
    }

    @SuppressWarnings("all")
    public void open(Player player) {
        if (player != null || player.isOnline()) {
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                createInventory(player);
                player.openInventory(inventory);
            }, 1L);
        }
    }

    private void createInventory(Player player) {
        inventory = plugin.getServer().createInventory(this, rows * 9, title);

        initializeInventory(inventory, player);
    }

    public void onClick(InventoryClickEvent event) {
        if (cancel) {
            event.setCancelled(true);
        }
        inventoryClick(event);
    }

    public void onClose(InventoryCloseEvent event) {
        inventoryClose(event);
    }

    protected abstract void initializeInventory(Inventory inventory, Player player);

    protected abstract void inventoryClick(InventoryClickEvent event);

    protected abstract void inventoryClose(InventoryCloseEvent event);
}