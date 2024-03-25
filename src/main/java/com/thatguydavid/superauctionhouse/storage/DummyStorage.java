package com.thatguydavid.superauctionhouse.storage;

import com.thatguydavid.superauctionhouse.util.AuctionItem;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A non-persistent storage for testing.
 */
public class DummyStorage implements Storage {
    private ArrayList<AuctionItem> items = new ArrayList<>();
    @Override
    public boolean storeAuction(AuctionItem item) {
        items.add(item);
        return true;
    }

    @Override
    public boolean storeAuctions(AuctionItem[] items) {
        this.items.addAll(List.of(items));
        return true;
    }

    @Override
    public AuctionItem[] getAllAuctions() {
        return items.toArray(new AuctionItem[0]);
    }
    @Override
    public AuctionItem[] getCurrentAuctions() {
        return items.stream()
                .filter(i -> i.getCreateTime().plusSeconds(i.getDuration().getSeconds()).isAfter(Instant.ofEpochMilli(System.currentTimeMillis())))
                .toArray(AuctionItem[]::new);
    }

    @Override
    public int getMaxId() {
        return items.stream()
                .mapToInt(AuctionItem::getId)
                .max().orElse(0);
    }

    @Override
    public boolean updateAuction(int id, AuctionItem item) {
        items = (ArrayList<AuctionItem>) items.stream()
                .filter(i -> i.getId() != id)
                .collect(Collectors.toList());
        return true;
    }

    @Override
    public boolean reset() {
        items.clear();
        return true;
    }
}
