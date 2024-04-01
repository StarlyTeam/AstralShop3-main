package kr.starly.astralshop.message;

import kr.starly.astralshop.api.AstralShop;
import kr.starly.core.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MessageContext {

    private static MessageContext instance;

    public static MessageContext getInstance() {
        if (instance == null) {
            instance = new MessageContext();
        }
        return instance;
    }

    private MessageContext() {}

    private final Map<Pair<MessageType, String>, String> map = new HashMap<>();

    public void loadMessagesFromConfig(FileConfiguration config) {
        map.clear();
        Arrays.stream(MessageType.values()).forEach(messageType -> {
            ConfigurationSection section = config.getConfigurationSection(messageType.getKey());
            if (section != null) {
                section.getKeys(true).forEach(key -> set(messageType, key, section.getString(key)));
            }
        });
    }

    public STMessage get(MessageType type, String key, String orElse) {
        return new STMessage(getPrefix(), map.getOrDefault(new Pair<>(type, key), orElse));
    }

    public STMessage get(MessageType type, String key) {
        return get(type, key, "");
    }

    public STMessage get(MessageType type, String key, String orElse, Function<String, String> replacer) {
        return new STMessage(getPrefix(), replacer.apply(get(type, key, orElse).getMessage()));
    }

    public STMessage get(MessageType type, String key, Function<String, String> replacer) {
        return get(type, key, "", replacer);
    }

    public void set(MessageType type, String key, String value) {
        map.put(new Pair<>(type, key), ChatColor.translateAlternateColorCodes('&', value));
    }

    public String getOnlyString(MessageType type, String key) {
        return map.getOrDefault(new Pair<>(type, key), "");
    }

    public String getPrefix() {
        return map.getOrDefault(new Pair<>(MessageType.NONE, "prefix"), "");
    }
}