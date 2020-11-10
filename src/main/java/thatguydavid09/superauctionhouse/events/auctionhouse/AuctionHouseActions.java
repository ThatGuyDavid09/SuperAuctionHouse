package thatguydavid09.superauctionhouse.events.auctionhouse;

import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class AuctionHouseActions {
    // FIXME why doesn't this work
    // Flip to next auction house page
    public static void nextPage(Player player) {
        int indexOfInv = BaseAuctionHouseMenu.auctionHousePages.indexOf(player.getOpenInventory().getTopInventory());
        player.closeInventory();
        player.openInventory(BaseAuctionHouseMenu.auctionHousePages.get(indexOfInv + 1));
    }

    // Flip to previous auction house page
    public static void previousPage(Player player) {
        int indexOfInv = BaseAuctionHouseMenu.auctionHousePages.indexOf(player.getOpenInventory().getTopInventory());
        player.closeInventory();
        player.openInventory(BaseAuctionHouseMenu.auctionHousePages.get(indexOfInv - 1));
    }
}
