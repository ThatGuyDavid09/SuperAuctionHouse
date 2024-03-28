package com.highmarsorbit.superauctionhouse.elements;

import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;

public class ElementBase {
    protected char character;
    protected GuiElement element;
    protected InventoryGui gui;

    public ElementBase(char character, InventoryGui gui) {
        this.gui = gui;
    }

    public InventoryGui getGui() { return gui; }

    public GuiElement getElement() {
        return element;
    }
}
