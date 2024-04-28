package com.highmarsorbit.superauctionhouse.util;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.mojang.datafixers.util.Pair;
import org.apache.commons.collections.map.LinkedMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public class AuctionItem implements Serializable {
    private final int id;
    private final ItemStack item;
    private final Player seller;
    private final String sellerName;
    private final Instant createTime;
    private final Instant endTime;
    private final AuctionType auctionType;
    private boolean bought = false;

    private final double initialPrice;
    private double price;
    private final LinkedMap bidders = new LinkedMap();

    public AuctionItem(ItemStack item, Player seller, double price, Duration duration, AuctionType auctionType) {
        this(item, seller, price, duration, auctionType, seller.getDisplayName());
    }

    public AuctionItem(ItemStack item, Player seller, double price, Duration duration, AuctionType auctionType, String sellerName) {
        this.id = SuperAuctionHouse.getAuctionManager().getNextUsableId();
        this.item = item;
        this.seller = seller;
        this.initialPrice = price;
        this.price = price;
        this.auctionType = auctionType;
        this.sellerName = sellerName;

        this.createTime = Instant.ofEpochMilli(System.currentTimeMillis());
        this.endTime = createTime.plusSeconds(duration.getSeconds());
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

    public Instant getEndTime() {
        return endTime;
    }

    public Duration getDurationRemaining() {
        return Duration.between(Instant.ofEpochMilli(System.currentTimeMillis()), endTime);
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public double getPrice() {
        return price;
    }

    public String toString() {
        String itemName = "";

        if (item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
            itemName = item.getItemMeta().getDisplayName();
        } else {
            itemName = item.getType().name();
        }
        return itemName + ChatColor.RESET;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public boolean getBought() {
        return bought;
    }

    public void setBid(double bid, Player bidder) {
        if (!isValid()) {
            return;
        }

        this.price = bid;
        this.bidders.put(bidder, bid);

        if (auctionType == AuctionType.BUY_IT_NOW) {
            bought = true;
        }
    }

    public Pair<Player, Double> getLastBidder() {
//        return bidders.get(bidders.size() - 1);
        Object lastBidder = bidders.lastKey();
        Object lastPrice = bidders.get(lastBidder);

        return new Pair<>((Player) lastBidder, (Double) lastPrice);
    }

    public LinkedMap getBidders() {
        return bidders;
    }

    public boolean isExpired() {
        return getDurationRemaining().isNegative();
    }

    public boolean isValid() {
        // An auction is valid (can be bid on) if it is not already bought and is not expired
        return !(bought || isExpired());
    }
}
