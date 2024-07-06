package kr.starly.astralshop.listener;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.repository.ShopRepository;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import kr.starly.astralshop.shop.inventory.ShopSettings;
import kr.starly.astralshop.shop.inventory.UserShop;
import kr.starly.libs.nbtapi.NBT;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class NPCInteractListener implements Listener {

    private final AstralShop plugin = AstralShop.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        if (!player.isOnline()) return;

        Entity entity = event.getRightClicked();
        String shopName = NBT.getPersistentData(entity, (nbt) -> nbt.getString(plugin.getName()));
        if (shopName == null || shopName.isEmpty()) return;

        ShopRepository shopRepository = AstralShop.getInstance().getShopRepository();
        Shop shop = shopRepository.getShop(shopName);
        if (shop == null) return;

        MessageContext messageContext = MessageContext.getInstance();
        if (player.isSneaking() && player.hasPermission("starly.astralshop.edit")) {
            new ShopSettings(player, shop).open();
        } else {
            if (!shop.isEnabled() && !player.isOp()) {
                messageContext.get(MessageType.ERROR, "shopDisabled").send(player);
                return;
            }

            ShopAccessibility accessibility = shop.getAccessibility();
            if (accessibility == ShopAccessibility.NONE) {
                if (!player.hasPermission("starly.astralshop.open." + shop.getName())) {
                    messageContext.get(MessageType.ERROR, "noPermission").send(player);
                    return;
                }
            }

            event.setCancelled(true);
            new UserShop(shop).open(player);
        }
    }
}