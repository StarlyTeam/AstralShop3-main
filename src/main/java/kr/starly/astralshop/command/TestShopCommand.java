package kr.starly.astralshop.command;

import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.registry.SQLShopRegistry;
import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@AllArgsConstructor
public class TestShopCommand implements CommandExecutor {

    private final SQLShopRegistry shopRegistry;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("이 명령어는 플레이어만 사용할 수 있습니다.");
            return true;
        }

        List<Shop> shopList = shopRegistry.getShops();

        shopList.forEach(shop -> System.out.println("테스트 " + shop.getShopPages().get(0).getItems().get(1).getItemStack()));

        return true;
    }
}