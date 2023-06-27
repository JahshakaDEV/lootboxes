package de.jahshaka.chestsystem.inventory.gui;

import com.google.common.collect.Range;
import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.inventory.InventoryGUI;
import de.jahshaka.chestsystem.items.Item;
import de.jahshaka.chestsystem.lootbox.Lootbox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class OpeningGUI extends InventoryGUI {

    private final Lootbox lootbox;
    HashMap<Item, Range<Integer>> items = new HashMap<>();
    Item winItem;
    private ItemStack win;

    private boolean cancel = false;

    public OpeningGUI(Lootbox lootbox) {
        this.lootbox = lootbox;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, 9, "");
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        if (lootbox.getAmountOfOwnedBoxes(event.getPlayer().getUniqueId()) >= 1) {
            initItemsAndWin();
            setPlaceHolders(false, event);
            Random r = new Random();
            List<ItemStack> items = new ArrayList<>();
            this.items.keySet().forEach(item -> items.add(item.createItemStack()));
            AtomicInteger i = new AtomicInteger(0);
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskTimer(ChestSystem.getPlugin(), (task) -> {
                if (i.get() < 15) {
                    i.getAndIncrement();
                    if (i.get() == 15) {
                        setPlaceHolders(true, event);
                        event.getInventory().setItem(4, win);
                        scheduler.runTaskLater(ChestSystem.getPlugin(), () -> {
                            event.getInventory().setItem(4, win);
                            event.getInventory().close();
                        }, 20L * 3);
                        task.cancel();
                    } else {
                        event.getInventory().setItem(4, items.get(r.nextInt(items.size())));
                    }
                } else {
                    task.cancel();
                }
            }, 0, 10);
        } else {
            cancel = true;
            event.getInventory().close();
        }
    }

    private void setPlaceHolders(boolean green, InventoryOpenEvent event) {
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                continue;
            }
            event.getInventory().setItem(i, green ? ItemStacks.greenGlassPlaceholder() : ItemStacks.placeHolder());
        }
    }

    private void initItemsAndWin() {
        int i = 0;
        for (String itemID : lootbox.getItemIDs()) {
            Item item = new Item(itemID);
            Range<Integer> r = Range.closed(i, (item.getProbability() + i));
            i = i + item.getProbability();
            items.put(item, r);
        }
        Random random = new Random();
        int r = random.nextInt(i + 1);
        items.forEach((key, value) -> {
            if (value.contains(r)) {
                win = key.createItemStack();
                winItem = key;
            }
        });
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        if (cancel) return;
        if (lootbox.removeLootboxes(event.getPlayer().getUniqueId(), 1)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), winItem.getCommand()
                    .replaceAll("%uuid", event.getPlayer().getUniqueId().toString())
                    .replaceAll("%name", event.getPlayer().getName()));
            ChestSystem.getPlugin().getSqlManager().addLootboxToHistory(event.getPlayer().getUniqueId(), lootbox.getId(), winItem.getId());
            event.getPlayer().sendMessage(Component.text("Du hast ", TextColor.color(0xC5C7C8))
                    .append(win.getItemMeta().displayName())
                    .append(Component.text(" gewonnen!", TextColor.color(0xC5C7C8))));
            super.onClose(event);
        }
    }
}
