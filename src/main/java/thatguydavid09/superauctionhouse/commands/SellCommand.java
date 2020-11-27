package thatguydavid09.superauctionhouse.commands;

import com.google.common.base.Strings;
import net.md_5.bungee.api.chat.hover.content.Item;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class SellCommand {
    public static boolean sell(Player player, String[] args) {
        BaseAuctionHouseMenu.addItem(player.getInventory().getItemInMainHand(), player, Long.parseLong(args[1]));
        ItemStack soldItem = player.getInventory().getItemInMainHand();

        if (Strings.isNullOrEmpty(soldItem.getItemMeta().getDisplayName())) {
            player.sendMessage(ChatColor.GREEN + "You have sold " + ChatColor.GOLD + soldItem.getAmount() + " " + WordUtils.capitalizeFully(String.valueOf(soldItem.getType()).replace("_", " ")) + ((soldItem.getAmount() != 1) ? "s" : "") + ChatColor.GREEN + " for " + ChatColor.GOLD + args[1]);
        } else {
            player.sendMessage(ChatColor.GREEN + "You have sold " + ChatColor.GOLD + soldItem.getAmount() + " " + soldItem.getItemMeta().getDisplayName() + ((soldItem.getAmount() != 1) ? "s" : "") + ChatColor.GREEN + " for " + ChatColor.GOLD + args[1]);
        }

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 0));
        return true;
    }
}
