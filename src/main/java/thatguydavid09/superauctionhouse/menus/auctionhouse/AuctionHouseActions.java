package thatguydavid09.superauctionhouse.menus.auctionhouse;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;

import java.util.List;

public class AuctionHouseActions {
    /**
     * Display the next ah page to the player
     *
     * @param player The player this is affecting
     */
    public static void nextPage(Player player) {
        // AH for this player
        List<Inventory> auctionHousePage = AuctionHouseCommand.getAuctionHouse(player).getAuctionHouse();
        // This monster piece of crap code gets the current index of the ah we are on by getting the name of the forward and back arrows I know it is garbage
        int currentIndexOfInv = Integer.parseInt(player.getOpenInventory().getItem(50).getItemMeta().getDisplayName().split("/")[0].split(" ")[3]);
        player.openInventory(auctionHousePage.get(currentIndexOfInv));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
    }

    /**
     * Display the previous ah page to the player
     *
     * @param player The player this is affecting
     */
    public static void previousPage(Player player) {
        List<Inventory> auctionHousePage = AuctionHouseCommand.getAuctionHouse(player).getAuctionHouse();
        int currentIndexOfInv = Integer.parseInt(player.getOpenInventory().getItem(48).getItemMeta().getDisplayName().split("/")[0].split(" ")[3]);
        player.openInventory(auctionHousePage.get(currentIndexOfInv - 2));

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
    }

    /**
     * Change the sort mode of the ah
     *
     * @param player The player this is affecting
     */
    public static void cycleSortMode(Player player) {
        AuctionHouseCommand.getAuctionHouse(player).sortMode++;

        if (AuctionHouseCommand.getAuctionHouse(player).sortMode > 3) {
            AuctionHouseCommand.getAuctionHouse(player).sortMode = 0;
        }
        AuctionHouseCommand.getAuctionHouse(player).reloadAuctionHouse();

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
    }

    /**
     * Ask the player for the query to sort by item name
     *
     * @param player The player this is affecting
     */
    public static void find(Player player) {
        player.closeInventory();
        BaseAuctionHouse.playersFindingStuff.add(player);
        // Send player instructions
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Type the name of the item you want to find in chat!").color(ChatColor.GREEN).create());

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
    }
}
