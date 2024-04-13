package com.highmarsorbit.superauctionhouse.util;

public class AuctionListStatus {
    private final boolean eventCancelled;
    private final boolean listSucceeded; // False if eventCancelled is true
    private final AuctionItem auction;

    public AuctionListStatus(boolean listSucceeded, boolean eventCancelled, AuctionItem auction) {
        this.listSucceeded = listSucceeded;
        this.eventCancelled = eventCancelled;
        this.auction = auction;
    }

    public AuctionItem getAuction() {
        return auction;
    }

    public boolean isSuccessful() {
        return listSucceeded;
    }

    public boolean isCancelled() {
        return eventCancelled;
    }

    /**
     * Returns whether an auction failed for technical reasons, not because an event cancelled it
     * @return
     */
    public boolean isListFail() {
        return !listSucceeded && !eventCancelled;
    }
}
