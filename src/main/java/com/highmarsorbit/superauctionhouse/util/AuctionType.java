package com.highmarsorbit.superauctionhouse.util;

public enum AuctionType {
    AUCTION("auction", "Auction"),
    BUY_IT_NOW("buy_it_now", "Buy It Now");

    private final String internalName;
    private final String readableName;
    AuctionType(String internalName, String readableName) {
        this.internalName = internalName;
        this.readableName = readableName;
    }

    @Override
    public String toString() {
        return internalName;
    }

    public String getReadableName() {
        return readableName;
    }

}
