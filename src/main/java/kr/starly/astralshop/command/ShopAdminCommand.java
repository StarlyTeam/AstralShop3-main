package kr.starly.astralshop.command;

import kr.starly.astralshop.command.sub.CreateShopCommand;
import kr.starly.astralshop.command.sub.DeleteShopCommand;
import kr.starly.astralshop.command.sub.EditShopCommand;
import kr.starly.astralshop.command.sub.ReloadCommand;
import kr.starly.astralshop.message.MessageContext;
import kr.starly.astralshop.message.MessageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ShopAdminCommand implements TabExecutor {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    private boolean isKor(String label) {
        return label.equals("상점관리");
    }

    public ShopAdminCommand(JavaPlugin plugin) {
        PluginCommand command = plugin.getCommand("shopadmin");
        if (command != null) {
            command.setExecutor(this);
            registerSubCommands();
        }
    }

    private void registerSubCommands() {
        registerSubCommand(new CreateShopCommand());
        registerSubCommand(new DeleteShopCommand());
        registerSubCommand(new EditShopCommand());
        registerSubCommand(new ReloadCommand());
    }

    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getEngName(), subCommand);
        subCommands.put(subCommand.getKorName(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            Set<SubCommand> uniqueSubCommands = new HashSet<>(subCommands.values());

            sender.sendMessage(" §6§m                                         §r");
            for (SubCommand subCommand : uniqueSubCommands) {
                if (isKor(label)) {
                    sender.sendMessage(" §e§l| §r§f/상점관리 " + subCommand.getKorName() + " " + subCommand.getKorUsage() + " : " + subCommand.getKorDescription());
                } else {
                    sender.sendMessage(" §e§l| §r§f/shopadmin " + subCommand.getEngName() + " " + subCommand.getKorUsage() + " : " + subCommand.getKorDescription());
                }
            }
            sender.sendMessage("");
            return true;
        }

        MessageContext messageContext = MessageContext.getInstance();
        SubCommand subCmdInstance = subCommands.get(args[0].toLowerCase());
        if (subCmdInstance != null) {
            if (!subCmdInstance.hasPermission(sender)) {
                messageContext.get(MessageType.ERROR, "noPermission").send(sender);
                return false;
            }

            subCmdInstance.execute(sender, label, args);
            return true;
        }

        messageContext.get(MessageType.NORMAL, "wrongCommand").send(sender);
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (SubCommand subCommand : subCommands.values()) {
                if (!subCommand.hasPermission(sender)) continue;

                if (isKor(label)) {
                    completions.add(subCommand.getKorName());
                } else {
                    completions.add(subCommand.getEngName());
                }
            }
        } else {
            SubCommand subCmdInstance = subCommands.get(args[0].toLowerCase());
            if (subCmdInstance != null && subCmdInstance.hasPermission(sender)) {
                completions = subCmdInstance.tabComplete(sender, label, args);
            }
        }

        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}