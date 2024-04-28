package com.highmarsorbit.superauctionhouse;

import com.highmarsorbit.superauctionhouse.inventories.SellItemMenu;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

public class Globals {
    public static HashMap<Player, SellItemMenu> waitingForClick = new HashMap<>();
    public static String feeEquation;
}
