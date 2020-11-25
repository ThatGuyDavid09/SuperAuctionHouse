package thatguydavid09.superauctionhouse.events.generic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseActions;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class PreventItemRemoval implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            // Identify inventory as ah
            if (BaseAuctionHouseMenu.auctionHousePages.contains(event.getClickedInventory())) {
                if (event.getRawSlot() == 50 && event.getCurrentItem().getType() == Material.ARROW) {
                    AuctionHouseActions.nextPage((Player) event.getWhoClicked());
                    event.setCancelled(true);

                } else if (event.getRawSlot() == 48 && event.getCurrentItem().getType() == Material.ARROW) {
                    AuctionHouseActions.previousPage((Player) event.getWhoClicked());
                    event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
