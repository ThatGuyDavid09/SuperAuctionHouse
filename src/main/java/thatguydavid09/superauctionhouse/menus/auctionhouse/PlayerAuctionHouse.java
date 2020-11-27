package thatguydavid09.superauctionhouse.menus.auctionhouse;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerAuctionHouse extends BaseAuctionHouseMenu {
    public List<Inventory> auctionHouse = new ArrayList<>();
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

        for (ItemStack itemToAdd : sortItemsByName(itemsByName)) {
            addToMenu(itemToAdd, auctionHouse);
        }

        if (auctionHouse.size() == 0) {
            addPage(auctionHouse);
        }
    }
}
