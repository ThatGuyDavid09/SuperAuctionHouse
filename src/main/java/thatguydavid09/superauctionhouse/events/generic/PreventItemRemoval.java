package thatguydavid09.superauctionhouse.events.generic;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseActions;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import java.util.List;

public class PreventItemRemoval implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null && event.getView().getTitle().equals("Auction House")) {
            // Identify inventory as ah
            List<Inventory> auctionHousePage = AuctionHouseCommand.auctionHousesByPlayer.get(event.getWhoClicked()).auctionHouse;
            if (auctionHousePage.contains(event.getClickedInventory())) {
                if (event.getRawSlot() == 50 && event.getCurrentItem().getType() == Material.ARROW) {
                    AuctionHouseActions.nextPage((Player) event.getWhoClicked());
                    event.setCancelled(true);
                } else if (event.getRawSlot() == 48 && event.getCurrentItem().getType() == Material.ARROW) {
                    AuctionHouseActions.previousPage((Player) event.getWhoClicked());
                    event.setCancelled(true);
                } else if (event.getRawSlot() == 49) {
                    AuctionHouseActions.cycleSortMode((Player) event.getWhoClicked());
                    event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
