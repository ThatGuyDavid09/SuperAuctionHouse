package thatguydavid09.superauctionhouse.events.generic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseActions;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import static org.bukkit.Bukkit.getLogger;

public class PreventItemRemoval implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getInventory() == BaseAuctionHouseMenu.baseAuctionHouse && event.getSlot() >= 45) {
            if (event.getRawSlot() == 50 && event.getCurrentItem().getType() == Material.ARROW) {
                getLogger().info("Clicked go forward");
                AuctionHouseActions.nextPage((Player) event.getWhoClicked());
            } else if (event.getRawSlot() == 48 && event.getCurrentItem().getType() == Material.ARROW){
                getLogger().info("Clicked go back");
                AuctionHouseActions.previousPage((Player) event.getWhoClicked());
            }
            event.setCancelled(true);
        }
    }
}
