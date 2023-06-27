package de.jahshaka.chestsystem.inventory.gui;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.inventory.InventoryButton;
import de.jahshaka.chestsystem.lootbox.Lootbox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemStacks {

    static ItemStack allBoxes() {
        ItemStack is = new ItemStack(Material.CHEST);
        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Alle Kisten",
                TextColor.color(0xFF465E),
                TextDecoration.BOLD));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Klicke hier um alle deine Kisten anzusehen",
                        TextColor.color(0xC5C7C8))
                .decoration(TextDecoration.ITALIC, false));
        im.lore(lore);
        is.setItemMeta(im);
        return is;
    }

    static ItemStack credits(int playerCredits) {
        ItemStack is = new ItemStack(Material.GOLD_INGOT);
        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Du hast ")
                .color(TextColor.color(0xE8FF02))
                .decorate(TextDecoration.BOLD)
                .append(Component.text(playerCredits + " Credits!",
                        TextColor.color(0x48FF00),
                        TextDecoration.BOLD)));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Auf der Seite shop.iostein.net kannst du Credits kaufen!",
                        TextColor.color(0xC5C7C8))
                .decoration(TextDecoration.ITALIC, false));
        im.lore(lore);
        is.setItemMeta(im);
        return is;
    }

    static ItemStack buyBoxes() {
        ItemStack is = new ItemStack(Material.NETHER_STAR);
        ItemMeta im = is.getItemMeta();
        im.displayName(Component.text("Kisten kaufen!",
                TextColor.color(0xFFD100),
                TextDecoration.BOLD));
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Klicke hier, um Kisten zu kaufen!",
                        TextColor.color(0xC5C7C8))
                .decoration(TextDecoration.ITALIC, false));
        im.lore(lore);
        is.setItemMeta(im);
        return is;
    }

    static ItemStack backButton() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL("https://textures.minecraft.net/texture/864f779a8e3ffa231143fa69b96b14ee35c16d669e19c75fd1a7da4bf306c"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        meta.setOwnerProfile(profile);
        meta.displayName(Component.text("Zurück", TextColor.color(0xC5C7C8)));
        head.setItemMeta(meta);
        return head;
    }

    static ItemStack continueButton() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            textures.setSkin(new URL("https://textures.minecraft.net/texture/682ad1b9cb4dd21259c0d75aa315ff389c3cef752be3949338164bac84a96e"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        meta.setOwnerProfile(profile);
        meta.displayName(Component.text("Weiter", TextColor.color(0xC5C7C8)));
        head.setItemMeta(meta);
        return head;
    }

    static ItemStack placeHolder() {
        ItemStack itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(""));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    static ItemStack greenGlassPlaceholder() {
        ItemStack itemStack = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(Component.text(""));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    static InventoryButton createLootBoxButton(ItemStack itemStack, Lootbox lootbox) {
        return new InventoryButton()
                .creator(player -> itemStack)
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    if (event.isRightClick()) {
                        player.closeInventory();
                        ChestSystem.guiManager.openGUI(new LootboxItemsGUI(lootbox), player);
                    } else {
                        player.closeInventory();
                        if (lootbox.getAmountOfOwnedBoxes(player.getUniqueId()) >= 1) {
                            ChestSystem.guiManager.openGUI(new OpeningGUI(lootbox), player);
                        } else {
                            player.sendMessage(Component.text("Du hast nicht genügend ", TextColor.color(0xFF465E))
                                    .append(Component.text(lootbox.getName())));
                        }
                    }
                });
    }
}
