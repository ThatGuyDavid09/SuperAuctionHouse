package thatguydavid09.superauctionhouse.events.auctionhouse;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.menus.auctionhouse.PlayerAuctionHouse;

public class AuctionHouseRegister implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AuctionHouseCommand.auctionHousesByPlayer.put(event.getPlayer(), new PlayerAuctionHouse(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        AuctionHouseCommand.auctionHousesByPlayer.remove(event.getPlayer());
    }
}
