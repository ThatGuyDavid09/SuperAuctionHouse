package thatguydavid09.superauctionhouse.commands;

import net.md_5.bungee.api.chat.hover.content.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class SellCommand {
    public static boolean sell(Player player, String[] args) {
        BaseAuctionHouseMenu.addItem(player.getInventory().getItemInMainHand(), player, Integer.parseInt(args[1]));
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 0));
        return true;
    }
}
