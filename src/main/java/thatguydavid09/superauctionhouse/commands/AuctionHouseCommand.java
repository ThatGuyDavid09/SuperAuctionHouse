package thatguydavid09.superauctionhouse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class AuctionHouseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                ((Player) sender).openInventory(BaseAuctionHouseMenu.auctionHousePages.get(0));
                return true;
            } else if (args.length == 2) {
                // TODO implement sell feature
                if (args[0].equals("sell")) {
                    SellCommand.sell((Player) sender, args);
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
            return true;
        }
        return true;
    }
}
