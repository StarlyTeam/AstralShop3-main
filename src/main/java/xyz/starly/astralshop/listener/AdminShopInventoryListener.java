package xyz.starly.astralshop.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.InventoryHolder;
import xyz.starly.astralshop.inventory.container.AdminShopInventoryContainerImpl;

public class AdminShopInventoryListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        handleInventoryEvent(event, event.getView().getTopInventory().getHolder(), true);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        handleInventoryEvent(event, event.getView().getTopInventory().getHolder(), false);
    }

    private void handleInventoryEvent(InventoryEvent event, InventoryHolder inventoryHolder, boolean isClickEvent) {
        if (inventoryHolder instanceof AdminShopInventoryListener) {
            AdminShopInventoryContainerImpl container = (AdminShopInventoryContainerImpl) inventoryHolder.getInventory();
            if (isClickEvent) {
                container.onClick((InventoryClickEvent) event);
            } else {
                container.onClose((InventoryCloseEvent) event);
            }
        }
    }
}