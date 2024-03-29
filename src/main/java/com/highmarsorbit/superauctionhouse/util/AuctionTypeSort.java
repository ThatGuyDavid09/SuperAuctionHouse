package com.highmarsorbit.superauctionhouse.util;

public enum AuctionTypeSort {
    BOTH("both"),
    AUCTION_ONLY("auction_only"),
    BIN_ONLY("bin_only");

    private String value;
    AuctionTypeSort(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
