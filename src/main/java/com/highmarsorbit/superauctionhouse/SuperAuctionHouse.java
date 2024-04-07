package com.highmarsorbit.superauctionhouse;

import com.highmarsorbit.superauctionhouse.commands.AHCommand;
import com.highmarsorbit.superauctionhouse.listeners.AuctionListListener;
import com.highmarsorbit.superauctionhouse.managers.AuctionManager;
import com.highmarsorbit.superauctionhouse.storage.DummyStorage;
import com.highmarsorbit.superauctionhouse.storage.Storage;
import com.highmarsorbit.superauctionhouse.util.MessageLoader;
import fr.cleymax.signgui.SignManager;
import net.milkbowl.vault.economy.Economy;
import net.minecraft.server.v1_16_R3.BlockPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutBlockChange;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class SuperAuctionHouse extends JavaPlugin {
    public final static String prefix = ChatColor.RESET + "[" + ChatColor.AQUA + "SuperAuctionHouse" + ChatColor.RESET + "] ";
    public final static String prefixNoColor = ChatColor.stripColor(prefix);
    private static MessageLoader messages;

    private static SuperAuctionHouse instance;
    private static AuctionManager auctionManager;
    private Economy econ;
    private Storage store;
    private SignManager signGuiManager;


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

        this.getLogger().info("Enabled SuperAuctionHouse");
    }

    private void registerScheduledTasks() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            auctionManager.refreshAvailableAuctions();
        }, 0L, 20L*60L); // Refresh available auctions every minute
    }

    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new AuctionListListener(), this);
        signGuiManager = new SignManager(this);
        signGuiManager.init();
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
        getLogger().info("Loading configs");
        // For testing only.
        // TODO: uncomment for release
        saveResource("messages.yml", true);
        // Commented out for testing reasons.
        // TODO: when ready for release, uncomment
//        if (!messagesConfigFile.exists()) {
//            messagesConfigFile.getParentFile().mkdirs();
//            saveResource("messages.yml", false);
//        }

        File messagesConfigFile = new File(getDataFolder(), "messages.yml");

        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        messages = new MessageLoader(messagesConfig);
        getLogger().info("Loaded messages.yml");
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

    public static Economy getEconomy() { return instance.econ; }

    public SignManager getSignGuiManager() {
        return signGuiManager;
    }

    public static void sendMessageByPath(Player player, String messagePath) {
        player.sendMessage(prefix + messages.getMessage(messagePath));
    }
}
