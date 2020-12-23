package thatguydavid09.superauctionhouse.events.sell;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import thatguydavid09.superauctionhouse.menus.sell.SellMenu;

public class SellNameChatEvent implements Listener {
    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        if (SellMenu.playersEnteringName.containsKey(event.getPlayer())) {

            String name = event.getMessage();

            if (name.equals("")) {
                event.getPlayer().sendMessage(ChatColor.RED + "The name cannot be null!");
                event.setCancelled(true);
                return;
            }

            SellMenu.playersEnteringName.replace(event.getPlayer(), "", name);
            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("").color(ChatColor.GREEN).create());
            event.setCancelled(true);
        }
    }
}
