package thatguydavid09.superauctionhouse.menus.auctionhouse;

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

public class PlayerAuctionHouse extends BaseAuctionHouseMenu {
    private final Player player;
    public List<Inventory> auctionHouse = new ArrayList<>();
    // 0 is Alphabetically A-Z. 1 is alphabetically Z-A, 2 is by price ascending, 3 is by price descending
    public int sortMode = 0;
    private List<ItemStack> currentlyDisplayedItems = new ArrayList<>();

    public PlayerAuctionHouse(Player player) {
        this.player = player;
    }

    public void openAuctionHouse() {
        update();
        player.openInventory(auctionHouse.get(0));
    }

    public void closeAuctionHouse() {
        player.closeInventory();
    }

    public void reloadAuctionHouse() {
        closeAuctionHouse();
        update();
        openAuctionHouse();
        ;
    }

    public void update() {
        // Clear the GUI
        clearAuctionHouseGui(auctionHouse);
        currentlyDisplayedItems.clear();

        // Sort the items
        // TODO remove this once you add ability to find items by name and replace it with something appropriate
        currentlyDisplayedItems.addAll(itemsByName.keySet());

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
}
