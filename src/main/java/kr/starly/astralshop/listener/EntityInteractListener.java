package kr.starly.astralshop.listener;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.registry.ShopRepository;
import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopAccessibility;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import kr.starly.astralshop.shop.inventory.admin.impl.ShopSettings;
import kr.starly.astralshop.shop.inventory.global.impl.UserShop;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashMap;
import java.util.Map;

public class EntityInteractListener implements Listener {

    private static final Map<String, String> cacheMap = new HashMap<>();

    public static void fetchNPCNames() {
        cacheMap.clear();

        ShopRepository shopRepository = AstralShop.getInstance().getShopRepository();
        shopRepository.getShops().forEach((shop) -> {
            String shopNpc = shop.getNpc();
            if (shopNpc == null || shopNpc.isEmpty()) return;

            cacheMap.put(shopNpc, shop.getName());
        });
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        String npcName = event.getRightClicked().getCustomName();
        String shopName = cacheMap.get(npcName);
        if (shopName == null || shopName.isEmpty()) return;

        ShopRepository shopRepository = AstralShop.getInstance().getShopRepository();
        Shop shop = shopRepository.getShop(shopName);
        if (shop == null) return;

        MessageContext messageContext = MessageContext.getInstance();
        if (player.isSneaking() && player.hasPermission("starly.astralshop.edit")) {
            new ShopSettings(shop).open(player);
        } else {
            if (!shop.isEnabled() && !player.isOp()) {
                messageContext.get(MessageType.ERROR, "shopDisabled").send(player);
                return;
            }

            ShopAccessibility accessibility = shop.getAccessibility();
            if (accessibility == ShopAccessibility.PRIVATE) {
                if (!player.hasPermission("starly.astralshop.open." + shop.getName())) {
                    messageContext.get(MessageType.ERROR, "noPermission").send(player);
                    return;
                }
            }

            new UserShop(shop).open(player);
        }
    }
}