package thatguydavid09.superauctionhouse.events.auctionhouse;

import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class AuctionHouseActions {
    // Flip to next auction house page
    public static void nextPage(Player player) {
        // This monster piece of crap code gets the current index of the ah we are on by getting the name of the forward and back arrows I know it is garbage
        int currentIndexOfInv = Integer.parseInt(player.getOpenInventory().getItem(50).getItemMeta().getDisplayName().split("/")[0].split(" ")[3]);
        player.closeInventory();
        player.openInventory(BaseAuctionHouseMenu.auctionHousePages.get(currentIndexOfInv));
    }

    // Flip to previous auction house page
    public static void previousPage(Player player) {
        int currentIndexOfInv = Integer.parseInt(player.getOpenInventory().getItem(48).getItemMeta().getDisplayName().split("/")[0].split(" ")[3]);
        player.closeInventory();
        player.openInventory(BaseAuctionHouseMenu.auctionHousePages.get(currentIndexOfInv - 2));
    }
}
