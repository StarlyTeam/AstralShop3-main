package kr.starly.astralshop.shop.inventory;

import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.event.ShopTransactionEvent;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopTransaction;
import kr.starly.astralshop.shop.controlbar.ControlBar;
import kr.starly.astralshop.shop.controlbar.impl.PaginationControlBar;
import kr.starly.astralshop.shop.inventory.old.PaginatedShopInventory;
import kr.starly.astralshop.shop.inventory.old.PaginationHelper;
import kr.starly.libs.inventory.item.Click;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class UserShop extends PaginatedShopInventory {

    public UserShop(Shop shop) {
        super(shop);
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        paginationManager.getCurrentPageData().getItems().forEach((slot, shopItem) -> {
            if (slot >= rows * 9) return;

            ItemStack itemStack = shop.getTransactionHandler().toItemStack(shopItem);
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            inventory.setItem(slot, itemStack);
        });

        PaginationHelper paginationHelper = new PaginationHelper(paginationManager);
        ControlBar controlBar = new PaginationControlBar(paginationHelper, false); // new ShopControlBar(paginationHelper);
        controlBar.applyToInventory(inventory, player);
    }

    @Override
    protected void handleItemInteraction(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }

        int currentPage = paginationManager.getCurrentPage() - 1;
        int clickedSlot = event.getSlot();
        ShopItem shopItem = shop.getShopPages().get(currentPage).getItems().get(clickedSlot);

        // Commands
        shopItem.getCommands().forEach((commandLine) -> {
            plugin.getServer().dispatchCommand(player, commandLine);
        });

        // Transaction
        TransactionHandler transactionHandler = shop.getTransactionHandler();
        ShopTransaction transaction = transactionHandler.handleClick(new Click(event), shop, currentPage, clickedSlot, shopItem);
        if (transaction != null && transaction.getPlayer() == player) {
            ShopTransactionEvent transactionEvent = new ShopTransactionEvent(transaction);
            plugin.getServer().getPluginManager().callEvent(transactionEvent);

            if (!transactionEvent.isCancelled()) {
                transactionHandler.handleTransaction(transaction);
                saveShop();

                updateInventory(player);
            }
        }
    }
}