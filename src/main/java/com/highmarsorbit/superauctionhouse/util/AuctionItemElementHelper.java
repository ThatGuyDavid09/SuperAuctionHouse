package com.highmarsorbit.superauctionhouse.util;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.inventories.buybid.BuyBidMenu;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AuctionItemElementHelper {
    /**
     * For use in something like a GuiElementGroup, when it does not matter what the character is
     * @return
     */
    public static GuiElement getAuctionGuiElement(AuctionItem auction, InventoryGui gui) {
        return getAuctionGuiElement(auction, 'a', gui);
    }

    public static GuiElement getAuctionGuiElement(AuctionItem auction, char character, InventoryGui gui) {
        // FIXME this doesn't work properly with things like music discs and banners that have lore but technically don't.
        // more testing is required.
//        String[] name = {ItemUtils.getItemName(item)};
        String[] allExist = ItemUtils.getLoreWithSeparator(auction.getItem());

        return new DynamicGuiElement(character, (viewer) -> {
            String[] extraLore;

            if (auction.getDurationRemaining().isNegative()) {
                extraLore = new String[]{
                        ChatColor.RED + "This auction is expired!"
                };
            } else {
                String costWord = auction.getAuctionType() == AuctionType.AUCTION ? "Bid" : "Price";
                String purchaseWord = auction.getAuctionType() == AuctionType.AUCTION ? "bid" : "buy";
                extraLore = new String[]{
                        ChatUtils.RESET + ChatColor.GRAY + costWord + ": " + ChatColor.GOLD + SuperAuctionHouse.getEconomy().format(auction.getPrice()),
                        ChatUtils.RESET + ChatColor.GRAY + "Seller: " + auction.getSellerName(),
                        ChatUtils.RESET + ChatColor.GRAY + "Duration: " + ChatColor.YELLOW + DurationUtils.formatDuration(auction.getDurationRemaining()),
                        " ",
                        ChatUtils.RESET + ChatColor.YELLOW + String.format("Click to %s!", purchaseWord)
                };
            }

            return new StaticGuiElement(character, auction.getItem(),
                    click -> {
                        gui.playClickSound();
                        if (auction.isValid()) {
                            BuyBidMenu bidMenu = new BuyBidMenu((Player) click.getWhoClicked(), auction);
                            bidMenu.open();
                        }
                        return true;
                    },
                    (String[]) ArrayUtils.addAll(allExist, extraLore));
        });
    }

    public static GuiElement getAuctionPurchaseElement(AuctionItem auction, char character) {
        // TODO implement. This is for when a user wants to purchase/place a bid on an item.
        String[] allExist = ItemUtils.getLoreWithSeparator(auction.getItem());
        String buyWord = auction.getAuctionType() == AuctionType.AUCTION ? "bid on!" : "purchase!";
        String costWord = auction.getAuctionType() == AuctionType.AUCTION ? "Bid" : "Price";
        String[] extraLore = {
                ChatUtils.RESET + ChatColor.GRAY + costWord + ": " + ChatColor.GOLD + SuperAuctionHouse.getEconomy().format(auction.getPrice()),
                ChatUtils.RESET + ChatColor.GRAY + "Seller: " + auction.getSellerName(),
                " ",
                ChatUtils.RESET + ChatColor.YELLOW + "This is the item you will " + buyWord
        };

        return new StaticGuiElement(character, auction.getItem(),
                (String[]) ArrayUtils.addAll(allExist, extraLore));
    }
}
