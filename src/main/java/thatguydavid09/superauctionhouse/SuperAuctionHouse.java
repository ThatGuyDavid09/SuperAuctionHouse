package thatguydavid09.superauctionhouse;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.events.generic.PreventItemRemoval;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import java.util.Collections;

public final class SuperAuctionHouse extends JavaPlugin {
    private static SuperAuctionHouse instance;
    public static ItemStack placeholder;


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

        initMenus();

        this.getCommand("superauctionhouse").setExecutor(new AuctionHouseCommand());

        // Register command
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SuperAuctionHouse has been enabled!");

        // Register events
        getServer().getPluginManager().registerEvents(new PreventItemRemoval(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SuperAuctionHouse getInstance() {
        return instance;
    }

    public static ItemStack addEnchantGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.LURE, 1);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);

        return item;
    }

    private void initMenus() {
        BaseAuctionHouseMenu.createAuctionHouse();
    }
}
