package kr.starly.astralshop.shop.inventory.admin.impl;

import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.AstralShop;
import kr.starly.astralshop.shop.ShopItemImpl;
import kr.starly.astralshop.shop.inventory.admin.AdminPaginatedShopInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditAdminPaginatedShopInventory extends AdminPaginatedShopInventory {

    private final ShopRegistry shopRegistry = AstralShop.getInstance().getShopRegistry();

    public EditAdminPaginatedShopInventory(Shop shop) {
        super(shop, shop.getShopPages().get(0).getGuiTitle(), shop.getShopPages().get(0).getRows(), false);
    }

    @Override
    protected void handleItemInteraction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        ShopPage currentPage = shop.getShopPages().get(paginationManager.getCurrentPage() - 1);
        Map<Integer, ShopItem> updatedItems = new HashMap<>();

        int sizeWithoutControlBar = inventory.getSize() - 9;
        for (int i = 0; i < sizeWithoutControlBar; i++) {
            ItemStack itemStack = inventory.getItem(i);
            ShopItem shopItem = new ShopItemImpl(itemStack, 0, 0, 0, 0, new ArrayList<>());
            updatedItems.put(i, shopItem);
        }

        currentPage.setItems(updatedItems);
        shopRegistry.saveShop(shop);
    }
}