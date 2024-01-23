package xyz.starly.astralshop.shop.inventory.admin.impl;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.shop.inventory.admin.AdminPaginatedShopInventory;

public class EditAdminPaginatedShopInventory extends AdminPaginatedShopInventory {

    public EditAdminPaginatedShopInventory(Shop shop) {
        super(shop, shop.getShopPages().get(0).getGuiTitle(), shop.getShopPages().get(0).getRows(), true);
    }

    @Override
    protected void handleItemInteraction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        player.sendMessage("테스트");
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {

    }
}