package xyz.starly.astralshop.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import xyz.starly.astralshop.AstralShop;

@AllArgsConstructor
@Data
public class STMessage {

    private String prefix;
    private String message;

    public void send(CommandSender sender) {
        if (message.isEmpty()) {
            return;
        }
        sender.sendMessage(prefix + message);
    }

    public void send(ConsoleCommandSender console) {
        if (message.isEmpty()) {
            return;
        }
        console.sendMessage(prefix + message);
    }

    public void broadcast() {
        if (message.isEmpty()) {
            return;
        }
        AstralShop.getInstance().getServer().broadcastMessage(prefix + message);
    }

    public String getText() {
        return prefix + message;
    }
}