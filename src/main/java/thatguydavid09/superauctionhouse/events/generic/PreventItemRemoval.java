package thatguydavid09.superauctionhouse.events.generic;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import thatguydavid09.superauctionhouse.menus.BaseAuctionHouseMenu;

public class PreventItemRemoval implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getInventory() == BaseAuctionHouseMenu.auctionHouse && event.getSlot() >= 45) {
            event.setCancelled(true);
        }
    }
}
