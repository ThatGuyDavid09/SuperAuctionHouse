package com.thatguydavid.superauctionhouse.elements;

import com.thatguydavid.superauctionhouse.util.AuctionItem;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class AuctionsGuiElement extends ElementBase {
    public AuctionsGuiElement(char character, AuctionItem[] items, InventoryGui gui) {
        super(gui);
        element = new GuiElementGroup(character);
        for (AuctionItem auction : items) {
            ((GuiElementGroup) element).addElement(auction.getGuiElement(gui));
        }
    }
}
