package thatguydavid09.superauctionhouse.commands;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminCommands {
    public static boolean clear(Player player) {
        if (player.hasPermission("superauctionhouse.clear")) {
            BaseAuctionHouseMenu.clearAuctionHouse();
            player.sendMessage(ChatColor.GREEN + "Auction House has been cleared!");
            BaseAuctionHouseMenu.resetAuctionId();
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);

            Connection connection = null;
            Statement statement;
            try {
                SuperAuctionHouse.openConnection();
                connection = SuperAuctionHouse.getConnection();
                statement = connection.createStatement();

                statement.execute("TRUNCATE TABLE auctionhouse");
            } catch (SQLException e) {
                SuperAuctionHouse.getInstance().getLogger().warning("Something has gone wrong with the database, see error log below");
                e.printStackTrace();
            } finally {
                // Close connection
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        SuperAuctionHouse.getInstance().getLogger().warning("Something has gone wrong while closing the connection, see error log below");
                        e.printStackTrace();
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
