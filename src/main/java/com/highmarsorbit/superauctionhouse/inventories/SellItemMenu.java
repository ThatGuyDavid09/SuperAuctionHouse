package com.highmarsorbit.superauctionhouse.inventories;

import com.highmarsorbit.superauctionhouse.Globals;
import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.elements.sell.SellMenuDurationElement;
import com.highmarsorbit.superauctionhouse.elements.sell.SellMenuPriceElement;
import com.highmarsorbit.superauctionhouse.elements.sell.SellMenuSellerNameElement;
import com.highmarsorbit.superauctionhouse.util.*;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiStateElement;
import de.themoep.inventorygui.StaticGuiElement;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Random;

public class SellItemMenu extends BaseInventory {
    private ItemStack sellingItem = null;
    public double price = 0;
    public Duration duration = Duration.ZERO;
    public String sellerName;
    public AuctionType auctionType = AuctionType.AUCTION;

    // TODO temp for testing. Remove later.
    private double fee;

    public SellItemMenu(Player holder) {
        super(holder, "sell_menu_title", false);
        sellerName = holder.getDisplayName();

        // TODO temp for testing. Remove later.
        Random random = new Random();
        fee = random.nextDouble() * 100;

        initalizeGui();
    }

    public SellItemMenu(SellItemMenu copy) {
        super(copy.holder, "sell_menu_title");

        sellingItem = copy.sellingItem;
        price = copy.price;
        duration = copy.duration;
        sellerName = copy.sellerName;
        auctionType = copy.auctionType;
        // TODO remove
        fee = copy.fee;

        GuiStateElement typeElement = (GuiStateElement) gui.getElement('t');
        typeElement.setState(auctionType.toString());

        updateSellItemElement();
        updateConfirmElement();
        drawInventory();
    }

