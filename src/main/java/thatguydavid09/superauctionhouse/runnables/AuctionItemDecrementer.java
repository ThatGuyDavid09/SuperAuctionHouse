package thatguydavid09.superauctionhouse.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;
import thatguydavid09.superauctionhouse.menus.auctionhouse.PlayerAuctionHouse;

public class AuctionItemDecrementer {
    public void start() {
        if (SuperAuctionHouse.areAuctions) {
            Plugin plugin = SuperAuctionHouse.getInstance();
            BukkitScheduler scheduler = plugin.getServer().getScheduler();
            scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override
                public void run() {
                    BaseAuctionHouse.getAllItems().forEach(item -> {
                        item.decTime();
                        PlayerAuctionHouse.updateLore(item);
                    });

                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (BaseAuctionHouse.determineInvType(player) == 0) {
                            AuctionHouseCommand.getAuctionHouse(player).reloadAuctionHouse();
                        } else if (BaseAuctionHouse.determineInvType(player) == 1){
                            AuctionHouseCommand.getOwnAuctionHouse(player).reloadAuctionHouse();
                        }
                    }
                }
            }, 10L, 20L);
        }
    }
}
