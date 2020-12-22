package thatguydavid09.superauctionhouse.menus.sell;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static thatguydavid09.superauctionhouse.SuperAuctionHouse.placeholder;

public class SellMenu {
    private static ItemStack priceItem = null;
    private static ItemStack confirmItem = null;
    private static ItemStack cancelItem = null;
    private static ItemStack playerNameItem = null;
    private static ItemStack insbuyItem = null;
    private static ItemStack auctionItem = null;
    private static ItemStack timeItem = null;
    public final Player player;
    public final ItemStack item;
    public Inventory menu;

    public boolean auctionMode = false;

    public SellMenu(Player player, ItemStack item) {
        this.player = player;
        this.item = item;

        createMenu();
    }

    private void createMenu() {
        menu = Bukkit.createInventory(null, 54, "Sell item");

        if (priceItem == null) {
            // Create items
            priceItem = new ItemStack(Material.SUNFLOWER);
            ItemMeta meta = priceItem.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Set price");
            priceItem.setItemMeta(meta);

            confirmItem = new ItemStack(Material.GREEN_CONCRETE);
            meta = confirmItem.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Confirm purchase");
            confirmItem.setItemMeta(meta);

            cancelItem = new ItemStack(Material.BARRIER);
            meta = cancelItem.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "Cancel purchase");
            cancelItem.setItemMeta(meta);

            playerNameItem = new ItemStack(Material.PLAYER_HEAD);
            meta = playerNameItem.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Change selling name");
            playerNameItem.setItemMeta(meta);

            insbuyItem = new ItemStack(Material.GOLD_INGOT);
            meta = insbuyItem.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Instant buy");
            insbuyItem.setItemMeta(meta);

            auctionItem = new ItemStack(Material.CLOCK);
            meta = auctionItem.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "Auction");
            auctionItem.setItemMeta(meta);

            timeItem = new ItemStack(Material.CLOCK);
            meta = timeItem.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "Set time");
            timeItem.setItemMeta(meta);
        }

        // Set items
        menu.setItem(13, item);

        menu.setItem(29, priceItem);
        menu.setItem(31, confirmItem);

        menu.setItem(49, cancelItem);

        if (auctionMode) {
            menu.setItem(28, timeItem);
            menu.setItem(33, auctionItem);
        } else {
            menu.setItem(33, insbuyItem);
            menu.setItem(28, placeholder);
        }

        if (player.hasPermission("superauctionhouse.sell.asothers")) {
            menu.setItem(40, playerNameItem);
        }

        ItemStack[] contents = menu.getContents();
       int index = 0;
        for (ItemStack item : contents) {
            if (item == null) {
                contents[index] = placeholder;
            }
            index++;
        }

        menu.setContents(contents);
    }

    public Inventory getInventory() {
        return menu;
    }
}
