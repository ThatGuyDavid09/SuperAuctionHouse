package thatguydavid09.superauctionhouse.runnables;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;

public class AuctionHouseBackup {
    public static void scheduleBackups() {
        Plugin plugin = SuperAuctionHouse.getInstance();
        BukkitScheduler scheduler = plugin.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                BaseAuctionHouse.backUp();
                plugin.getLogger().info("Auction House has been backed up!");
            }
        }, 1200L, 1200L);
    }
}
