package de.jahshaka.chestsystem.inventory.gui;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.economy.Economy;
import de.jahshaka.chestsystem.inventory.InventoryButton;
import de.jahshaka.chestsystem.inventory.InventoryGUI;
import de.jahshaka.chestsystem.lootbox.Lootbox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class LootboxesGUI extends InventoryGUI {

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 4*9, Component.text("Lootboxes",
                TextColor.color(0xFF465E)));
    }

    @Override
    public void decorate(Player player) {
        for (String id:ChestSystem.lootboxManager.getIds()) {
            Lootbox lb = new Lootbox(id);
            if (lb.isOnMainPage()) {
                this.addButton(lb.getLocationOnMainPage(), ItemStacks.createLootBoxButton(lb.createItemStack(player.getUniqueId(), "mainPage"), lb));
            }
        }

        int playerCredits = Economy.balance(player.getUniqueId());
        this.addButton(30, this.allBoxesButton());
        this.addButton(32, this.buyBoxesButton());
        this.addButton(31, this.creditsButtons(playerCredits));
        super.decorate(player);
    }

    private InventoryButton buyBoxesButton() {
        return new InventoryButton()
                .creator(player -> ItemStacks.buyBoxes())
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    player.closeInventory();
                    ChestSystem.guiManager.openGUI(new ShopGUI(), player);
                });
    }

    private InventoryButton creditsButtons(int playerCredits) {
        return new InventoryButton()
                .creator(player -> ItemStacks.credits(playerCredits))
                .consumer(event -> {
                    return;
                });
    }

    private InventoryButton allBoxesButton() {
        return new InventoryButton()
                .creator(player -> ItemStacks.allBoxes())
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    player.closeInventory();
                    ChestSystem.guiManager.openGUI(new AllChestsGUI(1), player);
                });
    }
}
