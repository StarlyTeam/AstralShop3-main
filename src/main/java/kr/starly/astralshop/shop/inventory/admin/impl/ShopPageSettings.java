package kr.starly.astralshop.shop.inventory.admin.impl;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.repository.ShopRepository;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import kr.starly.libs.inventory.item.builder.ItemBuilder;
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

    private final ShopRepository shopRepository = AstralShop.getInstance().getShopRepository();

    private ShopPage pageData;

    public ShopPageSettings(Shop shop, ShopPage pageData) {
        super(shop, shop.getName() + " [페이지" + pageData.getPageNum() + " 관리]", 5, true);

        ItemStack item = new ItemStack(Material.valueOf("GRAY_STAINED_GLASS_PANE"));
        setOutline(
                new ItemBuilder(item)
                        .setDisplayName("&r")
                        .get()
        );

        this.pageData = pageData;
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        inventory.setItem(21,
                new ItemBuilder(Material.NAME_TAG)
                        .setDisplayName("&6페이지 제목")
                        .setLegacyLore(List.of(
                                "&e&l| &f현재 값: &6" + pageData.getGuiTitle(),
                                "",
                                "&e&l| &6좌클릭 &f시, 값을 변경합니다."
                        ))
                        .get()
        );
        inventory.setItem(22,
                new ItemBuilder(Material.STRING)
                        .setDisplayName("&6줄 수")
                        .setLegacyLore(List.of(
                                "&e&l| &f현재 값: &6" + pageData.getRows(),
                                "",
                                "&e&l| &6좌클릭 &f시, 줄 수를 &c1 &f줄입니다.",
                                "&e&l| &6우클릭 &f시, 줄 수를 &a1 &f늘립니다."
                        ))
                        .get()
        );
        inventory.setItem(23,
                new ItemBuilder(Material.BARRIER)
                        .setDisplayName("&c삭제")
                        .setLegacyLore(List.of(
                                "&e&l| &6Shift+좌클릭 &f시, 페이지를 삭제합니다."
                        ))
                        .get()
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
                                    .setDisplayName("&r")
                                    .get()
                    )
                    .onClick((clickedSlot, stateSnapshot) -> {
                        if (clickedSlot != AnvilGUI.Slot.OUTPUT) return new ArrayList<>();

                        String newTitle = ChatColor.translateAlternateColorCodes('&', stateSnapshot.getText());
                        pageData.setGuiTitle(newTitle);

                        return List.of(AnvilGUI.ResponseAction.close());
                    })
                    .onClose((stateSnapshot) -> {
                        setEventListening(true);
                        open(player);
                    })
                    .open(player);
            return;
        } else if (slot == 22 && click == ClickType.LEFT) {
            int currentRows = pageData.getRows();
            if (currentRows <= 2) return;

            pageData.setRows(currentRows - 1);
        } else if (slot == 22 && click == ClickType.RIGHT) {
            int currentRows = pageData.getRows();
            if (currentRows >= 6) return;

            pageData.setRows(currentRows + 1);
        } else if (slot == 23 && click == ClickType.SHIFT_LEFT) {
            shop.getShopPages().remove(pageData);

            player.closeInventory();
            return;
        } else return;

        saveShop();
        updateInventory(player);
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
        shopRepository.saveShop(shop);

        setEventListening(false);
        ShopEditor shopEditor = new ShopEditor(shop);
        shopEditor.getPaginationManager().setCurrentPage(pageData.getPageNum());
        shopEditor.open((Player) event.getPlayer());
    }

    @Override
    public void updateData() {
        super.updateData();
        this.pageData = shop.getShopPages().get(pageData.getPageNum() - 1);
    }
}