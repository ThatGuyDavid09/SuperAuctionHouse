package com.highmarsorbit.superauctionhouse.elements.buybid;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.elements.BaseElement;
import com.highmarsorbit.superauctionhouse.inventories.SellItemMenu;
import com.highmarsorbit.superauctionhouse.inventories.buybid.BuyBidMenu;
import com.highmarsorbit.superauctionhouse.util.AuctionType;
import com.highmarsorbit.superauctionhouse.util.ChatUtils;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import fr.cleymax.signgui.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuyBidPriceElement extends BaseElement {
    private SignGUI signGui;
    private BuyBidMenu buyBidMenu;
    public BuyBidPriceElement(char character, InventoryGui gui, BuyBidMenu buyBidMenu) {
        super(character, gui);
        this.buyBidMenu = buyBidMenu;

        createSignGui();

        element = new DynamicGuiElement(character, () -> {
            String setItemMessage = buyBidMenu.auctionType == AuctionType.AUCTION ? "Set initial bid: " : "Set item price: ";
            String setDetailedMessage = buyBidMenu.auctionType == AuctionType.AUCTION ? "Click to set the minimum bit for the item!"
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
                        Bukkit.getLogger().warning(ignored.toString());
                    }

                    // FIXME URGENT this does not fucking work WHYYYYYYYTYYYYYTYYY
                    Bukkit.getScheduler().runTask(SuperAuctionHouse.getInstance(), () -> {
                        buyBidMenu.getGui().close(false);
                        buyBidMenu = new BuyBidMenu(buyBidMenu);
                        buyBidMenu.drawInventory();
                        SuperAuctionHouse.getLogging().info("??????");
                        buyBidMenu.open(false);
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
