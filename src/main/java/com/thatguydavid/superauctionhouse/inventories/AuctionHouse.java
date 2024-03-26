package com.thatguydavid.superauctionhouse.inventories;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import com.thatguydavid.superauctionhouse.elements.AuctionsGuiElement;
import com.thatguydavid.superauctionhouse.managers.AuctionManager;
import com.thatguydavid.superauctionhouse.util.AuctionItem;
import com.thatguydavid.superauctionhouse.util.MessageLoader;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class AuctionHouse extends BaseInventory {
    BukkitTask refreshTask;

    public AuctionHouse(Player holder, String pathTitle) {
        super(holder, pathTitle);

        registerRefreshEvent();
        registerCloseAction();
    }

    private void registerRefreshEvent() {
        refreshTask = Bukkit.getScheduler().runTaskTimer(SuperAuctionHouse.getInstance(), () -> {
                    gui.draw();
                },
                0L,
                20L);
    }

    private void registerCloseAction() {
        gui.setCloseAction(viewer -> {
            refreshTask.cancel();
            return false;
        });
    }

    @Override
    protected void createGuiLayout() {
        /*
        i - General auction items
        b - Page back (arrow)
        r - Reset settings (only visible when different from default, barrier)
        l - Opens menu to filter by name (sign)
        o - Own auctions (book)
        s - Sort by price, time, etc (hopper)
        a - Filter by auction type (auction, BIN, etc, diamond (all), clock (auction), gold ingot (BIN))
        f - Page forward (arrow)
         */
        this.guiLayout = new String[]{
                " iiiiiii ",
                " iiiiiii ",
                " iiiiiii ",
                " iiiiiii ",
                " iiiiiii ",
                " brlosaf "
        };
    }

    @Override
    protected void populateGui() {
        gui.addElement(new AuctionsGuiElement('i', SuperAuctionHouse.getAuctionManager().getAllAuctions(), gui).getElement());
    }
}
