package com.highmarsorbit.superauctionhouse.managers;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.events.AuctionListEvent;
import com.highmarsorbit.superauctionhouse.storage.Storage;
import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import com.highmarsorbit.superauctionhouse.util.AuctionListStatus;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;

public class AuctionManager {
    private ArrayList<AuctionItem> currentAuctions;
    private Storage store;
    private int maxId;
    public AuctionManager(Storage store) {
        currentAuctions = new ArrayList<>();
        this.store = store;
        maxId = store.getMaxId();
    }

    /**
     * Returns the largest auction ID in use. Do not use to assign new IDs.
     * @return The largest auction ID currently in use.
     */
    public long queryMaxId() {
        return maxId;
    }

    /**
     * Returns the largest auction ID in use, plus one, rendering it safe to assign to new autions.
     * @return The next usable auction ID
     */
    public int getNextUsableId() {
        maxId += 1;
        return maxId;
    }

    public AuctionListStatus listAuction(AuctionItem auction) {
        // Fire event and abort if canceled
        AuctionListEvent event = new AuctionListEvent(auction, auction.getSeller());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return new AuctionListStatus(false, true, auction);
        }

        boolean success;

        // General catchall with database faults.
        try {
            success = store.storeAuction(auction);
        } catch (Exception e) {
            SuperAuctionHouse.getInstance().getLogger().severe("Error while listing item!");
            e.printStackTrace();
            success = false;
        }
        if (!success) {
            return new AuctionListStatus(false, false, auction);
        }
        currentAuctions.add(auction);
        return new AuctionListStatus(true, false, auction);
    }

    public void refreshAvailableAuctions() {
        currentAuctions = new ArrayList<>(Arrays.asList(store.getCurrentAuctions()));
    }

    public AuctionItem[] getAllAuctions() {
        return currentAuctions.toArray(new AuctionItem[0]);
    }
}
