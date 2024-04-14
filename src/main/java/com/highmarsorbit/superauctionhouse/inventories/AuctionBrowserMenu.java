package com.highmarsorbit.superauctionhouse.inventories;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.elements.browser.*;
import com.highmarsorbit.superauctionhouse.util.AuctionSortState;
import com.highmarsorbit.superauctionhouse.util.ChatUtils;
import de.themoep.inventorygui.GuiPageElement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class AuctionBrowserMenu extends BaseInventory {
    private BukkitTask refreshTask;
    public AuctionSortState sortState;

    public AuctionBrowserMenu(Player holder) {
        super(holder, "ah_title", false);

        sortState = new AuctionSortState();

        initalizeGui();

        registerRefreshEvent();
        registerCloseAction();
    }

    public AuctionBrowserMenu(AuctionBrowserMenu copy) {
        super(copy.holder, "ah_title", false);

        sortState = copy.sortState;

        initalizeGui();

        registerRefreshEvent();
        registerCloseAction();

    }

    private void registerRefreshEvent() {
        refreshTask = Bukkit.getScheduler().runTaskTimer(SuperAuctionHouse.getInstance(), () -> {
                    drawInventory();
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
        z - First page (crossbow)
        b - Page back (arrow)
        r - Reset settings (only visible when different from default, barrier)
        l - Opens menu to filter by name (sign)
        // TODO implement own auctions menu
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
        populateBaseGuiElements();
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
    protected void populateBaseGuiElements() {
        createTextSortElement();
        gui.addElement(new AuctionResetFilterElement('r', gui, this).getElement());
        gui.addElement(new AuctionTypeSortElement('a', gui, this).getElement());

        gui.addElement(new AuctionOrderSortElement('s', gui, this).getElement());


        GuiPageElement forwardPage = new GuiPageElement('f', new ItemStack(Material.ARROW),
                GuiPageElement.PageAction.NEXT,
                ChatUtils.RESET + ChatColor.GREEN + "Next page",
                ChatUtils.RESET + ChatColor.GRAY + "(%nextpage%/%pages%)",
                " ",
                ChatUtils.RESET + ChatColor.YELLOW + "Click to turn page!",
                " ",
                ChatUtils.RESET + ChatColor.BLUE + "Shift click to go to the last page!");
        forwardPage.setAction(click -> {
            click.getGui().playClickSound();
            switch (click.getType()) {
                case LEFT -> {
                    if (click.getGui().getPageNumber(click.getWhoClicked()) + 1 < click.getGui().getPageAmount(click.getWhoClicked())) {
                        click.getGui().setPageNumber(click.getWhoClicked(), click.getGui().getPageNumber(click.getWhoClicked()) + 1);
                    }
                }
                case SHIFT_LEFT -> {
                    click.getGui().setPageNumber(click.getWhoClicked(), click.getGui().getPageAmount(click.getWhoClicked()) - 1);
                }
            }
            return true;
        });
        gui.addElement(forwardPage);

        GuiPageElement backPage = new GuiPageElement('b', new ItemStack(Material.ARROW),
                GuiPageElement.PageAction.PREVIOUS,
                ChatUtils.RESET + ChatColor.GREEN + "Previous page",
                ChatUtils.RESET + ChatColor.GRAY + "(%prevpage%/%pages%)",
                " ",
                ChatUtils.RESET + ChatColor.YELLOW + "Click to turn page!",
                " ",
                ChatUtils.RESET + ChatColor.BLUE + "Shift click to go to the first page!");
        backPage.setAction(click -> {
            click.getGui().playClickSound();
            switch (click.getType()) {
                case LEFT -> {
                    if (click.getGui().getPageNumber(click.getWhoClicked()) > 0) {
                        click.getGui().setPageNumber(click.getWhoClicked(), click.getGui().getPageNumber(click.getWhoClicked()) - 1);
                    }
                }
                case SHIFT_LEFT -> {
                    click.getGui().setPageNumber(click.getWhoClicked(), 0);
                }
            }
            return true;
        });
        gui.addElement(backPage);

//        gui.addElement(new GuiPageElement('m', new ItemStack(Material.CROSSBOW),
//                GuiPageElement.PageAction.LAST,
//                ChatUtils.RESET + ChatColor.GREEN + "Last page",
//                ChatUtils.RESET + ChatColor.GRAY + "(%pages%)",
//                " ",
//                ChatUtils.RESET + ChatColor.YELLOW + "Click to go to last!"));
//        gui.addElement(new GuiPageElement('z', new ItemStack(Material.CROSSBOW),
//                GuiPageElement.PageAction.FIRST,
//                ChatUtils.RESET + ChatColor.GREEN + "First page",
//                " ",
//                ChatUtils.RESET + ChatColor.YELLOW + "Click to go to first!"));
    }

    @Override
    public void open(Player player, boolean checkOpen) {
        createAuctionGroupElement();
        super.open(player, checkOpen);
    }
}
