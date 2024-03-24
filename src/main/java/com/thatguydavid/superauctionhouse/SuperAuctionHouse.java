package com.thatguydavid.superauctionhouse;

import org.bukkit.plugin.java.JavaPlugin;

public final class SuperAuctionHouse extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("onEnable called!");
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable called!");
    }
}
