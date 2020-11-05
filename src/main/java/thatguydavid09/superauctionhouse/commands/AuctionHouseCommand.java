package thatguydavid09.superauctionhouse.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;

public class AuctionHouseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).openInventory(SuperAuctionHouse.auctionHouse);
            return true;
        } else {
         sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
         return true;
        }
    }
}
