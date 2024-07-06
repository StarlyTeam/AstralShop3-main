package kr.starly.astralshop.message;

import kr.starly.libs.inventory.access.component.AdventureComponentWrapper;
import kr.starly.libs.inventory.access.component.ComponentWrapper;
import kr.starly.libs.util.Pair;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                section.getKeys(false).forEach(key -> set(messageType, key, section.getString(key)));
            }
        });
    }

    public STMessage get(MessageType type, String key, String orElse) {
        return new STMessage(getPrefix(), map.getOrDefault(Pair.of(type, key), orElse));
    }

    public STMessage get(MessageType type, String key) {
        return get(type, key, "");
    }

    public STMessage get(MessageType type, String key, String orElse, TagResolver replacer) {
        return new STMessage(getPrefix(), get(type, key, orElse).getMessage(), replacer);
    }

    public STMessage get(MessageType type, String key, TagResolver replacer) {
        return get(type, key, "", replacer);
    }

    public void set(MessageType type, String key, String value) {
        map.put(Pair.of(type, key), value);
    }

    public String getOnlyString(MessageType type, String key) {
        return map.getOrDefault(Pair.of(type, key), "");
    }

    public String getPrefix() {
        return map.getOrDefault(Pair.of(MessageType.NONE, "prefix"), "");
    }


    public static String INFO_PREFIX = "<b><white>[<i><#FFCC00>!</i><white>]</b> ";
    public static String CONTROL_PREFIX = "<b><white>[<i><#F6904E>!</i><white>]</b> ";

    public static ComponentWrapper parseMessage(String message) {
        return new AdventureComponentWrapper(MiniMessage.miniMessage().deserialize(message));
    }

    public static List<ComponentWrapper> parseMessage(String... lines) {
        return Arrays.stream(lines).map((line) -> new AdventureComponentWrapper(MiniMessage.miniMessage().deserialize(line))).collect(Collectors.toList());
    }
}