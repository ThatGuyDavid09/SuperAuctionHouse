package com.thatguydavid.superauctionhouse.elements;

import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;

public class ElementBase {
    protected GuiElement element;
    protected InventoryGui gui;

    public ElementBase(InventoryGui gui) {
        this.gui = gui;
    }

    public InventoryGui getGui() { return gui; }

    public GuiElement getElement() {
        return element;
    }
}
