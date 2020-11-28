package thatguydavid09.superauctionhouse.events.generic;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import thatguydavid09.superauctionhouse.AuctionItem;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseActions;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;
import thatguydavid09.superauctionhouse.menus.buy.BuyMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getLogger;

public class PreventItemRemoval implements Listener {
    private static BuyMenu confirm = null;

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {

        // List of forbidden inventory titles
        List<String> forbiddenTitles = new ArrayList<>(Arrays.asList("Auction House", "Confirm purchase"));
        if (event.getClickedInventory() != null && forbiddenTitles.contains(event.getView().getTitle())) {
            // Identify inventory as ah
            List<Inventory> auctionHousePage = AuctionHouseCommand.getAuctionHouse((Player) event.getWhoClicked()).getAuctionHouse();
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
                    ItemMeta meta = null;
                    for (AuctionItem item : BaseAuctionHouseMenu.getAllItems()) {
                        if (item.getId() == event.getCurrentItem().getItemMeta().getPersistentDataContainer().get(BaseAuctionHouseMenu.auctionIdKey, PersistentDataType.LONG)) {
                            confirm = new BuyMenu(item, (Player) event.getWhoClicked());
                            break;
                        }
                    }
                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                    confirm.openBuyMenu();
                } else {
                    event.setCancelled(true);
                }
            } else if (confirm != null && event.getInventory() == confirm.getBuyMenu()) {
                // Identify inventory as BuyMenu and restrict to buyMenu
                if (event.getRawSlot() >= 0 || event.getRawSlot() <= 8) {
                    if (event.getRawSlot() == 2) {
                        getLogger().info("Confirm pressed");
                        confirm.confirmPurchase();
                        event.setCancelled(true);
                    } else if (event.getRawSlot() == 6) {
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
}
