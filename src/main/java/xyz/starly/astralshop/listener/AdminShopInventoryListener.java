package xyz.starly.astralshop.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryHolder;
import xyz.starly.astralshop.shop.inventory.ShopInventory;

public class AdminShopInventoryListener implements Listener {

    @EventHandler
    @SuppressWarnings("unused")
    public void onClick(InventoryClickEvent event) {
        handleInventoryEvent(event, event.getView().getTopInventory().getHolder(), true);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onClose(InventoryCloseEvent event) {
        handleInventoryEvent(event, event.getView().getTopInventory().getHolder(), false);
    }

    private void handleInventoryEvent(InventoryEvent event, InventoryHolder inventoryHolder, boolean isClickEvent) {
        if (inventoryHolder instanceof ShopInventory) {
            ShopInventory container = (ShopInventory) inventoryHolder;
            if (isClickEvent) {
                container.onClick((InventoryClickEvent) event);
            } else {
                container.onClose((InventoryCloseEvent) event);
            }
        }
    }
}