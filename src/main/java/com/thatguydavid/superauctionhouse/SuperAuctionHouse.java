package com.thatguydavid.superauctionhouse;

import com.thatguydavid.superauctionhouse.commands.AHCommand;
import com.thatguydavid.superauctionhouse.listeners.AuctionListListener;
import com.thatguydavid.superauctionhouse.managers.AuctionManager;
import com.thatguydavid.superauctionhouse.storage.DummyStorage;
import com.thatguydavid.superauctionhouse.storage.Storage;
import com.thatguydavid.superauctionhouse.util.MessageLoader;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class SuperAuctionHouse extends JavaPlugin {
    public final static String prefix = ChatColor.RESET + "[" + ChatColor.AQUA + "SuperAuctionHouse" + ChatColor.RESET + "] ";
    public final static String prefixNoColor = ChatColor.stripColor(prefix);
    private static MessageLoader messages;

    private static SuperAuctionHouse instance;
    private static AuctionManager auctionManager;
    private Economy econ;
    private Storage store;


    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();

        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands();
        registerEventListeners();
        createAuctionManager();
        registerScheduledTasks();
    }

    private void registerScheduledTasks() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            auctionManager.refreshAvailableAuctions();
        }, 0L, 20L*60L); // Refresh available auctions every minute
    }

    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new AuctionListListener(), this);
    }

    private void createAuctionManager() {
        store = new DummyStorage();
        auctionManager = new AuctionManager(store);
    }

    private void registerCommands() {
        this.getCommand("ah").setExecutor(new AHCommand());
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private void loadConfigs() {
        File messagesConfigFile = new File(getDataFolder(), "messages.yml");
        saveResource("messages.yml", true);
        // Commented out for testing reasons.
        // TODO: when ready for release, uncomment
//        if (!messagesConfigFile.exists()) {
//            messagesConfigFile.getParentFile().mkdirs();
//            saveResource("messages.yml", false);
//        }

        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        messages = new MessageLoader(messagesConfig);
    }

    @Override
    public void onDisable() {
//        getLogger().info("onDisable called!");
    }

    public static SuperAuctionHouse getInstance() {
        return instance;
    }

    public static MessageLoader getMessages() {
        return messages;
    }

    public static AuctionManager getAuctionManager() { return auctionManager; }

    public Economy getEconomy() { return econ; }
}
