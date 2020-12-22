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

    private static void permissionError(String message) {

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
                    return DevCommands.add(player);
                case "eco":
                    return DevCommands.eco(player);

                // Player commands
                case "sell":
                    return PlayerCommands.sell(player, args);

                // Admin commands
                case "clear":
                    return AdminCommands.clear(player);
            }

            switch (args.length) {
                case 1:
                    switch (args[0]) {
                        case "add":

                            break;
                        case "eco":

                            break;
                        case "clear":

                            break;
                    }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
            return true;
        }
        return true;
    }
}
