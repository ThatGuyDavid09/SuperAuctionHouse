package thatguydavid09.superauctionhouse.menus.auctionhouse;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerAuctionHouse extends BaseAuctionHouseMenu {
    private final Player player;
    public List<Inventory> auctionHouse = new ArrayList<>();
    // 0 is Alphabetically A-Z. 1 is alphabetically Z-A, 2 is by price ascending, 3 is by price descending
    public int sortMode = 0;
    public String query = "";
    private List<ItemStack> currentlyDisplayedItems = new ArrayList<>();

    public PlayerAuctionHouse(Player player) {
        this.player = player;
    }

    public void openAuctionHouse() {
        update();
        player.openInventory(auctionHouse.get(0));
    }

    public void openAuctionHouse(String query) {
        this.query = query;
        update();
        player.openInventory(auctionHouse.get(0));
    }

    public void closeAuctionHouse() {
        player.closeInventory();
    }

    public void reloadAuctionHouse() {
        update();
        player.updateInventory();
    }

    public void update() {
        // Clear the GUI
        clearAuctionHouseGui(auctionHouse);
        currentlyDisplayedItems.clear();
        if (Strings.isNullOrEmpty(query)) {
            // Sort the items
            currentlyDisplayedItems.addAll(itemsByName.keySet());
        } else {
            currentlyDisplayedItems.addAll(filterItemsByName(query, itemsByName));
        }

        BiMap<ItemStack, String> items = HashBiMap.create();
        for (ItemStack itemToAdd : currentlyDisplayedItems) {
            items.put(itemToAdd, itemToAdd.getItemMeta().getPersistentDataContainer().get(nameKey, PersistentDataType.STRING));
        }

        if (auctionHouse.size() == 0) {
            addPage(auctionHouse);
        }

        switch (sortMode) {
            case 0:
                for (ItemStack itemToAdd : sortItemsByName(items)) {
                    addToMenu(itemToAdd, auctionHouse);
                    currentlyDisplayedItems.add(itemToAdd);
                }
                break;
            case 1:
                // Reverse sorted array to sort Z-A
                ArrayList<ItemStack> itemsToSort = sortItemsByName(items);
                Collections.reverse(itemsToSort);
                for (ItemStack itemToAdd : itemsToSort) {
                    addToMenu(itemToAdd, auctionHouse);
                    currentlyDisplayedItems.add(itemToAdd);
                }
                break;
            case 2:
                // Reverse sorted array to sort price descending
                itemsToSort = sortItemsByPrice(new ArrayList<>(items.keySet()));
                Collections.reverse(itemsToSort);

                for (ItemStack itemToAdd : itemsToSort) {
                    addToMenu(itemToAdd, auctionHouse);
                    currentlyDisplayedItems.add(itemToAdd);
                }
                break;
            case 3:
                itemsToSort = sortItemsByPrice(new ArrayList<>(items.keySet()));

                for (ItemStack itemToAdd : itemsToSort) {
                    addToMenu(itemToAdd, auctionHouse);
                    currentlyDisplayedItems.add(itemToAdd);
                }
                break;
        }

        // Update the sorting sunflower
        ItemStack newSortItem = sortItem.clone();
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

    public List<ItemStack> filterItemsByName(String query, BiMap<ItemStack, String> whatToFilter) {
        List<ItemStack> listToUpdate = new ArrayList<>();
        List<String> allItemNames = new ArrayList<>(whatToFilter.values());
        for (String name : allItemNames) {
            if (ChatColor.stripColor(name.toLowerCase()).startsWith(ChatColor.stripColor(query.toLowerCase()))) {
                listToUpdate.add(whatToFilter.inverse().get(name));
            }
        }
        return listToUpdate;
    }
}
