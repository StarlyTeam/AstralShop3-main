package xyz.starly.astralshop.command;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import xyz.starly.astralshop.api.registry.ShopRegistry;

@AllArgsConstructor
public class TestShopCommand implements CommandExecutor {

    private final ShopRegistry shopRegistry;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("사용법: /shop create <상점이름>");
            sender.sendMessage("사용법: /shop delete <상점이름>");
            return true;
        }

        String shopName = args[1];

        switch (args[0]) {
            case "create":
                if (shopRegistry.createShop(shopName, shopName)) {
                    sender.sendMessage("성공적으로 " + shopName + "상점을 제작하였습니다!");
                } else {
                    sender.sendMessage("§c이미 같은 이름의 상점이 존재합니다!");
                }

                break;

            case "delete":
                if (shopRegistry.deleteShop(shopName)) {
                    sender.sendMessage("성공적으로 " + shopName + "상점을 제거하였습니다!");
                } else {
                    sender.sendMessage("§c상점을 찾을 수 없습니다!");
                }
                break;

            case "list":
                shopRegistry.getShops();
                break;
        }

        return true;
    }
}