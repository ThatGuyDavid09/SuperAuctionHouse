package com.thatguydavid.superauctionhouse.commands;

import com.thatguydavid.superauctionhouse.SuperAuctionHouse;
import com.thatguydavid.superauctionhouse.inventories.AuctionHouse;
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
import java.util.Random;

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
                new AuctionHouse(player, SuperAuctionHouse.getMessages().getMessage("ah_title")).open();
//                player.sendMessage(SuperAuctionHouse.prefix + "Pretend the auction house opened.");
                break;
            case 1:
                if (args[0].equals("sell")) {
                    for (int i = 0; i < 100; i++) {
                        Random random = new Random();
                        int material = random.nextInt(Material.values().length);
                        int amount = random.nextInt(64);
                        int auctionType = random.nextInt(2);
                        double price = random.nextDouble() * 30000 + 1;
                        int durationMins = random.nextInt(30);
                        ItemStack item = new ItemStack(Material.values()[material], Math.min(Material.values()[material].getMaxStackSize(), amount));
//                        if (item)
                        SuperAuctionHouse.getAuctionManager().listAuction(new AuctionItem(
                                SuperAuctionHouse.getAuctionManager().getNextUsableId(),
                                item,
                                player,
                                price,
                                Duration.ofMinutes(durationMins),
                                auctionType == 0 ? AuctionType.AUCTION : AuctionType.BUY_IT_NOW)
                        );
                    }
                } else if (args[0].equals("refresh")) {
                    SuperAuctionHouse.getAuctionManager().refreshAvailableAuctions();
                }
        }
        return true;
    }
}
