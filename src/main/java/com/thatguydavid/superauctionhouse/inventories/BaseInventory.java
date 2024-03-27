package com.thatguydavid.superauctionhouse.inventories;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import com.thatguydavid.superauctionhouse.util.MessageLoader;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BaseInventory {
    protected InventoryGui gui;
    protected Player holder;
    protected String[] guiLayout;
    protected String titlePath;

    public BaseInventory(Player holder, String titlePath) {
        this.holder = holder;
        this.titlePath = titlePath;
        initializeOtherVariables();
        createGuiLayout();
        createGuiBase();
        populateGui();
    }

    protected void initializeOtherVariables() {
    }

    protected void createGuiLayout() {
        guiLayout = new String[]{
                "         ",
                "         ",
                "         ",
                "         ",
                "         ",
                "         "
        };
    }

    protected void createGuiBase() {
        gui = new InventoryGui(SuperAuctionHouse.getInstance(), holder, SuperAuctionHouse.getMessages().getMessage(titlePath), guiLayout);
        gui.setFiller(new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
    }

    protected void populateGui() { }

    public void open() {
        open(holder);
    }

    public void open(Player player) {
        gui.show(player, true);
    }
}
