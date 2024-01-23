package xyz.starly.astralshop.shop.controlbar;

import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DynamicControlBar implements ControlBar {

    private int currentPage;
    private int totalPages;

    @Override
    public void applyToInventory(Inventory inventory, Player player) {
        int rows = inventory.getSize() / 9;
        int baseSlot = (rows - 1) * 9;

        inventory.setItem(baseSlot, createControlItem(Material.RED_STAINED_GLASS_PANE, "Prev Page", currentPage > 1));

        for (int i = 1; i < 7; i++) {
            int actualSlot = baseSlot + i;
            inventory.setItem(actualSlot, createPageItem(i));
        }

        inventory.setItem(baseSlot + 7, createControlItem(Material.GREEN_STAINED_GLASS_PANE, "Create Page", currentPage == totalPages));
        inventory.setItem(baseSlot + 8, createControlItem(Material.BLUE_STAINED_GLASS_PANE, "Next Page", currentPage < totalPages));
    }

    private ItemStack createControlItem(Material material, String name, boolean active) {
        ItemStack item = new ItemStack(active ? material : Material.AIR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createPageItem(int pageNumber) {
        Material material = pageNumber <= totalPages ? Material.BOOK : Material.AIR;
        ItemStack item = new ItemStack(material, pageNumber);
        if (pageNumber <= totalPages) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("Page " + pageNumber);
                List<String> lore = new ArrayList<>();
                lore.add("Items: " + pageNumber);
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        return item;
    }
}