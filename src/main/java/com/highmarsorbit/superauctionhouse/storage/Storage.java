package com.highmarsorbit.superauctionhouse.storage;

import com.highmarsorbit.superauctionhouse.util.AuctionItem;

public interface Storage {
    boolean storeAuction(AuctionItem item);
    boolean storeAuctions(AuctionItem[] items);
    AuctionItem[] getAllAuctions();
    AuctionItem[] getCurrentAuctions();
    int getMaxId();
    boolean updateAuction(AuctionItem item);
    boolean deleteAuction(int auctionId);
    boolean close();
    boolean reset();
    boolean selfTest();
}
