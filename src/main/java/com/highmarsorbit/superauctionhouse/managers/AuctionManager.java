package com.highmarsorbit.superauctionhouse.managers;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.events.AuctionBuyBidEvent;
import com.highmarsorbit.superauctionhouse.events.AuctionClaimEvent;
import com.highmarsorbit.superauctionhouse.events.AuctionListEvent;
import com.highmarsorbit.superauctionhouse.storage.Storage;
import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import com.highmarsorbit.superauctionhouse.util.AuctionUpdateStatus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class AuctionManager {
    private ArrayList<AuctionItem> currentAuctions;
    private Storage store;
    private int maxId;

    public AuctionManager(Storage store) {
        this.store = store;
        maxId = store.getMaxId();
    }

    public boolean init() {
        boolean success = store.open();
        if (!success) {
            return false;
        }

        refreshAvailableAuctions();
        return true;
    }

    /**
     * Returns the largest auction ID in use. Do not use to assign new IDs.
     *
     * @return The largest auction ID currently in use.
     */
    public long getMaxId() {
        return maxId;
    }

    /**
     * Returns the largest auction ID in use, plus one, rendering it safe to assign to new auctions.
     *
     * @return The next usable auction ID
     */
    public int getNextUsableId() {
        maxId += 1;
        return maxId;
    }

    public AuctionUpdateStatus listAuction(AuctionItem auction) {
        // Fire event and abort if cancelled
        AuctionListEvent event = new AuctionListEvent(auction, auction.getSeller());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return new AuctionUpdateStatus(false, false, "cancelled", auction);
        }

        boolean success;

        // General catchall with database faults.
        try {
            success = store.storeAuction(auction);
        } catch (Exception e) {
            SuperAuctionHouse.getInstance().getLogger().warning("Error while listing item!");
            e.printStackTrace();
            success = false;
        }
        if (!success) {
            return new AuctionUpdateStatus(false, true, "list fail", auction);
        }
        currentAuctions.add(auction);
        return new AuctionUpdateStatus(true, false, "success", auction);
    }

    public AuctionUpdateStatus placeBid(long auctionId, Player bidder, double bid) {
        AuctionItem auction = getAuctionItemById(auctionId);
        AuctionBuyBidEvent bidEvent = new AuctionBuyBidEvent(auction, bidder, bid);
        Bukkit.getPluginManager().callEvent(bidEvent);


        if (bidEvent.isCancelled()) {
            return new AuctionUpdateStatus(false, false, "cancelled", auction);
        }

        boolean success;

        // General catchall with database faults.
        try {
            success = store.updateAuction(auction);
        } catch (Exception e) {
            SuperAuctionHouse.getInstance().getLogger().warning("Error while placing bid on item!");
            e.printStackTrace();
            success = false;
        }
        if (!success) {
            return new AuctionUpdateStatus(false, true, "bid fail", auction);
        }

        auction.setBid(bid, bidder);
        currentAuctions.removeIf(i -> i.getId() == auctionId);
        currentAuctions.add(auction);
        return new AuctionUpdateStatus(true, false, "success", auction);
    }

    /**
     * Specifically for BIN usage. Gives item to player and removes it from auction house. Happens immediately after sale.
     * If there is no room in the player's inventory, the item is not deleted from AH and instead kept in stasis
     *
     * @param auctionId
     * @param player
     * @return
     */
    public AuctionUpdateStatus giveBINToPlayer(long auctionId, Player player) {
        AuctionItem auction = getAuctionItemById(auctionId);
        AuctionClaimEvent claimEvent = new AuctionClaimEvent(auction, player);
        Bukkit.getPluginManager().callEvent(claimEvent);

        if (claimEvent.isCancelled()) {
            return new AuctionUpdateStatus(false, false, "cancelled", auction);
        }

        if (player.getInventory().firstEmpty() == -1) {
            return new AuctionUpdateStatus(false, false, "inventory insert fail", auction);
        }


        boolean success = deleteAuction(auction.getId());
        if (!success) {
            return new AuctionUpdateStatus(false, true, "delete fail", auction);
        }

        HashMap<Integer, ItemStack> notPlaced = player.getInventory().addItem(auction.getItem());
        // Shouldn't happen, but if item could not be placed in inventory here, then it's already been deleted and thus
        // won't be shown in the "own auctions" page to claim. We drop an item on the ground in lieu of just deleting it.
        Set<Integer> keys = notPlaced.keySet();
        for (Integer key : keys) {
            ItemStack itemToDrop = notPlaced.get(key);
            player.getWorld().dropItem(player.getLocation(), itemToDrop);
        }
        return new AuctionUpdateStatus(true, false, "success", auction);
    }

    public boolean deleteAuction(int id) {
        boolean success;
        try {
            success = store.deleteAuction(id);
        } catch (Exception e) {
            SuperAuctionHouse.getInstance().getLogger().warning("Error while deleting auction!");
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    private AuctionItem getAuctionItemById(long auctionId) {
        return currentAuctions.stream().filter(item -> item.getId() == auctionId).collect(Collectors.toList()).get(0);
    }

    public void refreshAvailableAuctions() {
        currentAuctions = new ArrayList<>(Arrays.asList(store.getCurrentAuctions()));
    }

    public AuctionItem[] getAllAuctions() {
        return currentAuctions.toArray(new AuctionItem[0]);
    }

    public boolean disable() {
        boolean success = store.close();
        return success;
    }
}
