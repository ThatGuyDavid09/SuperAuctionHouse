package com.highmarsorbit.superauctionhouse.elements.browser;

import com.highmarsorbit.superauctionhouse.elements.BaseElement;
import com.highmarsorbit.superauctionhouse.util.*;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;

import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuctionsGuiElement extends BaseElement {
    private AuctionSortState sortState;
    public AuctionsGuiElement(char character, AuctionItem[] items, InventoryGui gui, AuctionSortState sortState) {
        super(character, gui);
        this.sortState = sortState;

        element = new GuiElementGroup(character);

        List<AuctionItem> filteredItems = filterItems(items);
        for (AuctionItem auction : filteredItems) {
            ((GuiElementGroup) element).addElement(AuctionItemElementHelper.getAuctionGuiElement(auction, gui));
        }
    }

    private List<AuctionItem> filterItems(AuctionItem[] items) {
        Instant now = Instant.ofEpochMilli(System.currentTimeMillis());

        Stream<AuctionItem> showableItems = Arrays.stream(items)
                .filter(AuctionItem::isValid);

        Stream<AuctionItem> typeFilteredItems = showableItems
                .filter(item -> switch (sortState.typeSort) {
                    case BOTH -> true;
                    case AUCTION_ONLY -> item.getAuctionType() == AuctionType.AUCTION;
                    case BIN_ONLY -> item.getAuctionType() == AuctionType.BUY_IT_NOW;
                });

        Stream<AuctionItem> nameFilteredItems = typeFilteredItems
                .filter(item -> ChatColor.stripColor(ItemUtils.getItemName(item.getItem())).toLowerCase()
                        .contains(sortState.textFilter.toLowerCase()));

        return switch (sortState.orderSort) {
            case LOWEST_PRICE -> nameFilteredItems
                    .sorted(Comparator.comparingDouble(AuctionItem::getPrice))
                    .collect(Collectors.toList());
            case HIGHEST_PRICE -> nameFilteredItems
                    .sorted(Comparator.comparingDouble(AuctionItem::getPrice).reversed())
                    .collect(Collectors.toList());
            case NEWEST_FIRST -> nameFilteredItems
                    .sorted(Comparator.comparingLong(item -> ((AuctionItem) item).getCreateTime().getEpochSecond()).reversed())
                    .collect(Collectors.toList());
            case ENDING_SOON -> nameFilteredItems
                    .sorted(Comparator.comparingLong(item -> item.getEndTime().getEpochSecond()))
                    .collect(Collectors.toList());
        };
    }
}
