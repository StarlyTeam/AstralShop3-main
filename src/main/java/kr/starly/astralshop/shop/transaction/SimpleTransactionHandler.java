package kr.starly.astralshop.shop.transaction;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopTransaction;
import kr.starly.astralshop.api.shop.ShopTransactionType;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import kr.starly.astralshop.shop.ShopTransactionImpl;
import kr.starly.libs.inventory.item.Click;
import kr.starly.libs.inventory.item.builder.ItemBuilder;
import kr.starly.libs.nms.NmsMultiVersion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static kr.starly.astralshop.message.MessageContext.*;

public class SimpleTransactionHandler implements TransactionHandler {

    private final MessageContext messageContext;
    private final AstralShop plugin;

    private Economy economy;

    public SimpleTransactionHandler() {
        this.messageContext = MessageContext.getInstance();
        this.plugin = AstralShop.getInstance();

        if (!setupEconomy()) {
            plugin.getLogger().severe("의존성 플러그인(Vault, Economy)을 발견하지 못했습니다.");
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    @Override
    public String getName() {
        return "기본";
    }

    @Override
    public ShopTransaction handleClick(Click click, Shop shop, int page, int slot, ShopItem item) {
        MessageContext messageContext = MessageContext.getInstance();
        Player player = click.getPlayer();
        Date date = new Date();

        return switch (click.getClickType()) {
            case LEFT, SHIFT_LEFT -> {
                if (item.getBuyPrice() < 0) {
                    if (!item.isMarker()) {
                        messageContext.get(MessageType.SIMPLE_HANDLER, "cannotBuy").send(player);
                    }

                    yield null;
                }

                yield new ShopTransactionImpl(player, ShopTransactionType.BUY, date, shop, page, slot, item, click.getClickType().isShiftClick() ? 64 : 1);
            }

            case RIGHT, SHIFT_RIGHT -> {
                if (item.getSellPrice() < 0) {
                    if (!item.isMarker()) {
                        messageContext.get(MessageType.SIMPLE_HANDLER, "cannotSell").send(player);
                    }

                    yield null;
                }

                yield new ShopTransactionImpl(player, ShopTransactionType.SELL, date, shop, page, slot, item, click.getClickType().isShiftClick() ? 64 : 1);
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
        if (amount <= 0) return;

        ItemStack itemStack = itemData.getItemStack();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        switch (type) {
            case BUY -> {
                if (itemStack.getAmount() != 1) {
                    plugin.getLogger().warning("itemStack.amount must be 1 to buy.");

                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                    return;
                }

                boolean isFull = false, isStockOut = false;
                AtomicInteger totalBought = new AtomicInteger();

                int toAdd = Math.min(amount, (int) (economy.getBalance(player) / itemData.getBuyPrice()));
                if (toAdd != 0) {
                    if (itemData.getRemainStock() >= 0) {
                        int toAdd1 = Math.min(toAdd, itemData.getRemainStock());
                        if (toAdd != toAdd1) {
                            toAdd = toAdd1;
                            isStockOut = true;
                        }
                    }

                    for (int i = 0; i < toAdd; i++) {
                        if (!player.getInventory().addItem(itemStack).isEmpty()) {
                            isFull = true;
                            break;
                        } else {
                            if (itemData.getRemainStock() >= 0) {
                                itemData.setRemainStock(itemData.getRemainStock() - 1);
                            }

                            totalBought.addAndGet(1);
                        }
                    }
                }

                double finalPrice = totalBought.get() * itemData.getBuyPrice();
                economy.withdrawPlayer(player, finalPrice);

                if (totalBought.get() == amount) {
                    messageContext.get(MessageType.SIMPLE_HANDLER, "itemBought1", TagResolver.builder()
                            .tag("name", Tag.inserting(Component.text(
                                    NmsMultiVersion.getItemTranslator().translateItemName(itemStack, Locale.KOREA))))
                            .tag("price", Tag.inserting(Component.text(
                                    finalPrice
                            )))
                            .tag("amount", Tag.inserting(Component.text(
                                    totalBought.get()
                            )))
                            .build()
                    ).send(player);
                } else if (totalBought.get() == 0) {
                    if (isStockOut) messageContext.get(MessageType.SIMPLE_HANDLER, "failedToBuy1").send(player);
                    else if (isFull) messageContext.get(MessageType.SIMPLE_HANDLER, "failedToBuy2").send(player);
                    else messageContext.get(MessageType.SIMPLE_HANDLER, "failedToBuy3").send(player);
                } else {
                    String key;
                    if (isStockOut) key = "itemBought2";
                    else if (isFull) key = "itemBought3";
                    else key = "itemBought4";

                    messageContext.get(MessageType.SIMPLE_HANDLER, key, TagResolver.builder()
                            .tag("name", Tag.inserting(Component.text(
                                    NmsMultiVersion.getItemTranslator().translateItemName(itemStack, Locale.KOREA))))
                            .tag("price", Tag.inserting(Component.text(
                                    finalPrice
                            )))
                            .tag("amount", Tag.inserting(Component.text(
                                    totalBought.get()
                            )))
                            .build()
                    ).send(player);
                }
            }

            case SELL -> {
                if (itemStack.getAmount() != 1) {
                    plugin.getLogger().warning("itemStack.amount must be 1 to sell.");

                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1f, 1f);
                    return;
                }

                AtomicBoolean isStockFull = new AtomicBoolean(false);
                AtomicInteger totalSold = new AtomicInteger();
                player.getInventory().all(itemStack.getType()).forEach((slot, itemStack1) -> {
                    if (!itemStack1.isSimilar(itemStack)) return;
                    if (isStockFull.get()) return;

                    int toRemove = Math.min(itemStack1.getAmount(), amount - totalSold.get());
                    if (itemData.getRemainStock() >= 0) {
                        int toRemove1 = Math.min(toRemove, itemData.getStock() - itemData.getRemainStock());
                        if (toRemove != toRemove1) {
                            toRemove = toRemove1;
                            isStockFull.set(true);
                        }

                        itemData.setRemainStock(itemData.getRemainStock() + toRemove);
                    }

                    itemStack1.setAmount(itemStack1.getAmount() - toRemove);
                    totalSold.addAndGet(toRemove);
                });

                double finalPrice = totalSold.get() * itemData.getSellPrice();
                economy.depositPlayer(player, finalPrice);

                if (totalSold.get() == amount) {
                    messageContext.get(MessageType.SIMPLE_HANDLER, "itemSold1", TagResolver.builder()
                            .tag("name", Tag.inserting(Component.text(
                                    NmsMultiVersion.getItemTranslator().translateItemName(itemStack, Locale.KOREA))))
                            .tag("price", Tag.inserting(Component.text(
                                    finalPrice
                            )))
                            .tag("amount", Tag.inserting(Component.text(
                                    totalSold.get()
                            )))
                            .build()
                    ).send(player);
                } else if (totalSold.get() == 0) {
                    if (isStockFull.get()) {
                        messageContext.get(MessageType.SIMPLE_HANDLER, "failedToSell1").send(player);
                    } else {
                        messageContext.get(MessageType.SIMPLE_HANDLER, "failedToSell2").send(player);
                    }
                } else {
                    String key;
                    if (isStockFull.get()) key = "itemSold2";
                    else key = "itemSold3";

                    messageContext.get(MessageType.SIMPLE_HANDLER, key, TagResolver.builder()
                            .tag("name", Tag.inserting(Component.text(
                                    NmsMultiVersion.getItemTranslator().translateItemName(itemStack, Locale.KOREA))))
                            .tag("price", Tag.inserting(Component.text(
                                    finalPrice
                            )))
                            .tag("amount", Tag.inserting(Component.text(
                                    totalSold.get()
                            )))
                            .build()
                    ).send(player);
                }
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
                .setLore(item.isMarker() ? new ArrayList<>() : parseMessage(
                        INFO_PREFIX + "<white>구매가격: " + (buyPrice == 0 ? "<aqua>무료" : (buyPrice < 0 ? "<red>구매불가" : "<gold>" + buyPrice)),
                        INFO_PREFIX + "<white>판매가격: " + (sellPrice == 0 ? "<aqua>무료" : (sellPrice < 0 ? "<red>판매불가" : "<gold>" + sellPrice)),
                        INFO_PREFIX + "<white>재고: " + (remainStock < 0 ? "<aqua>무제한" : "<gold>" + remainStock + "<gray>/<gold>" + stock),
                        "",
                        CONTROL_PREFIX + "<gold>좌클릭 <white>시, 아이템 <gold>1<white>개를 구매합니다.",
                        CONTROL_PREFIX + "<gold>Shift+좌클릭 <white>시, 아이템 <gold>64<white>개를 구매합니다.",
                        CONTROL_PREFIX + "<gold>우클릭 <white>시, 아이템 <gold>1<white>개를 판매합니다.",
                        CONTROL_PREFIX + "<gold>Shift+우클릭 <white>시, 아이템 <gold>64<white>개를 판매합니다."
                ))
                .get();
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