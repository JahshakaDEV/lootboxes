package de.jahshaka.chestsystem.inventory.gui;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.inventory.InventoryButton;
import de.jahshaka.chestsystem.inventory.InventoryGUI;
import de.jahshaka.chestsystem.lootbox.Lootbox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AllChestsGUI extends InventoryGUI {

    public AllChestsGUI(int page) {
        this.page = page;
    }
    private final List<String> ownedBoxes = new ArrayList<>();
    private int page = 1;

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 4 * 9, Component.text("Alle Kisten",
                TextColor.color(0xFF465E)));
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        for (String id : ChestSystem.lootboxManager.getIds()) {
            Lootbox lootbox = new Lootbox(id);
            int amount = lootbox.getAmountOfOwnedBoxes(event.getPlayer().getUniqueId());
            for (int i = 0; i < amount; i++) {
                ownedBoxes.add(id);
            }
        }
        super.onOpen(event);
    }

    @Override
    public void decorate(Player player) {
        int min = (page - 1) * 26;
        int max = min + 26;
        int location = 0;
        for (int i = (page - 1) * 26; i <= max; i++) {
            if (i >= ownedBoxes.size()) {
                break;
            } else {
                String id = ownedBoxes.get(i);
                Lootbox lootbox = new Lootbox(id);
                this.addButton(location, ItemStacks.createLootBoxButton(lootbox.createItemStack(player.getUniqueId(), "mainPage"), lootbox));
                location++;
            }
        }

        if (ownedBoxes.size() > page*27) {
            this.addButton(28, continueButton());
        }
        this.addButton(27, backButton());
        super.decorate(player);
    }

    private InventoryButton backButton() {
        return new InventoryButton()
                .creator(player -> ItemStacks.backButton())
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    if (page == 1) {
                        player.closeInventory();
                        ChestSystem.guiManager.openGUI(new LootboxesGUI(), player);
                    } else {
                        page = page - 1;
                        player.closeInventory();
                        ChestSystem.guiManager.openGUI(new AllChestsGUI(page), player);
                    }
                });
    }

    private InventoryButton continueButton() {
        return new InventoryButton()
                .creator(player -> ItemStacks.continueButton())
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    page++;
                    player.closeInventory();
                    ChestSystem.guiManager.openGUI(new AllChestsGUI(page), player);
                });
    }

}
