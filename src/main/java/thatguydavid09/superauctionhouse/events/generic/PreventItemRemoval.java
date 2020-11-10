package thatguydavid09.superauctionhouse.events.generic;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseActions;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class PreventItemRemoval implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getInventory() == BaseAuctionHouseMenu.baseAuctionHouse && event.getSlot() >= 45) {
            if (event.getCurrentItem() == BaseAuctionHouseMenu.goForwardArrow) {
                AuctionHouseActions.nextPage((Player) event.getWhoClicked());
            } else if (event.getCurrentItem() == BaseAuctionHouseMenu.goBackArrow){
                AuctionHouseActions.previousPage((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
    }
}
