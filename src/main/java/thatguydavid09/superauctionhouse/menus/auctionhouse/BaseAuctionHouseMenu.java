package thatguydavid09.superauctionhouse.menus.auctionhouse;

import com.google.common.base.Strings;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;

import java.util.*;

import static thatguydavid09.superauctionhouse.SuperAuctionHouse.placeholder;

public class BaseAuctionHouseMenu {
    public static List<Inventory> auctionHousePages;
    public static Inventory baseAuctionHouse;

    // Item to something
    public static BiMap<Player, List<ItemStack>> itemsForPlayer;
    public static HashMap<ItemStack, Player> itemsByPlayer = new HashMap<>();
    public static BiMap<ItemStack, String> itemsByName;

    // Items
    public static ItemStack findSign = null;
    public static ItemStack sortItem = null;
    public static ItemStack viewAuctions = null;
    public static ItemStack viewBids = null;
    public static ItemStack goBackArrow = null;
    public static ItemStack goForwardArrow = null;
    public static ItemStack howToSell = null;

    // Other necessary stuff
    public static SuperAuctionHouse plugin = SuperAuctionHouse.getInstance();
    public static long auctionId = 0;
    public static final NamespacedKey auctionIdKey = new NamespacedKey(plugin, "id");
    public static final NamespacedKey priceKey = new NamespacedKey(plugin, "price");
    public static final NamespacedKey sellingPlayerKey = new NamespacedKey(plugin, "sellingPlayer");
    public static final NamespacedKey nameKey = new NamespacedKey(plugin, "name");
    public static List<Player> playersFindingStuff = new ArrayList<>();
    public static HashMap<Player, List<ItemStack>> stashes = new HashMap<>();
    public static HashMap<Player, Long> banks = new HashMap<>();

    // TODO add way to back up ah
    public static void createAuctionHouse() {
        // Set variables
        auctionHousePages = new ArrayList<>();
        itemsForPlayer = HashBiMap.create();
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

        // Add first page
        addPage(auctionHousePages);
    }

    public static void addItem(ItemStack item, Player sellingPlayer, long price) {
        addNBT(item, auctionId, price);
        // Update dictionaries
        if (!itemsForPlayer.containsKey(sellingPlayer)) {
            itemsForPlayer.put(sellingPlayer, new ArrayList<>());
        } else {
            itemsForPlayer.get(sellingPlayer).add(item);
        }

        // This determines the name of the item. If it has a display name, we use that, else, we use its type
        if (item.getItemMeta().hasDisplayName() && !Strings.isNullOrEmpty(item.getItemMeta().getDisplayName())) {
            itemsByName.put(item, ChatColor.stripColor(item.getItemMeta().getDisplayName()) + " " + auctionId);
        } else {
            itemsByName.put(item, ChatColor.stripColor(item.getType().toString()) + " " + auctionId);
        }
        // Add correct lore
        ItemStack itemWithLore = addLore(item, sellingPlayer, price);

        // Add to auction house
        addToMenu(itemWithLore, auctionHousePages);
    }

    // TODO add remove item
    public static void removeItem(ItemStack item) {
        itemsByName.remove(item);
        itemsForPlayer.get(itemsForPlayer.get(item)).remove(item);
        itemsByPlayer.remove(item);
    }

    public static void giveItemToPlayer(ItemStack item, Player player) {
        ItemStack itemToGive = removeLore(item);
        itemToGive = removeNBT(itemToGive);

        List<ItemStack> itemsToAddToStash = (List<ItemStack>) player.getInventory().addItem(itemToGive).values();
        if (itemsToAddToStash.size() != 0) {
            player.sendMessage(ChatColor.RED + "An item couldn't be added to your inventory, so it was put into your stash. Type /ah stash to get all items in your stash!");
            if (!stashes.containsKey(player)) {
                stashes.put(player, new ArrayList<>());
            }
            stashes.get(player).addAll(itemsToAddToStash);
        }
    }

