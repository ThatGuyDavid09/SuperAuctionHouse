package com.thatguydavid.superauctionhouse.storage;

import com.thatguydavid.superauctionhouse.util.AuctionItem;

public interface Storage {
    boolean storeAuction(AuctionItem item);
    boolean storeAuctions(AuctionItem[] items);
    AuctionItem[] getAllAuctions();
    AuctionItem[] getCurrentAuctions();
    int getMaxId();
    boolean updateAuction(int id, AuctionItem item);
    boolean reset();
}
