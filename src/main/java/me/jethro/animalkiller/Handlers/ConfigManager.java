package me.jethro.animalkiller.Handlers;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {

    private final JavaPlugin plugin;
    @Getter
    private FileConfiguration config;
    private File configFile;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void init() {
        // Create the plugin data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        // Initialize config.yml
        configFile = new File(plugin.getDataFolder(), "config.yml");
        // If config.yml doesn't exist, create it by copying it from the resources
        if (!configFile.exists()) {
            copyDefaultConfig("config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);

    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void copyDefaultConfig(String fileName) {
        try (InputStream inputStream = plugin.getResource(fileName)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + fileName);
            }

            Files.copy(inputStream, configFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}