package thatguydavid09.superauctionhouse.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.menus.auctionhouse.PlayerAuctionHouse;

import java.util.HashMap;

public class AuctionHouseCommand implements CommandExecutor {
    public static HashMap<Player, PlayerAuctionHouse> auctionHousesByPlayer = new HashMap<>();

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
                auctionHousesByPlayer.get(player).openAuctionHouse(true);

                return true;
            }

            switch (args[0]) {
                // Dev commands
                case "add":
                    if (!DevCommands.add(player)) {
                        permissionError(player);
                    }
                    break;
                case "eco":
                    if (!DevCommands.eco(player)) {
                        permissionError(player);
                    }
                    break;

                // Player commands
                case "sell":
                    if (!PlayerCommands.sell(player)) {
                        permissionError(player);
                    }
                    break;

                // Admin commands
                case "clear":
                    if (args.length == 3) {
                        AdminCommands.clearConfirm(Bukkit.getPlayer(args[1]), args[2]);
                        break;
                    }

                    if (!AdminCommands.clear(player)) {
                        permissionError(player);
                    }
                    break;

                case "help":
                    if (!PlayerCommands.help(player)) {
                        permissionError(player);
                    }
                    break;

                case "setloc":
                    if (!AdminCommands.setloc(player)) {
                        permissionError(player);
                    }
                    break;

                default:
                    if (!PlayerCommands.openByPlayer(player, args[0])) {
                        permissionError(player);
                    }
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
        }
        return true;
    }
}