    private static ItemStack addLore(ItemStack item, Player sellingPlayer, long price) {
        ItemMeta meta = item.getItemMeta();
        if (meta.getLore() != null) {
            meta.setLore(ListUtils.union(meta.getLore(), Arrays.asList("", ChatColor.GRAY + "+------------------+", ChatColor.GREEN, "", "Sold by " + ChatColor.GOLD + sellingPlayer.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + price)));
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

    private static ItemStack addNBT(ItemStack item, Long id, Long price) {
        // nbt stuff
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        // Add ah id
        meta.getPersistentDataContainer().set(auctionIdKey, PersistentDataType.LONG, auctionId);
        auctionId++;

        // Add price as nbt
        container.set(priceKey, PersistentDataType.LONG, price);

        // Add name as nbt
        if (item.getItemMeta().hasDisplayName() && !Strings.isNullOrEmpty(item.getItemMeta().getDisplayName())) {
            container.set(nameKey, PersistentDataType.STRING, ChatColor.stripColor(item.getItemMeta().getDisplayName()) + auctionId);
        } else {
            container.set(nameKey, PersistentDataType.STRING, ChatColor.stripColor(item.getType().toString()) + auctionId);
        }

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack removeNBT(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.remove(auctionIdKey);
        container.remove(priceKey);
        container.remove(nameKey);

        item.setItemMeta(meta);
        return item;
    }

    public static void addToMenu(ItemStack item, List<Inventory> auctionHousePage) {
        if (auctionHousePage.size() == 0 || auctionHousePage.get(auctionHousePage.size() - 1).firstEmpty() == -1) {
            addPage(auctionHousePage);
        }
        Inventory lastInv = auctionHousePage.get(auctionHousePage.size() - 1);
        lastInv.setItem(lastInv.firstEmpty(), item);
    }

    /*************************************
     * The following deals with ah pages *
     ************************************/

    public static void addPage(List<Inventory> auctionHousePage) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 54, "Auction House");
        inventory.setContents(baseAuctionHouse.getContents());
        auctionHousePage.add(inventory);
        updateArrows(auctionHousePage);
    }

    public static void removePage(List<Inventory> auctionHousePages) {
        auctionHousePages.remove(auctionHousePages.size() - 1);
        updateArrows(auctionHousePages);
    }

    // Update the titles on the back and forward arrows in the menu with current page number
    private static void updateArrows(List<Inventory> auctionHousePage) {
        int pageNum = 1;

        for (Inventory inv : auctionHousePage) {
            if (auctionHousePage.size() == 1) {
                inv.setItem(48, placeholder);
                inv.setItem(50, placeholder);
            } else {
                if (pageNum == 1) {
                    inv.setItem(48, placeholder);
                    inv.setItem(50, createForwardArrowWithPage(1));
                } else if (pageNum == auctionHousePage.size()) {
                    inv.setItem(48, createBackArrowWithPage(auctionHousePage.size()));
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

    public static ArrayList<ItemStack> sortItemsByName(BiMap<ItemStack, String> itemsAndNames) {
        if (itemsAndNames.size() == 0) {
            return new ArrayList<>();
        }
        List<String> itemNames = new ArrayList<>(itemsAndNames.values());
        Collections.sort(itemNames);

        ArrayList<ItemStack> items = new ArrayList<>();
        ItemStack item;
        for (String name : itemNames) {
            item = itemsAndNames.inverse().get(name);
            items.add(item);
        }
        return items;
    }

    public static ArrayList<ItemStack> sortItemsByPrice(ArrayList<ItemStack> items) {
        if (items.size() == 0) {
            return new ArrayList<>();
        }
        BiMap<ItemStack, String> itemsAndPrices = HashBiMap.create();
        for (ItemStack item : items) {
            // This is done to ensure that no 2 values will be the same in the BiMap
            itemsAndPrices.put(item, String.valueOf(item.getItemMeta().getPersistentDataContainer().get(priceKey, PersistentDataType.LONG)) + " " + item.getItemMeta().getPersistentDataContainer().get(auctionIdKey, PersistentDataType.LONG));
        }

        List<List<Long>> prices = new ArrayList<>();
        for (String price : itemsAndPrices.values()) {
            prices.add(Arrays.asList(Long.parseLong(price.split(" ")[0]), Long.parseLong(price.split(" ")[1])));
        }
        prices.sort(Comparator.comparing(l -> l.get(0)));

        ArrayList<ItemStack> itemsToSort = new ArrayList<>();
        ItemStack item;
        for (List<Long> price : prices) {
            item = itemsAndPrices.inverse().get(price.get(0).toString() + " " + price.get(1).toString());
            itemsToSort.add(item);
        }
        return itemsToSort;
    }

    public static void clearAuctionHouseGui(List<Inventory> auctionHousePage) {
        auctionHousePage.clear();
    }

    // This removes all items from ah
    public static void clearAuctionHouse() {
        auctionHousePages.clear();
        itemsForPlayer = HashBiMap.create();
        itemsByName = HashBiMap.create();
        auctionId = 0;
    }
}
