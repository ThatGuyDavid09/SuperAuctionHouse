package thatguydavid09.superauctionhouse.events.bid;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.commands.PlayerCommands;
import thatguydavid09.superauctionhouse.events.generic.PreventItemRemoval;
import thatguydavid09.superauctionhouse.menus.bid.BidMenu;
import thatguydavid09.superauctionhouse.menus.bid.BidMenuActions;
import thatguydavid09.superauctionhouse.menus.sell.SellMenu;

public class CustomBidChatEvent implements Listener {
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if (BidMenuActions.playersEnteringBid.containsKey(event.getPlayer())) {
            try {
                Long.parseLong(event.getMessage());
            } catch (Exception e) {
                event.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "That is not a valid number!");
                event.setCancelled(true);
                return;
            }

            long bid = Long.parseLong(event.getMessage());

            if (bid < 0) {
                event.getPlayer().sendMessage(ChatColor.RED + "The bid must be greater than 0!");
                event.setCancelled(true);
                return;
            }

            if (bid <  SuperAuctionHouse.getInstance().getConfig().getInt("auctionhouse.minbidinterval")) {
                event.getPlayer().sendMessage(ChatColor.RED + "The bid must be greater than " + SuperAuctionHouse.getInstance().getConfig().getInt("auctionhouse.minbidinterval") + "!");
                event.setCancelled(true);
                return;
            }

            BidMenuActions.playersEnteringBid.replace(event.getPlayer(), bid);

            BidMenu menu = PreventItemRemoval.bidMenus.get(event.getPlayer());

            Bukkit.getScheduler().runTask(SuperAuctionHouse.getInstance(), () -> {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("").color(ChatColor.GREEN).create());
                menu.setBid(Math.max(0, BidMenuActions.playersEnteringBid.get(event.getPlayer()) - menu.getItem().getPrice()));
                BidMenuActions.playersEnteringBid.remove(event.getPlayer());
                menu.refreshInventory();
            });

            event.setCancelled(true);
        }
    }
}
