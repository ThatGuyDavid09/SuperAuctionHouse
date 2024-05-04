package com.highmarsorbit.superauctionhouse.elements.browser;

import com.highmarsorbit.superauctionhouse.elements.BaseElement;
import com.highmarsorbit.superauctionhouse.inventories.AuctionBrowserMenu;
import com.highmarsorbit.superauctionhouse.util.ChatUtils;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AuctionTypeSortElement extends BaseElement {
    private AuctionBrowserMenu ahRef;
    public AuctionTypeSortElement(char character, InventoryGui gui, AuctionBrowserMenu ahRef) {
        super(character, gui);

        this.ahRef = ahRef;

        element = new GuiStateElement(character,
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.typeSort = AuctionBrowserMenu.AuctionTypeSort.BOTH;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "both",
                        new ItemStack(Material.DIAMOND),
                        ChatUtils.RESET + ChatColor.BLUE + "Filter by:",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Both",
                        ChatUtils.RESET + ChatColor.GRAY + "Auctions only",
                        ChatUtils.RESET + ChatColor.GRAY + "BIN (Buy it Now) only",
                        " ",
                        ChatUtils.RESET + ChatColor.YELLOW + "Click to change filter!"
                ),
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.typeSort = AuctionBrowserMenu.AuctionTypeSort.AUCTION_ONLY;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "auction_only",
                        new ItemStack(Material.CLOCK),
                        ChatUtils.RESET + ChatColor.BLUE + "Filter by:",
                        ChatUtils.RESET + ChatColor.GRAY + "Both",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Auctions only",
                        ChatUtils.RESET + ChatColor.GRAY + "BIN (Buy it Now) only",
                        " ",
                        ChatUtils.RESET + ChatColor.YELLOW + "Click to change filter!"
                ),
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.typeSort = AuctionBrowserMenu.AuctionTypeSort.BIN_ONLY;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "bin_only",
                        new ItemStack(Material.GOLD_INGOT),
                        ChatUtils.RESET + ChatColor.BLUE + "Filter by:",
                        ChatUtils.RESET + ChatColor.GRAY + "Both",
                        ChatUtils.RESET + ChatColor.GRAY + "Auctions only",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ BIN (Buy it Now) only",
                        " ",
                        ChatUtils.RESET + ChatColor.YELLOW + "Click to change filter!"
                )
        );

        ((GuiStateElement) element).setState(ahRef.sortState.typeSort.toString());
    }
}
