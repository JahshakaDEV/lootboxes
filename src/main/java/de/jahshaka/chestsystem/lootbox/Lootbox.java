package de.jahshaka.chestsystem.lootbox;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.database.ConnectionPoolManager;
import de.jahshaka.chestsystem.economy.Economy;
import de.jahshaka.chestsystem.items.Item;
import de.jahshaka.chestsystem.utils.config.CustomConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lootbox {

    protected String name;
    protected String id;
    protected String material;
    protected int price;
    protected int locationOnMainPage;
    protected boolean inShop;
    protected boolean onMainPage;
    protected List<String> itemIDs;

    public Lootbox(String id) {
        this.id = id;
        CustomConfig lootboxes = ChestSystem.configManager.getConfig("lootboxes");
        if (lootboxes.getCustomConfig().contains(id)) {
            this.name = ChatColor.translateAlternateColorCodes('&', lootboxes.getCustomConfig().getString(id + ".name"));
            this.price = lootboxes.getCustomConfig().getInt(id + ".price");
            this.material = lootboxes.getCustomConfig().getString(id + ".material");
            this.locationOnMainPage = lootboxes.getCustomConfig().getInt(id + ".location");
            this.inShop = lootboxes.getCustomConfig().getBoolean(id + ".inShop");
            this.onMainPage = lootboxes.getCustomConfig().getBoolean(id + ".onMainPage");
            this.itemIDs = lootboxes.getCustomConfig().getStringList(id + ".items");
        } else {
            ChestSystem.getPlugin().getLogger().severe("1687547615156: Error with lootbox, id: " + id);
            throw new RuntimeException("1687547615156: Error with lootbox, id: " + id);
        }
    }

    public List<Item> itemsInBox() {
        List<Item> items = new ArrayList<>();
        for (String itemID : itemIDs) {
            Item item = new Item(itemID);
            items.add(item);
        }
        return items;
    }

    public ItemStack createItemStack(UUID playerUUID, String lore, int... balance) {
        ItemStack box = new ItemStack(Material.getMaterial(material));
        ItemMeta boxItemMeta = box.getItemMeta();
        boxItemMeta.displayName(Component.text(name));
        List<Component> loreList = new ArrayList<>();
        int owned = getAmountOfOwnedBoxes(playerUUID);
        switch (lore) {
            case "mainPage" -> {
                TextComponent ownedBoxes = Component.text("> Du hast ", TextColor.color(0xC5C7C8)).append(Component.text(owned + " ", TextColor.color(0x48FF00))).append(Component.text(name));
                loreList.add(ownedBoxes);
                TextComponent leftClick = Component.text("Linksklick", TextColor.color(0x3DCBA6)).append(Component.text(", um deine Kisten zu sehen!", TextColor.color(0xC5C7C8)));
                TextComponent rightClick = Component.text("Rechtsklick", TextColor.color(0xFF465E)).append(Component.text(", um den Kisteninhalt zu betrachten!", TextColor.color(0xC5C7C8)));
                loreList.add(leftClick);
                loreList.add(rightClick);
            }
            case "shopPage" -> {
                TextComponent clickToBuy = Component.text("Klicke, um ", TextColor.color(0xC5C7C8)).append(Component.text("1 ", TextColor.color(0xFFD100))).append(Component.text("Kiste für ", TextColor.color(0xC5C7C8))).append(Component.text(price + " ", TextColor.color(0xFFD100))).append(Component.text(" zu kaufen!", TextColor.color(0xC5C7C8)));
                loreList.add(clickToBuy);
                if (balance[0] < price) {
                    TextComponent notEnoughCredits = Component.text("Du hast nicht genügend ", TextColor.color(0xFF465E)).append(Component.text("Credits", TextColor.color(0xFFD100)));
                    loreList.add(notEnoughCredits);
                }
            }
        }
        boxItemMeta.lore(loreList);
        box.setItemMeta(boxItemMeta);
        return box;
    }

    public boolean buyLootBox(UUID playerUUID, int amount) {
        int balance = Economy.balance(playerUUID);
        if (balance >= getPrice()) {
            int newAccountBalance = balance - getPrice();
            if (newAccountBalance >= 0) {
                Economy.setBalance(playerUUID, newAccountBalance);
                addLootboxes(playerUUID, amount);
                return true;
            }
        }
        return false;
    }

    public boolean hasLootboxRow(UUID playerUUID) {
        ConnectionPoolManager cpl = ChestSystem.getPlugin().getSqlManager().getCpl();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM lootboxes WHERE (player=?::uuid) AND (box=?);");
            preparedStatement.setString(1, String.valueOf(playerUUID));
            preparedStatement.setString(2, this.getId());
            rs = preparedStatement.executeQuery();
            boolean exists = rs.next();
            rs.close();
            preparedStatement.close();
            cpl.close(connection, preparedStatement, rs);
            return exists;
        } catch (SQLException e) {
            ChestSystem.getPlugin().getLogger().warning("1687596655787: SQL Exception!");
            e.printStackTrace();
        } finally {
            cpl.close(connection, preparedStatement, rs);
        }
        return false;
    }

    public int getAmountOfOwnedBoxes(UUID playerUUID) {
        ConnectionPoolManager cpl = ChestSystem.getPlugin().getSqlManager().getCpl();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            connection = cpl.getConnection();
            preparedStatement = connection.prepareStatement("SELECT amount FROM lootboxes WHERE player=?::uuid AND box=?");
            preparedStatement.setString(1, String.valueOf(playerUUID));
            preparedStatement.setString(2, this.getId());
            rs = preparedStatement.executeQuery();
            int ownedAmount = 0;
            if (rs.next()) {
                ownedAmount = rs.getInt(1);
            }
            rs.close();
            preparedStatement.close();
            connection.close();
            cpl.close(connection, preparedStatement, rs);
            return ownedAmount;
        } catch (SQLException e) {
            ChestSystem.getPlugin().getLogger().warning("1687596651047: SQL Exception!");
            e.printStackTrace();
        } finally {
            cpl.close(connection, preparedStatement, rs);
        }
        return 0;
    }

    public void addLootboxes(UUID playerUUID, int amount) {
        if (!hasLootboxRow(playerUUID)) {
            ConnectionPoolManager cpl = ChestSystem.getPlugin().getSqlManager().getCpl();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = cpl.getConnection();
                preparedStatement = connection.prepareStatement("INSERT INTO lootboxes(player, box, amount) VALUES (?, ?, ?);");
                preparedStatement.setObject(1, playerUUID);
                preparedStatement.setString(2, this.getId());
                preparedStatement.setInt(3, amount);
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
                cpl.close(connection, preparedStatement, null);
            } catch (SQLException e) {
                ChestSystem.getPlugin().getLogger().warning("1687596643809: SQL Exception!");
                e.printStackTrace();
            } finally {
                cpl.close(connection, preparedStatement, null);
            }
        } else {
            int ownedBoxes = getAmountOfOwnedBoxes(playerUUID);
            ConnectionPoolManager cpl = ChestSystem.getPlugin().getSqlManager().getCpl();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                connection = cpl.getConnection();
                preparedStatement = connection.prepareStatement("UPDATE lootboxes SET amount=? WHERE player=?::uuid AND box=?;");
                preparedStatement.setInt(1, ownedBoxes + amount);
                preparedStatement.setString(2, String.valueOf(playerUUID));
                preparedStatement.setString(3, this.getId());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();
                cpl.close(connection, preparedStatement, null);
            } catch (SQLException e) {
                ChestSystem.getPlugin().getLogger().warning("1687596647079: SQL Exception!");
                e.printStackTrace();
            } finally {
                cpl.close(connection, preparedStatement, null);
            }
        }
    }

    public boolean removeLootboxes(UUID playerUUID, int amount) {
        if (!hasLootboxRow(playerUUID)) {
            return false;
        } else {
            int ownedBoxes = getAmountOfOwnedBoxes(playerUUID);
            ConnectionPoolManager cpl = ChestSystem.getPlugin().getSqlManager().getCpl();
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            if (ownedBoxes >= amount) {
                try {
                    connection = cpl.getConnection();
                    preparedStatement = connection.prepareStatement("UPDATE lootboxes SET amount=? WHERE player=?::uuid AND box=?;");
                    preparedStatement.setInt(1, ownedBoxes - amount);
                    preparedStatement.setString(2, String.valueOf(playerUUID));
                    preparedStatement.setString(3, this.getId());
                    preparedStatement.executeUpdate();
                    preparedStatement.close();
                    connection.close();
                    cpl.close(connection, preparedStatement, null);
                    return true;
                } catch (SQLException e) {
                    ChestSystem.getPlugin().getLogger().warning("1687596647079: SQL Exception!");
                    e.printStackTrace();
                    return false;
                } finally {
                    cpl.close(connection, preparedStatement, null);
                }
            } else {
                return false;
            }
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getMaterial() {
        return material;
    }

    public int getPrice() {
        return price;
    }

    public int getLocationOnMainPage() {
        return locationOnMainPage;
    }

    public boolean isOnMainPage() {
        return onMainPage;
    }

    public boolean isInShop() {
        return inShop;
    }

    public List<String> getItemIDs() {
        return itemIDs;
    }
}