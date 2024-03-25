package com.thatguydavid.superauctionhouse.commands;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AHCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SuperAuctionHouse.prefix + ChatColor.RED + "Only players may open the auction house!");
            return true;
        }
        Player player = (Player) sender;

        player.sendMessage(SuperAuctionHouse.prefix + "Pretend the auction house opened.");
        return true;
    }
}
