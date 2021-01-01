package thatguydavid09.superauctionhouse.menus.auctionhouse;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import thatguydavid09.superauctionhouse.AuctionItem;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;

import java.util.*;

import static thatguydavid09.superauctionhouse.SuperAuctionHouse.placeholder;

public class PlayerAuctionHouse extends BaseAuctionHouseMenu {
    private final Player player;
    // 0 is Alphabetically A-Z. 1 is alphabetically Z-A, 2 is by price ascending, 3 is by price descending
    public int sortMode = 0;
    public String query = "";
    private List<Inventory> auctionHouse = new ArrayList<>();
    private List<AuctionItem> currentlyDisplayedItems = new ArrayList<>();

    /**
     * Creates a personal auction house for the given player
     * @param player The <a href="#{@link}"{@link Player}> to who the ah belongs
     */
    public PlayerAuctionHouse(Player player) {
        this.player = player;
    }

    /*************************************
     * The following deals with ah pages *
     ************************************/

    /**
     * Adds a page to the auction house
     * @param auctionHousePages The list to which a page should be added
     */
    public static void addPage(List<Inventory> auctionHousePages) {
        Inventory inventory = Bukkit.getServer().createInventory(null, 54, SuperAuctionHouse.getInstance().getConfig().getString("auctionhouse.names.auctionhouse"));
        inventory.setContents(BaseAuctionHouseMenu.getBaseAuctionHouse().getContents());
        auctionHousePages.add(inventory);
        updateArrows(auctionHousePages);
    }

