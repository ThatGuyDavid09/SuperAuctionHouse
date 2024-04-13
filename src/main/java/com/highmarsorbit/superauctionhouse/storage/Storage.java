package com.highmarsorbit.superauctionhouse.storage;

import com.highmarsorbit.superauctionhouse.util.AuctionItem;

public interface Storage {
    boolean storeAuction(AuctionItem item);
    boolean storeAuctions(AuctionItem[] items);
    AuctionItem[] getAllAuctions();
    AuctionItem[] getCurrentAuctions();
    int getMaxId();
    boolean updateAuction(int id, AuctionItem item);
    boolean reset();
    boolean selfTest();
}
