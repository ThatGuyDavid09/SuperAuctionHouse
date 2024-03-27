package com.thatguydavid.superauctionhouse.inventories;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import com.thatguydavid.superauctionhouse.elements.AuctionsGuiElement;
import com.thatguydavid.superauctionhouse.managers.AuctionManager;
import com.thatguydavid.superauctionhouse.util.AuctionItem;
import com.thatguydavid.superauctionhouse.util.MessageLoader;
import de.themoep.inventorygui.GuiElementGroup;
import de.themoep.inventorygui.GuiPageElement;
import de.themoep.inventorygui.InventoryGui;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
                "zbrlosafm"
        };
    }

    @Override
    protected void populateGui() {
        gui.addElement(new AuctionsGuiElement('i', SuperAuctionHouse.getAuctionManager().getAllAuctions(), gui).getElement());
        gui.addElement(new GuiPageElement('f', new ItemStack(Material.ARROW),
                GuiPageElement.PageAction.NEXT,
                ChatColor.RESET + "" + ChatColor.GREEN + "Next page",
                ChatColor.RESET + "" + ChatColor.GRAY + "(%nextpage%/%pages%)",
                " ",
                ChatColor.RESET + "" + ChatColor.YELLOW + "Click to turn page!"));
        gui.addElement(new GuiPageElement('b', new ItemStack(Material.ARROW),
                GuiPageElement.PageAction.PREVIOUS,
                ChatColor.RESET + "" + ChatColor.GREEN + "Previous page",
                ChatColor.RESET + "" + ChatColor.GRAY + "(%prevpage%/%pages%)",
                " ",
                ChatColor.RESET + "" + ChatColor.YELLOW + "Click to turn page!"));

        gui.addElement(new GuiPageElement('m', new ItemStack(Material.CROSSBOW),
                GuiPageElement.PageAction.LAST,
                ChatColor.RESET + "" + ChatColor.GREEN + "Last page",
                ChatColor.RESET + "" + ChatColor.GRAY + "(%pages%)",
                " ",
                ChatColor.RESET + "" + ChatColor.YELLOW + "Click to go to last!"));
        gui.addElement(new GuiPageElement('z', new ItemStack(Material.CROSSBOW),
                GuiPageElement.PageAction.FIRST,
                ChatColor.RESET + "" + ChatColor.GREEN + "First page",
                " ",
                ChatColor.RESET + "" + ChatColor.YELLOW + "Click to go to first!"));
    }
}
