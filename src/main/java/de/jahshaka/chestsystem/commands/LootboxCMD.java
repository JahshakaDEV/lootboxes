package de.jahshaka.chestsystem.commands;

import de.jahshaka.chestsystem.ChestSystem;
import de.jahshaka.chestsystem.inventory.gui.LootboxesGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LootboxCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        ChestSystem.guiManager.openGUI(new LootboxesGUI(), player);
        return true;
    }
}
