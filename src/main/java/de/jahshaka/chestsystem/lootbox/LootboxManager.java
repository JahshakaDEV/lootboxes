package de.jahshaka.chestsystem.lootbox;

import de.jahshaka.chestsystem.ChestSystem;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class LootboxManager {

    public List<String> getIds() {
        return ids;
    }

    private List<String> ids = new ArrayList<>();

    public LootboxManager() {
        getLootboxesFromConfig();
    }

    public void getLootboxesFromConfig() {
        ConfigurationSection rootSection = ChestSystem.configManager.getConfig("lootboxes").getCustomConfig().getConfigurationSection("");
        for (String key : rootSection.getKeys(false)) {
            if (!ids.contains(key)) {
                ids.add(key);
            }
        }
    }

}
