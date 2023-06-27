package de.jahshaka.chestsystem.items;

import de.jahshaka.chestsystem.ChestSystem;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {

    public List<String> getIds() {return ids;}

    private List<String> ids = new ArrayList<>();

    public ItemManager() {getItemsFromConfig();}

    public void getItemsFromConfig() {
        ConfigurationSection rootSection = ChestSystem.configManager.getConfig("items").getCustomConfig().getConfigurationSection("");
        for (String key : rootSection.getKeys(false)) {
            if (!ids.contains(key)) {
                ids.add(key);
            }
        }
    }

}
