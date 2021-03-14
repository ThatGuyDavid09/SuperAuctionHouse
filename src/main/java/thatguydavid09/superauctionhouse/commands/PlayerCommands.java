package thatguydavid09.superauctionhouse.commands;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.AuctionItem;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.events.custom.PlayerSellEvent;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;
import thatguydavid09.superauctionhouse.menus.sell.SellMenu;

public class PlayerCommands {
    public static BiMap<Player, SellMenu> sellMenuByPlayer = HashBiMap.create();

    /**
     * Puts into motion a series of methods and updates various dictionaries in order to sell an item
     *
     * @param player The <a href="#{@link}"{@link Player}> selling the item
     * @return Whether the player has permission to sell the item
     */
    public static boolean sell(Player player) {
        if (player.hasPermission("superauctionhouse.sell")) {
            ItemStack soldItem = player.getInventory().getItemInMainHand();

            if (soldItem.getAmount() == 0) {
                player.sendMessage(ChatColor.RED + "Please put your hotbar cursor over the item you want to sell!");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
                return true;
            }

            sellMenuByPlayer.remove(player);
            SellMenu menu = new SellMenu(player, soldItem);
            sellMenuByPlayer.put(player, menu);

            player.openInventory(menu.getInventory());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finishes the sell process
     *
     * @param menu The <a href="#{@link}"{@link SellMenu}> associated with the sell operation
     */
    public static void confirmSell(SellMenu menu, Player player) {
        PlayerSellEvent event;
        if (Strings.isNullOrEmpty(menu.displayName)) {
            event = BaseAuctionHouse.addItem(menu.item, menu.player, menu.price, menu.time * 60, menu.mode == 2, menu.mode == 1);
        } else {
            // ItemStack item, Player sellingPlayer, long price, String playerName, long time, boolean infsell, boolean isAuction
            event = BaseAuctionHouse.addItem(menu.item, menu.player, menu.price, menu.displayName, menu.time, menu.mode == 2, menu.mode == 1);
        }
        if (event.isCancelled()) {
            return;
        }
        // Make sure to withdraw fee
        SuperAuctionHouse.getEconomy().withdrawPlayer(menu.player, menu.getFee());
        player.getInventory().setItemInMainHand(null);
        menu.player.sendMessage(SuperAuctionHouse.getPrefix() + ChatColor.GREEN + "Auction has been created!");
    }



    public static boolean help(Player player) {
        if (player.hasPermission("superauctionhouse.help")) {
            player.sendMessage(SuperAuctionHouse.getPrefix() + ChatColor.GREEN + "Available commands:");
            if (player.hasPermission("superauctionhouse.open")) {
                player.sendMessage(ChatColor.BLUE + "- /ah " + ChatColor.GREEN + ": Opens the auction house.");
            }

            if (player.hasPermission("superauctionhouse.sell")) {
                player.sendMessage(ChatColor.BLUE + "- /ah sell " + ChatColor.GREEN + ": Opens the sell menu for the currently held item.");
            }

            if (player.hasPermission("superauctionhouse.setopenloc")) {
                player.sendMessage(ChatColor.BLUE + "- /ah setloc " + ChatColor.GREEN + ": Makes the block at your feet able to open the auction house.");
            }

            if (player.hasPermission("superauctionhouse.unsetloc")) {
                player.sendMessage(ChatColor.BLUE + "- /ah setloc " + ChatColor.GREEN + ": Makes the block at your feet not able to open the auction house.");
            }

            if (player.hasPermission("superauctionhouse.clear")) {
                player.sendMessage(ChatColor.BLUE + "- /ah clear " + ChatColor.GREEN + ": Removes all items from the auction house.");
            }

            if (player.hasPermission("superauctionhouse.help")) {
                player.sendMessage(ChatColor.BLUE + "- /ah help " + ChatColor.GREEN + ": Displays this text.");
            }

            if (player.hasPermission("superauctionhouse.ping")) {
                player.sendMessage(ChatColor.BLUE + "- /ah ping " + ChatColor.GREEN + ": Pings the plugin, returns the name and version.");
            }

            if (player.hasPermission("superauctionhouse.backup")) {
                player.sendMessage(ChatColor.BLUE + "- /ah backup " + ChatColor.GREEN + ": Backs up the auction house.");
            }

            if (player.hasPermission("superauctionhouse.viewplayerah")) {
                player.sendMessage(ChatColor.BLUE + "- /ah <playerName> " + ChatColor.GREEN + ": Shows you all auctions for sale by the specified player.");
            }
            return true;
        }
        return false;
    }

    /**
     * Opens the auction house filtered by a given player name
     * @param player The player who the ah should be opened for (must be online)
     * @param playerName The name of the player to filter by
     * @return Whether the player has permission to open the ah
     */
    public static boolean openByPlayer(Player player, String playerName) {
        if (player.hasPermission("superauctionhouse.viewplayerah")) {
            AuctionHouseCommand.getAuctionHouse(player).openAuctionHouse(playerName, true);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Sends the player some info about the plugin.
     * @param player The player to send info to.
     * @return Whether the player has permission to execute this command.
     */
    public static boolean ping(Player player) {
        if (player.hasPermission("superauctionhouse.ping")) {
            player.sendMessage(SuperAuctionHouse.getPrefix() + ChatColor.AQUA + " SuperAuctionHouse, version " + ChatColor.GREEN + SuperAuctionHouse.getInstance().getDescription().getVersion());
            return true;
        }
        return false;
    }
}
