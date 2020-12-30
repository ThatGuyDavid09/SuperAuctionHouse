package thatguydavid09.superauctionhouse;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.UUID;

public class AuctionItem implements Serializable {
    private final String playerName;
    private final boolean infsell;
    private final boolean auction;
    private final long time;
    private final long id;
    private final long price;
    private final String name;
    private final ItemStack item;
    private final UUID playerId;

    public AuctionItem(ItemStack item, long id, long price, UUID playerId, long time, boolean infsell, String playerName) {
        this.item = item;
        this.id = id;
        this.price = price;
        if (item.getItemMeta().hasDisplayName()) {
            name = item.getItemMeta().getDisplayName();
        } else {
            name = item.getType().toString();
        }
        this.playerId = playerId;

        this.time = time < 0 ? -1 : time;
        this.infsell = infsell;
        this.auction = time > 0;

        this.playerName = playerName;
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

    public UUID getPlayerId() {
        return playerId;
    }

    public String getPlayerName() { return playerName; }

    public boolean isInfsell() { return infsell; }

    public boolean isAuction() { return auction; }

    public long getTime() { return time; }
}
