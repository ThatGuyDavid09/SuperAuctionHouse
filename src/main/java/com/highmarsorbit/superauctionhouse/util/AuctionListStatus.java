package com.highmarsorbit.superauctionhouse.util;

public class AuctionListStatus {
    private final boolean eventCanceled;
    private final boolean listSucceeded; // False if eventCanceled is true
    private final AuctionItem auction;

    public AuctionListStatus(boolean listSucceeded, boolean eventCanceled, AuctionItem auction) {
        this.listSucceeded = listSucceeded;
        this.eventCanceled = eventCanceled;
        this.auction = auction;
    }

    public AuctionItem getAuction() {
        return auction;
    }

    public boolean isSuccessful() {
        return listSucceeded;
    }

    public boolean isCanceled() {
        return eventCanceled;
    }

    /**
     * Returns whether an auction failed for technical reasons, not because an event canceled it
     * @return
     */
    public boolean isListFail() {
        return !listSucceeded && !eventCanceled;
    }
}
