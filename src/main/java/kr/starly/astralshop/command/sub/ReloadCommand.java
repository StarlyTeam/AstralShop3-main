package kr.starly.astralshop.command.sub;

import kr.starly.astralshop.api.registry.ShopMenuRegistry;
import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.AstralShop;
import kr.starly.astralshop.command.SubCommand;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.shop.inventory.ShopInventory;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements SubCommand {

    @Override
    public String getEngName() {
        return "reload";
    }

    @Override
    public String getKorName() {
        return "리로드";
    }

    @Override
    public String getEngDescription() {
        return "Reload Config";
    }

    @Override
    public String getKorDescription() {
        return "콘피그를 리로드합니다.";
    }

    @Override
    public String getEngUsage() {
        return "";
    }

    @Override
    public String getKorUsage() {
        return "";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("starly.astralshop.edit");
    }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            long startTime = System.currentTimeMillis();

            AstralShop astralShop = AstralShop.getInstance();
            ShopRegistry shopRegistry = astralShop.getShopRegistry();
            ShopMenuRegistry shopMenuRegistry = astralShop.getShopMenuRegistry();
            MessageContext messageContext = MessageContext.getInstance();

            astralShop.getServer().getOnlinePlayers().forEach(player -> {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof ShopInventory) {
                    player.closeInventory();
                    player.sendMessage("상점 점검으로 인해 상점이 닫혔습니다.");
                }
            });

            astralShop.reloadConfig();
//            shopRegistry.loadShops();
            shopMenuRegistry.loadMenuItems();
            messageContext.initialize(astralShop.getFile());

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.0;

            sender.sendMessage("콘피그 리로드 완료. (" + String.format("%.3f", duration) + "s)");
        } else {
            sender.sendMessage("올바른 명령어를 입력해 주세요.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}