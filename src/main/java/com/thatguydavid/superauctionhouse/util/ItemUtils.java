package com.thatguydavid.superauctionhouse.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class ItemUtils {
    public static String[] getItemLoreArray(ItemStack item) {
        String[] itemLore;

        if (item.hasItemMeta() && item.getItemMeta().hasLore()) {
            itemLore = item.getItemMeta().getLore().toArray(new String[0]);
        } else {
            itemLore = new String[0];
        }

        return itemLore;
    }

    public static String getItemName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return WordUtils.capitalizeFully(item.getType().name().replace("_", " "));
    }

    public static String[] getSeparatorLoreArray() {
        return new String[]{
//                " ",
                ChatColor.RESET + "" +  ChatColor.GRAY + ChatColor.BOLD + "________________",
                " ",
        };
    }
}
