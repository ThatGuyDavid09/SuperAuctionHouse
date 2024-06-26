package com.highmarsorbit.superauctionhouse.elements.browser;

import com.highmarsorbit.superauctionhouse.elements.BaseElement;
import com.highmarsorbit.superauctionhouse.inventories.AuctionBrowserMenu;
import com.highmarsorbit.superauctionhouse.util.ChatUtils;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AuctionResetFilterElement extends BaseElement {
    private AuctionBrowserMenu ahRef;

    public AuctionResetFilterElement(char character, InventoryGui gui, AuctionBrowserMenu ahRef) {
        super(character, gui);

        this.ahRef = ahRef;

        element = new DynamicGuiElement(character, () -> {
            AuctionBrowserMenu.AuctionSortState baseState = new AuctionBrowserMenu.AuctionSortState();

            if (ahRef.sortState.equals(baseState)) {
                return ahRef.getGui().getFiller();
            }
            return new StaticGuiElement(character, new ItemStack(Material.BARRIER),
                    click -> {
                        gui.playClickSound();
                        ahRef.sortState = baseState;
                        ahRef.recreateElements();
                        ahRef.recreateAuctionGroupElement();
                        return true;
                    },
                    ChatUtils.RESET + ChatColor.RED + "Reset filters",
                    " ",
                    ChatUtils.RESET + ChatColor.YELLOW + "Click to reset search filters!!");
        });
    }
}
