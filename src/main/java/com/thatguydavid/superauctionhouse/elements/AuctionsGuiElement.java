package com.thatguydavid.superauctionhouse.elements;

import com.thatguydavid.superauctionhouse.util.AuctionItem;
import com.thatguydavid.superauctionhouse.util.AuctionSortState;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AuctionsGuiElement extends ElementBase {
    private AuctionSortState sortState;
    public AuctionsGuiElement(char character, AuctionItem[] items, InventoryGui gui, AuctionSortState sortState) {
        super(character, gui);
        this.sortState = sortState;

        element = new GuiElementGroup(character);

        List<AuctionItem> filteredItems = filterItems(items);
        for (AuctionItem auction : filteredItems) {
            ((GuiElementGroup) element).addElement(auction.getGuiElement(gui));
        }
    }

    private List<AuctionItem> filterItems(AuctionItem[] items) {
        Instant now = Instant.ofEpochMilli(System.currentTimeMillis());
        return switch (sortState.orderSort) {
            case LOWEST_PRICE -> Arrays.stream(items)
                    .filter(item -> item.getEndTime().isAfter(now))
                    .sorted(Comparator.comparingDouble(AuctionItem::getPrice))
                    .collect(Collectors.toList());
            case HIGHEST_PRICE -> Arrays.stream(items)
                    .filter(item -> item.getEndTime().isAfter(now))
                    .sorted(Comparator.comparingDouble(AuctionItem::getPrice).reversed())
                    .collect(Collectors.toList());
            case NEWEST_FIRST -> Arrays.stream(items)
                    .filter(item -> item.getEndTime().isAfter(now))
                    .sorted(Comparator.comparingLong(item -> ((AuctionItem) item).getCreateTime().getEpochSecond()).reversed())
                    .collect(Collectors.toList());
            case ENDING_SOON -> Arrays.stream(items)
                    .filter(item -> item.getEndTime().isAfter(now))
                    .sorted(Comparator.comparingLong(item -> item.getEndTime().getEpochSecond()))
                    .collect(Collectors.toList());
        };
    }
}
