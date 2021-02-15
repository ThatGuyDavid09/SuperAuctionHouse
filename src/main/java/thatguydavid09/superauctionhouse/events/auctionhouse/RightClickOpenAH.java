package thatguydavid09.superauctionhouse.events.auctionhouse;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;

public class RightClickOpenAH implements Listener {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("superauctionhouse.open.withblock")) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location loc = event.getClickedBlock().getLocation();
                String playerloc = loc.getWorld().getName() + "," + Math.floor(loc.getX()) + "," + Math.floor(loc.getY()) + "," + Math.floor(loc.getZ());
                FileConfiguration config = SuperAuctionHouse.getOpenBlocksConfig();

                for (String location : config.getStringList("auctionhouse.openblocks")) {
                    if (location.equals(playerloc)) {
                        AuctionHouseCommand.auctionHousesByPlayer.get(player).openAuctionHouse();
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
