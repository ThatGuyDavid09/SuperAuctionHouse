package thatguydavid09.superauctionhouse.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AHCommandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            List<String> commands = new ArrayList<>();

            if (player.hasPermission("superauctionhouse.sell")) {
                commands.add("sell");
            }

            if (player.hasPermission("superauctionhouse.setopenloc")) {
                commands.add("setopenloc");
            }

            if (player.hasPermission("superauctionhouse.unsetloc")) {
                commands.add("unsetloc");
            }

            if (player.hasPermission("superauctionhouse.clear")) {
                commands.add("clear");
            }

            if (player.hasPermission("superauctionhouse.help")) {
                commands.add("help");
            }

            if (player.hasPermission("superauctionhouse.ping")) {
                commands.add("ping");
            }

            if (player.hasPermission("superauctionhouse.backup")) {
                commands.add("backup");
            }

            Collections.sort(commands);

            if (player.hasPermission("superauctionhouse.viewplayerah")) {
                for (Player i : Bukkit.getOnlinePlayers()) {
                    commands.add(i.getDisplayName());
                }
            }

            return commands;
        }

        return Collections.emptyList();
    }
}
