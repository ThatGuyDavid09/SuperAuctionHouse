package com.highmarsorbit.superauctionhouse.inventories;

import de.themoep.inventorygui.GuiStorageElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellItemMenu extends BaseInventory {
    private ItemStack sellingItem;

    public SellItemMenu(Player holder, String titlePath, ItemStack itemToSell) {
        super(holder, titlePath);

        this.sellingItem = itemToSell;
    }

    @Override
    protected void createGuiLayout() {
        /*
        i - Item to be sold
        c - Confirm sale
        p - Set price
        d - Set duration
        t - Set type
        x - Cancel
        n - Set sell name
         */
        this.guiLayout = new String[]{
                "         ",
                "    i    ",
                "         ",
                "  c p d  ",
                "         ",
                "   txn   "
        };
    }

    @Override
    protected void populateGui() {
        gui.addElement(new StaticGuiElement('i', sellingItem));

        gui.addElement(new StaticGuiElement('x', new ItemStack(Material.BARRIER), click -> {
            gui.playClickSound();
            gui.close();
            return true;
        },
                ChatColor.RESET + "" + ChatColor.RED + "Cancel Sale",
                " ",
                ChatColor.RESET + "" + ChatColor.YELLOW + "Click to cancel the sale",
                ChatColor.RESET + "" + ChatColor.YELLOW + "and close the menu!"));
    }
}
