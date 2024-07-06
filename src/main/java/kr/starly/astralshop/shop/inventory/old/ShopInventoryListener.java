package kr.starly.astralshop.shop.inventory.old;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;

public class ShopInventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        handleInventoryEvent(event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        handleInventoryEvent(event);
    }

    private void handleInventoryEvent(InventoryEvent event) {
        if (event.getInventory().getHolder() instanceof BaseShopInventory container) {
            if (event instanceof InventoryClickEvent) {
                container.onClick((InventoryClickEvent) event);
            } else {
                container.onClose((InventoryCloseEvent) event);
            }
        }
    }
}