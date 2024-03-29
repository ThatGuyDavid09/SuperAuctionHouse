package com.highmarsorbit.superauctionhouse.commands;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.inventories.SellItemMenu;
import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import com.highmarsorbit.superauctionhouse.util.AuctionType;
import com.highmarsorbit.superauctionhouse.inventories.AuctionBrowserMenu;
import com.highmarsorbit.superauctionhouse.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
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
                new AuctionBrowserMenu(player, SuperAuctionHouse.getMessages().getMessage("ah_title")).open();
//                player.sendMessage(SuperAuctionHouse.prefix + "Pretend the auction house opened.");
                break;
            case 1:
                switch (args[0]) {
                    case "sell100" -> {
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
                    }
                    case "refresh" -> SuperAuctionHouse.getAuctionManager().refreshAvailableAuctions();
                    case "sell" -> {
                        ItemStack itemToSell = player.getInventory().getItemInMainHand();
                        Bukkit.getLogger().info(ItemUtils.getItemName(itemToSell));
                        SellItemMenu sellMenu = new SellItemMenu(player, SuperAuctionHouse.getMessages().getMessage("sell_menu_title"), itemToSell);
                        sellMenu.open();
                    }
                }
        }
        return true;
    }
}
