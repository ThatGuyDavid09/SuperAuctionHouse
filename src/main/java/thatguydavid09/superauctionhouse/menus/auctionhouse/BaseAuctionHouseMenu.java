package thatguydavid09.superauctionhouse.menus.auctionhouse;

import org.apache.commons.collections.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static thatguydavid09.superauctionhouse.SuperAuctionHouse.placeholder;

public class BaseAuctionHouseMenu {
    public static List<Inventory> auctionHousePages = new LinkedList<>();
    public static Inventory baseAuctionHouse;

    public static HashMap itemByPlayer = new LinkedHashMap<ItemStack, Player>();
    public static HashMap itemByPrice = new LinkedHashMap<ItemStack, Double>();

    // Items
    public static ItemStack findSign;
    public static ItemStack sortItem;
    public static ItemStack viewAuctions;
    public static ItemStack viewBids;
    public static ItemStack goBackArrow;
    public static ItemStack goForwardArrow;
    public static ItemStack howToSell;

    public static void createAuctionHouse() {
        // Sets base auction house inventory
        // Make auction house inventory
        baseAuctionHouse = Bukkit.getServer().createInventory(null, 54, "Auction House");

        // Set placeholder items
        baseAuctionHouse.setItem(47, placeholder);
        baseAuctionHouse.setItem(52, placeholder);
        baseAuctionHouse.setItem(53, placeholder);

        // Set view auctions diamond
        viewAuctions = new ItemStack(Material.DIAMOND, 1);
        ItemMeta itemMeta = viewAuctions.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "View your auctions");
        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "You currently have " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "outstanding ",
                ChatColor.BLUE + "auctions and " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "auctions ready to claim."));
        viewAuctions.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(45, viewAuctions);

        // Set view bids item
        viewBids = new ItemStack(Material.GOLD_INGOT, 1);
        itemMeta = viewBids.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "View your bids");
        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "You currently have " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "outstanding ",
                ChatColor.BLUE + "bids and " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "bids ready to claim."));
        viewBids.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(46, viewBids);

        // Set the sort item
        sortItem = new ItemStack(Material.SUNFLOWER, 1);
        itemMeta = sortItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Sort by:");
        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "Alphabetically, A-Z"));
        sortItem.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(49, sortItem);

        // Set the arrows
        goBackArrow = new ItemStack(Material.ARROW, 1);
        goForwardArrow = new ItemStack(Material.ARROW, 1);

        itemMeta = goBackArrow.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Previous page " + ChatColor.GRAY + "(Page 0/0)");
        goBackArrow.setItemMeta(itemMeta);

        itemMeta = goForwardArrow.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Next page " + ChatColor.GRAY + "(Page 0/0)");
        goForwardArrow.setItemMeta(itemMeta);

        // These are set later
        baseAuctionHouse.setItem(48, placeholder); // For back arrow
        baseAuctionHouse.setItem(50, placeholder); // For forward arrow

        // Set the search sign
        findSign = new ItemStack(Material.OAK_SIGN, 1);
        itemMeta = findSign.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Find an item");
        findSign.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(51, findSign);

        // Set the how to sell book
        howToSell = new ItemStack(Material.BOOK, 1);
        itemMeta = howToSell.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "How do I sell an item?");
        itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Simply type " + ChatColor.AQUA + "/ah sell <price>",
                ChatColor.GREEN + "while holding the item you",
                ChatColor.GREEN + "want to sell!"));
        howToSell.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(53, howToSell);
    }

    public static void addItem(ItemStack item, Player sellingPlayer, double price) {
        // TODO finish this
        // Update dictionaries
        itemByPlayer.put(item, sellingPlayer);
        itemByPrice.put(item, price);

        // Add correct lore
        ItemStack itemWithLore = addLore(item, sellingPlayer, price);

        // Add to auction house
        addToMenu(itemWithLore);
    }

    private static ItemStack addLore(ItemStack item, Player sellingPlayer, double price) {
        ItemMeta meta = item.getItemMeta();
        // TODO finish this
        if (meta.getLore() != null) {
            meta.setLore(ListUtils.union(meta.getLore(), Arrays.asList("\n" + ChatColor.GRAY + "+------------------+", ChatColor.GREEN + "Sold by " + sellingPlayer.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + price)));
        } else {
            meta.setLore(Arrays.asList(ChatColor.GREEN + "Sold by " + sellingPlayer.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + price));
        }
        item.setItemMeta(meta);
        return item;
    }

    private static void addToMenu(ItemStack item) {
        // If first page
        if (auctionHousePages.size() == 0) {
            Inventory tempFirstPage = baseAuctionHouse;
            auctionHousePages.add(tempFirstPage);

            baseAuctionHouse.setItem(auctionHousePages.get(0).firstEmpty(), item);
        } else {
            if (auctionHousePages.get(auctionHousePages.size() - 1).firstEmpty() != -1) {
                baseAuctionHouse.setItem(auctionHousePages.get(auctionHousePages.size() - 1).firstEmpty(), item);
            } else {
                addPage();
            }
        }
    }

    private static void addPage() {
        // TODO make the arrows update name based on num of pages
        // TODO fix the bug that sets a back arrow for the first page for some reason
        auctionHousePages.add(baseAuctionHouse);
        auctionHousePages.get(auctionHousePages.size() - 2).setItem(50, goForwardArrow);
        auctionHousePages.get(auctionHousePages.size() - 1).setItem(48, goBackArrow);
    }
}
