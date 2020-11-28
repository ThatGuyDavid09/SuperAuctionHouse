package thatguydavid09.superauctionhouse;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseChat;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHousePlayerFreeze;
import thatguydavid09.superauctionhouse.events.generic.PreventItemRemoval;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import java.util.Collections;
import java.util.logging.Logger;

public final class SuperAuctionHouse extends JavaPlugin {
    // For vault
    private static final Logger log = Logger.getLogger("Minecraft");
    public static ItemStack placeholder;
    private static SuperAuctionHouse instance;
    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    // End vault stuff

    public static SuperAuctionHouse getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

    @Override
    public void onEnable() {
        // Vault stuff
        if (!setupEconomy()) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // End vault stuff

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
        getServer().getPluginManager().registerEvents(new AuctionHouseChat(), this);
        getServer().getPluginManager().registerEvents(new AuctionHousePlayerFreeze(), this);
    }

    @Override
    public void onDisable() {
        // Vault stuff
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        // End vault stuff
    }

    private void initMenus() {
        BaseAuctionHouseMenu.createAuctionHouse();
    }

    // Vault stuff
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    // End vault stuff
}
