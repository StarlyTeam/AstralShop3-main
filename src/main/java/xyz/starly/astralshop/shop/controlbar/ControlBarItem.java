package xyz.starly.astralshop.shop.controlbar;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@Getter
public class ControlBarItem {

    private final Material material;
    private final String name;
    private final ControlBarAction action;
    private final boolean paginated;
    private final Material orElse;

    public ControlBarItem(ConfigurationSection section) {
        this.material = Material.valueOf(section.getString("material"));
        this.name = section.isSet("name") ? section.getString("name") : "";
        this.action = ControlBarAction.fromString(section.getString("action"));
        this.paginated = section.getBoolean("paginated.enabled", false);
        String orElseMaterialStr = section.getString("paginated.orElse.material", "AIR");
        this.orElse = Material.valueOf(orElseMaterialStr);
    }

    public ItemStack toItemStack(boolean isPaginated, boolean isLastPage) {
        Material finalMaterial = this.material;

        if (this.paginated && isLastPage && this.action == ControlBarAction.NEXT_PAGE && this.orElse != null) {
            finalMaterial = this.orElse;
        }
        else if (this.paginated && !isPaginated && this.action == ControlBarAction.PREV_PAGE && this.orElse != null) {
            finalMaterial = this.orElse;
        }

        ItemStack item = new ItemStack(finalMaterial);
        if (name != null && !name.isEmpty()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(name);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}