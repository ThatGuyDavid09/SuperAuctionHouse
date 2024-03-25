package com.thatguydavid.superauctionhouse.inventories;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import com.thatguydavid.superauctionhouse.util.MessageLoader;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionHouse extends BaseInventory {

    public AuctionHouse(Player holder, String pathTitle) {
        super(holder, pathTitle);
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
        a - Filter by auction type (auction, BIN, etc, sunflower)
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

    }
}
