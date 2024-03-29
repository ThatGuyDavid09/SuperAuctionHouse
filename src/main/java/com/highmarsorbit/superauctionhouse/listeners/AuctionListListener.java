package com.highmarsorbit.superauctionhouse.listeners;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import com.highmarsorbit.superauctionhouse.util.ItemUtils;
import com.highmarsorbit.superauctionhouse.events.AuctionListEvent;
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
                        + ChatColor.RESET + "auctioned %d %s" +
                        " for %.2f",
                event.getSeller().getName(),
                event.getAuction().getItem().getAmount(),
                ItemUtils.getItemName(auction.getItem()),
                event.getAuction().getPrice());
        // FIXME sort out this nonsense with the fine logs not showing up
        Bukkit.getLogger().info(SuperAuctionHouse.prefixNoColor + message);
        auction.getSeller().sendMessage(SuperAuctionHouse.prefix + message);
    }
}