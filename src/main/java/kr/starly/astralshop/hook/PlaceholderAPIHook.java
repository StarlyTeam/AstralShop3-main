package kr.starly.astralshop.hook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@AllArgsConstructor
public class PlaceholderAPIHook {

    @Getter private static PlaceholderAPIHook hook;

    @Getter private final boolean isHooked;

    public static void initializeHook(JavaPlugin plugin) {
        if (hook != null) throw new UnsupportedOperationException("Cannot re-assign singleton instance.");

        boolean isHooked = false;
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            isHooked = true;
        } catch (ClassNotFoundException ignored) {
            plugin.getLogger().warning("의존성 플러그인(PlaceholderAPI)을 발견하지 못했습니다.");
        }

        hook = new PlaceholderAPIHook(isHooked);
    }

    public String setPlaceholders(Player player, String message) {
        if (!isHooked) return message;

        return PlaceholderAPI.setPlaceholders(player, message);
    }

    public List<String> setPlaceholders(Player player, String... messages) {
        return setPlaceholders(player, List.of(messages));
    }

    public List<String> setPlaceholders(Player player, List<String> messages) {
        if (!isHooked) return messages;

        return PlaceholderAPI.setPlaceholders(player, messages);
    }
}