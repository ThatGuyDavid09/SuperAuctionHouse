package com.thatguydavid.superauctionhouse.events;

import com.thatguydavid.superauctionhouse.util.AuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AuctionListEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final AuctionItem auction;
    private final Player seller;
    private boolean isCancelled;

    public AuctionListEvent(AuctionItem auction, Player seller) {
        this.auction = auction;
        this.seller = seller;
    }

    public AuctionItem getAuction() {
        return auction;
    }

    public Player getSeller() {
        return seller;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
