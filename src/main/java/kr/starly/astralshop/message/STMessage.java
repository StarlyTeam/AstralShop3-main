package kr.starly.astralshop.message;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.hook.PlaceholderAPIHook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class STMessage {

    private final String prefix;
    private final String message;

    public String getPrefix(Player player) {
        return PlaceholderAPIHook.getHook().setPlaceholders(player, prefix);
    }
    public String getMessage(Player player) {
        return PlaceholderAPIHook.getHook().setPlaceholders(player, message);
    }

    public void send(CommandSender sender) {
        if (message.isEmpty()) return;

        if (sender instanceof Player player) send(player);
        else sender.sendMessage(prefix + message);
    }

    private void send(Player player) {
        String finalMessage = PlaceholderAPIHook.getHook().setPlaceholders(player, prefix + message);
        player.sendMessage(finalMessage);
    }

    public void broadcast() {
        if (message.isEmpty()) return;

        AstralShop.getInstance().getServer().getOnlinePlayers().forEach(this::send);
    }

    public String getText() {
        return prefix + message;
    }
}