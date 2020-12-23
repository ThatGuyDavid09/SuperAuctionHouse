package thatguydavid09.superauctionhouse.events.generic;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.commands.PlayerCommands;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseActions;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;
import thatguydavid09.superauctionhouse.menus.buy.BuyMenu;
import thatguydavid09.superauctionhouse.menus.sell.SellMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreventItemRemoval implements Listener {
    private static BuyMenu confirm = null;

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {

        // List of forbidden inventory titles
        List<String> forbiddenTitles = new ArrayList<>(Arrays.asList("Auction House", "Confirm purchase", "Sell item"));
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
                    confirm = new BuyMenu(BaseAuctionHouseMenu.itemStackToAuctionItem(event.getCurrentItem()), (Player) event.getWhoClicked());

                    ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                    confirm.openBuyMenu();
                } else {
                    event.setCancelled(true);
                }
            } else if (confirm != null && event.getInventory() == confirm.getBuyMenu()) {
                // Identify inventory as BuyMenu and restrict to buyMenu
                if (event.getRawSlot() >= 0 || event.getRawSlot() <= 8) {
                    if (event.getRawSlot() == 2) {
                        confirm.confirmPurchase();
                        event.setCancelled(true);
                    } else if (event.getRawSlot() == 6) {
                        confirm.cancelPurchase();
                        event.setCancelled(true);
                    } else {
                        event.setCancelled(true);
                    }
                }
                // Identify as sell menu inventory
            } else if (event.getInventory().getItem(29).getType() == Material.SUNFLOWER) {
                SellMenu menu = PlayerCommands.sellMenuByPlayer.get(event.getWhoClicked());

                // Handle price
                if (event.getRawSlot() == 29 && event.getInventory().getItem(29).getType() == Material.SUNFLOWER) {
                    event.getWhoClicked().closeInventory();
                    SellMenu.playersEnteringPrice.put((Player) event.getWhoClicked(), -1L);
                    ((Player) event.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Type the desired price of the item in chat! (Not negative and no decimals)").color(ChatColor.GREEN).create());
                    event.setCancelled(true);
                }

                // Handle setting custom name
                if (event.getRawSlot() == 40 && event.getInventory().getItem(40).getType() == Material.PLAYER_HEAD) {
                    event.getWhoClicked().closeInventory();
                    SellMenu.playersEnteringName.put((Player) event.getWhoClicked(), "");
                    ((Player) event.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Type the desired name to sell as in chat (Color codes work)").color(ChatColor.GREEN).create());
                    event.setCancelled(true);
                }

                // Handle setting time
                if (event.getRawSlot() == 28 && event.getInventory().getItem(40).getType() == Material.CLOCK) {
                    event.getWhoClicked().closeInventory();
                    SellMenu.playersEnteringTime.put((Player) event.getWhoClicked(), -1L);
                    ((Player) event.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Type the desired auction time (in minutes)").color(ChatColor.GREEN).create());
                    event.setCancelled(true);
                }

                event.setCancelled(true);
            }
        }
    }
}
