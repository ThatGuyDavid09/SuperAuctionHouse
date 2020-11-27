package thatguydavid09.superauctionhouse.events.auctionhouse;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;

import java.util.List;

public class AuctionHouseActions {
    // Flip to next auction house page
    public static void nextPage(Player player) {
        // AH for this player
        List<Inventory> auctionHousePage = AuctionHouseCommand.auctionHousesByPlayer.get(player).auctionHouse;
        // This monster piece of crap code gets the current index of the ah we are on by getting the name of the forward and back arrows I know it is garbage
        int currentIndexOfInv = Integer.parseInt(player.getOpenInventory().getItem(50).getItemMeta().getDisplayName().split("/")[0].split(" ")[3]);
        player.closeInventory();
        player.openInventory(auctionHousePage.get(currentIndexOfInv));
    }

    // Flip to previous auction house page
    public static void previousPage(Player player) {
        List<Inventory> auctionHousePage = AuctionHouseCommand.auctionHousesByPlayer.get(player).auctionHouse;
        int currentIndexOfInv = Integer.parseInt(player.getOpenInventory().getItem(48).getItemMeta().getDisplayName().split("/")[0].split(" ")[3]);
        player.closeInventory();
        player.openInventory(auctionHousePage.get(currentIndexOfInv - 2));
    }

    // Cycle mode of sorting
    public static void cycleSortMode(Player player) {
        AuctionHouseCommand.auctionHousesByPlayer.get(player).sortMode++;

        if (AuctionHouseCommand.auctionHousesByPlayer.get(player).sortMode > 3) {
            AuctionHouseCommand.auctionHousesByPlayer.get(player).sortMode = 0;
        }
        AuctionHouseCommand.auctionHousesByPlayer.get(player).reloadAuctionHouse();
    }
}
