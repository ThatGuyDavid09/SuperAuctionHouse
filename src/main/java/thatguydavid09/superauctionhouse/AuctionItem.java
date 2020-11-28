package thatguydavid09.superauctionhouse;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class AuctionItem implements Serializable {
    private final long id;
    private final long price;
    private final String name;
    private final ItemStack item;
    private final Player player;

    public AuctionItem(ItemStack item, long id, long price, Player player) {
        this.item = item;
        this.id = id;
        this.price = price;
        if (item.getItemMeta().hasDisplayName()) {
            name = item.getItemMeta().getDisplayName();
        } else {
            name = item.getType().toString();
        }
        this.player = player;
    }

    public long getPrice() {
        return price;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public Player getPlayer() {
        return player;
    }
}
