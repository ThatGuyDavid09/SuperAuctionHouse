package thatguydavid09.superauctionhouse.menus.auctionhouse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.collections.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;

import java.util.*;

import static thatguydavid09.superauctionhouse.SuperAuctionHouse.placeholder;

public class BaseAuctionHouseMenu {
    public static List<Inventory> auctionHousePages;
    public static Inventory baseAuctionHouse;

    // Item to something
    public static BiMap<ItemStack, String> itemsByPlayerName;
    public static BiMap<ItemStack, Integer> itemsByPrice;
    public static BiMap<ItemStack, String> itemsByName;

    // Items
    public static ItemStack findSign;
    public static ItemStack sortItem;
    public static ItemStack viewAuctions;
    public static ItemStack viewBids;
    public static ItemStack goBackArrow;
    public static ItemStack goForwardArrow;
    public static ItemStack howToSell;

    public static SuperAuctionHouse plugin = SuperAuctionHouse.getInstance();

    public static void createAuctionHouse() {
        // Set variables
        auctionHousePages = new ArrayList<>();
        itemsByPlayerName = HashBiMap.create();
        itemsByPrice = HashBiMap.create();
        itemsByName = HashBiMap.create();

        // Sets base auction house inventory
        // Make auction house inventory
        baseAuctionHouse = Bukkit.getServer().createInventory(null, 54, "Auction House");

        // Set placeholder items
        baseAuctionHouse.setItem(47, placeholder);
        baseAuctionHouse.setItem(51, placeholder);

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

        baseAuctionHouse.setItem(52, findSign);

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

    public static void addItem(ItemStack item, Player sellingPlayer, int price) {
        // Add ah id
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(plugin, "id");
        meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, itemsByPlayerName.size() + 1);
        item.setItemMeta(meta);

        // Update dictionaries
        itemsByPlayerName.put(item, sellingPlayer.getDisplayName());
        itemsByPrice.put(item, price);
        itemsByName.put(item, ChatColor.stripColor(item.getItemMeta().getDisplayName()));

        // Add correct lore
        ItemStack itemWithLore = addLore(item, sellingPlayer, price);

        // Add to auction house
        addToMenu(itemWithLore);

        // TODO temporary sort
        sortItemsByName();
    }

    // TODO add remove item
    // TODO add sort item feature

    private static ItemStack addLore(ItemStack item, Player sellingPlayer, int price) {
        ItemMeta meta = item.getItemMeta();
        if (meta.getLore() != null) {
            meta.setLore(ListUtils.union(meta.getLore(), Arrays.asList("\n", ChatColor.GRAY + "+------------------+", ChatColor.GREEN, "\n", "Sold by " + ChatColor.GOLD + sellingPlayer.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + price)));
        } else {
            meta.setLore(Arrays.asList(ChatColor.GREEN + "Sold by " + ChatColor.GOLD + sellingPlayer.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + price));
        }
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack removeLore(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.getLore().remove(meta.getLore().size() - 1);
        if (meta.getLore().size() > 0) {
            meta.getLore().remove(meta.getLore().size() - 1);
            meta.getLore().remove(meta.getLore().size() - 1);
            meta.getLore().remove(meta.getLore().size() - 1);
        }
        item.setItemMeta(meta);

        return item;
    }

    private static void addToMenu(ItemStack item) {
        if (auctionHousePages.size() == 0 || auctionHousePages.get(auctionHousePages.size() - 1).firstEmpty() == -1) {
            plugin.getLogger().info("Detected full page");
            addPage();
        }
        plugin.getLogger().info("Adding item");
        Inventory lastInv = auctionHousePages.get(auctionHousePages.size() - 1);
        lastInv.setItem(lastInv.firstEmpty(), item);
    }

    /*************************************
     * The following deals with ah pages *
     ************************************/

    public static void addPage() {
        Inventory inventory = Bukkit.getServer().createInventory(null, 54, "Auction House");
        inventory.setContents(baseAuctionHouse.getContents());
        auctionHousePages.add(inventory);
        updateArrows();
        plugin.getLogger().info("Added page");
    }

    public static void removePage() {
        auctionHousePages.remove(auctionHousePages.size() - 1);
        updateArrows();
    }

    // Update the titles on the back and forward arrows in the menu with current page number
    private static void updateArrows() {
        int pageNum = 1;

        for (Inventory inv : auctionHousePages) {
            if (auctionHousePages.size() == 1) {
                inv.setItem(48, placeholder);
                inv.setItem(50, placeholder);
            } else {
                if (pageNum == 1) {
                    inv.setItem(48, placeholder);
                    inv.setItem(50, createForwardArrowWithPage(1));
                } else if (pageNum == auctionHousePages.size()) {
                    inv.setItem(48, createBackArrowWithPage(auctionHousePages.size()));
                    inv.setItem(50, placeholder);
                } else {
                    inv.setItem(48, createBackArrowWithPage(pageNum));
                    inv.setItem(50, createForwardArrowWithPage(pageNum));
                }
            }

            pageNum++;
        }
    }

    private static ItemStack createBackArrowWithPage(int currentPage) {
        Material type;
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Previous page " + ChatColor.GRAY + "(Page " + currentPage + "/" + auctionHousePages.size() + ")");
        arrow.setItemMeta(meta);
        return arrow;
    }

    private static ItemStack createForwardArrowWithPage(int currentPage) {
        Material type;
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Next page " + ChatColor.GRAY + "(Page " + currentPage + "/" + auctionHousePages.size() + ")");
        arrow.setItemMeta(meta);
        return arrow;
    }

    private static void sortItemsByName() {
        List<String> itemNames = new ArrayList(itemsByName.values());
        Collections.sort(itemNames);

        clearAuctionHouse();

        ItemStack item = null;
        for (String name : itemNames)
            item = itemsByName.inverse().get(name);
            // FIXME WHY DOES THIS LOOKUP FAIL THEY ARE THE EXACT SAME
            // This garbage parses the lore of the item to get the player selling it because for some reason looking it up from the BiMap just doesn't work
            Player player = Bukkit.getPlayer(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).split("§6")[1].split("§a")[0]);
            // Same here for the price
            int price = Integer.parseInt(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1).split("§6")[2]);
            addToMenu(item/*, player, price*/);

    }

    private static void clearAuctionHouse() {
        auctionHousePages.clear();
    }
}
