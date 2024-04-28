package com.highmarsorbit.superauctionhouse.commands;

import com.highmarsorbit.superauctionhouse.Globals;
import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.inventories.SellItemMenu;
import com.highmarsorbit.superauctionhouse.util.AuctionItem;
import com.highmarsorbit.superauctionhouse.util.AuctionType;
import com.highmarsorbit.superauctionhouse.inventories.AuctionBrowserMenu;
import com.highmarsorbit.superauctionhouse.util.DurationUtils;
import com.highmarsorbit.superauctionhouse.util.ItemUtils;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.commandmanager.CommandHook;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AHCommand implements CommandExecutor {
    @CommandHook("ah")
    public void onAhOpen(Player player) {
        new AuctionBrowserMenu(player).open();
    }

    @CommandHook("sell")
    public void onSellItem(Player player, double price, String duration) {
        // Verify duration is in proper format, as otherwise SellItemMenu constructor throws an error
        if (duration != null && duration.trim().length() > 0) {
            try {
                DurationUtils.fromString(duration);
            } catch (IllegalArgumentException e) {
                duration = null;
            }
        }

        ItemStack itemToSell = player.getInventory().getItemInMainHand();

        SellItemMenu sellMenu = new SellItemMenu(player, price, duration);
        sellMenu.setSaleItem(itemToSell);
        sellMenu.open();
    }

    @CommandHook("refresh")
    public void onRefresh(Player player) {
        SuperAuctionHouse.getAuctionManager().refreshAvailableAuctions();
        SuperAuctionHouse.sendMessageByPath(player, "ah_refresh_success");
    }

    // TODO: remove
    @CommandHook("sell100")
    public void onSell100(Player player) {
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
                    item,
                    player,
                    price,
                    Duration.ofMinutes(durationMins),
                    auctionType == 0 ? AuctionType.AUCTION : AuctionType.BUY_IT_NOW)
            );
        }
    }

    @CommandHook("clear")
    public void onClear(Player player, String confirmCode) {
        if (confirmCode != null && confirmCode.trim().length() > 0) {
            String confirmStr = Globals.clearConfirmCodes.getOrDefault(player, "");
            if (confirmCode.equals(confirmStr)) {
                boolean success = SuperAuctionHouse.getAuctionManager().clear();
                if (success) {
                    player.sendMessage(SuperAuctionHouse.prefix + ChatColor.GREEN + "Auction house cleared!");
                } else {
                    player.sendMessage(SuperAuctionHouse.prefix + ChatColor.RED + "Error while clearing auction house!");
                }
            } else {
                player.sendMessage(SuperAuctionHouse.prefix + ChatColor.RED + "Authorization expired! Run /ah clear again.");
            }
            Globals.clearConfirmCodes.remove(player);
            return;
        }

        player.sendMessage(ChatColor.RED + "Are you sure you want to completely clear the" +
                " auction house? This is not reversible.");

        // To ensure somebody randomly typing doesn't accidentally confirm. They have to be REALLY sure
        Globals.clearConfirmCodes.remove(player);
        String confirmStr = RandomStringUtils.random(20, true, false);
        Globals.clearConfirmCodes.put(player, confirmStr);

        TextComponent clickableSection = new TextComponent("HERE");
        clickableSection.setBold(true);
        clickableSection.setColor(net.md_5.bungee.api.ChatColor.RED);
        clickableSection.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ah clear " + confirmStr));

        TextComponent hoverComponent = new TextComponent("This is irreversible!");
        hoverComponent.setItalic(true);
        hoverComponent.setColor(net.md_5.bungee.api.ChatColor.YELLOW);
        clickableSection.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text(new BaseComponent[]{ hoverComponent })));

        TextComponent section1 = new TextComponent("Please click ");
        section1.setColor(net.md_5.bungee.api.ChatColor.GOLD);
        TextComponent section2 = new TextComponent(" to confirm.");
        section2.setColor(net.md_5.bungee.api.ChatColor.GOLD);

        player.spigot().sendMessage(section1, clickableSection, section2);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(SuperAuctionHouse.getMessages().getMessage("ah_command_not_player_error"));
            return true;
        }
        Player player = (Player) sender;
        switch (args.length) {
            case 0 -> onAhOpen((Player) sender);

//                player.sendMessage(SuperAuctionHouse.prefix + "Pretend the auction house opened.");
            case 1 -> {
                switch (args[0]) {
                    case "sell100" -> onSell100((Player) sender);
                    case "refresh" -> onRefresh((Player) sender);
                    case "sell" -> onSellItem((Player) sender, -1, null);
                }
            }
        }
        return true;
    }
}
