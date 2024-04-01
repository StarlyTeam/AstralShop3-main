package kr.starly.astralshop.command.sub;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.registry.ShopRegistry;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.command.SubCommand;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import kr.starly.astralshop.shop.inventory.admin.impl.ShopSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
    public String getKorDescription() {
        return "상점을 편집합니다.";
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
        MessageContext messageContext = MessageContext.getInstance();
        if (!(sender instanceof Player)) {
            messageContext.get(MessageType.ERROR, "noConsole").send(sender);
            return;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            messageContext.get(MessageType.ERROR, "wrongCommand").send(sender);
            return;
        }

        String name = args[1];
        Shop shop = shopRegistry.getShop(name);
        if (shop == null) {
            messageContext.get(MessageType.ERROR, "shopNotExists").send(sender);
            return;
        }

        new ShopSettings(shop).open(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            return shopRegistry.getShops().stream().map(Shop::getName).toList();
        }
        return Collections.emptyList();
    }
}