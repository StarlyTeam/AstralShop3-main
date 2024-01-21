package xyz.starly.astralshop.inventory.container;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.inventory.ShopInventory;

public abstract class AdminShopInventoryContainerImpl extends ShopInventory {

    protected final JavaPlugin plugin = AstralShop.getInstance();

    protected Shop shop;
    private final String title;
    private final int rows;
    private final boolean cancel;

    public AdminShopInventoryContainerImpl(Shop shop, String title, int rows, boolean cancel) {
        this.shop = shop;
        this.title = title;
        this.rows = rows;
        this.cancel = cancel;
    }

    @Getter
    private Inventory inventory;

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

        initializeInventory(inventory);
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

    protected abstract void initializeInventory(Inventory inventory);
    protected abstract void inventoryClick(InventoryClickEvent event);
    protected abstract void inventoryClose(InventoryCloseEvent event);
}