package com.highmarsorbit.superauctionhouse;

import com.highmarsorbit.superauctionhouse.commands.AHCommand;
import com.highmarsorbit.superauctionhouse.config.Config;
import com.highmarsorbit.superauctionhouse.config.Messages;
import com.highmarsorbit.superauctionhouse.listeners.AuctionListListener;
import com.highmarsorbit.superauctionhouse.listeners.InventoryClickListener;
import com.highmarsorbit.superauctionhouse.managers.AuctionManager;
import com.highmarsorbit.superauctionhouse.storage.DummyStorage;
import com.highmarsorbit.superauctionhouse.storage.SQLiteStorage;
import com.highmarsorbit.superauctionhouse.storage.Storage;
import com.highmarsorbit.superauctionhouse.config.MessageLoader;
import com.mojang.datafixers.util.Pair;
import fr.cleymax.signgui.SignManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.objecthunter.exp4j.ExpressionBuilder;
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
import java.util.logging.Logger;

public final class SuperAuctionHouse extends JavaPlugin {
    public final static String prefix = ChatColor.RESET + "[" + ChatColor.AQUA + "SuperAuctionHouse" + ChatColor.RESET + "] ";
    public final static String prefixNoColor = ChatColor.stripColor(prefix);
    private static MessageLoader messages;

    private static SuperAuctionHouse instance;
    private static AuctionManager auctionManager;
    private Economy econ;
    private SignManager signGuiManager;
    private Permission perms;

    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();
        validateConfigs();

        if (Config.log_fine) {
            getLogger().setLevel(Level.ALL);
            Bukkit.getLogger().setLevel(Level.ALL);
        }
//        getLogger().getHandlers()[0].setLevel(Level.ALL);

        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!setupPermissions()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Loaded Vault hooks");

        registerCommands();
        registerEventListeners();

        Pair<Boolean, Storage> storeResult = createStorage();
        if (!storeResult.getFirst()) {
            getLogger().severe("Disabled due to auction database self test failed! Check your database configuration.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Loaded database");

        boolean auctionManagerResult = createAuctionManager(storeResult.getSecond());
        if (!auctionManagerResult) {
            getLogger().severe("Disabled due to auction manager init fail! Check your database.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("Loaded auctions");

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

    private Pair<Boolean, Storage> createStorage() {
//        Storage store = new DummyStorage();
        Storage store = new SQLiteStorage();
        boolean testResult = store.selfTest();

        return new Pair<>(testResult, store);
    }

    private boolean createAuctionManager(Storage store) {
        auctionManager = new AuctionManager(store);
        boolean success = auctionManager.init();
        return success;
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


    private boolean setupPermissions() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perms = rsp.getProvider();
        return perms != null;
    }


    private void loadConfigs() {
        getLogger().info("Loading configs");

        ConfigManager.create(this).target(Config.class).saveDefaults().load();
        getLogger().info("Loaded config.yml");


        ConfigManager.create(this, "messages.yml").target(Messages.class).saveDefaults().load();
        File messagesConfigFile = new File(getDataFolder(), "messages.yml");
        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        messages = new MessageLoader(messagesConfig);
        getLogger().info("Loaded messages.yml");
    }


    private void validateConfigs() {
        getLogger().info("Validating configs");

        // Run fee equation through a sample evaluation to make sure there are no errors. If there are, output warning
        // to console and replace with default.
        String eqn = Config.fee_equation;

        try {
            new ExpressionBuilder(eqn)
                    .variables("d", "h", "m", "s", "p")
                    .build()
                    .setVariable("d", 1)
                    .setVariable("h", 1)
                    .setVariable("m", 1)
                    .setVariable("s", 1)
                    .setVariable("p", 1)
                    .evaluate();

            Globals.feeEquation = eqn;
        } catch (Exception e) {
            String defaultEqn = "p * 0.06 + h";
            getLogger().warning(String.format("Fee equation is invalid! Using default value %s", defaultEqn));
            Globals.feeEquation = defaultEqn;
        }

        getLogger().info("Validated configs");
    }

    @Override
    public void onDisable() {
        getLogger().info("Closing auction storage");
        boolean success = auctionManager.disable();
        if (!success) {
            getLogger().severe("Error while closing storage! Some information may be lost or corrupted.");
        } else {
            getLogger().info("Auction storage closed");
        }
        getLogger().info("SuperAuctionHouse disabled");
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

    public static Permission getPermissions() { return instance.perms; }

    public static Logger getLogging() { return instance.getLogger(); }

    public static FileConfiguration getConfiguration() { return instance.getConfig(); }

    public SignManager getSignGuiManager() {
        return signGuiManager;
    }

    public static void sendMessageByPath(Player player, String messagePath) {
        player.sendMessage(prefix + messages.getMessage(messagePath));
    }
}
