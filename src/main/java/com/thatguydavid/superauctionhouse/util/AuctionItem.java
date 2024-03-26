package com.thatguydavid.superauctionhouse.util;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.GuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.Instant;

public class AuctionItem {
    private final int id;
    private final ItemStack item;
    private final Player seller;
    private final String sellerName;
    private final Instant createTime;
    private final Instant endTime;
    private final AuctionType auctionType;

    private double price;
    private Player highestBidder;

    public AuctionItem(int id, ItemStack item, Player seller, double price, Duration duration, AuctionType auctionType) {
        this(id, item, seller, price, duration, auctionType, seller.getDisplayName());
    }

    public AuctionItem(int id, ItemStack item, Player seller, double price, Duration duration, AuctionType auctionType, String sellerName) {
        this.id = id;
        this.item = item;
        this.seller = seller;
        this.price = price;
        this.auctionType = auctionType;
        this.sellerName = sellerName;
        this.highestBidder = null;

        this.createTime = Instant.ofEpochMilli(System.currentTimeMillis());
        this.endTime = createTime.plusSeconds(duration.getSeconds());
    }

    public int getId() {
        return id;
    }

    public ItemStack getItem() {
        return item;
    }

    public Player getSeller() {
        return seller;
    }

    public String getSellerName() {
        return sellerName;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Duration getDurationRemaining() {
        return Duration.between(Instant.ofEpochMilli(System.currentTimeMillis()), endTime);
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public double getPrice() {
        return price;
    }

    public Player getHighestBidder() {
        return highestBidder;
    }

    public String toString() {
        String itemName = "";

        if (item.hasItemMeta() && !item.getItemMeta().hasDisplayName()) {
            itemName = item.getItemMeta().getDisplayName();
        } else {
            itemName = item.getType().name();
        }
        return itemName + ChatColor.RESET;
    }

    /**
     * For use in something like a GuiElementGroup, when it does not matter what the character is
     *
     * @return
     */
    public GuiElement getGuiElement(InventoryGui gui) {
        return getGuiElement('a', gui);
    }

    public GuiElement getGuiElement(char character, InventoryGui gui) {
        String[] name = {ItemUtils.getItemName(item)};
        String[] existingLore = (String[]) ArrayUtils.addAll(name, ItemUtils.getItemLoreArray(item));

        String[] separatorLore = ItemUtils.getSeparatorLoreArray();

        return new DynamicGuiElement(character, (viewer) -> {
            String[] extraLore;

            if (getDurationRemaining().isNegative()) {
                extraLore = new String[]{
                        ChatColor.RED + "This auction is expired!"
                };
            } else {
                extraLore = new String[]{
                        ChatColor.RESET + "" + ChatColor.GRAY + "Price: " + ChatColor.GOLD + SuperAuctionHouse.getInstance().getEconomy().format(price),
                        ChatColor.RESET + "" + ChatColor.GRAY + "Seller: " + sellerName,
                        ChatColor.RESET + "" + ChatColor.GRAY + "Duration: " + ChatColor.YELLOW + DurationUtils.formatDuration(getDurationRemaining()),
                        " ",
                        ChatColor.RESET + "" + ChatColor.YELLOW + String.format("Click to %s!", auctionType == AuctionType.AUCTION ? "bid" : "buy")
                };
            }

            return new StaticGuiElement(character, item,
                    click -> {
                        gui.playClickSound();
                        // TODO implement buy menu here
                        return true;
                    },
                    (String[]) ArrayUtils.addAll(ArrayUtils.addAll(existingLore, separatorLore), extraLore));
        });
    }
}
