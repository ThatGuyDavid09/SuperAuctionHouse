package thatguydavid09.superauctionhouse.events.auctionhouse;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;

public class AuctionHouseChat implements Listener {
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if (BaseAuctionHouse.playersFindingStuff.contains(event.getPlayer())) {
            BaseAuctionHouse.playersFindingStuff.remove(event.getPlayer());
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("").color(ChatColor.GREEN).create());
            // Run synchronously, WHY DOES MINECRAFT RUN ON A SINGLE THREAD
            Bukkit.getScheduler().runTask(SuperAuctionHouse.getInstance(), () -> {
                if (BaseAuctionHouse.determineInvType(event.getPlayer()) == 0) {
                    AuctionHouseCommand.getAuctionHouse(event.getPlayer()).openAuctionHouse(event.getMessage());

                } else if (BaseAuctionHouse.determineInvType(event.getPlayer()) == 1) {
                    AuctionHouseCommand.getOwnAuctionHouse(event.getPlayer()).openAuctionHouse(event.getMessage());
                }
            });
            event.setCancelled(true);
        }
    }
}
