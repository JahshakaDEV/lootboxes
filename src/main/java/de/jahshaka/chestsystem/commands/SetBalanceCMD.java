package de.jahshaka.chestsystem.commands;

import de.jahshaka.chestsystem.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetBalanceCMD implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String value = args[0];
            Player player = (Player) sender;
            Economy.setBalance(player.getUniqueId(), Integer.parseInt(value));
            return true;
        }
        return false;
    }
}
