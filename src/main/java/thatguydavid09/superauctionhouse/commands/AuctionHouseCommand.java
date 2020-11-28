package thatguydavid09.superauctionhouse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;
import thatguydavid09.superauctionhouse.menus.auctionhouse.PlayerAuctionHouse;

import java.util.HashMap;

public class AuctionHouseCommand implements CommandExecutor {
    private static HashMap<Player, PlayerAuctionHouse> auctionHousesByPlayer = new HashMap<>();

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
            } else if (args.length == 2) {
                if (args[0].equals("sell")) {
                    SellCommand.sell((Player) sender, args);
                } else if (args[0].equals("eco")) {
                    if (!BaseAuctionHouseMenu.banks.containsKey(player)) {
                        BaseAuctionHouseMenu.banks.put(player, 0L);
                    }

                    BaseAuctionHouseMenu.banks.put(player, BaseAuctionHouseMenu.banks.get(player) + Long.parseLong(args[1]));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                }
            } else if (args.length == 1) {
                if (args[0].equals("add")) {
                    for (int i = 0; i <= 41; i++) {
                        BaseAuctionHouseMenu.addItem(new ItemStack(Material.GRASS_BLOCK, 1), (Player) sender, (int) (Math.random() * (100 - 5 + 1) + 5));
                    }
                    player.sendMessage(ChatColor.GREEN + "41 grass blocks priced randomly have been added to the auction house!");
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                } else if (args[0].equals("stash")) {
                    for (ItemStack item : BaseAuctionHouseMenu.stashes.get(player)) {
                        BaseAuctionHouseMenu.giveItemToPlayer(item, player);
                    }
                    player.sendMessage(ChatColor.GREEN + "Your stash has been returned to you!");

                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                } else if (args[0].equals("eco")) {
                    if (!BaseAuctionHouseMenu.banks.containsKey(player)) {
                        BaseAuctionHouseMenu.banks.put(player, 0L);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                    }

                    player.sendMessage(String.valueOf(BaseAuctionHouseMenu.banks.get(player)));
                }

                if (args[0].equals("clear")) {
                    BaseAuctionHouseMenu.clearAuctionHouse();
                    player.sendMessage(ChatColor.GREEN + "Auction House has been cleared!");
                    BaseAuctionHouseMenu.auctionId = 0;
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
            return true;
        }
        return true;
    }

    public static PlayerAuctionHouse getAuctionHouse(Player player) {
        return auctionHousesByPlayer.get(player);
    }
}
