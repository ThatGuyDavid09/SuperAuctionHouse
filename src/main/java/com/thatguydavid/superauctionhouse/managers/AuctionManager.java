package com.thatguydavid.superauctionhouse.managers;

import com.thatguydavid.superauctionhouse.events.AuctionListEvent;
import com.thatguydavid.superauctionhouse.storage.Storage;
import com.thatguydavid.superauctionhouse.util.AuctionItem;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;

public class AuctionManager {
    private ArrayList<AuctionItem> currentAuctions;
    private Storage store;
    private long maxId;
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
    public long getNextUsableId() {
        maxId += 1;
        return maxId;
    }

    public boolean listAuction(AuctionItem auction) {
        // Fire event and abort if canceled
        AuctionListEvent event = new AuctionListEvent(auction, auction.getSeller());
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return false;
        }

        boolean success = store.storeAuction(auction);
        if (!success) {
            return false;
        }
        currentAuctions.add(auction);
        return true;
    }

    public void refreshAvailableAuctions() {
        currentAuctions = (ArrayList<AuctionItem>) Arrays.asList(store.getCurrentAuctions());
    }

    public AuctionItem[] getAllAuctions() {
        return currentAuctions.toArray(new AuctionItem[0]);
    }
}
