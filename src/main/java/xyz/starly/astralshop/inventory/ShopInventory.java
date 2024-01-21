package xyz.starly.astralshop.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public abstract class ShopInventory implements InventoryHolder {

    protected abstract void open(Player player);
}