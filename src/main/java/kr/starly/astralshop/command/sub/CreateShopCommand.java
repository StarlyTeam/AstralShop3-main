package kr.starly.astralshop.command.sub;

import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.AstralShop;
import kr.starly.astralshop.command.SubCommand;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class CreateShopCommand implements SubCommand {

    @Override
    public String getEngName() {
        return "create";
    }

    @Override
    public String getKorName() {
        return "생성";
    }

    @Override
    public String getEngDescription() {
        return "Create a shop";
    }

    @Override
    public String getKorDescription() {
        return "상점을 생성합니다.";
    }

    @Override
    public String getEngUsage() {
        return "<name>";
    }

    @Override
    public String getKorUsage() {
        return "<이름>";
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
        if (shopRegistry.createShop(name)) {
            sender.sendMessage(name + "의 상점을 생성하였습니다.");
        } else {
            sender.sendMessage("이미 존재하는 상점입니다.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}