    public void setSaleItem(ItemStack item) {
        this.sellingItem = item;
        updateSellItemElement();
        drawInventory();
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
    protected void populateBaseGuiElements() {
        populateInputGuiElements();

        gui.addElement(new StaticGuiElement('x', new ItemStack(Material.BARRIER), click -> {
            gui.playClickSound();
            gui.close(false);
            return true;
        },
        ChatUtils.RESET + ChatColor.RED + "Cancel Sale",
        " ",
        ChatUtils.RESET + ChatColor.YELLOW + "Click to cancel the sale",
        ChatUtils.RESET + ChatColor.YELLOW + "and close the menu!"));

        // TODO have this check if player has permission to sell specific type
        // actually need a check in general for setting auctiontype in constructor
        gui.addElement(new GuiStateElement('t',
                new GuiStateElement.State(
                        change -> {
                            auctionType = AuctionType.AUCTION;
                            drawInventory();
                        },
                        "auction",
                        new ItemStack(Material.GOLD_BLOCK),
                        ChatUtils.RESET + ChatColor.BLUE + "Set auction type:",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Auction",
                        ChatUtils.RESET + "Buy It Now",
                        " ",
                        ChatUtils.RESET + ChatColor.YELLOW + "Click to change the type of auction!"
                ),
                new GuiStateElement.State(
                        change -> {
                            auctionType = AuctionType.BUY_IT_NOW;
                            drawInventory();
                        },
                        "buy_it_now",
                        new ItemStack(Material.GOLD_BLOCK),
                        ChatUtils.RESET + ChatColor.BLUE +"Set auction type:",
                        ChatUtils.RESET + "Auction",
                        ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "▶ Buy It Now",
                        " ",
                        ChatUtils.RESET + ChatColor.YELLOW + "Click to change the type of auction!"
                )
        ));
    }

    public void populateInputGuiElements() {
        gui.addElement(new SellMenuPriceElement('p', gui, this).getElement());
        gui.addElement(new SellMenuDurationElement('d', gui, this).getElement());
        // TODO check if player has permission to do this
        gui.addElement(new SellMenuSellerNameElement('n', gui, this).getElement());
    }

    private boolean isSellItemValid() {
        // TODO: implement actual logic for detecting if an item is valid
        return true;
    }

    public void updateSellItemElement() {
        if (sellingItem == null || sellingItem.getType().isAir()) {
            gui.addElement(new StaticGuiElement('i', new ItemStack(Material.CHEST),
                    ChatUtils.RESET + ChatColor.GREEN + "Click an item in your inventory to sell it!"));
            updateConfirmElement();
            Globals.waitingForClick.put(holder, this);

            return;
        }

        // Just in case
        Globals.waitingForClick.remove(holder);

        String[] name = {null};
        String[] existingLore = ItemUtils.getItemLoreArray(sellingItem);

        String[] separatorLore = ItemUtils.getSeparatorLoreArray();

        String[] allExist = (String[]) ArrayUtils.addAll(ArrayUtils.addAll(name, existingLore), separatorLore);
        
        String[] newLore = new String[]{
            ChatUtils.RESET + ChatColor.YELLOW + "This is the item you will be listing for auction!"
        };

        gui.addElement(new StaticGuiElement('i', sellingItem,
                (String[]) ArrayUtils.addAll(allExist, newLore)));

        updateConfirmElement();
    }

    public void updateConfirmElement() {
        if (sellingItem == null || sellingItem.getType().isAir()) {
            setConfirmFailElement("Please select an item to sell!");
            return;
        }

        if (!isSellItemValid()) {
            setConfirmFailElement("This item is not allowed to be sold!");
            return;
        }

        if (price <= 0.001) {
            setConfirmFailElement("Price must be non-zero!");
            return;
        }

        if (duration.toSeconds() < 1) {
            setConfirmFailElement("Duration must be non zero!");
            return;
        }

        double fee = calculateSaleFee();
        if (!userHasFeeMoney()) {
            setConfirmFailElement("You do not have money to cover the auction fee!", fee);
            return;
        }

        gui.addElement(new DynamicGuiElement('c', () -> new StaticGuiElement('c', new ItemStack(Material.GREEN_CONCRETE),
                click -> {
                    gui.playClickSound();
                    gui.close(false);

                    // If player dropped item out of inventory during selling process
                    boolean inventoryContainsItem = holder.getInventory().contains(sellingItem);
                    if (!inventoryContainsItem) {
                        SuperAuctionHouse.sendMessageByPath(holder, "sell_no_item_found");
                        return true;
                    }

                    // Need to check this here if player somehow lost money from the last time the confirm item refreshed
                    if (!SuperAuctionHouse.getEconomy().has(holder, calculateSaleFee())) {
                        SuperAuctionHouse.sendMessageByPath(holder, "sell_cannot_pay_fee");
                        return true;
                    }

                    AuctionUpdateStatus auctionStatus = SuperAuctionHouse.getAuctionManager().listAuction(new AuctionItem(sellingItem, holder, price, duration, auctionType, sellerName));
                    if (auctionStatus.isTechnicalFailure()) {
                        // Log technical failures to console
                        SuperAuctionHouse.sendMessageByPath(holder, "sell_item_technical_fail");
                        SuperAuctionHouse.getInstance().getLogger().warning(String.format("Player %s attempted to sell " +
                                "an item and it failed to list. Check to see if the database is working properly", holder.getDisplayName()));
                        return true;
                    }

                    if (!auctionStatus.isSuccessful()) {
                        SuperAuctionHouse.sendMessageByPath(holder, "sell_item_fail");
                        return true;
                    }

                    // Actually withdrawing fee and removing item should happen after all checks are compelte
                    holder.getInventory().remove(sellingItem);
                    SuperAuctionHouse.getEconomy().withdrawPlayer(holder, calculateSaleFee());

                    SuperAuctionHouse.sendMessageByPath(holder, "sell_success");

                  return true;
                },
                ChatUtils.RESET + ChatColor.GREEN + "Confirm item sale",
                " ",
                ChatUtils.RESET + "Auction type: " + ChatColor.BLUE + auctionType.getReadableName(),
                ChatUtils.RESET + "Item " + getPriceWord() + ": " + ChatColor.GOLD + SuperAuctionHouse.getEconomy().format(price),
                ChatUtils.RESET + "Duration: " + ChatColor.YELLOW + DurationUtils.formatDuration(duration),
                ChatUtils.RESET + "Seller: " + sellerName,
                " ",
                ChatUtils.RESET + "Sale fee: " + ChatColor.GOLD + SuperAuctionHouse.getEconomy().format(fee),
                " ",
                ChatUtils.RESET + ChatColor.YELLOW + "Click to list this item!")));
    }

    @Override
    public void drawInventory() {
        populateInputGuiElements();
        updateConfirmElement();

        super.drawInventory();
    }

    private String getPriceWord() {
        return auctionType == AuctionType.AUCTION ? "initial bid" : "price";
    }

    private void setConfirmFailElement(String message) {
        gui.addElement(new StaticGuiElement('c', new ItemStack(Material.RED_CONCRETE),
                ChatUtils.RESET + ChatColor.RED + "Cannot sell item!",
                " ",
                ChatUtils.RESET + ChatColor.YELLOW + message));
    }

    // This method is specifically for the case where the user does not have enough money to pay the fee
    private void setConfirmFailElement(String message, double fee) {
        gui.addElement(new StaticGuiElement('c', new ItemStack(Material.RED_CONCRETE),
                ChatUtils.RESET + ChatColor.RED + message,
                ChatUtils.RESET + ChatColor.GRAY + "Fee: " + ChatColor.GOLD + SuperAuctionHouse.getEconomy().format(fee)));
    }

    private boolean userHasFeeMoney() {
        return SuperAuctionHouse.getEconomy().has(holder, calculateSaleFee());
    }

    private double calculateSaleFee() {
        // TODO: actually implement sale fee logic
        return fee;
    }
}
