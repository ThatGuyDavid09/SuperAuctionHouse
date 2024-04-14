package com.highmarsorbit.superauctionhouse.inventories;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BaseInventory {
    protected InventoryGui gui;
    protected Player holder;
    protected String[] guiLayout;
    protected String titlePath;

    public BaseInventory(Player holder, String titlePath) {
        this(holder, titlePath, true);
    }

    public BaseInventory(Player holder, String titlePath, boolean autoInit) {
        this.holder = holder;
        this.titlePath = titlePath;

        if (autoInit) {
            initalizeGui();
        }
    }

    public void initalizeGui() {
        createGuiLayout();
        createGuiBase();
        populateBaseGuiElements();
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

    protected void populateBaseGuiElements() { }

    public InventoryGui getGui() {
        return gui;
    }

    public void open() {
        open(holder);
    }

    public void open(Player player) {
        open(player, true);
    }

    public void open(boolean checkOpen) {
        open(holder, checkOpen);
    }

    public void open(Player player, boolean checkOpen) {
        // TODO remove
        for (InventoryGui gui : InventoryGui.getHistory(holder)) {
            Bukkit.getLogger().info(gui.getTitle());
        }

        gui.show(player, checkOpen);
    }

    public void close() {
        gui.close(false);
    }

    public void drawInventory() {
        gui.draw();
    }

    public Player getHolder() {
        return holder;
    }
}
