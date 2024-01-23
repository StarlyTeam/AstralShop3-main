package xyz.starly.astralshop.shop.inventory.impl;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.shop.inventory.PaginatedInventoryContainer;

public class EditShopInventoryContainerImpl extends PaginatedInventoryContainer {

    public EditShopInventoryContainerImpl(Shop shop) {
        super(shop, shop.getShopPages().get(0).getGuiTitle(), shop.getShopPages().get(0).getRows(), true);
    }

    @Override
    protected void handleItemInteraction(InventoryClickEvent event) {
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {

    }
}