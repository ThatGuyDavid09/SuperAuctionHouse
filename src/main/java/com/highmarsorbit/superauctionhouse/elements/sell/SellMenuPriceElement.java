package com.highmarsorbit.superauctionhouse.elements.sell;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.inventories.AuctionBrowserMenu;
import com.highmarsorbit.superauctionhouse.inventories.SellItemMenu;
import com.highmarsorbit.superauctionhouse.elements.BaseElement;
import com.highmarsorbit.superauctionhouse.util.ChatUtils;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import fr.cleymax.signgui.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellMenuPriceElement extends BaseElement {
    private SignGUI signGui;
    private SellItemMenu sellMenu;
    public SellMenuPriceElement(char character, InventoryGui gui, SellItemMenu sellMenu) {
        super(character, gui);
        this.sellMenu = sellMenu;

        createSignGui();

        element = new DynamicGuiElement(character, () -> {
            String setItemMessage = sellMenu.auctionType == AuctionBrowserMenu.AuctionType.AUCTION ? "Set initial bid: " : "Set item price: ";
            String setDetailedMessage = sellMenu.auctionType == AuctionBrowserMenu.AuctionType.AUCTION ? "Click to set the minimum bit for the item!"
                    : "Click to set the item's sale price";

            return new StaticGuiElement(character, new ItemStack(Material.GOLD_NUGGET),
                    click -> {
                        gui.playClickSound();
//                        gui.close();
                        signGui.open((Player) click.getWhoClicked());
                        return true;
                    },
                    ChatUtils.RESET + ChatColor.GRAY + setItemMessage + ChatColor.GOLD + SuperAuctionHouse.getEconomy().format(sellMenu.price),
                    " ",
                    ChatUtils.RESET + ChatColor.YELLOW + setDetailedMessage);
        });
    }

    private void createSignGui() {
        signGui = new SignGUI(SuperAuctionHouse.getInstance().getSignGuiManager(),
                state -> {
                    // This allows for non-digit input, like commas, dollar signs, etc.
                    String strippedPrice = state.getLines()[0].strip().replaceAll("[^1234567890.]", "");
                    try {
                        sellMenu.price = Double.parseDouble(strippedPrice);
                    } catch (NumberFormatException ignored) {
//                        Bukkit.getLogger().warning(ignored.toString());
                    }
                    // Needed to run task synchronously as otherwise Bukkit throws an error. Why does this work in
                    // AuctionTextSortElement but not here?
                    Bukkit.getScheduler().runTask(SuperAuctionHouse.getInstance(), () -> {
                        sellMenu.getGui().close(false);
                        sellMenu.drawInventory();
//                        sellMenu = new SellItemMenu(sellMenu);
                        sellMenu.open(false);
                    });
                })
                .withLines(
                        "",
                        "^^^^^^^^^^^^^^^^",
                        "Enter price",
                        ""
                );
    }
}
