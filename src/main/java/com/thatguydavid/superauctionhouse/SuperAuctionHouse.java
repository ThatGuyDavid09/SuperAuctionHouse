package com.thatguydavid.superauctionhouse;

import com.thatguydavid.superauctionhouse.commands.AHCommand;
import com.thatguydavid.superauctionhouse.util.MessageLoader;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class SuperAuctionHouse extends JavaPlugin {
    public final static String prefix = ChatColor.RESET + "[" + ChatColor.AQUA + "SuperAuctionHouse" + ChatColor.RESET + "] ";
    private static MessageLoader messages;

    private static JavaPlugin instance;


    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();
        registerCommands();
    }

    private void registerCommands() {
        this.getCommand("ah").setExecutor(new AHCommand());
    }

    public static JavaPlugin getInstance() {
        return instance;
    }

    public static MessageLoader getMessages() {
        return messages;
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
        getLogger().info("onDisable called!");
    }
}
