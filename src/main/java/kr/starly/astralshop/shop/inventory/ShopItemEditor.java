package kr.starly.astralshop.shop.inventory;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.libs.inventory.gui.Gui;
import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.item.builder.ItemBuilder;
import kr.starly.libs.inventory.item.impl.SimpleItem;
import kr.starly.libs.inventory.item.impl.SuppliedItem;
import kr.starly.libs.inventory.window.AnvilWindow;
import kr.starly.libs.inventory.window.Window;
import kr.starly.libs.scheduler.Do;
import org.apache.commons.collections4.ListUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

import static kr.starly.astralshop.message.MessageContext.*;

public class ShopItemEditor {

    private final AstralShop plugin = AstralShop.getInstance();

    private Player player;
    private final int page;
    private final int slot;
    private final Shop shop;
    private final ShopItem shopItem;

    private final Gui gui;
    private Window window;

    private final List<Runnable> closeHandlers;

    public ShopItemEditor(Shop shop, int page, int slot) {
        this.page = page;
        this.slot = slot;
        this.shop = shop;
        this.shopItem = shop.getShopPages().get(page - 1).getItems().get(slot);

        this.gui = createGui();
        this.window = null;

        this.closeHandlers = List.of(
                () -> Do.syncLater(1, () -> this.open(player)),
                () -> Do.async(() -> plugin.getShopRepository().saveShop(shop))
        );
    }

