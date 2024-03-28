package com.highmarsorbit.superauctionhouse.util;

public class AuctionSortState {
    public String textFilter;
    public AuctionTypeSort typeSort;
    public AuctionOrderSort orderSort;

    public AuctionSortState() {
        this("", AuctionTypeSort.BOTH, AuctionOrderSort.LOWEST_PRICE);
    }

    public AuctionSortState(String textFilter, AuctionTypeSort typeSort, AuctionOrderSort orderSort) {
        this.textFilter = textFilter;
        this.typeSort = typeSort;
        this.orderSort = orderSort;
    }
}
