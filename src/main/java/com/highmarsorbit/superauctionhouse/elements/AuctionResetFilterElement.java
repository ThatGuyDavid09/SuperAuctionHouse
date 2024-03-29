package com.highmarsorbit.superauctionhouse.elements;

import com.highmarsorbit.superauctionhouse.inventories.AuctionHouse;
import com.highmarsorbit.superauctionhouse.util.AuctionSortState;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionResetFilterElement extends ElementBase {
    private AuctionHouse ahRef;

    public AuctionResetFilterElement(char character, InventoryGui gui, AuctionHouse ahRef) {
        super(character, gui);

        this.ahRef = ahRef;

        element = new DynamicGuiElement(character, () -> {
            AuctionSortState baseState = new AuctionSortState();

            if (ahRef.sortState.equals(baseState)) {
                return ahRef.getGui().getFiller();
            }
            return new StaticGuiElement(character, new ItemStack(Material.BARRIER),
                    click -> {
                        gui.playClickSound();
                        ahRef.sortState = baseState;
                        ahRef.recreateAuctionGroupElement();
                        return true;
                    },
                    ChatColor.RESET + "" + ChatColor.RED + "Reset filters",
                    " ",
                    ChatColor.RESET + "" + ChatColor.YELLOW + "Click to reset search filters!!");
        });
    }
}