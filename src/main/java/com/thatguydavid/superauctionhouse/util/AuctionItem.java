package com.thatguydavid.superauctionhouse.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class AuctionItem {
    private final int id;
    private final ItemStack item;
    private final Player seller;
    private final String sellerName;
    private final Instant createTime;
    private final Duration duration;
    private final AuctionType auctionType;

    private double price;
    private Player highestBidder;

    public AuctionItem(int id, ItemStack item, Player seller, double price, Duration duration, AuctionType auctionType) {
        this(id, item, seller, price, duration, auctionType, seller.getDisplayName());
    }

    public AuctionItem(int id, ItemStack item, Player seller, double price, Duration duration, AuctionType auctionType, String sellerName) {
        this.id = id;
        this.item = item;
        this.seller = seller;
        this.price = price;
        this.duration = duration;
        this.auctionType = auctionType;
        this.sellerName = sellerName;
        this.highestBidder = null;

        this.createTime = Instant.ofEpochMilli(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public ItemStack getItem() {
        return item;
    }

    public Player getSeller() {
        return seller;
    }

    public String getSellerName() {
        return sellerName;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public double getPrice() {
        return price;
    }

    public Player getHighestBidder() {
        return highestBidder;
    }

    public String toString() {
        String itemName = "";

        if (item.getItemMeta() == null || !item.getItemMeta().hasDisplayName()) {
            itemName = item.getItemMeta().getDisplayName();
        } else {
            itemName = item.getType().name();
        }
        return itemName + ChatColor.RESET;
    }
}
