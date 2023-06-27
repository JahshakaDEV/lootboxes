package de.jahshaka.chestsystem.utils.config;

import de.jahshaka.chestsystem.ChestSystem;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CustomConfig {
    private final File customFile;
    private final FileConfiguration customConfig;

    public CustomConfig(String name) {
        ChestSystem plugin = ChestSystem.getPlugin();
        this.customFile = new File(plugin.getDataFolder(), name + ".yml");
        if (!customFile.exists()) {
            try {
                customFile.createNewFile();
            } catch (IOException exception) {
                plugin.getLogger().warning("1687539210340: " + name + " config couldn't be created!");
                exception.printStackTrace();
            }
        }
        this.customConfig = YamlConfiguration.loadConfiguration(customFile);
        getCustomConfig().options().copyDefaults(true);
        InputStream defaultConfig = plugin.getResource(name + ".yml");
        if (defaultConfig != null) {
            getCustomConfig().setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defaultConfig)));
        }
        saveConfig();
    }

    public FileConfiguration getCustomConfig() {
        return customConfig;
    }

    public void saveConfig() {
        try {
            this.customConfig.save(customFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

