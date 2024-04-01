package kr.starly.astralshop.message;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.astralshop.api.AstralShop;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class STMessage {

    private final String prefix;
    private final String message;

    public void send(CommandSender sender) {
        if (message.isEmpty()) return;

        if (sender instanceof Player player && AstralShop.getInstance().isPapiAvailable()) {
            PlaceholderAPI.setPlaceholders(player, message);
        }
        sender.sendMessage(prefix + message);
    }

    public void broadcast() {
        if (message.isEmpty()) return;
        AstralShop.getInstance().getServer().broadcastMessage(prefix + message);
    }

    public String getText() {
        return prefix + message;
    }
}