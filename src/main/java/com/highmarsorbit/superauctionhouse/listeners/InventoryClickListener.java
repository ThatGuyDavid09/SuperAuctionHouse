package com.highmarsorbit.superauctionhouse.listeners;

import com.highmarsorbit.superauctionhouse.Globals;
import com.highmarsorbit.superauctionhouse.inventories.SellItemMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        boolean inPlayerInv = slot >= player.getOpenInventory().getTopInventory().getSize();
        if (Globals.waitingForClick.containsKey(player) && inPlayerInv) {
            SellItemMenu menu = Globals.waitingForClick.get(player);
            menu.getGui().playClickSound();
            menu.setSaleItem(event.getCurrentItem());
            Globals.waitingForClick.remove(player);

            event.setCancelled(true);
        }
    }
}
