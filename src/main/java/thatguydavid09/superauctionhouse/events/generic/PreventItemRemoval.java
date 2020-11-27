package thatguydavid09.superauctionhouse.events.generic;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseActions;
import thatguydavid09.superauctionhouse.menus.buy.BuyMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class PreventItemRemoval implements Listener {
    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        BuyMenu confirm = null;
        // List of forbidden inventory titles
        List<String> forbiddenTitles = new ArrayList<>(Arrays.asList("Auction House", "Confirm purchase"));
        if (event.getClickedInventory() != null && forbiddenTitles.contains(event.getView().getTitle())) {
            // Identify inventory as ah
            List<Inventory> auctionHousePage = AuctionHouseCommand.auctionHousesByPlayer.get(event.getWhoClicked()).auctionHouse;
            if (auctionHousePage.contains(event.getClickedInventory())) {
                if (event.getRawSlot() == 50 && event.getCurrentItem().getType() == Material.ARROW) {
                    AuctionHouseActions.nextPage((Player) event.getWhoClicked());
                    event.setCancelled(true);
                } else if (event.getRawSlot() == 48 && event.getCurrentItem().getType() == Material.ARROW) {
                    AuctionHouseActions.previousPage((Player) event.getWhoClicked());
                    event.setCancelled(true);
                } else if (event.getRawSlot() == 49 && event.getCurrentItem().getType() == Material.SUNFLOWER) {
                    AuctionHouseActions.cycleSortMode((Player) event.getWhoClicked());
                    event.setCancelled(true);
                } else if (event.getRawSlot() == 52 && event.getCurrentItem().getType() == Material.OAK_SIGN) {
                    event.setCancelled(true);
                    AuctionHouseActions.find((Player) event.getWhoClicked());
                } else if (event.getRawSlot() <= 44) {
                    // AH item is clicked
                    confirm = new BuyMenu(event.getCurrentItem(), (Player) event.getWhoClicked());
                    confirm.openBuyMenu();
                } else {
                    event.setCancelled(true);
                }
            } else if (event.getInventory() == BuyMenu.buyMenu) {
                // Identify inventory as BuyMenu
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Confirm purchase")) {
                    getLogger().info("Confirm pressed");
                    confirm.confirmPurchase();
                    event.setCancelled(true);
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Cancel purchase")) {
                    getLogger().info("Cancel pressed");
                    confirm.cancelPurchase();
                    event.setCancelled(true);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
}
