package com.highmarsorbit.superauctionhouse.util;

import de.themoep.inventorygui.GuiElement;
import org.apache.commons.lang.ArrayUtils;
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
        return ChatColor.RESET + WordUtils.capitalizeFully(item.getType().name().replace("_", " "));
    }

    public static String[] getSeparatorLoreArray() {
        return new String[]{
//                " ",
                ChatUtils.RESET +  ChatColor.GRAY + ChatColor.BOLD + "________________",
                " ",
        };
    }

    public static String[] getLoreWithSeparator(ItemStack item) {
        // First item in array is null to prevent display name from being overwritten
        // (this way maintains language settings)
        String[] name = {null};
        String[] existingLore = getItemLoreArray(item);

        String[] separatorLore = getSeparatorLoreArray();

        String[] allExist;

//        if (existingLore.length == 0) {
//            allExist = name;
//        } else {
        allExist = (String[]) ArrayUtils.addAll(ArrayUtils.addAll(name, existingLore), separatorLore);
//        }
        return allExist;
    }
}
