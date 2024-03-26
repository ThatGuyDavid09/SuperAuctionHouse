package com.thatguydavid.superauctionhouse.listeners;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import com.thatguydavid.superauctionhouse.events.AuctionListEvent;
import com.thatguydavid.superauctionhouse.util.AuctionItem;
import com.thatguydavid.superauctionhouse.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class AuctionListListener implements Listener {
    @EventHandler( priority = EventPriority.MONITOR)
    public void onAuctionList(AuctionListEvent event) {
        AuctionItem auction = event.getAuction();
        String message = String.format(
                "Player %s "
                        + ChatColor.RESET + "auctioned item %s" +
                        " for %.2f", event.getSeller().getDisplayName(), ItemUtils.getItemName(auction.getItem()), event.getAuction().getPrice());
        // FIXME sort out this nonsense with the fine logs not showing up
        Bukkit.getLogger().info(SuperAuctionHouse.prefixNoColor + message);
        auction.getSeller().sendMessage(SuperAuctionHouse.prefix + message);
    }
}
