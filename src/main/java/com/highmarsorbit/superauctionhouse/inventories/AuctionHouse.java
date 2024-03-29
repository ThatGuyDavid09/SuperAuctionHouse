package com.highmarsorbit.superauctionhouse.inventories;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.elements.AuctionResetFilterElement;
import com.highmarsorbit.superauctionhouse.elements.AuctionSortElement;
import com.highmarsorbit.superauctionhouse.elements.AuctionsGuiElement;
import com.highmarsorbit.superauctionhouse.util.AuctionSortState;
import com.highmarsorbit.superauctionhouse.elements.AuctionTextSortElement;
import de.themoep.inventorygui.GuiPageElement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class AuctionHouse extends BaseInventory {
    private BukkitTask refreshTask;
    public AuctionSortState sortState;

    public AuctionHouse(Player holder, String pathTitle) {
        super(holder, pathTitle);

        registerRefreshEvent();
        registerCloseAction();
    }

    @Override
    protected void initializeOtherVariables() {
        sortState = new AuctionSortState();
    }

    private void registerRefreshEvent() {
        refreshTask = Bukkit.getScheduler().runTaskTimer(SuperAuctionHouse.getInstance(), () -> {
                    drawInventory();
                },
                0L,
                20L);
    }

    public void drawInventory() {
        gui.draw();
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
        z - First page (crossbow)
        b - Page back (arrow)
        r - Reset settings (only visible when different from default, barrier)
        l - Opens menu to filter by name (sign)
        o - Own auctions (book)
        s - Sort by price, time, etc (hopper)
        a - Filter by auction type (auction, BIN, etc, diamond (all), clock (auction), gold ingot (BIN))
        f - Page forward (arrow)
        z - Last page (crossbow)
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

    public void recreateElements() {
        populateGui();
        drawInventory();
    }

    public void recreateTextSortElement() {
        createTextSortElement();
        drawInventory();
    }

    private void createTextSortElement() {
        gui.addElement(new AuctionTextSortElement('l', gui, this).getElement());
    }

    public void recreateAuctionGroupElement() {
        createAuctionGroupElement();
        drawInventory();
    }

    private void createAuctionGroupElement() {
        gui.addElement(new AuctionsGuiElement('i', SuperAuctionHouse.getAuctionManager().getAllAuctions(), gui, sortState).getElement());
    }

    @Override
    protected void populateGui() {
        createTextSortElement();
        gui.addElement(new AuctionResetFilterElement('r', gui, this).getElement());

        gui.addElement(new AuctionSortElement('s', gui, this).getElement());

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

    @Override
    public void open(Player player, boolean checkOpen) {
        Bukkit.getLogger().info("Opened!");
        createAuctionGroupElement();
        super.open(player, checkOpen);
    }
}
