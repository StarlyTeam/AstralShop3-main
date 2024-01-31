package xyz.starly.astralshop.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.command.SubCommand;
import xyz.starly.astralshop.shop.inventory.admin.impl.EditAdminPaginatedShopInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EditShopCommand implements SubCommand {

    @Override
    public String getEngName() {
        return "edit";
    }

    @Override
    public String getKorName() {
        return "편집";
    }

    @Override
    public String getEngDescription() {
        return "Edit a shop";
    }

    @Override
    public String getKorDescription() {
        return "상점을 편집합니다.";
    }

    @Override
    public String getEngUsage() {
        return "<shop>";
    }

    @Override
    public String getKorUsage() {
        return "<상점>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("starly.astralshop.edit");
    }

    private final ShopRegistry shopRegistry = AstralShop.getInstance().getShopRegistry();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("플레이어만 사용할 수 있는 명령어욤..");
            return;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            sender.sendMessage("올바른 명령어를 입력해 주세요.");
            return;
        }

        String name = args[1];
        Shop shop = shopRegistry.getShop(name);
        if (shop == null) {
            sender.sendMessage("존재하지 않는 상점입니다.");
            return;
        }

        new EditAdminPaginatedShopInventory(shop).open(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], shopRegistry.getShops().stream().map(Shop::getName).collect(Collectors.toList()), new ArrayList<>());
        }
        return Collections.emptyList();
    }
}