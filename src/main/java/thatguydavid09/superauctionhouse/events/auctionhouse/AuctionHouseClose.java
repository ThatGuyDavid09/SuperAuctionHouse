package thatguydavid09.superauctionhouse.events.auctionhouse;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;

public class AuctionHouseClose implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        FileConfiguration config = SuperAuctionHouse.getInstance().getConfig();
        String AHName = config.getString("auctionhouse.names.auctionhouse");

        if (event.getView().getTitle().equals(AHName)) {
            BaseAuctionHouse.playersWithAHOpen.remove(player);
        }
    }
}
