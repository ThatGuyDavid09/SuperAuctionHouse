package com.highmarsorbit.superauctionhouse.events;

import com.highmarsorbit.superauctionhouse.inventories.AuctionBrowserMenu;
import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AuctionBuyBidEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final AuctionItem auction;
    private final Player bidder;
    private final double price;
    private boolean isCancelled = false;

    public AuctionBuyBidEvent(AuctionItem auction, Player bidder, double price) {
        this.auction = auction;
        this.bidder = bidder;
        this.price = price;
    }

    public AuctionItem getAuction() {
        return auction;
    }

    public Player getBidder() {
        return bidder;
    }

    public double getPrice() {
        return price;
    }

    public AuctionBrowserMenu.AuctionType getAuctionType() {
        return auction.getAuctionType();
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
