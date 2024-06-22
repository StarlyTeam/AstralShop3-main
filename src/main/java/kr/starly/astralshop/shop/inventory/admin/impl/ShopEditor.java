package kr.starly.astralshop.shop.inventory.admin.impl;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.repository.ShopRepository;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.shop.ShopItemImpl;
import kr.starly.astralshop.shop.inventory.admin.AdminPaginatedShopInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public class ShopEditor extends AdminPaginatedShopInventory {

    private final ShopRepository shopRepository = AstralShop.getInstance().getShopRepository();

    public ShopEditor(Shop shop) {
        super(shop, shop.getGuiTitle(), shop.getShopPages().get(0).getRows(), false);
    }

    @Override
    protected void handleItemInteraction(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getClick() == ClickType.SHIFT_RIGHT) {
            savePage(inventory);

            setEventListening(false);
            new ShopItemEditor(shop, paginationManager.getCurrentPage(), event.getSlot()).open(player);
        }
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        int clickedSlot = event.getSlot();
        if (clickedSlot >= inventory.getSize() - 9 && clickedSlot <= inventory.getSize() - 1) {
            Inventory inventory = event.getClickedInventory();
            savePage(inventory);
        }

        super.inventoryClick(event);
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
        setEventListening(false);
        new ShopSettings(shop).open((Player) event.getPlayer());

        Inventory inventory = event.getInventory();
        savePage(inventory);
    }

    private void savePage(Inventory inventory) {
        ShopPage currentPage = paginationManager.getCurrentPageData();
        Map<Integer, ShopItem> newItems = currentPage.getItems();

        int sizeWithoutControlBar = inventory.getSize() - 9;
        for (int i = 0; i < sizeWithoutControlBar; i++) {
            ItemStack originalItem = newItems.containsKey(i) ? newItems.get(i).getItemStack() : null;
            ItemStack newItem = inventory.getItem(i);
            if (newItem != null && newItem.getType() != Material.AIR) {
                if (newItem.isSimilar(originalItem)) continue;
            } else {
                newItems.remove(i);
            }

            ShopItem shopItem = new ShopItemImpl(newItem, -1, -1, -1, -1, false, new ArrayList<>());
            newItems.put(i, shopItem);
        }

        currentPage.setItems(newItems);
        shopRepository.saveShop(shop);
    }
}