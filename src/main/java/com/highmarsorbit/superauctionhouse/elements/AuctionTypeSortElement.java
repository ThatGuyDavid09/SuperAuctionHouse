package com.highmarsorbit.superauctionhouse.elements;

import com.highmarsorbit.superauctionhouse.inventories.AuctionHouse;
import com.highmarsorbit.superauctionhouse.util.AuctionOrderSort;
import com.highmarsorbit.superauctionhouse.util.AuctionTypeSort;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AuctionTypeSortElement extends ElementBase {
    private AuctionHouse ahRef;
    public AuctionTypeSortElement(char character, InventoryGui gui, AuctionHouse ahRef) {
        super(character, gui);

        this.ahRef = ahRef;

        element = new GuiStateElement(character,
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.typeSort = AuctionTypeSort.BOTH;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "both",
                        new ItemStack(Material.DIAMOND),
                        ChatColor.RESET + "" + ChatColor.BLUE + "Filter by:",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Both",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Auctions only",
                        ChatColor.RESET + "" + ChatColor.GRAY + "BIN (Buy it Now) only",
                        " ",
                        ChatColor.RESET + "" + ChatColor.YELLOW + "Click to change filter!"
                ),
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.typeSort = AuctionTypeSort.AUCTION_ONLY;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "auction_only",
                        new ItemStack(Material.CLOCK),
                        ChatColor.RESET + "" + ChatColor.BLUE + "Filter by:",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Both",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Auctions only",
                        ChatColor.RESET + "" + ChatColor.GRAY + "BIN (Buy it Now) only",
                        " ",
                        ChatColor.RESET + "" + ChatColor.YELLOW + "Click to change filter!"
                ),
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.typeSort = AuctionTypeSort.BIN_ONLY;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "bin_only",
                        new ItemStack(Material.GOLD_INGOT),
                        ChatColor.RESET + "" + ChatColor.BLUE + "Filter by:",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Both",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Auctions only",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ BIN (Buy it Now) only",
                        " ",
                        ChatColor.RESET + "" + ChatColor.YELLOW + "Click to change filter!"
                )
        );

        ((GuiStateElement) element).setState(ahRef.sortState.typeSort.toString());
    }
}
