package thatguydavid09.superauctionhouse;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import thatguydavid09.superauctionhouse.commands.AHCommandTabCompleter;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseChat;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseRegister;
import thatguydavid09.superauctionhouse.events.auctionhouse.RightClickOpenAH;
import thatguydavid09.superauctionhouse.events.bid.CustomBidChatEvent;
import thatguydavid09.superauctionhouse.events.generic.PlayerFreeze;
import thatguydavid09.superauctionhouse.events.generic.PreventItemRemoval;
import thatguydavid09.superauctionhouse.events.sell.SellNameChatEvent;
import thatguydavid09.superauctionhouse.events.sell.SellPriceChatEvent;
import thatguydavid09.superauctionhouse.events.sell.SellTimeChatEvent;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;
import thatguydavid09.superauctionhouse.runnables.AuctionHouseBackup;
import thatguydavid09.superauctionhouse.runnables.AuctionItemDecrementer;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SuperAuctionHouse extends JavaPlugin {
    // For vault
    private static final Logger log = Logger.getLogger("Minecraft");
    public static ItemStack empty;
    public static boolean areAuctions;
    public static boolean areInstaBuys;
    // Database stuff
    private static String host, port, database, username, password;
    private static SuperAuctionHouse instance;
    private static Economy econ = null;
    // Config
    private static FileConfiguration config;
    private static File openblocksFile;
    private static FileConfiguration openblocks;
    private static Connection connection;
    private static String chatPrefix;

    /**
     * This returns an instance of the plugin
     *
     * @return An instance of the plugin
     */
    public static SuperAuctionHouse getInstance() {
        return instance;
    }

    /**
     * This returns an instance of the economy
     *
     * @return An instance of the economy
     */
    public static Economy getEconomy() {
        return econ;
    }

    // Database stuff

    /**
     * This opens a connection to the database
     */
    public static void openConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://"
                            + host + ":" + port + "/" + database,
                    username, password);
            if (connection != null && !connection.isClosed()) {
                connection.createStatement().executeQuery("USE " + SuperAuctionHouse.database + ";");
            }
        } catch (SQLException | ClassNotFoundException e) {
            getInstance().getLogger().warning("Something went wrong while connecting to the database, are your credentials correct? View a detailed log below");
            e.printStackTrace();
        }
    }

    /**
     * This gets an available connection to the database
     *
     * @return A <a href="#{@link}"{@link Connection}> to the database
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * This returns the openblocks config
     *
     * @return The open blocks config
     */
    public static FileConfiguration getOpenBlocksConfig() {
        return openblocks;
    }

    /**
     * This returns the openblocks config file
     *
     * @return The open blocks config file
     */
    public static File getOpenblocksConfigFile() {
        return openblocksFile;
    }

    /**
     * This gets the host of the database
     *
     * @return The host of the database
     */
    public static String getHost() {
        return host;
    }

    /**
     * This gets the port of the database
     *
     * @return The port of the database
     */
    public static String getPort() {
        return port;
    }

    /**
     * This gets the name of the database
     *
     * @return The name of the database
     */
    public static String getDatabase() {
        return database;
    }

    /**
     * This returns the chat prefix for this plugin
     *
     * @return The chat prefix
     */
    public static String getPrefix() {
        return chatPrefix;
    }

    // Vault stuff

    /**
     * This is the method called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        getLogger().setLevel(Level.FINEST);
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
        empty = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = empty.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLore(Collections.emptyList());
        empty.setItemMeta(itemMeta);

        // Register command
        this.getCommand("superauctionhouse").setExecutor(new AuctionHouseCommand());

        // Register tabcompleter
        getCommand("superauctionhouse").setTabCompleter(new AHCommandTabCompleter());

        // Register events
        getServer().getPluginManager().registerEvents(new PreventItemRemoval(), this);
        getServer().getPluginManager().registerEvents(new AuctionHouseChat(), this);
        getServer().getPluginManager().registerEvents(new SellPriceChatEvent(), this);
        getServer().getPluginManager().registerEvents(new SellNameChatEvent(), this);
        getServer().getPluginManager().registerEvents(new SellTimeChatEvent(), this);
        getServer().getPluginManager().registerEvents(new CustomBidChatEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerFreeze(), this);
        getServer().getPluginManager().registerEvents(new AuctionHouseRegister(), this);
        getServer().getPluginManager().registerEvents(new RightClickOpenAH(), this);

        // Load config
        config();
        openBlockConfig();
        areAuctions = config.getBoolean("auctionhouse.allowauction");
        areInstaBuys = config.getBoolean("auctionhouse.allowinsbuy");

        // Init database vars
        host = config.getString("database.host");
        port = config.getString("database.port");
        database = config.getString("database.database");
        username = config.getString("database.username");
        password = config.getString("database.password");

        // Init chat prefix
        chatPrefix = config.getString("settings.prefix");

        // Database
        setupDatabase();
        BaseAuctionHouse.loadFromBackup();

        // Schedule updates for auctions
        AuctionItemDecrementer auctionScheduler = new AuctionItemDecrementer();
        auctionScheduler.start();

        getLogger().info(ChatColor.GREEN + "SuperAuctionHouse has been enabled!");

        // Schedule backups
        AuctionHouseBackup.scheduleBackups();
    }
    // End vault stuff

    /**
     * This is the method called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        BaseAuctionHouse.backUp();

        // Vault stuff
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        // End vault stuff

    }

    /**
     * This loads the config
     */
    private void config() {
        if (!getDataFolder().exists()) {
            getLogger().info("Data folder not found, creating...");
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        getLogger().info("Loading config.yml...");
        config = getConfig();
    }

    /**
     * This loads the open blocks config
     */
    private void openBlockConfig() {
        openblocksFile = new File(getDataFolder(), "openblocksconfig.yml");
        if (!openblocksFile.exists()) {
            openblocksFile.getParentFile().mkdirs();
            saveResource("openblocksconfig.yml", false);
        }

        openblocks = new YamlConfiguration();
        try {
            openblocks.load(openblocksFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * This sets up the economy
     *
     * @return Whether the economy was set up correctly
     */
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

    /**
     * This sets up the database
     */
    public void setupDatabase() {
        try {
            openConnection();
            Statement statement = connection.createStatement();

            statement.executeQuery("USE " + database + ";");

            // Check tables existence
            // Check existence of ah table
            if (!statement.executeQuery("SHOW TABLES FROM `" + database + "` LIKE 'auctionhouse';").next()) {
                statement.executeUpdate("CREATE TABLE auctionhouse (" +
                        "auctionitem LONGTEXT NOT NULL," +
                        "auctionid INTEGER NOT NULL)" +
                        "ENGINE=InnoDB;");
            }
            getLogger().info("Database setup complete");
        } catch (SQLException e) {
            getLogger().warning("Something has gone wrong with the database, see error log below");
            e.printStackTrace();
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    getLogger().warning("Something has gone wrong while closing the connection, see error log belo");
                    e.printStackTrace();
                }
            }
        }
    }
}
