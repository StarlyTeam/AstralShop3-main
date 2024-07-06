package kr.starly.astralshop.message;

import kr.starly.astralshop.api.AstralShop;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
@Getter
public class STMessage {

    private final String prefix;
    private final String message;
    private final TagResolver replacer;

    public STMessage(String prefix, String message) {
        this.prefix = prefix;
        this.message = message;
        this.replacer = TagResolver.empty();
    }

    public void send(CommandSender sender) {
        Component text = getText();
        if (text != null) sender.sendMessage(text);
    }

    public void broadcast() {
        AstralShop.getInstance().getServer().getOnlinePlayers().forEach(this::send);
    }

    public Component getText() {
        return message.isEmpty() ? null : MiniMessage.miniMessage().deserialize(prefix + message, replacer);
    }
}