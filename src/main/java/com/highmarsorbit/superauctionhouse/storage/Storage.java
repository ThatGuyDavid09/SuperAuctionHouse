package com.highmarsorbit.superauctionhouse.storage;

import com.highmarsorbit.superauctionhouse.util.AuctionItem;

import java.util.List;

public interface Storage {
    boolean storeAuction(AuctionItem item);
    boolean storeAuctions(AuctionItem[] items);
    List<AuctionItem> getAllAuctions();
    List<AuctionItem> getCurrentAuctions();
    int getMaxId();
    boolean updateAuction(AuctionItem item);
    boolean deleteAuction(int auctionId);
    boolean open();
    boolean close();

    boolean selfTest();

    boolean clear();
}
