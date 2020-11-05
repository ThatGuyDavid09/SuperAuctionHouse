package thatguydavid09.superauctionhouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class SuperAuctionHouse extends JavaPlugin {
    private static SuperAuctionHouse instance;
    public static ItemStack placeholder;
    public static Inventory auctionHouse;

    @Override
    public void onEnable() {
        // Allows for getInstance to work
        instance = this;

        // Sets placeholder item
        placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = placeholder.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLore(Collections.emptyList());
        placeholder.setItemMeta(itemMeta);

        // Sets base auction house inventory
        // Make auction house inventory
        auctionHouse = Bukkit.getServer().createInventory(null, 54, "Auction House");

        // Set placeholder items
        auctionHouse.setItem(46, placeholder);
        auctionHouse.setItem(47, placeholder);
        auctionHouse.setItem(51, placeholder);
        auctionHouse.setItem(52, placeholder);

        // Set the arrows
        ItemStack goBackArrow = new ItemStack(Material.ARROW, 1);
        ItemStack goForwardArrow = new ItemStack(Material.ARROW, 1);

        itemMeta = goBackArrow.getItemMeta();
        itemMeta.setDisplayName(ChatColor.BLUE + "Previous page");
        goBackArrow.setItemMeta(itemMeta);

        itemMeta = goForwardArrow.getItemMeta();
        itemMeta.setDisplayName(ChatColor.BLUE + "Next page");
        goForwardArrow.setItemMeta(itemMeta);

        auctionHouse.setItem(48, goBackArrow);
        auctionHouse.setItem(50, goForwardArrow);

        // Set the barrier
        ItemStack closeBarrier = new ItemStack(Material.BARRIER, 1);
        itemMeta = closeBarrier.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Close auction house");
        closeBarrier.setItemMeta(itemMeta);

        auctionHouse.setItem(49, closeBarrier);

        // Set the search sign
        ItemStack findSign = new ItemStack(Material.OAK_SIGN, 1);
        itemMeta = findSign.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Find an item");
        findSign.setItemMeta(itemMeta);

        auctionHouse.setItem(53, findSign);

        // Set the sort sign
        ItemStack sortSing = new ItemStack(Material.OAK_SIGN, 1);
        itemMeta = sortSing.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Sort by:");
        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "Alphabetically, A-Z"));
        sortSing.setItemMeta(itemMeta);

        auctionHouse.setItem(45, sortSing);

        this.getCommand("superauctionhouse").setExecutor(new AuctionHouseCommand());

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SuperAuctionHouse has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public SuperAuctionHouse getInstance() {
        return instance;
    }

    public static ItemStack addEnchantGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.LURE, 1);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);

        return item;
    }
}
