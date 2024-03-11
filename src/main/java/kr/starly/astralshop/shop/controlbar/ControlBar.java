package kr.starly.astralshop.shop.controlbar;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public interface ControlBar {

    void applyToInventory(Inventory inventory, Player player);
}