package xyz.starly.astralshop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.shop.inventory.global.PaginatedShopInventory;
import xyz.starly.astralshop.shop.inventory.global.ShopMainInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShopCommand implements TabExecutor {

    private final ShopRegistry shopRegistry = AstralShop.getInstance().getShopRegistry();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있는 명령어입니다.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            new ShopMainInventory().open(player);
            return true;
        }

        String name = args[0];
        Shop shop = shopRegistry.getShop(name);
        if (shop != null) {
            new PaginatedShopInventory(shop).open(player);
        } else {
            player.sendMessage("존재하지 않는 상점이에요..");
            System.out.println("테스트" + shopRegistry.getShops());
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], shopRegistry.getShops().stream().map(Shop::getName).collect(Collectors.toList()), new ArrayList<>());
        }
        return Collections.emptyList();
    }
}