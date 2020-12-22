package thatguydavid09.superauctionhouse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import java.util.UUID;

public class DevCommands {
    public static boolean add(Player player) {
        if (player.getUniqueId().equals(UUID.fromString("72644f06-45ae-44b8-80cd-aa96e1e1a873"))) {
            for (int i = 0; i <= 41; i++) {
                BaseAuctionHouseMenu.addItem(new ItemStack(Material.GRASS_BLOCK, 1), player, (int) (Math.random() * (100 - 5 + 1) + 5));
            }
            player.sendMessage(ChatColor.GREEN + "41 grass blocks priced randomly have been added to the auction house!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

            return true;
        } else {
            return false;
        }
    }

    public static boolean eco(Player player) {
        if (player.getUniqueId() == UUID.fromString("72644f06-45ae-44b8-80cd-aa96e1e1a873")) {
            player.sendMessage(String.valueOf(BaseAuctionHouseMenu.getMoney(player)));
            return true;
        } else {
            return false;
        }
    }
}
