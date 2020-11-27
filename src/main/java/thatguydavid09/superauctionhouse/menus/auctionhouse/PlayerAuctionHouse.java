package thatguydavid09.superauctionhouse.menus.auctionhouse;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerAuctionHouse extends BaseAuctionHouseMenu {
    public List<Inventory> auctionHouse = new ArrayList<>();
    private List<ItemStack> currentlyDisplayedItems = new ArrayList<>();
    private final Player player;

    public PlayerAuctionHouse(Player player) {
        this.player = player;
    }

    public void openAuctionHouse() {
        update();
        player.openInventory(auctionHouse.get(0));
    }

    public void update() {
        clearAuctionHouseGui(auctionHouse);
        currentlyDisplayedItems.clear();

        // TODO remove this once you add ability to find items by name and replace it with something appropriate
        currentlyDisplayedItems.addAll(itemsByName.keySet());

        BiMap<ItemStack, String> items = HashBiMap.create();
        for (ItemStack itemToAdd : currentlyDisplayedItems) {
            items.put(itemToAdd, itemsByName.get(itemToAdd));
        }

        for (ItemStack itemToAdd : sortItemsByName(items)) {
            addToMenu(itemToAdd, auctionHouse);
            currentlyDisplayedItems.add(itemToAdd);
        }

        if (auctionHouse.size() == 0) {
            addPage(auctionHouse);
        }
    }
}
