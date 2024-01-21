package xyz.starly.astralshop.command;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {

    String getEngName();

    String getKorName();

    String getEngDescription();

    String getKorDescription();

    String getEngUsage();

    String getKorUsage();

    boolean hasPermission(CommandSender sender);

    void execute(CommandSender sender, String label, String[] args);

    List<String> tabComplete(CommandSender sender, String label, String[] args);
}
