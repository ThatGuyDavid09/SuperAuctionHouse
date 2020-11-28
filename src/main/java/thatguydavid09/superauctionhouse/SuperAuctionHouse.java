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
import thatguydavid09.superauctionhouse.events.auctionhouse.AuctionHousePlayerFreeze;
import thatguydavid09.superauctionhouse.events.generic.PreventItemRemoval;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.logging.Logger;

public final class SuperAuctionHouse extends JavaPlugin {
    // For vault
    private static final Logger log = Logger.getLogger("Minecraft");
    public static ItemStack placeholder;
    private static SuperAuctionHouse instance;
    private static Economy econ = null;
    // End vault stuff
    // Config
    private static FileConfiguration config;
    // Database stuff
    private static String host, port, database, username, password;
    private static Connection connection;

    public static SuperAuctionHouse getInstance() {
        return instance;
    }

    public static Economy getEconomy() {
        return econ;
    }

    // Database stuff
    public static Connection openConnection() throws SQLException,
            ClassNotFoundException {

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://"
                        + host + ":" + port + "/" + database,
                username, password);
        if (connection != null && !connection.isClosed()) {
            return connection;
        } else {
            return null;
        }
    }

    public static Connection getConnection() {
        return connection;
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

        // Load config
        config();

        // Init database
        host = config.getString("databases.host");
        port = config.getString("databases.port");
        database = config.getString("databases.auctionhouse.database");
        username = config.getString("databases.auctionhouse.user");
        password = config.getString("databases.auctionhouse.pass");

        try {
            openConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Vault stuff
        log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
        // End vault stuff

        // Database stuff
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    // End vault stuff

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
}
