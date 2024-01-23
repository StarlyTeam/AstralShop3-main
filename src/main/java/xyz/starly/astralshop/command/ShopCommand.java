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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
            // TODO 전체 상점 오픈
            return true;
        }

        String name = args[0];
        Shop shop = shopRegistry.getShop(name);
        if (shop != null) {
            new PaginatedShopInventory(shop).open(player);
        } else {
            player.sendMessage("존재하지 않는 상점이에요..");
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], shopRegistry.getShopNames(), new ArrayList<>());
        }
        return Collections.emptyList();
    }
}