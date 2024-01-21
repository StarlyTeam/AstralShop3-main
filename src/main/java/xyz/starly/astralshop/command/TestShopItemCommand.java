package xyz.starly.astralshop.command;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.api.shop.ShopItem;
import xyz.starly.astralshop.api.shop.ShopPage;
import xyz.starly.astralshop.registry.YamlShopRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class TestShopItemCommand implements TabExecutor {

    private final ShopRegistry shopRegistry;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage("사용법: /상점 <상점이름> <페이지> <슬롯>");
            return true;
        }

        String shopName = args[0];
        int page, slot;
        try {
            page = Integer.parseInt(args[1]) - 1;
            slot = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("페이지와 슬롯 번호는 숫자여야 합니다.");
            return true;
        }

        Shop shop = shopRegistry.getShop(shopName);

        ShopPage shopPage = shop.getShopPages().get(page);
        if (shopPage == null) {
            sender.sendMessage("해당 페이지가 존재하지 않습니다.");
            return true;
        }

        ShopItem shopItem = shopPage.getItems().get(slot);
        if (shopItem == null) {
            sender.sendMessage("해당 슬롯에 아이템이 없습니다.");
            return true;
        }

        Player player = (Player) sender;
        player.getInventory().addItem(shopItem.getItemStack());
        sender.sendMessage(shopItem.getItemStack().getType() + " 아이템을 지급받았습니다.");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        try {
            if (args.length == 1) {
                return getShopNames();
            } else if (args.length == 2) {
                Shop shop = shopRegistry.getShop(args[0]);
                return getPageNumbers(shop);
            } else if (args.length == 3) {
                Shop shop = shopRegistry.getShop(args[0]);
                int pageIndex = Integer.parseInt(args[1]) - 1;
                if (pageIndex >= 0 && pageIndex < shop.getShopPages().size()) {
                    ShopPage shopPage = shop.getShopPages().get(pageIndex);
                    if (shopPage != null) {
                        return getSlotNumbers(shopPage);
                    }
                }
            }
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }

        return null;
    }

    private List<String> getShopNames() {
        return new ArrayList<>(shopRegistry.getShopNames());
    }

    private List<String> getPageNumbers(Shop shop) {
        List<String> pageNumbers = new ArrayList<>();
        List<ShopPage> pages = shop.getShopPages();
        for (int i = 0; i < pages.size(); i++) {
            pageNumbers.add(String.valueOf(i + 1));
        }
        return pageNumbers;
    }

    private List<String> getSlotNumbers(ShopPage shopPage) {
        List<String> slotNumbers = new ArrayList<>();
        Map<Integer, ShopItem> items = shopPage.getItems();
        if (items != null) {
            for (Integer slot : items.keySet()) {
                slotNumbers.add(String.valueOf(slot));
            }
        }
        return slotNumbers;
    }
}