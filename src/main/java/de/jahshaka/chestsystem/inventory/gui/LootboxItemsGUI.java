package de.jahshaka.chestsystem.inventory.gui;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.inventory.InventoryButton;
import de.jahshaka.chestsystem.inventory.InventoryGUI;
import de.jahshaka.chestsystem.items.Item;
import de.jahshaka.chestsystem.lootbox.Lootbox;
import de.jahshaka.chestsystem.lootbox.LootboxManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LootboxItemsGUI extends InventoryGUI {

    public LootboxItemsGUI(Lootbox lootbox) {
        this.lootbox = lootbox;
    }
    private Lootbox lootbox = null;

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 4*9, Component.text("Inhalt der Kiste",
                TextColor.color(0xFF465E)));
    }

    @Override
    public void decorate(Player player) {
        for (int i = 0; i < lootbox.itemsInBox().size(); i++) {
            Item item = lootbox.itemsInBox().get(i);
            this.addButton(i, this.createItemButton(item.createItemStack()));
        }
        this.addButton(27, backButton());
        super.decorate(player);
    }

    private InventoryButton createItemButton(ItemStack itemStack) {
        return new InventoryButton()
                .creator(player -> itemStack)
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    player.sendMessage("item clicked");
                });
    }

    private InventoryButton backButton() {
        return new InventoryButton()
                .creator(player -> ItemStacks.backButton())
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    player.closeInventory();
                    ChestSystem.guiManager.openGUI(new LootboxesGUI(), player);
                });
    }

}
