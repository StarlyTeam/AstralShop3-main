package xyz.starly.astralshop.lang;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;

public class LanguageManager {

    private final JavaPlugin plugin;
    private final Logger LOGGER;
    private YamlConfiguration message;

    public LanguageManager(JavaPlugin plugin, File file) {
        this.plugin = plugin;
        this.LOGGER = plugin.getLogger();
        loadLanguage(file);
    }

    private void loadLanguage(File file) {
        try {
            JarFile jar = new JarFile(file);
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (name.startsWith("message/") && !entry.isDirectory()) {
                    plugin.saveResource(name, true);
                }
            }
        } catch (IOException e) {
            LOGGER.warning("Cannot found message files:" + e);
        }

        String lang = plugin.getConfig().getString("lang");
        message = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "message/" + lang + ".yml"));
    }
}