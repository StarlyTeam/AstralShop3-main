package kr.starly.astralshop.shop.inventory.admin.impl;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.ItemAttributeProvider;
import kr.starly.astralshop.api.registry.ItemAttributeRegistry;
import kr.starly.astralshop.api.registry.ShopRepository;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import kr.starly.libs.inventory.item.builder.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
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

public class ShopItemEditor extends BaseShopInventory {

    private final ShopRepository shopRepository = AstralShop.getInstance().getShopRepository();
    private final ItemAttributeRegistry itemAttributeRegistry = AstralShop.getInstance().getItemAttributeRegistry();

    private final int page;
    private final int slot;
    private ShopPage pageData;
    private ShopItem itemData;

    public ShopItemEditor(Shop shop, int page, int slot) {
        super(shop, "%s [%d@%d]".formatted(shop.getName(), page, slot), 3, true);

        ItemStack item = new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"));
        setOutline(
                new ItemBuilder(item)
                        .setDisplayName("&r")
                        .get()
        );

        this.page = page;
        this.slot = slot;
        this.pageData = shop.getShopPages().get(page - 1);
        this.itemData = pageData.getItems().get(slot);
    }

    @Override
    protected void createInventory(Player player) {
        this.rows = 3;

        int providersSize = itemAttributeRegistry.getProviders().size();
        if (providersSize > 0) {
            this.rows += (int) Math.ceil(providersSize / 5.0D);
        }

        super.createInventory(player);
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        Material commandsMaterial = Material.valueOf("REPEATING_COMMAND_BLOCK");
        Material hideLoreMaterial = Material.valueOf("OAK_SIGN");

        List<String> commandsLore = new ArrayList<>();
        itemData.getCommands().forEach((commandLine) -> commandsLore.add("&b&l| &f" + commandLine));
        if (commandsLore.isEmpty()) commandsLore.add("&b&l| &c없음");
        commandsLore.add("");
        commandsLore.add("&e&l| &6좌클릭 &f시, 명령어 편집기를 엽니다.");

        inventory.setItem(11,
                new ItemBuilder(Material.EMERALD)
                        .setDisplayName("&6구매 가격")
                        .setLegacyLore(List.of(
                                "&e&l| &f현재 값: &6" + itemData.getBuyPrice(),
                                "",
                                "&e&l| &b유저&f가 구매할 때의 가격입니다.",
                                "&e&l| &b음수&f를 입력하면 &c구매불가 &f상태가 됩니다.",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        ))
                        .get()
        );
        inventory.setItem(12,
                new ItemBuilder(Material.EMERALD)
                        .setDisplayName("&6판매 가격")
                        .setLegacyLore(List.of(
                                "&e&l| &f현재 값: &6" + itemData.getSellPrice(),
                                "",
                                "&e&l| &b유저&f가 판매할 때의 가격입니다.",
                                "&e&l| &b음수&f를 입력하면 &c판매불가 &f상태가 됩니다.",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        ))
                        .get()
        );
        inventory.setItem(13,
                new ItemBuilder(Material.ANVIL)
                        .setDisplayName("&6재고")
                        .setLegacyLore(List.of(
                                "&e&l| &f최대 값: &6" + itemData.getStock(),
                                "&e&l| &f현재 값: &6" + itemData.getRemainStock(),
                                "",
                                "&e&l| &b음수&f를 입력하면 &a무제한 &f상태가 됩니다.",
                                "&e&l| &6좌클릭 &f시, 최대 값을 변경합니다.",
                                "&e&l| &6우클릭 &f시, 현재 값을 변경합니다.",
                                "&e&l| &6Shift+좌클릭 &f시, 재고를 최대로 채웁니다."
                        ))
                        .get()
        );
        inventory.setItem(14,
                new ItemBuilder(hideLoreMaterial)
                        .setDisplayName("&6로어")
                        .setLegacyLore(List.of(
                                "&e&l| &f현재 값: " + (itemData.isHideLore() ? "&c숨김" : "&a표시"),
                                "",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        ))
                        .get()
        );
        inventory.setItem(15,
                new ItemBuilder(commandsMaterial)
                        .setDisplayName("&6명령어")
                        .setLegacyLore(commandsLore)
                        .get()
        );

        int[] attributeIconIndexes = new int[]{
                20, 21, 22, 23, 24,
                29, 30, 31, 32, 33,
                38, 39, 40, 41, 42
        };
        List<ItemAttributeProvider> attributeProviders = new ArrayList<>(itemAttributeRegistry.getProviders().values());
        for (int i = 0; i < attributeProviders.size(); i++) {
            if (i > 14) return;

            int iconIndex = attributeIconIndexes[i];
            inventory.setItem(iconIndex, attributeProviders.get(i).getIcon(player));
        }
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();
        ClickType click = event.getClick();
        if (slot == 11 && click == ClickType.LEFT) {
            setEventListening(false);
            new AnvilGUI.Builder()
                    .title("변경할 값을 입력해주세요.")
                    .plugin(plugin)
                    .interactableSlots(AnvilGUI.Slot.OUTPUT)
                    .itemLeft(
                            new ItemBuilder(Material.PAPER)
                                    .setDisplayName("&r")
                                    .get()
                    )
                    .onClick((clickedSlot, stateSnapshot) -> {
                        if (clickedSlot != AnvilGUI.Slot.OUTPUT) return new ArrayList<>();

                        try {
                            int newValue = Integer.parseInt(stateSnapshot.getText());

                            itemData.setBuyPrice(newValue);
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
                        } catch (NumberFormatException ignored) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        }

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .onClose((stateSnapshot) -> {
                        setEventListening(true);
                        open(player);
                    })
                    .open(player);
            return;
        } else if (slot == 12 && click == ClickType.LEFT) {
            setEventListening(false);
            new AnvilGUI.Builder()
                    .title("변경할 값을 입력해주세요.")
                    .plugin(plugin)
                    .interactableSlots(AnvilGUI.Slot.OUTPUT)
                    .itemLeft(
                            new ItemBuilder(Material.PAPER)
                                    .setDisplayName("&r")
                                    .get()
                    )
                    .onClick((clickedSlot, stateSnapshot) -> {
                        if (clickedSlot != AnvilGUI.Slot.OUTPUT) return new ArrayList<>();

                        try {
                            int newValue = Integer.parseInt(stateSnapshot.getText());

                            itemData.setSellPrice(newValue);
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
                        } catch (NumberFormatException ignored) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        }

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .onClose((stateSnapshot) -> {
                        setEventListening(true);
                        open(player);
                    })
                    .open(player);
            return;
        } else if (slot == 13 && click == ClickType.LEFT) {
            setEventListening(false);
            new AnvilGUI.Builder()
                    .title("변경할 값을 입력해주세요.")
                    .plugin(plugin)
                    .interactableSlots(AnvilGUI.Slot.OUTPUT)
                    .itemLeft(
                            new ItemBuilder(Material.PAPER)
                                    .setDisplayName("&r")
                                    .get()
                    )
                    .onClick((clickedSlot, stateSnapshot) -> {
                        if (clickedSlot != AnvilGUI.Slot.OUTPUT) return new ArrayList<>();

                        try {
                            int newValue = Integer.parseInt(stateSnapshot.getText());

                            itemData.setStock(newValue);
                            if (itemData.getRemainStock() > newValue) {
                                itemData.setRemainStock(newValue);
                            }

                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
                        } catch (NumberFormatException ignored) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        }

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .onClose((stateSnapshot) -> {
                        setEventListening(true);
                        open(player);
                    })
                    .open(player);
            return;
        } else if (slot == 13 && click == ClickType.RIGHT) {
            setEventListening(false);
            new AnvilGUI.Builder()
                    .title("변경할 값을 입력해주세요.")
                    .plugin(plugin)
                    .interactableSlots(AnvilGUI.Slot.OUTPUT)
                    .itemLeft(
                            new ItemBuilder(Material.PAPER)
                                    .setDisplayName("&r")
                                    .get()
                    )
                    .onClick((clickedSlot, stateSnapshot) -> {
                        if (clickedSlot != AnvilGUI.Slot.OUTPUT) return new ArrayList<>();

                        try {
                            int newValue = Integer.parseInt(stateSnapshot.getText());
                            if (newValue < 1) {
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                            } else {
                                itemData.setRemainStock(newValue);
                                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 1f);
                            }
                        } catch (NumberFormatException ignored) {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                        }

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .onClose((stateSnapshot) -> {
                        setEventListening(true);
                        open(player);
                    })
                    .open(player);
            return;
        } else if (slot == 13 && click == ClickType.SHIFT_LEFT) {
            itemData.setRemainStock(itemData.getStock());
        } else if (slot == 14 && click == ClickType.LEFT) {
            itemData.setHideLore(!itemData.isHideLore());
        } else if (slot == 15 && click == ClickType.LEFT) {
            setEventListening(false);
            new ShopItemCommandsEditor(shop, page, this.slot).open(player);
            return;
        } else if (slot <= (rows * 9) - 11 && rows > 3) {
            List<Integer> attributeIconIndexes = List.of(
                    20, 21, 22, 23, 24,
                    29, 30, 31, 32, 33,
                    38, 39, 40, 41, 42
            );
            List<ItemAttributeProvider> providers = new ArrayList<>(itemAttributeRegistry.getProviders().values());
            ItemAttributeProvider provider = providers.get(attributeIconIndexes.indexOf(slot));

            boolean listenEvent = isEventListening();
            try {
                setEventListening(false);

                provider.onIconClick(event);
            } finally {
                setEventListening(listenEvent);
            }
            return;
        }

        saveShop();
        updateInventory(player);
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
        shopRepository.saveShop(shop);

        setEventListening(false);
        ShopEditor shopEditor = new ShopEditor(shop);
        shopEditor.getPaginationManager().setCurrentPage(page);
        shopEditor.open((Player) event.getPlayer());
    }

    @Override
    public void updateData() {
        super.updateData();
        this.pageData = shop.getShopPages().get(page - 1);
        this.itemData = pageData.getItems().get(slot);
    }
}