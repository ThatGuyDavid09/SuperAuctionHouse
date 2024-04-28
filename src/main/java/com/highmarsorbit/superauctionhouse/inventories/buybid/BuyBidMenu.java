package com.highmarsorbit.superauctionhouse.inventories.buybid;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.elements.buybid.BuyBidPriceElement;
import com.highmarsorbit.superauctionhouse.inventories.BaseInventory;
import com.highmarsorbit.superauctionhouse.util.*;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BuyBidMenu extends BaseInventory {
    private AuctionItem item;
    public AuctionType auctionType;
    private double price;
    private String bidWord;

    public BuyBidMenu(Player holder, AuctionItem item) {
        super(holder, "bid_title", false);
        this.item = item;
        auctionType = item.getAuctionType();
        this.bidWord = auctionType == AuctionType.AUCTION ? "bid" : "buy";
        this.titlePath = bidWord + "_title";

        initializeGui();
    }

    public BuyBidMenu(BuyBidMenu copy) {
        super(copy.holder, copy.titlePath, false);
        this.item = copy.item;
        auctionType = copy.auctionType;
        this.bidWord = copy.bidWord;
        this.titlePath = copy.titlePath;

        initializeGui();
        updateConfirmElement();
        drawInventory();
    }

    @Override
    protected void createGuiLayout() {
        /*
        i - Item being bought
        c - Confirm purchase
        p - Set bid
        b - Back to auction browser
         */
        switch (auctionType) {
            case AUCTION -> guiLayout = new String[]{
                    "         ",
                    "    i    ",
                    "         ",
                    "  c   p  ",
                    "         ",
                    "    b    "
            };


            case BUY_IT_NOW -> guiLayout = new String[]{
                    "         ",
                    "    i    ",
                    "         ",
                    "    c    ",
                    "         ",
                    "    b    "
            };

        }
    }

    @Override
    protected void populateBaseGuiElements() {
        gui.addElement(new StaticGuiElement('b', new ItemStack(Material.ARROW),
                click -> {
                    gui.playClickSound();
                    gui.close(false);
                    return true;
                },
                ChatUtils.RESET + ChatColor.AQUA + "Back to auction browser",
                " ",
                ChatUtils.RESET + ChatColor.YELLOW + "Click to go back to auction browser!")
        );

        gui.addElement(AuctionItemElementHelper.getAuctionPurchaseElement(item, 'i'));
        populatePriceElement();

        updateConfirmElement();
    }

    private void populatePriceElement() {
        gui.addElement(new BuyBidPriceElement('p', gui, this).getElement());
    }

    @Override
    public void drawInventory() {
        updateConfirmElement();
        populatePriceElement();

        super.drawInventory();
    }

    public void updateConfirmElement() {
        boolean userCanAffordItem = SuperAuctionHouse.getEconomy().has(holder, price);
        if (!userCanAffordItem) {
            String auctionWord = auctionType == AuctionType.AUCTION ? "bid" : "item";
            setConfirmFailElement(String.format("You do not have money to pay for this %s!", auctionWord));
            return;
        }

        // TODO maybe implement a minimum bid increase?
        if (auctionType == AuctionType.AUCTION && price < item.getPrice()) {
            setConfirmFailElement("Bid must be greater than previous bid!");
            return;
        }

        gui.addElement(new DynamicGuiElement('c', () -> new StaticGuiElement('c', new ItemStack(Material.GREEN_CONCRETE),
                click -> {
                    gui.playClickSound();
                    gui.close(false);

                    // Need to check this here if player somehow lost money from the last time the confirm item refreshed
                    if (!SuperAuctionHouse.getEconomy().has(holder, price)) {
                        SuperAuctionHouse.sendMessageByPath(holder, "generic_no_money");
                        return true;
                    }

                    AuctionUpdateStatus auctionStatus = SuperAuctionHouse.getAuctionManager().placeBid(item.getId(), holder, price);
                    if (auctionStatus.isTechnicalFailure()) {
                        // Log technical failures to console
                        SuperAuctionHouse.sendMessageByPath(holder, "buy_item_technical_fail");
                        SuperAuctionHouse.getLogging().warning(String.format("Player %s attempted to bid on " +
                                "an item and it failed. Check to see if the database is working properly", holder.getDisplayName()));
                        return true;
                    }

                    if (!auctionStatus.isSuccessful()) {
                        SuperAuctionHouse.sendMessageByPath(holder, bidWord + "_item_fail");
                        return true;
                    }


                    // BIN items can be given straight to the player
                    if (auctionType == AuctionType.BUY_IT_NOW) {
                        AuctionUpdateStatus giveStatus = SuperAuctionHouse.getAuctionManager().giveBINToPlayer(item.getId(), holder);

                        // Messy logic, but here is the flow:
                        // If the status of giving the user the item was not successful, check if it was because the item
                        // could not be inserted. If it was, notify the user and continue. If it wasn't, send a failure message
                        // depending on if it was a technical failure or not and return.
                        if (!giveStatus.isSuccessful()) {
                            if (giveStatus.getMessage().equals("inventory insert fail")) {
                                // This is technically a "success", but it is marked as a failure. We still want to deduct
                                // the money from the user, so we do not return here
                                SuperAuctionHouse.sendMessageByPath(holder, "buy_insert_fail");
                            } else {
                                if (giveStatus.isTechnicalFailure()) {
                                    SuperAuctionHouse.sendMessageByPath(holder, "buy_item_technical_fail");
                                } else {
                                    SuperAuctionHouse.sendMessageByPath(holder, "buy_item_fail");
                                }
                                return true;
                            }
                        }

                    }
                    SuperAuctionHouse.getEconomy().withdrawPlayer(holder, price);

                    return true;
                },
                ChatUtils.RESET + ChatColor.GREEN + "Confirm item " + (auctionType == AuctionType.AUCTION ? "bid" : "purchase"),
                " ",
                ChatUtils.RESET + "Price: " + ChatColor.GOLD + SuperAuctionHouse.getEconomy().format(price),
                " ",
                ChatUtils.RESET + ChatColor.YELLOW + String.format("Click to %s!", auctionType == AuctionType.AUCTION ? "place bid" : "purchase item"))));
    }

    private void setConfirmFailElement(String message) {
        gui.addElement(new StaticGuiElement('c', new ItemStack(Material.RED_CONCRETE),
                ChatUtils.RESET + ChatColor.RED + "Cannot place bid!",
                " ",
                ChatUtils.RESET + ChatColor.YELLOW + message));
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
