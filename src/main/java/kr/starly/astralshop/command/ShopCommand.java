package kr.starly.astralshop.command;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.repository.ShopRepository;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import kr.starly.astralshop.shop.inventory.global.impl.UserShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShopCommand implements TabExecutor {

    private final ShopRepository shopRepository = AstralShop.getInstance().getShopRepository();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MessageContext messageContext = MessageContext.getInstance();
        if (!(sender instanceof Player player)) {
            messageContext.get(MessageType.ERROR, "noConsole").send(sender);
            return false;
        } else if (args.length == 0) {
            messageContext.get(MessageType.ERROR, "wrongCommand").send(player);
            return false;
        }

        String name = args[0];
        Shop shop = shopRepository.getShop(name);
        if (shop != null) {
            if (!shop.isEnabled() && !player.isOp()) {
                messageContext.get(MessageType.ERROR, "shopDisabled").send(player);
                return false;
            }

            ShopAccessibility accessibility = shop.getAccessibility();
            if (accessibility == ShopAccessibility.PROTECTED || accessibility == ShopAccessibility.PRIVATE) {
                if (!player.hasPermission("starly.astralshop.open." + shop.getName())) {
                    messageContext.get(MessageType.ERROR, "noPermission").send(player);
                    return false;
                }
            }

            new UserShop(shop).open(player);
            return true;
        } else {
            messageContext.get(MessageType.ERROR, "shopNotExists").send(player);
            return false;
        }
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], shopRepository.getShops().stream().map(Shop::getName).toList(), new ArrayList<>());
        }

        return Collections.emptyList();
    }
}