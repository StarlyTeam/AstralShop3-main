package xyz.starly.astralshop.shop.inventory.global;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.registry.ShopMenuRegistry;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.shop.controlbar.ShopControlBarAction;
import xyz.starly.astralshop.shop.controlbar.ShopMainControlBar;
import xyz.starly.astralshop.shop.inventory.ShopInventory;

import java.util.Objects;

public class ShopMainInventory extends ShopInventory {

    private final ShopMenuRegistry shopMenuRegistry = AstralShop.getInstance().getShopMenuRegistry();
    private final ShopRegistry shopRegistry = AstralShop.getInstance().getShopRegistry();
    private final ShopMainControlBar shopMainControlBar;

    public ShopMainInventory() {
        super(AstralShop.getInstance().getShopMenuRegistry().getGuiTitle(),
                AstralShop.getInstance().getShopMenuRegistry().getRows(),
                true);
        this.shopMainControlBar = new ShopMainControlBar();
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        shopMenuRegistry.getMenuItems().forEach(shopMenuItem -> {
            int slot = shopMenuItem.getSlot();
            ItemStack itemStack = shopMenuItem.getItemStack();

            inventory.setItem(slot, itemStack);
        });

        shopMainControlBar.applyToInventory(inventory, player);
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }

        shopMenuRegistry.getMenuItems().stream()
                .filter(shopMenuItem -> clickedItem.isSimilar(shopMenuItem.getItemStack()))
                .findFirst()
                .ifPresent(shopMenuItem -> {
                    Shop newShop = shopRegistry.getShop(shopMenuItem.getShop());
                    if (newShop != null) {
                        new PaginatedShopInventory(newShop).open(player);
                    }
                });

        int clickedSlot = event.getRawSlot();
        if (clickedSlot >= inventory.getSize() - 9 && clickedSlot < inventory.getSize()) {
            shopMainControlBar.getItem(clickedSlot % 9).ifPresent(controlBarItem -> {
                switch (controlBarItem.getAction()) {
                    case BACK:
                    case CLOSE:
                        player.closeInventory();
                        break;
                }
            });
        }
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
    }
}