package de.jahshaka.chestsystem.utils.config;

import de.jahshaka.chestsystem.ChestSystem;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

public class ConfigManager {

    private final HashMap<String, CustomConfig> configs = new HashMap<>();
    private final ChestSystem plugin = ChestSystem.getPlugin();

    public void newConfig(String name) {
        if (!configs.containsKey(name)) {
            CustomConfig config = new CustomConfig(name);
            configs.put(name, config);
        } else {
            plugin.getLogger().log(Level.SEVERE, "[1676856767] Config " + name + " couldn't be created.");
        }
    }

    public CustomConfig getConfig(String name) {
        if (configs.containsKey(name)) {
            return configs.get(name);
        } else {
            plugin.getLogger().log(Level.SEVERE, "[1676356767] Config " + name + " couldn't be loaded.");
            return null;
        }
    }

    public void createNewLootbox(String id, String name, int price, String materialName, int inventoryLocation) {
        CustomConfig lootboxes = getConfig("lootboxes");
        lootboxes.getCustomConfig().set(id + ".name", name);
        lootboxes.getCustomConfig().set(id + ".price", price);
        lootboxes.getCustomConfig().set(id + ".material", materialName);
        lootboxes.getCustomConfig().set(id + ".location", inventoryLocation);
        lootboxes.saveConfig();
    }

    public void createNewItem() {
        CustomConfig items = getConfig("items");
        items.getCustomConfig().set("testItem.name", "&7TestItem");
        items.getCustomConfig().set("testItem.amount", 3);
        items.getCustomConfig().set("testItem.material", "DIAMOND_SWORD");
        List<String> lore = new ArrayList<>();
        lore.add("123");
        lore.add("321");
        items.getCustomConfig().set("testItem.lore", lore);
        HashMap<NamespacedKey, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.PROTECTION_FIRE.getKey(), 1);
        enchants.put(Enchantment.DURABILITY.getKey(), 3);
        String jsonString = new JSONObject(enchants).toJSONString();
        items.getCustomConfig().set("testItem.enchantments", jsonString);
        items.saveConfig();
    }
}
