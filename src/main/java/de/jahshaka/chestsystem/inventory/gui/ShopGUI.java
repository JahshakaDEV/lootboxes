package de.jahshaka.chestsystem.inventory.gui;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.economy.Economy;
import de.jahshaka.chestsystem.inventory.InventoryButton;
import de.jahshaka.chestsystem.inventory.InventoryGUI;
import de.jahshaka.chestsystem.lootbox.Lootbox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopGUI extends InventoryGUI {
    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 36, Component.text("Kisten kaufen",
                TextColor.color(0xFFD100),
                TextDecoration.BOLD));
    }

    @Override
    public void decorate(Player player) {
        int location = 10;
        int balance = Economy.balance(player.getUniqueId());
        for (int i = 0; i < ChestSystem.lootboxManager.getIds().size(); i++) {
            String id = ChestSystem.lootboxManager.getIds().get(i);
            Lootbox lb = new Lootbox(id);
            if (lb.isInShop()) {
                this.addButton(location+i, this.createLootBoxBuyButton(lb.createItemStack(player.getUniqueId(), "shopPage", balance), lb, balance));
                location++;
            }
        }

        this.addButton(27, backButton());
        super.decorate(player);
    }

    private InventoryButton createLootBoxBuyButton(ItemStack itemStack, Lootbox lootbox, int balance) {
        return new InventoryButton()
                .creator(player -> itemStack)
                .consumer(event -> {
                    Player player = (Player) event.getWhoClicked();
                    if (event.isLeftClick()) {
                        if (balance >= lootbox.getPrice()) {
                            if (lootbox.buyLootBox(player.getUniqueId(), 1)) {
                                player.sendMessage(Component.text("Du hast erfolgreich eine ", TextColor.color(0x48FF00))
                                        .append(Component.text(lootbox.getName()))
                                        .append(Component.text(" gekauft!", TextColor.color(0x48FF00))));
                                player.closeInventory();
                            }
                        } else {
                            player.sendMessage(Component.text("Du hast nicht genügend Credits um ", TextColor.color(0xFF465E))
                                    .append(Component.text(lootbox.getName()))
                                    .append(Component.text(" zu kaufen!", TextColor.color(0xFF465E))));
                            player.closeInventory();
                        }
                    } else if (event.isRightClick()) {
                        player.closeInventory();
                        ChestSystem.guiManager.openGUI(new LootboxItemsGUI(lootbox), player);
                    }

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
