package kr.starly.astralshop.shop.inventory.admin.impl;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.api.registry.TransactionHandlerRegistry;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.dispatcher.EntityInteractDispatcher;
import kr.starly.astralshop.listener.EntityInteractListener;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import kr.starly.core.builder.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShopSettings extends BaseShopInventory {

    private final ShopRegistry shopRegistry = AstralShop.getInstance().getShopRegistry();

    public ShopSettings(Shop shop) {
        super(shop, shop.getName() + " [상점 관리]", 6, true);

        ItemStack item = new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"));
        setOutline(
                new ItemBuilder(item)
                        .setName("&r")
                        .build()
        );
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        ItemStack enableStatusItem = new ItemStack(Material.valueOf(shop.isEnabled() ? "GREEN_WOOL" : "RED_WOOL"));
        Material saveValuesMaterial = Material.valueOf("GOLDEN_PICKAXE");
        ItemStack npcItem = new ItemStack(Material.valueOf("VILLAGER_SPAWN_EGG"));

        inventory.setItem(20,
                new ItemBuilder(enableStatusItem)
                        .setName("&6활성화")
                        .setLore(
                                "&e&l| &f현재 값: " + (shop.isEnabled() ? "&a활성화" : "&c비활성화"),
                                "",
                                "&e&l| &c비활성화 &f시, 관리자만 상점을 열 수 있습니다.",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        )
                        .build()
        );
        inventory.setItem(21,
                new ItemBuilder(Material.SHIELD)
                        .setName("&6접근성")
                        .setLore(
                                "&e&l| &f현재 값: &b" + shop.getAccessibility().getLabel(),
                                "",
                                "&e&l| &b공개&7: &f퍼미션 없이 사용할 수 있습니다.",
                                "&e&l| &b일부공개&7: &fNPC만 퍼미션 없이 사용할 수 있습니다.",
                                "&e&l| &b비공개&7: &f퍼미션이 있어야 사용할 수 있습니다.",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        )
                        .build()
        );
        inventory.setItem(23,
                new ItemBuilder(Material.EMERALD)
                        .setName("&6거래 방식")
                        .setLore(
                                "&e&l| &f현재 값: &b" + shop.getTransactionHandler().getName(),
                                "",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        )
                        .build()
        );
        inventory.setItem(24,
                new ItemBuilder(Material.BARRIER)
                        .setName("&c상점 삭제")
                        .setLore(
                                "&e&l| &6Shift+좌클릭 &f시, 상점을 삭제합니다."
                        )
                        .build()
        );
        inventory.setItem(29,
                new ItemBuilder(Material.CHEST)
                        .setName("&6아이템 편집")
                        .setLore(
                                "&e&l| &6좌클릭 &f시, 아이템 편집기를 엽니다."
                        )
                        .build()
        );
        inventory.setItem(30,
                new ItemBuilder(npcItem)
                        .setName("&6NPC")
                        .setLore(
                                "&e&l| &f현재 값: &6" + (shop.getNpc().isEmpty() ? "&c없음" : shop.getNpc()),
                                "",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다.",
                                "&e&l| &6Shift+우클릭 &f시, 값을 초기화합니다."
                        )
                        .build()
        );
        inventory.setItem(32,
                new ItemBuilder(Material.NAME_TAG)
                        .setName("&6제목 - 기본값")
                        .setLore(
                                "&e&l| &f현재 값: &6" + shop.getGuiTitle(),
                                "",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        )
                        .build()
        );
        inventory.setItem(33,
                new ItemBuilder(Material.NAME_TAG)
                        .setName("&6제목 - 일괄변경")
                        .setLore(
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        )
                        .build()
        );
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();
        ClickType click = event.getClick();
        if (slot == 20 && click == ClickType.LEFT) {
            shop.setEnabled(!shop.isEnabled());
        } else if (slot == 21 && click == ClickType.LEFT) {
            List<ShopAccessibility> options = List.of(ShopAccessibility.values());
            int currentIndex = options.indexOf(shop.getAccessibility());
            int nextIndex = currentIndex == options.size() - 1 ? 0 : currentIndex + 1;

            shop.setAccessibility(options.get(nextIndex));
        } else if (slot == 23 && click == ClickType.LEFT) {
            TransactionHandlerRegistry transactionHandlerRegistry = plugin.getTransactionHandlerRegistry();
            List<TransactionHandler> options = new ArrayList<>(transactionHandlerRegistry.getHandlers().values());
            int currentIndex = options.indexOf(shop.getTransactionHandler());
            int nextIndex = currentIndex == options.size() - 1 ? 0 : currentIndex + 1;

            shop.setTransactionHandler(options.get(nextIndex));
        } else if (slot == 24 && click == ClickType.SHIFT_LEFT) {
            shopRegistry.deleteShop(shop.getName());

            setEventListening(false);
            player.closeInventory();
            return;
        } else if (slot == 29 && click == ClickType.LEFT) {
            setEventListening(false);
            new ShopEditor(shop).open(player);
            return;
        } else if (slot == 30 && click == ClickType.LEFT) {
            setEventListening(false);
            player.closeInventory();

            EntityInteractDispatcher.attachConsumer(player.getUniqueId(), (npc) -> {
                if (npc == null || npc.isEmpty()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                } else {
                    shop.setNpc(npc);
                    shopRegistry.saveShop(shop);
                    EntityInteractListener.fetchNPCNames();

                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
                }

                setEventListening(true);
                open(player);
            });
        } else if (slot == 30 && click == ClickType.RIGHT) {
            shop.setNpc("");
            EntityInteractListener.fetchNPCNames();
        } else if (slot == 32 && click == ClickType.LEFT) {
            setEventListening(false);
            new AnvilGUI.Builder()
                    .title("변경할 제목을 입력해주세요.")
                    .plugin(plugin)
                    .interactableSlots(AnvilGUI.Slot.OUTPUT)
                    .itemLeft(
                            new ItemBuilder(Material.PAPER)
                                    .setName("&r")
                                    .build()
                    )
                    .onClick((clickedSlot, stateSnapshot) -> {
                        if (clickedSlot != AnvilGUI.Slot.OUTPUT) return new ArrayList<>();

                        String newTitle = ChatColor.translateAlternateColorCodes('&', stateSnapshot.getText());
                        shop.setGuiTitle(newTitle);

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .onClose((stateSnapshot) -> {
                        setEventListening(true);
                        open(player);
                    })
                    .open(player);
            return;
        } else if (slot == 33 && click == ClickType.LEFT) {
            setEventListening(false);
            new AnvilGUI.Builder()
                    .title("변경할 제목을 입력해주세요.")
                    .plugin(plugin)
                    .interactableSlots(AnvilGUI.Slot.OUTPUT)
                    .itemLeft(
                            new ItemBuilder(Material.PAPER)
                                    .setName("&r")
                                    .build()
                    )
                    .onClick((clickedSlot, stateSnapshot) -> {
                        if (clickedSlot != AnvilGUI.Slot.OUTPUT) return new ArrayList<>();

                        String newTitle = ChatColor.translateAlternateColorCodes('&', stateSnapshot.getText());
                        shop.getShopPages().forEach(page -> page.setGuiTitle(newTitle));

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .onClose((stateSnapshot) -> {
                        setEventListening(true);
                        open(player);
                    })
                    .open(player);
            return;
        } else return;

        saveShop();
        updateInventory(player);
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
        shopRegistry.saveShop(shop);
    }
}