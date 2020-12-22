package thatguydavid09.superauctionhouse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class AdminCommands {
    public static boolean clear(Player player) {
        if (player.hasPermission("superauctionhouse.clear")) {
            BaseAuctionHouseMenu.clearAuctionHouse();
            player.sendMessage(ChatColor.GREEN + "Auction House has been cleared!");
            BaseAuctionHouseMenu.resetAuctionId();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

            return true;
        } else {
            return false;
        }
    }

    public static boolean sellWithSpecialName(Player player, String[] args) {
        if (player.hasPermission("superauctionhouse.sell.asothers")) {
            BaseAuctionHouseMenu.addItem(player.getInventory().getItemInMainHand(), player, Long.parseLong(args[1]), args[2]);
            return true;
        } else {
            return false;
        }
    }
}
