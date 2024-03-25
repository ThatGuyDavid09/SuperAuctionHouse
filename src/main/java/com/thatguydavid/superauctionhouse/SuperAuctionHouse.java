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
    public static String prefix = ChatColor.RESET + "[" + ChatColor.AQUA + "SuperAuctionHouse" + ChatColor.RESET + "] ";
    public static MessageLoader messages;


    @Override
    public void onEnable() {
        loadConfigs();
        registerCommands();
    }

    private void registerCommands() {
        this.getCommand("ah").setExecutor(new AHCommand());
    }

    private void loadConfigs() {
        File messagesConfigFile = new File(getDataFolder(), "messages.yml");
        if (!messagesConfigFile.exists()) {
            messagesConfigFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        YamlConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesConfigFile);
        messages = new MessageLoader(messagesConfig);
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable called!");
    }
}
