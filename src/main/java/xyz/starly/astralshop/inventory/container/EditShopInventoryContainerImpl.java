package xyz.starly.astralshop.inventory.container;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import xyz.starly.astralshop.api.shop.Shop;

public class EditShopInventoryContainerImpl extends AdminShopInventoryContainerImpl {

    public EditShopInventoryContainerImpl(Shop shop, String title, int rows, boolean cancel) {
        super(shop, title, rows, cancel);
    }

    @Override
    protected void initializeInventory(Inventory inventory) {
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {

    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {

    }
}