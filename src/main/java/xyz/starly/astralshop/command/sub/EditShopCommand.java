package xyz.starly.astralshop.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.starly.astralshop.AstralShop;
import xyz.starly.astralshop.api.registry.ShopRegistry;
import xyz.starly.astralshop.api.shop.Shop;
import xyz.starly.astralshop.command.SubCommand;

import java.util.Collections;
import java.util.List;

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

        if (args.length != 2) {
            sender.sendMessage("올바른 명령어를 입력해 주세요.");
            return;
        }

        String name = args[0];
        sender.sendMessage(name + "이욤..");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}