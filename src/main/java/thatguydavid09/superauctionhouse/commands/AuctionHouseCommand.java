package thatguydavid09.superauctionhouse.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.menus.auctionhouse.PlayerAuctionHouse;

import java.util.HashMap;

public class AuctionHouseCommand implements CommandExecutor {
    private static HashMap<Player, PlayerAuctionHouse> auctionHousesByPlayer = new HashMap<>();

    public static PlayerAuctionHouse getAuctionHouse(Player player) {
        return auctionHousesByPlayer.get(player);
    }

    private static void permissionError(Player player) {
        player.sendMessage(ChatColor.RED + "You don't have permission to do that!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length == 0) {
                // Open ah
                if (!auctionHousesByPlayer.containsKey(player)) {
                    auctionHousesByPlayer.put(player, new PlayerAuctionHouse(player));
                }
                // Reset query upon player opening ah
                auctionHousesByPlayer.get(player).query = "";
                auctionHousesByPlayer.get(player).openAuctionHouse();

                return true;
            }

            switch (args[0]) {
                // Dev commands
                case "add":
                    if (!DevCommands.add(player)) {
                        permissionError(player);
                    }
                case "eco":
                    if (!DevCommands.eco(player)) {
                        permissionError(player);
                    }

                // Player commands
                case "sell":
                    if (!PlayerCommands.sell(player, args)) {
                        permissionError(player);
                    }

                // Admin commands
                case "clear":
                    if (!AdminCommands.clear(player)) {
                        permissionError(player);
                    }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
        }
        return true;
    }
}
