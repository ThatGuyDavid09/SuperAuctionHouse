package com.thatguydavid.superauctionhouse.elements;

import com.thatguydavid.superauctionhouse.inventories.AuctionHouse;
import com.thatguydavid.superauctionhouse.util.AuctionOrderSort;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.InventoryGui;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AuctionSortElement extends ElementBase {
    private AuctionHouse ahRef;
    public AuctionSortElement(char character, InventoryGui gui, AuctionHouse ahRef) {
        super(character, gui);

        this.ahRef = ahRef;

        element = new GuiStateElement(character,
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.orderSort = AuctionOrderSort.LOWEST_PRICE;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "lowest_price",
                        new ItemStack(Material.HOPPER),
                        ChatColor.RESET + "" + ChatColor.BLUE + "Sort by:",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Lowest price",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Highest price",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Newest first",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Ending soon",
                        " ",
                        ChatColor.RESET + "" + ChatColor.YELLOW + "Click to change sort!"
                ),
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.orderSort = AuctionOrderSort.HIGHEST_PRICE;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "highest_price",
                        new ItemStack(Material.HOPPER),
                        ChatColor.RESET + "" + ChatColor.BLUE + "Sort by:",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Lowest price",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Highest price",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Newest first",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Ending soon",
                        " ",
                        ChatColor.RESET + "" + ChatColor.YELLOW + "Click to change sort!"
                ),
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.orderSort = AuctionOrderSort.NEWEST_FIRST;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "newest_first",
                        new ItemStack(Material.HOPPER),
                        ChatColor.RESET + "" + ChatColor.BLUE + "Sort by:",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Lowest price",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Highest price",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Newest first",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Ending soon",
                        " ",
                        ChatColor.RESET + "" + ChatColor.YELLOW + "Click to change sort!"
                ),
                new GuiStateElement.State(
                        change -> {
                            ahRef.sortState.orderSort = AuctionOrderSort.ENDING_SOON;
                            ahRef.recreateAuctionGroupElement();
                        },
                        "ending_soon",
                        new ItemStack(Material.HOPPER),
                        ChatColor.RESET + "" + ChatColor.BLUE + "Sort by:",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Lowest price",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Highest price",
                        ChatColor.RESET + "" + ChatColor.GRAY + "Newest first",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Ending soon",
                        " ",
                        ChatColor.RESET + "" + ChatColor.YELLOW + "Click to change sort!"
                )
        );
    }
}
