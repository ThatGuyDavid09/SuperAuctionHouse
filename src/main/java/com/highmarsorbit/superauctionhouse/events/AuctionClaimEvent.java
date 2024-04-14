package com.highmarsorbit.superauctionhouse.events;

import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AuctionClaimEvent extends Event implements Cancellable {
    private boolean cancelled = false;
    private static final HandlerList HANDLERS = new HandlerList();
    private final AuctionItem auction;
    private final Player claimant;

    public AuctionClaimEvent(AuctionItem auction, Player claimant) {
        this.auction = auction;
        this.claimant = claimant;
    }

    public AuctionItem getAuction() {
        return auction;
    }

    public Player getClaimant() {
        return claimant;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
