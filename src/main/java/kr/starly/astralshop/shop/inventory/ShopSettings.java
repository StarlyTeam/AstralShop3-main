package kr.starly.astralshop.shop.inventory;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.addon.TransactionHandler;
import kr.starly.astralshop.api.registry.TransactionHandlerRegistry;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import kr.starly.libs.inventory.gui.Gui;
import kr.starly.libs.inventory.item.Item;
import kr.starly.libs.inventory.item.builder.ItemBuilder;
import kr.starly.libs.inventory.item.impl.SimpleItem;
import kr.starly.libs.inventory.item.impl.SuppliedItem;
import kr.starly.libs.inventory.window.AnvilWindow;
import kr.starly.libs.inventory.window.Window;
import kr.starly.libs.scheduler.Do;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kr.starly.astralshop.message.MessageContext.*;

public class ShopSettings {

    private static final Map<Shop, Gui> guiMap = new HashMap<>();
    private final AstralShop plugin = AstralShop.getInstance();
    private final MessageContext messageContext = MessageContext.getInstance();

    private final Player player;
    private final Shop shop;

    private final Gui gui;
    private Window window;

    public ShopSettings(Player player, Shop shop) {
        this.player = player;
        this.shop = shop;

        this.gui = guiMap.computeIfAbsent(shop, (k) -> createGui());
        this.window = null;
    }

