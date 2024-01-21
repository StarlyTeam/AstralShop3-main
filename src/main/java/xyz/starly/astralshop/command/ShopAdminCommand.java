package xyz.starly.astralshop.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.starly.astralshop.command.sub.CreateShopCommand;
import xyz.starly.astralshop.command.sub.DeleteShopCommand;
import xyz.starly.astralshop.command.sub.EditShopCommand;

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
            command.setTabCompleter(this);
            registerSubCommands();
        }
    }

    private void registerSubCommands() {
        registerSubCommand(new CreateShopCommand());
        registerSubCommand(new DeleteShopCommand());
        registerSubCommand(new EditShopCommand());
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
                    sender.sendMessage(" §e§l| §f/상점관리 " + subCommand.getKorName() + " " + subCommand.getKorUsage() + " : " + subCommand.getKorDescription());
                } else {
                    sender.sendMessage(" §e§l| §f/shopadmin " + subCommand.getEngName() + " " + subCommand.getEngUsage() + " : " + subCommand.getEngDescription());
                }
            }
            sender.sendMessage("");
            return true;
        }

        SubCommand subCmdInstance = subCommands.get(args[0].toLowerCase());
        if (subCmdInstance != null && subCmdInstance.hasPermission(sender)) {
            subCmdInstance.execute(sender, label, args);
            return true;
        }

        sender.sendMessage("§c존재하지 않는 명령어입니다.");
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (SubCommand subCommand : subCommands.values()) {
                if (isKor(label)) {
                    completions.add(subCommand.getKorName());
                } else {
                    completions.add(subCommand.getEngName());
                }
            }
        } else {
            SubCommand subCmdInstance = subCommands.get(args[0].toLowerCase());
            if (subCmdInstance != null) {
                completions = subCmdInstance.tabComplete(sender, label, args);
            }
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
    }
}
