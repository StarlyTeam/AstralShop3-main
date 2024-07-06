package kr.starly.astralshop.command.sub;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.repository.ShopRepository;
import kr.starly.astralshop.command.SubCommand;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
    public String getKorDescription() {
        return "상점을 생성합니다.";
    }

    @Override
    public String getKorUsage() {
        return "<이름>";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("starly.astralshop.create");
    }

    private final ShopRepository shopRepository = AstralShop.getInstance().getShopRepository();

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        MessageContext messageContext = MessageContext.getInstance();
        if (args.length != 2) {
            messageContext.get(MessageType.ERROR, "wrongCommand").send(sender);
            return;
        }

        String name = args[1];
        if (shopRepository.createShop(name)) {
            messageContext.get(MessageType.NORMAL, "shopCreated", TagResolver.builder()
                    .tag("name", Tag.inserting(Component.text(name)))
                    .build()
            ).send(sender);
        } else {
            messageContext.get(MessageType.ERROR, "shopAlreadyExists").send(sender);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}