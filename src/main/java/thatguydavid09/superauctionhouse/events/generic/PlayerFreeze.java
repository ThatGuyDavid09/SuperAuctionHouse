package thatguydavid09.superauctionhouse.events.generic;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;
import thatguydavid09.superauctionhouse.menus.sell.SellMenu;

public class PlayerFreeze implements Listener {
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (BaseAuctionHouseMenu.playersFindingStuff.contains(event.getPlayer()) || SellMenu.playersEnteringPrice.containsKey(event.getPlayer()) || SellMenu.playersEnteringName.containsKey(event.getPlayer()) || SellMenu.playersEnteringTime.containsKey(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
