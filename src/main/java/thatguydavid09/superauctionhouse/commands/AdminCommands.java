package thatguydavid09.superauctionhouse.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AdminCommands {
    private static HashMap<Player, String> clearcodes = new HashMap<>();

    /**
     * This sends the player a message
     *
     * @param player The <a href="#{@link}"{@link Player}> who called for the clear
     * @return Whether the player has permission to clear the auction house
     */
    public static boolean clear(Player player) {
        if (player.hasPermission("superauctionhouse.clear")) {
            player.sendMessage(ChatColor.RED + "Are you sure you want to do this?");
            TextComponent confirm = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "[CONFIRM CLEAR]");

            String code =  randomString(20);
            try {
                clearcodes.put(player, code);
            } catch (Exception ignored) {
                clearcodes.replace(player, code);
            }
            confirm.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ah clear " + player.getDisplayName() + " " + code));

            player.spigot().sendMessage(confirm);

            return true;
        } else {
            return false;
        }
    }

    /**
     * This confirms the auction house clear and notifies the player
     *
     * @param player The <a href="#{@link}"{@link Player}> who called for the clear
     */
    public static void clearConfirm(Player player, String clearcode) {
        if (clearcodes.get(player).equals(clearcode)) {
            clearcodes.remove(player);

            BaseAuctionHouseMenu.clearAuctionHouse();
            player.sendMessage(SuperAuctionHouse.getPrefix() + ChatColor.GREEN + "Auction House has been cleared!");
            BaseAuctionHouseMenu.resetAuctionId();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
        } else {
            clearcodes.clear();
        }
    }

    private static String randomString(int length) {
        String characters = "abcdefhijklmnopqrstuvwxyz0123456789";
        StringBuilder builder = new StringBuilder();
        while (length-- >= 0) {
            int character = (int) (Math.random()*characters.length());
            builder.append(characters.charAt(character));
        }
        return builder.toString();
    }


    public static boolean setloc(Player player) {
        if (player.hasPermission("superauctionhouse.setopenloc")) {
            FileConfiguration config = SuperAuctionHouse.getOpenBlocksConfig();
            Location loc = player.getLocation();
            String playerloc = loc.getWorld().getName() + "," + Math.floor(loc.getX()) + "," + Math.floor(loc.getY()) + "," + Math.floor(loc.getZ());

            List<String> blocks = config.getStringList("auctionhouse.openblocks");
            blocks.add(playerloc);
            config.set("auctionhouse.openblocks", blocks);

            try {
                config.save(SuperAuctionHouse.getOpenblocksConfigFile());
                player.sendMessage(SuperAuctionHouse.getPrefix() + ChatColor.GREEN + "This location can now be used to open the auction house!");
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "An error occurred saving this to the config file! See server logs for details.");
                SuperAuctionHouse.getInstance().getLogger().severe(e.getMessage());
                return true;
            }

            return true;
        }
        return false;
    }

    public static boolean unsetloc(Player player) {
        if (player.hasPermission("superauctionhouse.unsetloc")) {
            FileConfiguration config = SuperAuctionHouse.getOpenBlocksConfig();
            Location loc = player.getLocation();
            String playerloc = loc.getWorld().getName() + "," + Math.floor(loc.getX()) + "," + Math.floor(loc.getY()) + "," + Math.floor(loc.getZ());

            List<String> blocks = config.getStringList("auctionhouse.openblocks");
            blocks.remove(playerloc);
            config.set("auctionhouse.openblocks", blocks);

            try {
                config.save(SuperAuctionHouse.getOpenblocksConfigFile());
                player.sendMessage(SuperAuctionHouse.getPrefix() + ChatColor.GREEN + "This location will no longer open the auction house!");
            } catch (IOException e) {
                player.sendMessage(ChatColor.RED + "An error occurred saving this to the config file! See server logs for details.");
                SuperAuctionHouse.getInstance().getLogger().severe(e.getMessage());
                return true;
            }

            return true;
        }
        return false;
    }
}
