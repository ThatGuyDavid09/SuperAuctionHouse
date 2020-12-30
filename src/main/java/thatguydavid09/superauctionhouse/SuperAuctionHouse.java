package thatguydavid09.superauctionhouse;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseChat;
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHouseRegister;
import thatguydavid09.superauctionhouse.events.generic.PlayerFreeze;
import thatguydavid09.superauctionhouse.events.generic.PreventItemRemoval;
import thatguydavid09.superauctionhouse.events.sell.SellNameChatEvent;
import thatguydavid09.superauctionhouse.events.sell.SellPriceChatEvent;
import thatguydavid09.superauctionhouse.events.sell.SellTimeChatEvent;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

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
    public static ItemStack placeholder;
    // Database stuff
    public static String host, port, database, username, password;
    private static SuperAuctionHouse instance;
    private static Economy econ = null;
    // End vault stuff
    // Config
    private static FileConfiguration config;
    private static Connection connection;

    public static SuperAuctionHouse getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

    // Database stuff
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

    public static Connection getConnection() {
        return connection;
    }

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
        placeholder = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
        ItemMeta itemMeta = placeholder.getItemMeta();
        itemMeta.setDisplayName(" ");
        itemMeta.setLore(Collections.emptyList());
        placeholder.setItemMeta(itemMeta);


        this.getCommand("superauctionhouse").setExecutor(new AuctionHouseCommand());

        // Register command
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SuperAuctionHouse has been enabled!");

        // Register events
        getServer().getPluginManager().registerEvents(new PreventItemRemoval(), this);
        getServer().getPluginManager().registerEvents(new AuctionHouseChat(), this);
        getServer().getPluginManager().registerEvents(new SellPriceChatEvent(), this);
        getServer().getPluginManager().registerEvents(new SellNameChatEvent(), this);
        getServer().getPluginManager().registerEvents(new SellTimeChatEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerFreeze(), this);
        getServer().getPluginManager().registerEvents(new AuctionHouseRegister(), this);

        // Load config
        config();

        // Init database vars
        host = config.getString("database.host");
        port = config.getString("database.port");
        database = config.getString("database.database");
        username = config.getString("database.username");
        password = config.getString("database.password");

        setupDatabase();
        BaseAuctionHouseMenu.loadFromBackup();
    }

    @Override
    public void onDisable() {
        // Vault stuff
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        // End vault stuff

    }

    private void config() {
        if (!getDataFolder().exists()) {
            getLogger().info("Data folder not found, creating...");
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        getLogger().info("Loading config.yml...");
        config = getConfig();
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

    public void setupDatabase() {
        try {
            openConnection();
            Statement statement = connection.createStatement();

            statement.executeQuery("USE " + database + ";");

            // Check tables existence
            // Check existence of ah table
            if (!statement.executeQuery("SHOW TABLES FROM `" + database + "` LIKE 'auctionhouse';").next()) {
                statement.executeUpdate("CREATE TABLE auctionhouse (" +
                        "auctionitem TEXT NOT NULL," +
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
