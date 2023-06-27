package de.jahshaka.chestsystem.listener;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.inventory.gui.LootboxesGUI;
import de.jahshaka.chestsystem.inventory.gui.ShopGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionListener implements Listener {

    ChestSystem plugin = ChestSystem.getPlugin();
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (!plugin.getSqlManager().isPlayerInEconomyTable(event.getPlayer().getUniqueId())) {
            plugin.getSqlManager().addPlayerToEconomyTable(event.getPlayer().getUniqueId());
        }
    }

}
