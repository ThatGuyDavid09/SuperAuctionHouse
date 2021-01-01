package thatguydavid09.superauctionhouse.commands;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;
import thatguydavid09.superauctionhouse.menus.sell.SellMenu;

public class PlayerCommands {
    public static BiMap<Player, SellMenu> sellMenuByPlayer = HashBiMap.create();

    /**
     * Puts into motion a series of methods and updates various dictionaries in order to sell an item
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
     * @param menu The <a href="#{@link}"{@link SellMenu}> associated with the sell operation
     */
    public static void confirmSell(SellMenu menu) {
        if (Strings.isNullOrEmpty(menu.displayName)) {
            BaseAuctionHouseMenu.addItem(menu.item, menu.player, menu.price, menu.time, menu.mode == 2);
        } else {
            BaseAuctionHouseMenu.addItem(menu.item, menu.player, menu.price, menu.displayName, menu.time, menu.mode == 2);
        }
        // Make sure to withdraw fee
        SuperAuctionHouse.getEconomy().withdrawPlayer(menu.player, menu.getFee());
        menu.player.sendMessage(ChatColor.GREEN + "Auction has been created!");
    }
}
