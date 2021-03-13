package thatguydavid09.superauctionhouse.menus.bid;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BidMenuActions {
    public static HashMap<Player, Long> playersEnteringBid = new HashMap<>();

    public static void getCustomBid(Player player) {
        player.closeInventory();
        playersEnteringBid.put(player, 0L);

        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Type the bid you want in chat!").color(ChatColor.GREEN).create());
    }
}
