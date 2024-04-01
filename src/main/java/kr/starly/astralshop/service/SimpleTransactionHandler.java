package kr.starly.astralshop.service;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopTransaction;
import kr.starly.astralshop.message.MessageType;
import kr.starly.astralshop.shop.ShopTransactionImpl;
import kr.starly.astralshop.api.shop.ShopTransactionType;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.core.builder.ItemBuilder;
import kr.starly.core.util.ItemStackNameUtil;
import kr.starly.core.util.Language;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleTransactionHandler implements TransactionHandler {

    private final AstralShop plugin;
    private final MessageContext messageContext;
    private Economy economy;

    public SimpleTransactionHandler() {
        plugin = AstralShop.getInstance();
        messageContext = MessageContext.getInstance();

        if (!setupEconomy()) {
            plugin.getLogger().severe("의존성 플러그인(Vault)을 발견하지 못했습니다.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    @Override
    public ShopTransaction handleClick(InventoryClickEvent event, Shop shop, int page, int slot, ShopItem item) {
        MessageContext messageContext = MessageContext.getInstance();
        Player player = (Player) event.getWhoClicked();
        Date date = new Date();

        return switch (event.getClick()) {
            case LEFT, SHIFT_LEFT -> {
                if (item.getBuyPrice() < 0) {
                    if (!item.isHideLore()) {
                        messageContext.get(MessageType.ERROR, "handler-cannotBuy").send(player);
                    }

                    yield null;
                }

                yield new ShopTransactionImpl(player, ShopTransactionType.BUY, date, shop, page, slot, item, event.getClick().isShiftClick() ? 64 : 1, -1);
            }

            case RIGHT, SHIFT_RIGHT -> {
                if (item.getSellPrice() < 0) {
                    if (!item.isHideLore()) {
                        messageContext.get(MessageType.ERROR, "handler-cannotSell").send(player);
                    }

                    yield null;
                }

                yield new ShopTransactionImpl(player, ShopTransactionType.SELL, date, shop, page, slot, item, event.getClick().isShiftClick() ? 64 : 1, -1);
            }

            default -> null;
        };
    }

    @Override
    public void handleTransaction(ShopTransaction transaction) {
        Player player = transaction.getPlayer();
        ShopTransactionType type = transaction.getType();
        ShopItem itemData = transaction.getItem();
        int amount = transaction.getAmount();

        if (player == null) return;
        if (type == null) return;
        if (itemData == null) return;
        if (amount < 0) return;

        ItemStack itemStack = itemData.getItemStack();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        if (type == ShopTransactionType.BUY) {
            boolean isFull = false, isStockOut = false;

            int totalBought = 0;
            for (int i = 0; i < amount; i++) {
                if (economy.getBalance(player) < (totalBought + itemStack.getAmount()) * itemData.getBuyPrice()) {
                    break;
                } else if (itemData.getRemainStock() >= 0) {
                    if (itemData.getRemainStock() == 0) {
                        isStockOut = true;
                        break;
                    }

                    itemData.setRemainStock(itemData.getRemainStock() - itemStack.getAmount());
                } else if (!player.getInventory().addItem(itemStack).isEmpty()) {
                    isFull = true;
                    break;
                } else {
                    totalBought++;
                }
            }

            double finalPrice = totalBought * itemData.getBuyPrice();
            economy.withdrawPlayer(player, finalPrice);

            if (totalBought == amount) {
                messageContext.get(MessageType.NORMAL, "handler-itemBought1", (msg) -> msg
                        .replace("{name}", ItemStackNameUtil.getNameInLanguage(itemStack, Language.KOREAN))
                        .replace("{price}", "%.2f".formatted(finalPrice))
                        .replace("{amount}", String.valueOf(amount))
                ).send(player);
            } else if (totalBought == 0) {
                if (isStockOut) messageContext.get(MessageType.ERROR, "handler-failedToBuy1").send(player);
                else if (isFull) messageContext.get(MessageType.ERROR, "handler-failedToBuy2").send(player);
                else messageContext.get(MessageType.ERROR, "handler-failedToBuy3").send(player);
            } else {
                String key;
                if (isStockOut) key = "handler-itemBought2";
                else if (isFull) key = "handler-itemBought3";
                else key = "handler-itemBought4";

                messageContext.get(MessageType.NORMAL, key, (msg) -> msg
                        .replace("{name}", ItemStackNameUtil.getNameInLanguage(itemStack, Language.KOREAN))
                        .replace("{price}", "%.2f".formatted(finalPrice))
                        .replace("{amount}", String.valueOf(amount))
                ).send(player);
            }
        } else if (type == ShopTransactionType.SELL) {
//            if (itemStack.getAmount() != 1) {
//                plugin.getLogger().warning("itemStack.amount must be 1 for sell.");
//
//                player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
//                return;
//            }

            AtomicBoolean isStockFull = new AtomicBoolean(false);

            AtomicInteger totalSold = new AtomicInteger();
            player.getInventory().all(itemStack).forEach((slot, itemStack1) -> {
                int
                        v1 = itemStack1.getAmount(),
                        v2 = amount - totalSold.get(),
                        v3 = itemData.getStock() - itemData.getRemainStock();
                int toRemove = Math.min(v1, v2);
                if (itemData.getRemainStock() >= 0) {
                    toRemove = Math.min(toRemove, v3);

                    if (toRemove != v1 && toRemove != v2) { // toRemove = v3;
                        isStockFull.set(true);
                    }
                }

                itemStack1.setAmount(itemStack1.getAmount() - toRemove);
                totalSold.addAndGet(toRemove);
            });

            double finalPrice = totalSold.get() * itemData.getSellPrice();
            economy.depositPlayer(player, finalPrice);

            if (totalSold.get() == amount) {
                messageContext.get(MessageType.NORMAL, "handler-itemSold1", (msg) -> msg
                        .replace("{name}", ItemStackNameUtil.getNameInLanguage(itemStack, Language.KOREAN))
                        .replace("{price}", "%.2f".formatted(finalPrice))
                        .replace("{amount}", String.valueOf(amount))
                ).send(player);
            } else if (totalSold.get() == 0) {
                if (isStockFull.get()) messageContext.get(MessageType.ERROR, "handler-failedToSell1").send(player);
                else messageContext.get(MessageType.ERROR, "handler-failedToSell2").send(player);
            } else {
                String key;
                if (isStockFull.get()) key = "handler-itemSold2";
                else key = "handler-itemSold3";

                messageContext.get(MessageType.NORMAL, key, (msg) -> msg
                        .replace("{name}", ItemStackNameUtil.getNameInLanguage(itemStack, Language.KOREAN))
                        .replace("{price}", "%.2f".formatted(finalPrice))
                        .replace("{amount}", String.valueOf(amount))
                ).send(player);
            }
        }
    }

    @Override
    public ItemStack toItemStack(ShopItem item) {
        if (item == null) return null;
        else if (item.getItemStack() == null || item.getItemStack().getType() == Material.AIR) return null;

        double buyPrice = item.getBuyPrice();
        double sellPrice = item.getSellPrice();
        int stock = item.getStock();
        int remainStock = item.getRemainStock();
        return new ItemBuilder(item.getItemStack().clone())
                .setLore(item.isHideLore() ? new ArrayList<>() : List.of(
                        "&e&l| &f구매가격: " + (buyPrice == 0 ? "&b무료" : (buyPrice < 0 ?"&c구매불가" : "&6" + buyPrice)),
                        "&e&l| &f판매가격: " + (sellPrice == 0 ? "&b무료" : (sellPrice < 0 ? "&c판매불가" : "&6" + sellPrice)),
                        "&e&l| &f재고: " + (remainStock < 0 ? "&b무제한" : "&6" + remainStock + "&7/&6" + stock),
                        "&r",
                        "&e&l| &6좌클릭 &f시, 아이템 &61&f개를 구매합니다.",
                        "&e&l| &6Shift+좌클릭 &f시, 아이템 &664&f개를 구매합니다.",
                        "&e&l| &6우클릭 &f시, 아이템 &61&f개를 판매합니다.",
                        "&e&l| &6Shift+우클릭 &f시, 아이템 &664&f개를 판매합니다."
                ))
                .build();
    }

    private boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return true;
    }
}