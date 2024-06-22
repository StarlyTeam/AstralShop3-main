package kr.starly.astralshop.command.sub;

import kr.starly.astralshop.api.repository.ShopRepository;
import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.command.SubCommand;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import kr.starly.astralshop.shop.inventory.BaseShopInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
    public String getKorDescription() {
        return "콘피그를 리로드합니다.";
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
        MessageContext messageContext = MessageContext.getInstance();
        if (args.length == 1) {
            long startTime = System.currentTimeMillis();

            AstralShop plugin = AstralShop.getInstance();
            ShopRepository shopRepository = plugin.getShopRepository();

            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof BaseShopInventory) {
                    player.closeInventory();
                    messageContext.get(MessageType.NORMAL, "shopClosedWhileMaintaining").send(player);
                }
            });

            plugin.reloadConfig();
            shopRepository.saveShops();
            shopRepository.loadShops();
            messageContext.loadMessagesFromConfig(plugin.getConfig());

            long endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000.0;
            messageContext.get(MessageType.NORMAL, "reloadComplete", TagResolver.builder()
                    .tag("duration", Tag.inserting(Component.text(duration + "s")))
                    .build()
            ).send(sender);
        } else {
            messageContext.get(MessageType.ERROR, "wrongCommand").send(sender);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}