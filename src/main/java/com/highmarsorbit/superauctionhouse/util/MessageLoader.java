package com.highmarsorbit.superauctionhouse.util;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessageLoader {
    private final YamlConfiguration config;
    private final boolean upToDate;
    public MessageLoader(YamlConfiguration yamlConfig) {
        config = yamlConfig;
        String configVersion = config.getString("version");
        upToDate = SuperAuctionHouse.getInstance().getDescription().getVersion().equals(configVersion);
        if (!upToDate) {
            SuperAuctionHouse.getInstance().getLogger().severe("messages.yml not up to date! Some chat messages " +
                    "won't display properly. Delete messages.yml and reload the plugin to recreate a new version.");
        }
    }

    public String getMessage(String path) {
        String unReplaced = config.getString(path);
        if (unReplaced == null) {
            if (upToDate) {
                SuperAuctionHouse.getInstance().getLogger().warning(String.format("%s not found in up-to-date messages.yml! " +
                        "Did you remove any entries? If not, this is a bug.", path));
            } else {
                SuperAuctionHouse.getInstance().getLogger().warning(String.format("%s not found in up-to-date messages.yml! " +
                        "This is likely because your messages.yml is out of date.", path));
            }
            return path;
        }
        String replaced = unReplaced.replace("($PREFIX)", SuperAuctionHouse.prefix);
        replaced = replaced.replace("($BLACK)", ChatColor.BLACK.toString());
        replaced = replaced.replace("($DARK_BLUE)", ChatColor.DARK_BLUE.toString());
        replaced = replaced.replace("($DARK_GREEN)", ChatColor.DARK_GREEN.toString());
        replaced = replaced.replace("($DARK_AQUA)", ChatColor.DARK_AQUA.toString());
        replaced = replaced.replace("($DARK_RED)", ChatColor.DARK_RED.toString());
        replaced = replaced.replace("($DARK_PURPLE)", ChatColor.DARK_PURPLE.toString());
        replaced = replaced.replace("($GOLD)", ChatColor.GOLD.toString());
        replaced = replaced.replace("($GRAY)", ChatColor.GRAY.toString());
        replaced = replaced.replace("($DARK_GRAY)", ChatColor.DARK_GRAY.toString());
        replaced = replaced.replace("($BLUE)", ChatColor.BLUE.toString());
        replaced = replaced.replace("($GREEN)", ChatColor.GREEN.toString());
        replaced = replaced.replace("($AQUA)", ChatColor.AQUA.toString());
        replaced = replaced.replace("($RED)", ChatColor.RED.toString());
        replaced = replaced.replace("($LIGHT_PURPLE)", ChatColor.LIGHT_PURPLE.toString());
        replaced = replaced.replace("($YELLOW)", ChatColor.YELLOW.toString());
        replaced = replaced.replace("($MAGIC)", ChatColor.MAGIC.toString());
        replaced = replaced.replace("($BOLD)", ChatColor.BOLD.toString());
        replaced = replaced.replace("($STRIKETHROUGH)", ChatColor.STRIKETHROUGH.toString());
        replaced = replaced.replace("($UNDERLINE)", ChatColor.UNDERLINE.toString());
        replaced = replaced.replace("($ITALIC)", ChatColor.ITALIC.toString());
        replaced = replaced.replace("($RESET)", ChatColor.RESET.toString());
        return replaced;
    }
}
