package kr.starly.astralshop.shop.inventory;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.repository.ShopRepository;
import kr.starly.astralshop.api.shop.Shop;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class BaseShopInventory implements InventoryHolder {

    protected final AstralShop plugin = AstralShop.getInstance();
    private final ShopRepository shopRepository = plugin.getShopRepository();

    @Getter protected Inventory inventory;
    @Getter protected Shop shop;
    @Getter protected String title;
    @Getter protected int rows;

    protected boolean cancelClick;
    protected boolean listenEvent;

    @Getter @Setter(value = AccessLevel.PROTECTED)
    private ItemStack outline = null;

    public BaseShopInventory(String title, int rows, boolean cancelClick) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.rows = rows;
        this.cancelClick = cancelClick;
        this.listenEvent = true;
    }

    public BaseShopInventory(Shop shop, String title, int rows, boolean cancelClick) {
        this.shop = shop;
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        this.rows = rows;
        this.cancelClick = cancelClick;
        this.listenEvent = true;
    }

    public boolean isClickCancelling() {
        return cancelClick;
    }
    protected void setClickCancelling(boolean cancelClick) {
        this.cancelClick = cancelClick;
    }

    public boolean isEventListening() {
        return listenEvent;
    }
    protected void setEventListening(boolean listenEvent) {
        this.listenEvent = listenEvent;
    }

    @SuppressWarnings("all")
    public void open(Player player) {
        if (player != null && player.isOnline()) {
            player.closeInventory();

            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                createInventory(player);
                player.openInventory(inventory);
            }, 1L);
        }
    }

    protected void createInventory(Player player) {
        inventory = plugin.getServer().createInventory(this, rows * 9, title);

        initializeOutline(inventory, player);
        initializeInventory(inventory, player);
    }

    private void clearInventory(Inventory inventory) {
        for (int i = 0; i < rows * 9; i++) {
            inventory.setItem(i, null);
        }
    }

    private void initializeOutline(Inventory inventory, Player player) {
        if (outline == null || outline.getType() != Material.AIR) {
            for (int i = 0; i < inventory.getSize(); i++) {
                int row = i / 9;
                int column = (i % 9) + 1;

                if (row == 0 || row == rows - 1 || column == 1 || column == 9) {
                    ItemStack slotItem = inventory.getItem(i);
                    if (!(slotItem == null || slotItem.getType() == Material.AIR)) continue;

                    inventory.setItem(i, outline);
                }
            }
        }
    }

    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        if (cancelClick) {
            event.setCancelled(true);
        }

        if (listenEvent && event.getClickedInventory().equals(inventory)) {
            updateData();
            inventoryClick(event);
        }
    }

    public void onClose(InventoryCloseEvent event) {
        if (listenEvent && event.getInventory().equals(inventory)) {
            updateData();
            inventoryClose(event);
        }
    }

    protected abstract void initializeInventory(Inventory inventory, Player player);
    protected abstract void inventoryClick(InventoryClickEvent event);
    protected abstract void inventoryClose(InventoryCloseEvent event);

    public void updateInventory(Player player) {
        boolean listenEvent = isEventListening();
        try {
            setEventListening(false);

            clearInventory(inventory);
            initializeOutline(inventory, player);
            initializeInventory(inventory, player);
        } finally {
            setEventListening(listenEvent);
        }
    }

    public void updateData() {
        shop = shopRepository.getShop(shop.getName());
    }

    public void saveShop() {
        shopRepository.saveShop(shop);
    }
}