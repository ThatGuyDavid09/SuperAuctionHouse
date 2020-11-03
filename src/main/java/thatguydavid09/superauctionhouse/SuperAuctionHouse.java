package thatguydavid09.superauctionhouse;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;

public final class SuperAuctionHouse extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getCommand("superauctionhouse").setExecutor(new AuctionHouseCommand());

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "SuperAuctionHouse has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
