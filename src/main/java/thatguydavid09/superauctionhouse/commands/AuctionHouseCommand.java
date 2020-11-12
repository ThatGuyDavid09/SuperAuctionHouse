package thatguydavid09.superauctionhouse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class AuctionHouseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length == 0) {
                try {
                    ((Player) sender).openInventory(BaseAuctionHouseMenu.auctionHousePages.get(0));
                } catch (Exception e) {
                    BaseAuctionHouseMenu.addPage();
                    ((Player) sender).openInventory(BaseAuctionHouseMenu.auctionHousePages.get(0));
                }
                return true;
            } else if (args.length == 2) {
                // TODO implement sell feature
                if (args[0].equals("sell")) {
                    SellCommand.sell((Player) sender, args);
                }
            } else if (args.length == 1) {
                if (args[0].equals("add")) {
                    for (int i = 0; i <= 41; i++) {
                        BaseAuctionHouseMenu.addItem(new ItemStack(Material.GRASS_BLOCK, 1), (Player) sender, Math.random() * (100 - 5 + 1) + 5);
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
            return true;
        }
        return true;
    }
}
