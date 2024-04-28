package com.highmarsorbit.superauctionhouse.inventories;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BaseInventory {
    protected InventoryGui gui;
    protected Player holder;
    protected String[] guiLayout;
    protected String titlePath;
    protected boolean guiInitialized = false;

    public BaseInventory(Player holder, String titlePath) {
        this(holder, titlePath, true);
    }

    public BaseInventory(Player holder, String titlePath, boolean autoInit) {
        this.holder = holder;
        this.titlePath = titlePath;

        if (autoInit) {
            initializeGui();
        }
    }

    public void initializeGui() {
        guiInitialized = true;
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
        if (guiInitialized) {
            gui.show(player, checkOpen);
        } else {
            SuperAuctionHouse.sendMessageByPath(player, "menu_no_initialize");
        }
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
