package thatguydavid09.superauctionhouse.runnables;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import thatguydavid09.superauctionhouse.AuctionItem;
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
                    for (AuctionItem item : BaseAuctionHouse.getAllItems()) {
                        if (item.isAuction()) {
                            item.decTime();
                            PlayerAuctionHouse.updateLore(item);
                        }
                    }

                    for (Player player : BaseAuctionHouse.playersWithAHOpen) {
                        AuctionHouseCommand.auctionHousesByPlayer.get(player).update(true);
                        AuctionHouseCommand.auctionHousesByPlayer.get(player).openAuctionHouse();
                    }
                }
            }, 10L, 20L);
        }
    }
}
