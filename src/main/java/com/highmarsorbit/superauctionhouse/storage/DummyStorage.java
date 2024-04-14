package com.highmarsorbit.superauctionhouse.storage;

import com.highmarsorbit.superauctionhouse.util.AuctionItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A non-persistent storage for testing.
 */
public class DummyStorage implements Storage {
    private ArrayList<AuctionItem> auctions = new ArrayList<>();
    @Override
    public boolean storeAuction(AuctionItem item) {
        auctions.add(item);
        return true;
    }

    @Override
    public boolean storeAuctions(AuctionItem[] items) {
        this.auctions.addAll(List.of(items));
        return true;
    }

    @Override
    public AuctionItem[] getAllAuctions() {
        return auctions.toArray(new AuctionItem[0]);
    }
    @Override
    public AuctionItem[] getCurrentAuctions() {
        return auctions.stream()
                .filter(i -> !i.isExpired())
                .toArray(AuctionItem[]::new);
    }

    @Override
    public int getMaxId() {
        return auctions.stream()
                .mapToInt(AuctionItem::getId)
                .max().orElse(0);
    }

    @Override
    public boolean updateAuction(AuctionItem item) {
        int id = item.getId();
        auctions.removeIf(i -> i.getId() == id);
        auctions.add(item);
        return true;
    }

    @Override
    public boolean deleteAuction(int auctionId) {
        auctions.removeIf(i -> i.getId() == auctionId);
        return true;
    }

    @Override
    public boolean reset() {
        auctions.clear();
        return true;
    }

    @Override
    public boolean selfTest() {
        return true;
    }

    @Override
    public boolean close() {
        // TODO make this save to file
        return true;
    }
}
