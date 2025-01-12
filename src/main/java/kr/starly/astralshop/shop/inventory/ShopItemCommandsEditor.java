package kr.starly.astralshop.shop.inventory;

import kr.starly.astralshop.api.shop.Shop;
import kr.starly.astralshop.api.shop.ShopItem;
import kr.starly.astralshop.api.shop.ShopPage;
import kr.starly.astralshop.shop.inventory.old.BaseShopInventory;
import kr.starly.libs.inventory.gui.Gui;
import kr.starly.libs.inventory.item.builder.ItemBuilder;
import kr.starly.libs.inventory.window.AnvilWindow;
import kr.starly.libs.scheduler.Do;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static kr.starly.astralshop.message.MessageContext.*;

public class ShopItemCommandsEditor extends BaseShopInventory {

    private final int page;
    private final int slot;

    private ShopPage pageData;
    private ShopItem itemData;

    public ShopItemCommandsEditor(Shop shop, int page, int slot) {
        super(shop, "%s [%d@%d 명령어]".formatted(shop.getName(), page, slot), 6, true);

        this.page = page;
        this.slot = slot;
        this.pageData = shop.getShopPages().get(page - 1);
        this.itemData = pageData.getItems().get(slot);
    }

    @Override
    protected void initializeInventory(Inventory inventory, Player player) {
        ItemStack border = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)
                .setDisplayName(parseMessage(""))
                .get();

        List<String> commands = itemData.getCommands();
        for (int i = 0; i < Math.min(45, commands.size()); i++) {
            String commandLine = commands.get(i);
            inventory.setItem(i,
                    new ItemBuilder(Material.REPEATING_COMMAND_BLOCK)
                            .setDisplayName(parseMessage("<b><gold>%d번 명령어</b>".formatted(i + 1)))
                            .setLegacyLore(List.of(
                                    INFO_PREFIX + "<white>현재 값: <aqua>" + commandLine,
                                    "",
                                    CONTROL_PREFIX + "<gold>좌클릭 <white>시, 값을 변경합니다.",
                                    CONTROL_PREFIX + "<gold>Shift+우클릭 <white>시, 삭제합니다."
                            ))
                            .get()
            );
        }

        for (int i = 45; i < 53; i++) inventory.setItem(i, border);
        inventory.setItem(53,
                new ItemBuilder(Material.IRON_PICKAXE)
                        .setDisplayName(parseMessage("<green>명령어 추가"))
                        .setLegacyLore(List.of(
                                CONTROL_PREFIX + "<gold>좌클릭 <white>시, 명령어를 추가합니다."
                        ))
                        .get()
        );
    }

    @Override
    protected void inventoryClick(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();

        int slot = event.getSlot();
        ClickType click = event.getClick();
        if (slot >= 0 && slot < 45 && click == ClickType.LEFT) {
            setEventListening(false);
//            new AnvilGUI.Builder() TODO TODO TODO TODO TODO TODO
//                    .title("변경할 명령어를 입력해주세요.")
//                    .plugin(plugin)
//                    .interactableSlots(AnvilGUI.Slot.OUTPUT)
//                    .itemLeft(
//                            new ItemBuilder(Material.PAPER)
//                                    .setDisplayName("§r")
//                                    .get()
//                    )
//                    .onClick((clickedSlot, stateSnapshot) -> {
//                        if (clickedSlot != AnvilGUI.Slot.OUTPUT) return new ArrayList<>();
//
//                        String newCommand = stateSnapshot.getText();
//                        if (newCommand.startsWith("/")) newCommand = newCommand.substring(1);
//                        itemData.getCommands().set(slot, newCommand);
//
//                        return List.of(AnvilGUI.ResponseAction.close());
//                    })
//                    .onClose((stateSnapshot) -> {
//                        setEventListening(true);
//                        open(player);
//                    })
//                    .open(player);
            return;
        } else if (slot >= 0 && slot < 45 && click == ClickType.SHIFT_RIGHT) {
            itemData.getCommands().remove(slot);
        } else if (slot == 53 && click == ClickType.LEFT) {
            if (itemData.getCommands().size() >= 45) return;

            setEventListening(false);

            Gui gui = Gui.normal()
                    .setStructure("x . .")
                    .addIngredient('x', new ItemBuilder(Material.PAPER).setDisplayName("/"))
                    .build();

            AnvilWindow.single()
                    .setGui(gui)
                    .setTitle("명령어를 입력 후 닫아주세요.")
                    .addRenameHandler((newCommand) -> {
                        if (newCommand.startsWith("/")) newCommand = newCommand.substring(1);
                        itemData.getCommands().add(newCommand);
                    })
                    .addCloseHandler(() -> Do.syncLater(1, () -> {
                        setEventListening(true);
                        open(player);
                    }))
                    .addCloseHandler(() -> Do.async(ShopItemCommandsEditor.this::saveShop))
                    .open(player);
            return;
        }

        saveShop();
        updateInventory(player);
    }

    @Override
    protected void inventoryClose(InventoryCloseEvent event) {
        saveShop();

        setEventListening(false);
        Do.syncLater(1, () -> new ShopItemEditor(shop, page, slot).open((Player) event.getPlayer()));
    }

    @Override
    public void updateData() {
        super.updateData();
        this.pageData = shop.getShopPages().get(page - 1);
        this.itemData = pageData.getItems().get(slot);
    }
}