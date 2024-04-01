package kr.starly.astralshop.shop.inventory.global.impl;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.event.ShopTransactionEvent;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopTransaction;
import kr.starly.astralshop.shop.controlbar.ControlBar;
import kr.starly.astralshop.shop.controlbar.impl.PaginationControlBar;
import kr.starly.astralshop.shop.controlbar.impl.ShopControlBar;
import kr.starly.astralshop.shop.inventory.PaginationHelper;
import kr.starly.astralshop.shop.inventory.global.PaginatedShopInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserShop extends PaginatedShopInventory {

    public UserShop(Shop shop) {
        super(shop);
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        paginationManager.getCurrentPageData().getItems().forEach((slot, shopItem) -> {
            if (slot >= rows * 9) return;

            ItemStack itemStack = plugin.getTransactionHandler().toItemStack(shopItem);
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            inventory.setItem(slot, itemStack);
        });

        PaginationHelper paginationHelper = new PaginationHelper(paginationManager);
//        ControlBar controlBar = new ShopControlBar(paginationHelper);
        ControlBar controlBar = new PaginationControlBar(paginationHelper, false);
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
        TransactionHandler transactionHandler = AstralShop.getInstance().getTransactionHandler();
        ShopTransaction transaction = transactionHandler.handleClick(event, shop, currentPage, clickedSlot, shopItem);
        if (transaction != null && transaction.getPlayer() == player) {
            ShopTransactionEvent transactionEvent = new ShopTransactionEvent(transaction);
            plugin.getServer().getPluginManager().callEvent(transactionEvent);
            if (!transactionEvent.isCancelled()) {
                transactionHandler.handleTransaction(transaction);
            }
        }
    }
}