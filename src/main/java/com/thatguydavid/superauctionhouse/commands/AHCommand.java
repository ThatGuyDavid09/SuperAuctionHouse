package com.thatguydavid.superauctionhouse.commands;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import com.thatguydavid.superauctionhouse.util.AuctionItem;
import com.thatguydavid.superauctionhouse.util.AuctionType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.Objects;

public class AHCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SuperAuctionHouse.getMessages().getMessage("ah_command_not_player_error"));
            return true;
        }
        Player player = (Player) sender;
        switch (args.length) {
            case 0:
                player.sendMessage(SuperAuctionHouse.prefix + "Pretend the auction house opened.");
                break;
            case 1:
                if (Objects.equals(args[0], "sell")) {
                    SuperAuctionHouse.getAuctionManager().listAuction(new AuctionItem(
                            1,
                            new ItemStack(Material.ACACIA_BOAT),
                            player,
                            100,
                            Duration.ofHours(20),
                            AuctionType.AUCTION)
                    );
                }
        }
        return true;
    }
}