    private Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . 1 2 . 3 4 . #",
                        "# . 5 6 . 7 8 . #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', createBorder())
                .addIngredient('1', createEnableBtn())
                .addIngredient('2', createAccessibilityBtn())
                .addIngredient('3', createTransactionHandlerBtn())
                .addIngredient('4', createDeleteBtn())
                .addIngredient('5', createItemsBtn())
                .addIngredient('6', createNpcBtn())
                .addIngredient('7', createGuiTitleBtn())
                .addIngredient('8', createRowsBtn())
                .build();
    }

    private Window createWindow() {
        return Window.single()
                .setGui(gui)
                .setTitle(shop.getGuiTitle() + "§8 [상점 관리]")
                .addCloseHandler(() -> Do.async(() -> plugin.getShopRepository().saveShop(shop)))
                .build(player);
    }

    public void open() {
        this.window = createWindow();
        window.open();
    }


    private Item createBorder() {
        return new SimpleItem(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(parseMessage(""))
        );
    }

    private Item createEnableBtn() {
        return new SuppliedItem(
                () -> {
                    boolean enabled = shop.isEnabled();
                    String lore1 = enabled ? "활성화" : "비활성화";
                    String lore2 = enabled
                            ? "접근성이 허용하는 범위 내에서 누구나 상점을 열 수 있습니다."
                            : "관리자(OP)만 상점을 열 수 있습니다.";

                    return new ItemBuilder(enabled ? Material.GREEN_WOOL : Material.RED_WOOL)
                            .setDisplayName(parseMessage("<b><gold>활성화</b>"))
                            .setLore(parseMessage(
                                    INFO_PREFIX + "<white>현재 값: <aqua>" + lore1,
                                    INFO_PREFIX + "<red>" + lore2,
                                    "",
                                    CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 변경합니다."
                            ));
                },
                (click) -> {
                    shop.setEnabled(!shop.isEnabled());
                    return true;
                }
        );
    }

    private Item createAccessibilityBtn() {
        return new SuppliedItem(
                () -> {
                    ShopAccessibility accessibility = shop.getAccessibility();

                    return new ItemBuilder(Material.SHIELD)
                            .setDisplayName(parseMessage("<b><gold>접근성</b>"))
                            .setLore(parseMessage(
                                    INFO_PREFIX + "<white>현재 값: <aqua>" + accessibility.getLabel(),
                                    INFO_PREFIX + "<red>" + accessibility.getDescription(),
                                    "",
                                    CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 변경합니다."
                            ));
                },
                (click) -> {
                    List<ShopAccessibility> options = List.of(ShopAccessibility.values());
                    int currentIndex = options.indexOf(shop.getAccessibility());
                    int nextIndex = currentIndex == options.size() - 1 ? 0 : currentIndex + 1;

                    shop.setAccessibility(options.get(nextIndex));
                    return true;
                }
        );
    }

    private Item createTransactionHandlerBtn() {
        return new SuppliedItem(
                () -> {
                    TransactionHandler transactionHandler = shop.getTransactionHandler();

                    return new ItemBuilder(Material.EMERALD)
                            .setDisplayName(parseMessage("<b><gold>거래 방식</b>"))
                            .setLore(parseMessage(
                                    INFO_PREFIX + "<white>현재 값: <aqua>" + transactionHandler.getName(),
                                    "",
                                    CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 변경합니다."
                            ));
                },
                (click) -> {
                    TransactionHandlerRegistry transactionHandlerRegistry = plugin.getTransactionHandlerRegistry();
                    List<TransactionHandler> options = new ArrayList<>(transactionHandlerRegistry.getHandlers().values());
                    int currentIndex = options.indexOf(shop.getTransactionHandler());
                    int nextIndex = currentIndex == options.size() - 1 ? 0 : currentIndex + 1;

                    shop.setTransactionHandler(options.get(nextIndex));
                    return true;
                }
        );
    }

    private Item createDeleteBtn() {
        return new SimpleItem(new ItemBuilder(Material.BARRIER)
                .setDisplayName(parseMessage("<red><b>상점 삭제"))
                .setLore(parseMessage(
                        INFO_PREFIX + "<red>삭제된 상점은 복구할 수 없습니다.",
                        "",
                        CONTROL_PREFIX + "<gold>Shift+좌클릭 <white>시, 상점을 삭제합니다."
                )),
                (click) -> {
                    window.close();

                    plugin.getShopRepository().deleteShop(shop.getName());
                }
        );
    }

    private Item createItemsBtn() {
        return new SuppliedItem(
                () -> new ItemBuilder(Material.CHEST)
                        .setDisplayName(parseMessage("<b><gold>상품</b>"))
                        .setLore(parseMessage(
                                INFO_PREFIX + "<white>상품 편집기를 엽니다.",
                                "",
                                CONTROL_PREFIX + "<white>상품을 <gold>Shift+우클릭 <white>시, 설정창을 엽니다."
                        )),
                (click) -> {
                    new ShopItemsEditor(shop).open(player);
                    return false;
                }
        );
    }

    private Item createNpcBtn() {
        return new SimpleItem(new ItemBuilder(Material.VILLAGER_SPAWN_EGG)
                .setDisplayName(parseMessage("<b><gold>상점 NPC</b>"))
                .setLore(parseMessage(
                        CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 설정합니다.",
                        CONTROL_PREFIX + "<gold>우클릭 <white>시, 값을 초기화합니다."
                )),
                (click) -> {
                    if (click.getClickType() == ClickType.LEFT) {
                        window.close();

                        Player player = click.getPlayer();
                        Listener listener = new Listener() {
                            @EventHandler(priority = EventPriority.LOWEST)
                            public void onInteractAtEntity(NPCRightClickEvent event) {
                                if (event.getClicker() != player) return;
                                if (!event.getNPC().isSpawned()) return;
                                event.setCancelled(true);

                                NPC npc = event.getNPC();
                                npc.data().setPersistent(plugin.getName(), shop.getName());

                                messageContext.get(MessageType.NORMAL, "npcSet").send(player);
                                unregister();
                            }

                            @EventHandler(priority = EventPriority.LOWEST)
                            public void onInteract(PlayerInteractEvent event) {
                                if (event.getPlayer() != player) return;
                                if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR)
                                    return;
                                event.setCancelled(true);

                                unregister();
                            }

                            @EventHandler(priority = EventPriority.LOWEST)
                            public void onQuit(PlayerQuitEvent event) {
                                if (event.getPlayer() == player) unregister();
                            }

                            private void unregister() {
                                window.open();
                                HandlerList.unregisterAll(this);
                            }
                        };
                        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

                        messageContext.get(MessageType.NORMAL, "setNpc").send(player);
                    } else if (click.getClickType() == ClickType.RIGHT) {
                        window.close();

                        Player player = click.getPlayer();
                        Listener listener = new Listener() {
                            @EventHandler(priority = EventPriority.LOWEST)
                            public void onInteractAtEntity(NPCRightClickEvent event) {
                                if (event.getClicker() != player) return;
                                if (!event.getNPC().isSpawned()) return;
                                event.setCancelled(true);

                                NPC npc = event.getNPC();
                                npc.data().remove(plugin.getName());

                                messageContext.get(MessageType.NORMAL, "npcRemove").send(player);
                                unregister();
                            }

                            @EventHandler(priority = EventPriority.LOWEST)
                            public void onInteract(PlayerInteractEvent event) {
                                if (event.getPlayer() != player) return;
                                if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_AIR)
                                    return;
                                event.setCancelled(true);

                                unregister();
                            }

                            @EventHandler(priority = EventPriority.LOWEST)
                            public void onQuit(PlayerQuitEvent event) {
                                if (event.getPlayer() == player) unregister();
                            }

                            private void unregister() {
                                window.open();
                                HandlerList.unregisterAll(this);
                            }
                        };
                        plugin.getServer().getPluginManager().registerEvents(listener, plugin);

                        messageContext.get(MessageType.NORMAL, "removeNpc").send(player);
                    }
                }
        );
    }

    private Item createGuiTitleBtn() {
        return new SimpleItem(new ItemBuilder(Material.NAME_TAG)
                .setDisplayName(parseMessage("<b><gold>GUI 제목</b>"))
                .setLore(parseMessage(
                        INFO_PREFIX + "<white>현재 값: " + shop.getGuiTitle().replace('§', '&'),
                        "",
                        CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 변경합니다."
                )),
                (click) -> {
                    Gui gui = Gui.normal()
                            .setStructure("x . .")
                            .addIngredient('x', new ItemBuilder(Material.PAPER).setDisplayName(shop.getGuiTitle()))
                            .build();

                    AnvilWindow.single()
                            .setGui(gui)
                            .setTitle("제목을 입력 후 닫아주세요.")
                            .addRenameHandler((text) -> {
                                String newTitle = ChatColor.translateAlternateColorCodes('&', text);
                                shop.setGuiTitle(newTitle);
                            })
                            .addCloseHandler(() -> Do.syncLater(1, ShopSettings.this::open))
                            .addCloseHandler(() -> Do.async(() -> plugin.getShopRepository().saveShop(shop)))
                            .open(player);
                }
        );
    }

    private Item createRowsBtn() {
        return new SuppliedItem(
                () -> new ItemBuilder(Material.BLAZE_ROD)
                        .setDisplayName(parseMessage("<b><gold>줄 수</b>"))
                        .setLore(parseMessage(
                                INFO_PREFIX + "<white>현재 값: <aqua>" + shop.getRows(),
                                "",
                                CONTROL_PREFIX + "<gold>좌클릭 <white>시, 줄 수를 <green>1 <white>늘립니다.",
                                CONTROL_PREFIX + "<gold>우클릭 <white>시, 줄 수를 <red>1 <white>줄입니다."
                        )),
                (click) -> {
                    if (click.getClickType() == ClickType.LEFT) {
                        shop.setRows(Math.min(6, shop.getRows() + 1));
                    } else if (click.getClickType() == ClickType.RIGHT) {
                        shop.setRows(Math.max(2, shop.getRows() - 1));
                    }

                    return true;
                }
        );
    }
}