    private Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . 1 2 3 4 5 . #",
                        "# . a b c d e . #",
                        "# . f g h i j . #",
                        "# . k l m n o . #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', createBorder())
                .addIngredient('1', createBuyPriceBtn())
                .addIngredient('2', createSellPriceBtn())
                .addIngredient('3', createStockBtn())
                .addIngredient('4', createMarkerBtn())
                .addIngredient('5', createCommandBtn())
                .build();
    }

    private Window createWindow() {
        return Window.single()
                .setGui(gui)
                .setTitle(shop.getGuiTitle() + "§8 [%d@%d]".formatted(page, slot))
                .addCloseHandler(() -> Do.async(() -> plugin.getShopRepository().saveShop(shop)))
                .addCloseHandler(() -> new ShopItemsEditor(shop, page).open(player))
                .build(player);
    }

    public void open(Player player) {
        this.player = player;
        this.window = createWindow();
        window.open();
    }


    private Item createBorder() {
        return new SimpleItem(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(parseMessage(""))
        );
    }

    private Item createBuyPriceBtn() {
        return new SuppliedItem(
                () -> new ItemBuilder(Material.EMERALD)
                        .setDisplayName(parseMessage("<b><gold>구매가격</b>"))
                        .setLore(parseMessage(
                                INFO_PREFIX + "<white>현재 값: <aqua>" + shopItem.getBuyPrice(),
                                "",
                                CONTROL_PREFIX + "<gold>유저<white>가 구매할 때의 가격입니다.",
                                CONTROL_PREFIX + "<gold>음수<white>를 입력하면 <red>구매불가 <white>상태가 됩니다.",
                                CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 변경합니다."
                        )),
                (click) -> {
                    Gui gui = Gui.normal()
                            .setStructure("x . .")
                            .addIngredient('x', new ItemBuilder(Material.PAPER)
                                    .setDisplayName(String.valueOf(shopItem.getBuyPrice())))
                            .build();

                    window.setCloseHandlers(new ArrayList<>());
                    AnvilWindow.single()
                            .setGui(gui)
                            .setTitle("구매가격을 입력 후 닫아주세요.")
                            .addRenameHandler((text) -> {
                                try {
                                    double newValue = Double.parseDouble(text);
                                    shopItem.setBuyPrice(newValue);

                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .8f, 1f);
                                } catch (NumberFormatException ignored) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, .8f, 1f);
                                }
                            })
                            .setCloseHandlers(ListUtils.union(List.of(() -> window.setCloseHandlers(closeHandlers)), closeHandlers))
                            .open(player);
                    return false;
                }
        );
    }

    private Item createSellPriceBtn() {
        return new SuppliedItem(
                () -> new ItemBuilder(Material.EMERALD)
                        .setDisplayName(parseMessage("<b><gold>판매가격</b>"))
                        .setLore(parseMessage(
                                INFO_PREFIX + "<white>현재 값: <aqua>" + shopItem.getSellPrice(),
                                "",
                                CONTROL_PREFIX + "<gold>유저<white>가 판매할 때의 가격입니다.",
                                CONTROL_PREFIX + "<gold>음수<white>를 입력하면 <red>판매불가 <white>상태가 됩니다.",
                                CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 변경합니다."
                        )),
                (click) -> {
                    Gui gui = Gui.normal()
                            .setStructure("x . .")
                            .addIngredient('x', new ItemBuilder(Material.PAPER)
                                    .setDisplayName(String.valueOf(shopItem.getSellPrice())))
                            .build();

                    window.setCloseHandlers(new ArrayList<>());
                    AnvilWindow.single()
                            .setGui(gui)
                            .setTitle("판매가격을 입력 후 닫아주세요.")
                            .addRenameHandler((text) -> {
                                try {
                                    double newValue = Double.parseDouble(text);
                                    shopItem.setSellPrice(newValue);

                                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .8f, 1f);
                                } catch (NumberFormatException ignored) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, .8f, 1f);
                                }
                            })
                            .setCloseHandlers(ListUtils.union(List.of(() -> window.setCloseHandlers(closeHandlers)), closeHandlers))
                            .open(player);
                    return false;
                }
        );
    }

    private Item createStockBtn() {
        return new SuppliedItem(
                () -> new ItemBuilder(Material.ANVIL)
                        .setDisplayName(parseMessage("<b><gold>재고</b>"))
                        .setLore(parseMessage(
                                INFO_PREFIX + "<white>최대 값: <aqua>" + shopItem.getStock(),
                                INFO_PREFIX + "<white>현재 값: <aqua>" + shopItem.getRemainStock(),
                                "",
                                CONTROL_PREFIX + "<gold>음수<white>를 입력하면 <green>재고 무제한 <white>상태가 됩니다.",
                                CONTROL_PREFIX + "<gold>좌클릭 <white>시, 최대 값을 변경합니다.",
                                CONTROL_PREFIX + "<gold>우클릭 <white>시, 현재 값을 변경합니다.",
                                CONTROL_PREFIX + "<gold>Shift+좌클릭 <white>시, 재고를 최대로 채웁니다."
                        )),
                (click) -> {
                    if (click.getClickType() == ClickType.LEFT) {
                        Gui gui = Gui.normal()
                                .setStructure("x . .")
                                .addIngredient('x', new ItemBuilder(Material.PAPER)
                                        .setDisplayName(String.valueOf(shopItem.getSellPrice())))
                                .build();

                        window.setCloseHandlers(new ArrayList<>());
                        AnvilWindow.single()
                                .setGui(gui)
                                .setTitle("최대 재고를 입력 후 닫아주세요.")
                                .addRenameHandler((text) -> {
                                    try {
                                        int newValue = Integer.parseInt(text);
                                        shopItem.setStock(newValue);

                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .8f, 1f);
                                    } catch (NumberFormatException ignored) {
                                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, .8f, 1f);
                                    }
                                })
                                .setCloseHandlers(ListUtils.union(List.of(() -> window.setCloseHandlers(closeHandlers)), closeHandlers))
                                .open(player);
                        return false;
                    } else if (click.getClickType() == ClickType.RIGHT) {
                        Gui gui = Gui.normal()
                                .setStructure("x . .")
                                .addIngredient('x', new ItemBuilder(Material.PAPER)
                                        .setDisplayName(String.valueOf(shopItem.getSellPrice())))
                                .build();

                        window.setCloseHandlers(new ArrayList<>());
                        AnvilWindow.single()
                                .setGui(gui)
                                .setTitle("현재 재고를 입력 후 닫아주세요.")
                                .addRenameHandler((text) -> {
                                    try {
                                        int newValue = Integer.parseInt(text);
                                        shopItem.setRemainStock(newValue);

                                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, .8f, 1f);
                                    } catch (NumberFormatException ignored) {
                                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, .8f, 1f);
                                    }
                                })
                                .setCloseHandlers(ListUtils.union(List.of(() -> window.setCloseHandlers(closeHandlers)), closeHandlers))
                                .open(player);
                        return false;
                    } else if (click.getClickType() == ClickType.SHIFT_RIGHT) {
                        shopItem.setRemainStock(shopItem.getStock());
                        return true;
                    }

                    return false;
                }
        );
    }

    private Item createMarkerBtn() {
        return new SuppliedItem(
                () -> new ItemBuilder(Material.NAME_TAG)
                        .setDisplayName(parseMessage("<b><gold>마커</b>"))
                        .setLore(parseMessage(
                                INFO_PREFIX + "<white>현재 값: <aqua>" + (shopItem.isMarker() ? "마커" : "상품"),
                                "",
                                CONTROL_PREFIX + "<gold>마커 <white>아이템은 상품으로 인식되지 않습니다.",
                                CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 변경합니다."
                        )),
                (click) -> {
                    shopItem.setMarker(!shopItem.isMarker());
                    return true;
                }
        );
    }

    private Item createCommandBtn() {
        return new SuppliedItem(
                () -> new ItemBuilder(Material.COMMAND_BLOCK)
                        .setDisplayName(parseMessage("<b><gold>명령어</b>"))
                        .setLore(parseMessage(
                                CONTROL_PREFIX + "<gold>아이템 클릭 시, 명령어를 실행합니다.",
                                CONTROL_PREFIX + "<gold>좌클릭 <white>시, 편집창으로 이동합니다."
                        )),
                (click) -> {
                    window.setCloseHandlers(new ArrayList<>());
                    new ShopItemCommandsEditor(shop, page, slot).open(player);
                    return false;
                }
        );
    }
}