package thatguydavid09.superauctionhouse.events.auctionhouse;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class AuctionHousePlayerFreeze implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (BaseAuctionHouseMenu.playersFindingStuff.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
