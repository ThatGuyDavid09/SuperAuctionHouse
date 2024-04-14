package com.highmarsorbit.superauctionhouse.util;

public class AuctionUpdateStatus {
    private final boolean technicalFailure;
    private final boolean success;
    private final AuctionItem auction;
    private String extraMessage;

    public AuctionUpdateStatus(boolean success, boolean technicalFailure, String message, AuctionItem auction) {
        this.technicalFailure = technicalFailure;
        this.success = success;
        this.auction = auction;
        this.extraMessage = message;
    }

    public AuctionItem getAuction() {
        return auction;
    }

    public boolean isSuccessful() {
        return success;
    }

    public void setMessage(String msg) {
        extraMessage = msg;
    }

    public String getMessage() {
        return extraMessage;
    }

    /**
     * Returns whether an auction failed for technical reasons, not because an event cancelled it
     * @return
     */
    public boolean isTechnicalFailure() {
        return technicalFailure;
    }
}
