package thatguydavid09.superauctionhouse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;
import thatguydavid09.superauctionhouse.menus.auctionhouse.PlayerAuctionHouse;

import java.util.HashMap;

public class AuctionHouseCommand implements CommandExecutor {
    public static HashMap<Player, PlayerAuctionHouse> auctionHousesByPlayer = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                // Open ah
                if (!auctionHousesByPlayer.containsKey(player)) {
                    auctionHousesByPlayer.put(player, new PlayerAuctionHouse(player));
                }
                auctionHousesByPlayer.get(player).openAuctionHouse();
                return true;
            } else if (args.length == 2) {
                // TODO implement buy feature
                if (args[0].equals("sell")) {
                    SellCommand.sell((Player) sender, args);
                }
            } else if (args.length == 1) {
                if (args[0].equals("add")) {
                    for (int i = 0; i <= 41; i++) {
                        BaseAuctionHouseMenu.addItem(new ItemStack(Material.GRASS_BLOCK, 1), (Player) sender, (int) (Math.random() * (100 - 5 + 1) + 5));
                    }
                    player.sendMessage(ChatColor.GREEN + "41 grass blocks priced randomly have been added to the auction house!");
                }

                if (args[0].equals("clear")) {
                    BaseAuctionHouseMenu.clearAuctionHouse();
                    player.sendMessage(ChatColor.GREEN + "Auction House has been cleared!");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
            return true;
        }
        return true;
    }
}
