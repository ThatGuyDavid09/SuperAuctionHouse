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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AuctionSortState)) {
            return false;
        }

        AuctionSortState otherState = (AuctionSortState) obj;
        return otherState.textFilter.equals(textFilter) && otherState.orderSort == orderSort && otherState.typeSort == typeSort;
    }
}
