package com.highmarsorbit.superauctionhouse;

import com.highmarsorbit.superauctionhouse.commands.AHCommand;
import com.highmarsorbit.superauctionhouse.config.Config;
import com.highmarsorbit.superauctionhouse.config.Messages;
import com.highmarsorbit.superauctionhouse.listeners.AuctionListListener;
import com.highmarsorbit.superauctionhouse.listeners.InventoryClickListener;
import com.highmarsorbit.superauctionhouse.managers.AuctionManager;
import com.highmarsorbit.superauctionhouse.storage.DummyStorage;
import com.highmarsorbit.superauctionhouse.storage.Storage;
import com.highmarsorbit.superauctionhouse.config.MessageLoader;
import fr.cleymax.signgui.SignManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.config.ConfigManager;

import java.io.File;
import java.util.logging.Level;

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

        if (getConfig().getBoolean("settings.log-fine")) {
            getLogger().setLevel(Level.ALL);
            Bukkit.getLogger().setLevel(Level.ALL);
        }
//        getLogger().getHandlers()[0].setLevel(Level.ALL);

        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerCommands();
        registerEventListeners();
        boolean storageTestResult = createAuctionManager();
        if (!storageTestResult) {
            getLogger().severe("Disabled due to auction database self test failed! Check your database configuration.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

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
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);

        signGuiManager = new SignManager(this);
        signGuiManager.init();

        try {
            // This is a kind of hacky fix to the problem that SignGUI registers handlers on player log in and
            // sometimes players may be online when the plugin initializes (like if the plugin is relaoded).
            // We call the onPlayerQuit and then onPlayerJoin methods with a every online player to ensure that handlers
            // are properly created.
            for (Player player : Bukkit.getOnlinePlayers()) {
                signGuiManager.onPlayerQuit(new PlayerQuitEvent(player, ""));
                signGuiManager.onPlayerJoin(new PlayerJoinEvent(player, ""));
//                ChannelPipeline pipeline = ((CraftPlayer)player).getHandle().playerConnection.networkManager.channel.pipeline();
//                if (pipeline.get(player.getName()) == null) {
//                    signGuiManager.onPlayerJoin(new PlayerJoinEvent(player, ""));
//                }
            }
        } catch (Exception e) {
            getLogger().warning("Failed to totally initialize Sign GUI. Auction house may not work properly for " +
                    "currently online players until they relog.");
//            e.printStackTrace();
        }
    }

    private boolean createAuctionManager() {
        store = new DummyStorage();
        boolean testResult = store.selfTest();

        if (!testResult) return false;

        auctionManager = new AuctionManager(store);
        return true;
    }

    private void registerCommands() {
//        this.getCommand("ah").setExecutor(new AHCommand());
        new CommandParser(this.getResource("command.rdcml")).parse().register("superauctionhouse", new AHCommand());
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
        // TODO: remove for release

        ConfigManager.create(this).target(Config.class).saveDefaults().load();
        getLogger().info("Loaded config.yml");


        ConfigManager messagesMan = ConfigManager.create(this, "messages.yml").target(Messages.class).saveDefaults().load();
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

    public static FileConfiguration getConfiguration() { return instance.getConfig(); }

    public SignManager getSignGuiManager() {
        return signGuiManager;
    }

    public static void sendMessageByPath(Player player, String messagePath) {
        player.sendMessage(prefix + messages.getMessage(messagePath));
    }
}
