package xyz.starly.astralshop.command.sub;

import org.bukkit.command.CommandSender;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.command.SubCommand;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DeleteShopCommand implements SubCommand {

    @Override
    public String getEngName() {
        return "delete";
    }

    @Override
    public String getKorName() {
        return "삭제";
    }

    @Override
    public String getEngDescription() {
        return "Delete a shop";
    }

    @Override
    public String getKorDescription() {
        return "상점을 삭제합니다.";
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
        return sender.hasPermission("starly.astralshop.create");
    }

    private final ShopRegistry shopRegistry = AstralShop.getInstance().getShopRegistry();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("올바른 명령어를 입력해 주세요.");
            return;
        }

        String name = args[1];
        if (shopRegistry.deleteShop(name)) {
            sender.sendMessage(name + "의 상점을 삭제하였습니다.");
        } else {
            sender.sendMessage("존재하지 않는 상점입니다.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            return shopRegistry.getShopNames();
        }
        return Collections.emptyList();
    }
}