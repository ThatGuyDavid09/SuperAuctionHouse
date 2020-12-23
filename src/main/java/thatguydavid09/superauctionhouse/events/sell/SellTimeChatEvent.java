package thatguydavid09.superauctionhouse.events.sell;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.commands.PlayerCommands;
import thatguydavid09.superauctionhouse.menus.sell.SellMenu;

public class SellTimeChatEvent implements Listener {
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if (SellMenu.playersEnteringTime.containsKey(event.getPlayer())) {
            try {
                Long.parseLong(event.getMessage());
            } catch (Exception e) {
                event.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "That is not a valid number!");
                event.setCancelled(true);
                return;
            }

            Long time = Long.parseLong(event.getMessage());

            if (time < 0) {
                event.getPlayer().sendMessage(ChatColor.RED + "The time must be greater than 0!");
                event.setCancelled(true);
                return;
            }

            SellMenu.playersEnteringTime.replace(event.getPlayer(), -1L, time);

            SellMenu menu = PlayerCommands.sellMenuByPlayer.get(event.getPlayer());

            Bukkit.getScheduler().runTask(SuperAuctionHouse.getInstance(), () -> {
                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("").color(ChatColor.GREEN).create());
                menu.time = SellMenu.playersEnteringTime.get(event.getPlayer());
                SellMenu.playersEnteringTime.remove(event.getPlayer());
                event.getPlayer().openInventory(PlayerCommands.sellMenuByPlayer.get(event.getPlayer()).getInventory());
            });

            event.setCancelled(true);
        }
    }
}
