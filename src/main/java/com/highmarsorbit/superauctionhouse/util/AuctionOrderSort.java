package com.highmarsorbit.superauctionhouse.util;

public enum AuctionOrderSort {
    LOWEST_PRICE("lowest_price"),
    HIGHEST_PRICE("highest_price"),
    NEWEST_FIRST("newest_price"),
    ENDING_SOON("ending_soon");

    private String value;
    AuctionOrderSort(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