    /**
     * Updates the arrows in the auction house that allow you to change pages
     * @param auctionHousePages The list of pages to update arrows on
     */
    private static void updateArrows(List<Inventory> auctionHousePages) {
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

    /**
     * Generates a back arrow with the current page number
     * @param currentPage The current page number
     * @return A back arrow with the correct title and lore
     */
    private static ItemStack createBackArrowWithPage(int currentPage) {
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Previous page " + ChatColor.GRAY + "(Page " + currentPage + "/" + BaseAuctionHouseMenu.getNumOfItems() / 44 + ")");
        arrow.setItemMeta(meta);
        return arrow;
    }

    /**
     * Generates a forward arrow with the current page number
     * @param currentPage The current page number
     * @return A forward arrow with the correct title and lore
     */
    private static ItemStack createForwardArrowWithPage(int currentPage) {
        ItemStack arrow = new ItemStack(Material.ARROW, 1);
        ItemMeta meta = arrow.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Next page " + ChatColor.GRAY + "(Page " + currentPage + "/" + BaseAuctionHouseMenu.getNumOfItems() / 44 + ")");
        arrow.setItemMeta(meta);
        return arrow;
    }

    /**
     * This sorts the items in the given BiMap by name
     * @param itemsAndNames The <a href="#{@link}"{@link BiMap}> with the items and names to be sorted
     * @return The sorted <a href="#{@link}"{@link BiMap}>
     */
    public static ArrayList<AuctionItem> sortItemsByName(BiMap<AuctionItem, String> itemsAndNames) {
        if (itemsAndNames.size() == 0) {
            return new ArrayList<>();
        }
        List<String> itemNames = new ArrayList<>(itemsAndNames.values());
        Collections.sort(itemNames);

        ArrayList<AuctionItem> items = new ArrayList<>();
        AuctionItem item;
        for (String name : itemNames) {
            item = itemsAndNames.inverse().get(name);
            items.add(item);
        }
        return items;
    }

    /**
     * This sorts the items in the given List by price
     * @param items The <a href="#{@link}"{@link ArrayList}> with the <a href="#{@link}"{@link AuctionItem}>s to be sorted
     * @return The sorted <a href="#{@link}"{@link ArrayList}>
     */
    public static ArrayList<AuctionItem> sortItemsByPrice(ArrayList<AuctionItem> items) {
        if (items.size() == 0) {
            return new ArrayList<>();
        }
        BiMap<AuctionItem, String> itemsAndPrices = HashBiMap.create();
        for (AuctionItem item : items) {
            // This is done to ensure that no 2 values will be the same in the BiMap
            itemsAndPrices.put(item, item.getPrice() + " " + item.getId());
        }

        List<List<Long>> prices = new ArrayList<>();
        for (String price : itemsAndPrices.values()) {
            prices.add(Arrays.asList(Long.parseLong(price.split(" ")[0]), Long.parseLong(price.split(" ")[1])));
        }
        prices.sort(Comparator.comparing(l -> l.get(0)));

        ArrayList<AuctionItem> itemsToSort = new ArrayList<>();
        AuctionItem item;
        for (List<Long> price : prices) {
            item = itemsAndPrices.inverse().get(price.get(0).toString() + " " + price.get(1).toString());
            itemsToSort.add(item);
        }
        return itemsToSort;
    }

    /**
     * This removes all items from the ah gui
     * @param auctionHousePages The list of pages
     */
    public static void clearAuctionHouseGui(List<Inventory> auctionHousePages) {
        auctionHousePages.clear();
    }

    /**
     * This adds an <a href="#{@link}"{@link ItemStack}> to the auction house gui
     * @param item The <a href="#{@link}"{@link ItemStack}> to be added
     * @param auctionHousePages The list of pages it should be added to
     */
    public static void addToMenu(ItemStack item, List<Inventory> auctionHousePages) {
        if (auctionHousePages.size() == 0 || auctionHousePages.get(auctionHousePages.size() - 1).firstEmpty() == -1) {
            addPage(auctionHousePages);
        }
        Inventory lastInv = auctionHousePages.get(auctionHousePages.size() - 1);
        lastInv.setItem(lastInv.firstEmpty(), item);
    }

    /**
     * This opens the auction house for the player this class belongs to
     */
    public void openAuctionHouse() {
        update();
        player.openInventory(auctionHouse.get(0));
    }

    /**
     * This opens the auction house for the player this class belongs to but filtered by a certain query
     * @param query The query to filter by
     */
    public void openAuctionHouse(String query) {
        this.query = query;
        update();
        player.openInventory(auctionHouse.get(0));
    }

    /**
     * This refreshes the auction house
     */
    public void reloadAuctionHouse() {
        update();
        openAuctionHouse();
    }

    /**
     * This updates the auction house
     */
    public void update() {
        // Clear the GUI
        clearAuctionHouseGui(auctionHouse);
        currentlyDisplayedItems.clear();
        if (Strings.isNullOrEmpty(query)) {
            // Sort the items
            currentlyDisplayedItems.addAll(BaseAuctionHouseMenu.getAllItems());
        } else {
            currentlyDisplayedItems.addAll(filterItemsByName(query, BaseAuctionHouseMenu.getAllItems()));
        }

        BiMap<AuctionItem, String> items = HashBiMap.create();
        for (AuctionItem itemToAdd : currentlyDisplayedItems) {
            items.put(itemToAdd, itemToAdd.getName() + "" + itemToAdd.getId());
        }

        if (auctionHouse.size() == 0) {
            addPage(auctionHouse);
        }

        switch (sortMode) {
            case 0:
                for (AuctionItem itemToAdd : sortItemsByName(items)) {
                    addToMenu(itemToAdd.getItem(), auctionHouse);
                    currentlyDisplayedItems.add(itemToAdd);
                }
                break;
            case 1:
                // Reverse sorted array to sort Z-A
                ArrayList<AuctionItem> itemsToSort = sortItemsByName(items);
                Collections.reverse(itemsToSort);
                for (AuctionItem itemToAdd : itemsToSort) {
                    addToMenu(itemToAdd.getItem(), auctionHouse);
                    currentlyDisplayedItems.add(itemToAdd);
                }
                break;
            case 2:
                // Reverse sorted array to sort price descending
                itemsToSort = sortItemsByPrice(new ArrayList<>(items.keySet()));
                Collections.reverse(itemsToSort);

                for (AuctionItem itemToAdd : itemsToSort) {
                    addToMenu(itemToAdd.getItem(), auctionHouse);
                    currentlyDisplayedItems.add(itemToAdd);
                }
                break;
            case 3:
                itemsToSort = sortItemsByPrice(new ArrayList<>(items.keySet()));

                for (AuctionItem itemToAdd : itemsToSort) {
                    addToMenu(itemToAdd.getItem(), auctionHouse);
                    currentlyDisplayedItems.add(itemToAdd);
                }
                break;
        }

        // Update the sorting sunflower
        ItemStack newSortItem = BaseAuctionHouseMenu.getSortItem().clone();
        switch (sortMode) {
            case 0:
                ItemMeta meta = newSortItem.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.BLUE + "Alphabetically, A-Z"));
                newSortItem.setItemMeta(meta);
                break;
            case 1:
                meta = newSortItem.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.BLUE + "Alphabetically, Z-A"));
                newSortItem.setItemMeta(meta);
                break;
            case 2:
                meta = newSortItem.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.BLUE + "Price, descending"));
                newSortItem.setItemMeta(meta);
                break;
            case 3:
                meta = newSortItem.getItemMeta();
                meta.setLore(Arrays.asList(ChatColor.BLUE + "Price, ascending"));
                newSortItem.setItemMeta(meta);
                break;
        }

        for (Inventory page : auctionHouse) {
            page.setItem(49, newSortItem);
        }
    }

    /**
     * This filters items in the given list by name
     * @param query The query to filter by
     * @param whatToFilter The list of items to filter
     * @return The filtered list of items
     * <a href="#{@link}"{@link ItemStack}>
     */
    public List<AuctionItem> filterItemsByName(String query, List<AuctionItem> whatToFilter) {
        List<AuctionItem> listToUpdate = new ArrayList<>();
        List<String> allItemNames = new ArrayList<>();

        for (AuctionItem item : whatToFilter) {
            allItemNames.add(item.getName());
        }

        for (String name : allItemNames) {
            if (ChatColor.stripColor(name.toLowerCase()).contains(ChatColor.stripColor(query.toLowerCase()))) {
                for (AuctionItem item : whatToFilter) {
                    if (item.getName().equals(name)) {
                        listToUpdate.add(item);
                    }
                }
            }
        }
        return listToUpdate;
    }

    /**
     * Gets the current auction house
     * @return The current auction house fpr this player
     */
    public List<Inventory> getAuctionHouse() {
        return auctionHouse;
    }
}
