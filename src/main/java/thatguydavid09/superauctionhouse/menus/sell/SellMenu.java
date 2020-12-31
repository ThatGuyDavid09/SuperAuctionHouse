package thatguydavid09.superauctionhouse.menus.sell;

import com.google.common.base.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;

import java.util.HashMap;

import static thatguydavid09.superauctionhouse.SuperAuctionHouse.placeholder;

public class SellMenu {
    public static HashMap<Player, Long> playersEnteringPrice = new HashMap<>();
    public static HashMap<Player, Long> playersEnteringTime = new HashMap<>();
    public static HashMap<Player, String> playersEnteringName = new HashMap<>();

    public ItemStack priceItem = null;
    public ItemStack confirmItem = null;
    public ItemStack cancelItem = null;
    public ItemStack playerNameItem = null;
    public ItemStack instaBuyItem = null;
    public ItemStack auctionItem = null;
    public ItemStack infSellItem = null;
    public ItemStack timeItem = null;

    public final Player player;
    public final ItemStack item;
    public Inventory menu;

    public long price = -1;
    public long time = -1;
    public String displayName = "";
    public int mode = 0; // 0 for instabuy, 1 for auction, 2 for infSell

    /**
     * Represents the menu that players see when they try to sell an item
     * @param player The player trying to sell an item
     * @param item The item in question
     */
    public SellMenu(Player player, ItemStack item) {
        this.player = player;
        this.item = item;

        if (priceItem == null) {
            createItems();
        }

        createMenu();
    }

    /**
     * Updates the menu
     */
    public void refreshInventory() {
        createMenu();

        player.openInventory(menu);
    }

    /**
     * This creates the sell menu gui
     */
    private void createMenu() {
        updateItems();

        menu = null;
        menu = Bukkit.createInventory(null, 54, "Sell item");

        // Set items
        menu.setItem(13, item);

        menu.setItem(29, priceItem);
        menu.setItem(31, placeholder);

        menu.setItem(49, cancelItem);

        if (price > 0) {
            if (mode == 1) {
                if (time > 0) {
                    menu.setItem(31, confirmItem);
                }
            } else {
                menu.setItem(31, confirmItem);
            }
        }

        // Set correct item for mode
        switch (mode) {
            case 0:
                menu.setItem(33, instaBuyItem);
                menu.setItem(28, placeholder);
                break;
            case 1:
                menu.setItem(33, auctionItem);
                menu.setItem(28, timeItem);
                break;
            case 2:
                menu.setItem(33, infSellItem);
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

    /**
     * This updates the items in the gui, such as the price and the time items
     */
    private void updateItems() {
        ItemMeta meta = priceItem.getItemMeta();
        if (price > 0) {
            meta.setDisplayName(ChatColor.GOLD + "Set price: " + ChatColor.GREEN + price + ChatColor.GOLD + " " + (price == 1 ? SuperAuctionHouse.getEconomy().currencyNameSingular() : SuperAuctionHouse.getEconomy().currencyNamePlural()));
        } else {
            meta.setDisplayName(ChatColor.GOLD + "Set price");
        }
        priceItem.setItemMeta(meta);

        meta = timeItem.getItemMeta();
        if (time > 0) {
            meta.setDisplayName(ChatColor.GREEN + "Set time: " + ChatColor.GOLD + time + ChatColor.GOLD + " " + (time == 1 ? "minute" : "minutes"));
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Set time");
        }
        timeItem.setItemMeta(meta);

        meta = playerNameItem.getItemMeta();
        if (!Strings.isNullOrEmpty(displayName)) {
            meta.setDisplayName(ChatColor.GREEN + "Change selling name: " + ChatColor.GRAY + displayName);
        } else {
            meta.setDisplayName(ChatColor.GREEN + "Change selling name");
        }
        playerNameItem.setItemMeta(meta);
    }

    /**
     * This creates the items in the gui
     */
    private void createItems() {
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

        instaBuyItem = new ItemStack(Material.GOLD_INGOT);
        meta = instaBuyItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Instant buy");
        instaBuyItem.setItemMeta(meta);

        auctionItem = new ItemStack(Material.CLOCK);
        meta = auctionItem.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Auction");
        auctionItem.setItemMeta(meta);

        timeItem = new ItemStack(Material.CLOCK);
        meta = timeItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Set time");
        timeItem.setItemMeta(meta);

        infSellItem = new ItemStack(Material.GOLD_BLOCK);
        meta = infSellItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Infinite Sell");
        infSellItem.setItemMeta(meta);
    }

    /**
     * This returns the gui
     * @return The <a href="#{@link}"{@link Inventory}> that this class represents
     */
    public Inventory getInventory() {
        return menu;
    }

    /**
     * This increments the sell mode
     */
    public void incrementMode() {
        if (mode > 1) {
            mode = 0;
            return;
        }

        if (mode == 1) {
            if (player.hasPermission("superauctionhouse.sell.infsell")) {
                mode++;
            } else {
                mode = 0;
            }
        } else {
            mode++;
        }
    }
}
