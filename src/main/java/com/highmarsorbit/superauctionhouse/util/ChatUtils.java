package com.highmarsorbit.superauctionhouse.util;

import org.bukkit.ChatColor;

public class ChatUtils {
    // Needed because cannot string 2 chatcolors back to back
    // Also for setting to gray since lore default is purple
    public final static String RESET = ChatColor.RESET + "" + ChatColor.GRAY + "";
}
