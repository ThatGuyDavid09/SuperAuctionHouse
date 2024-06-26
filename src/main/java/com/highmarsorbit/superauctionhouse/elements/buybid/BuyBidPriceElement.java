package com.highmarsorbit.superauctionhouse.elements.buybid;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.elements.BaseElement;
import com.highmarsorbit.superauctionhouse.inventories.AuctionBrowserMenu;
import com.highmarsorbit.superauctionhouse.inventories.buybid.BuyBidMenu;
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

import java.util.Deque;

public class BuyBidPriceElement extends BaseElement {
    private SignGUI signGui;
    private BuyBidMenu buyBidMenu;
    public BuyBidPriceElement(char character, InventoryGui gui, BuyBidMenu buyBidMenu) {
        super(character, gui);
        this.buyBidMenu = buyBidMenu;

        createSignGui();

        element = new DynamicGuiElement(character, () -> {
            String setItemMessage = buyBidMenu.auctionType == AuctionBrowserMenu.AuctionType.AUCTION ? "Set initial bid: " : "Set item price: ";
            String setDetailedMessage = buyBidMenu.auctionType == AuctionBrowserMenu.AuctionType.AUCTION ? "Click to set the minimum bit for the item!"
                    : "Click to set the item's sale price";

            return new StaticGuiElement(character, new ItemStack(Material.GOLD_NUGGET),
                    click -> {
                        gui.playClickSound();
//                        gui.close(false);
                        signGui.open((Player) click.getWhoClicked());
                        return true;
                    },
                    ChatUtils.RESET + ChatColor.GRAY + setItemMessage + ChatColor.GOLD + SuperAuctionHouse.getEconomy().format(buyBidMenu.getPrice()),
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
                        buyBidMenu.setPrice(Double.parseDouble(strippedPrice));
                    } catch (NumberFormatException ignored) {
                    }

                    Deque<InventoryGui> history = InventoryGui.getHistory(state.getPlayer());
                    buyBidMenu.drawInventory();

                    Bukkit.getScheduler().runTask(SuperAuctionHouse.getInstance(), () -> {
                        buyBidMenu.close(true);
                        buyBidMenu.open(false);

                        InventoryGui.clearHistory(state.getPlayer());
                        // For some reason the history gets cleared at some point when opening inventory
                        // (not because it is told to, it happens regardless), so we manually re-add in all the previously
                        // opened stuff so when menu is closed it still works

                        for (InventoryGui item : history) {
                            InventoryGui.addHistory(state.getPlayer(), item);
                        }
                        InventoryGui.addHistory(state.getPlayer(), buyBidMenu.getGui());
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
