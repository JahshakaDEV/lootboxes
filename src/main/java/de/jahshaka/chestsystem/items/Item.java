package de.jahshaka.chestsystem.items;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.utils.config.CustomConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Item {

    protected String id;
    protected String name;
    protected List<String> lore;
    protected int amount = 1;
    protected String material;
    protected HashMap<NamespacedKey, Integer> enchantments;
    protected int probability;
    protected String command;

    public Item(String id) {
        this.id = id;
        CustomConfig items = ChestSystem.configManager.getConfig("items");
        if (items.getCustomConfig().contains(id)) {
            this.name = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(items.getCustomConfig().getString(id + ".name")));
            this.amount = items.getCustomConfig().getInt(id + ".amount");
            this.material = items.getCustomConfig().getString(id + ".material");
            this.probability = items.getCustomConfig().getInt(id + ".probability");
            this.command = items.getCustomConfig().getString(id + ".command");
            List<String> loreList = items.getCustomConfig().getStringList(id + ".lore");
            if (loreList.size() > 0) {
                for (int i = 0; i < loreList.size(); i++) {
                    String lore = loreList.get(i);
                    lore = ChatColor.translateAlternateColorCodes('&', lore);
                    loreList.remove(i);
                    loreList.add(i, lore);
                }
                this.lore = loreList;
            }
            String jsonEnchantments = items.getCustomConfig().getString(id + ".enchantments");
            if (jsonEnchantments != null) {
                try {
                    this.enchantments = new ObjectMapper().readValue(jsonEnchantments, HashMap.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            ChestSystem.getPlugin().getLogger().severe("1687625393462: Error with item, id: " + id);
            throw new RuntimeException("1687625393462: Error with item, id: " + id);
        }
    }

    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(Material.getMaterial(this.material));
        ItemMeta itemMeta = item.getItemMeta();
        if (enchantments != null && enchantments.size() > 0) {
            enchantments.entrySet().forEach(enchantmentIntegerEntry -> {
                String key = String.valueOf(enchantmentIntegerEntry.getKey());
                int level = enchantmentIntegerEntry.getValue();
                itemMeta.addEnchant(Enchantment.getByKey(NamespacedKey.fromString(key)), level, true);
            });
        }
        itemMeta.displayName(Component.text(this.name));
        if (lore != null) {
            List<Component> loreComponent = new ArrayList<>();
            lore.forEach(s -> {
                Component comp = Component.text(s);
                loreComponent.add(comp);
            });
            itemMeta.lore(loreComponent);
        }
        if (amount != 0) {
            item.setAmount(this.amount);
        }

        item.setItemMeta(itemMeta);
        return item;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getAmount() {
        return amount;
    }

    public String getMaterial() {
        return material;
    }

    public HashMap<NamespacedKey, Integer> getEnchantments() {
        return enchantments;
    }

    public int getProbability() {
        return probability;
    }

    public String getCommand() {
        return command;
    }
}
