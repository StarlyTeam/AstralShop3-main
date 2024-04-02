package kr.starly.astralshop.shop.inventory.admin.impl;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.shop.ShopPageImpl;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import kr.starly.astralshop.shop.inventory.PaginationManager;
import kr.starly.core.builder.ItemBuilder;
import lombok.Getter;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ShopPageSettings extends BaseShopInventory {

    private final ShopRegistry shopRegistry = AstralShop.getInstance().getShopRegistry();

    private final ShopPage page;

    public ShopPageSettings(Shop shop, ShopPage page) {
        super(shop, shop.getName() + " [페이지" + page.getPageNum() + " 관리]", 5, true);

        ItemStack item;
        try {
            item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7);
        } catch (NoSuchFieldError ignored) {
            item = new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"));
        }
        setOutline(
                new ItemBuilder(item)
                        .setName("&r")
                        .build()
        );

        this.page = page;
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        inventory.setItem(21,
                new ItemBuilder(Material.NAME_TAG)
                        .setName("&6페이지 제목")
                        .setLore(
                                "&e&l| &f현재 값: &6" + page.getGuiTitle(),
                                "",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        )
                        .build()
        );
        inventory.setItem(22,
                new ItemBuilder(Material.STRING)
                        .setName("&6줄 수")
                        .setLore(
                                "&e&l| &f현재 값: &6" + page.getRows(),
                                "",
                                "&e&l| &6좌클릭 &f시, 줄 수를 &c1 &f줄입니다.",
                                "&e&l| &6우클릭 &f시, 줄 수를 &a1 &f늘립니다."
                        )
                        .build()
        );
        inventory.setItem(23,
                new ItemBuilder(Material.BARRIER)
                        .setName("&c삭제")
                        .setLore(
                                "&e&l| &6Shift+좌클릭 &f시, 페이지를 삭제합니다."
                        )
                        .build()
        );
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();
        ClickType click = event.getClick();
        if (slot == 21 && click == ClickType.LEFT) {
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
                        page.setGuiTitle(newTitle);

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .onClose((stateSnapshot) -> {
                        setEventListening(true);
                        open(player);
                    })
                    .open(player);
            return;
        } else if (slot == 22 && click == ClickType.LEFT) {
            int currentRows = page.getRows();
            if (currentRows <= 2) return;

            page.setRows(currentRows - 1);
        } else if (slot == 22 && click == ClickType.RIGHT) {
            int currentRows = page.getRows();
            if (currentRows >= 6) return;

            page.setRows(currentRows + 1);
        } else if (slot == 23 && click == ClickType.SHIFT_LEFT) {
            shop.getShopPages().remove(page);

            player.closeInventory();
            return;
        } else return;

        updateInventory(player);
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
        shopRegistry.saveShop(shop);

        setEventListening(false);
        ShopEditor shopEditor = new ShopEditor(shop);
        shopEditor.getPaginationManager().setCurrentPage(page.getPageNum());
        shopEditor.open((Player) event.getPlayer());
    }
}