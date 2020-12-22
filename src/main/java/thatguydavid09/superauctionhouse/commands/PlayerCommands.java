package thatguydavid09.superauctionhouse.commands;

import com.google.common.base.Strings;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import java.text.NumberFormat;

public class PlayerCommands {
    public static boolean sell(Player player, String[] args) {
        ItemStack soldItem = player.getInventory().getItemInMainHand();

        if (soldItem.getAmount() == 0) {
            player.sendMessage(ChatColor.RED + "Please put your hotbar cursor over the item you want to sell!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            return true;
        }

        try {
            Long.parseLong(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("Usage: /ah sell <price>");
            return true;
        }

        if (Long.parseLong(args[1]) < 0) {
            player.sendMessage(ChatColor.RED + "The price of an item cannot be negative!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            return true;
        }

        BaseAuctionHouseMenu.addItem(player.getInventory().getItemInMainHand(), player, Long.parseLong(args[1]));

        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setGroupingUsed(true);

        if (Strings.isNullOrEmpty(soldItem.getItemMeta().getDisplayName())) {
            player.sendMessage(ChatColor.GREEN + "You have sold " + ChatColor.GOLD + soldItem.getAmount() + " " + WordUtils.capitalizeFully(String.valueOf(soldItem.getType()).replace("_", " ")) + ChatColor.GREEN + " for " + ChatColor.GOLD + numberFormat.format(Long.parseLong(args[1])) + " " + ChatColor.GREEN + ((Long.parseLong(args[1]) == 1) ? SuperAuctionHouse.getEconomy().currencyNameSingular() : SuperAuctionHouse.getEconomy().currencyNamePlural()) + "!");
        } else {
            player.sendMessage(ChatColor.GREEN + "You have sold " + ChatColor.GOLD + soldItem.getAmount() + " " + soldItem.getItemMeta().getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + numberFormat.format(Long.parseLong(args[1])) + " " + ChatColor.GREEN + ((Long.parseLong(args[1]) == 1) ? SuperAuctionHouse.getEconomy().currencyNameSingular() : SuperAuctionHouse.getEconomy().currencyNamePlural()) + "!");
        }

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 0));
        return true;
    }
}
