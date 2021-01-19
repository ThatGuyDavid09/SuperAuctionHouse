package thatguydavid09.superauctionhouse;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.UUID;

public class AuctionItem implements Serializable, Cloneable {
    private final String playerName;
    private final boolean infsell;
    private final boolean auction;
    private final long id;
    private final long price;
    private final String name;
    private final UUID playerId;
    private long time;
    private ItemStack item;

    /**
     * <a href="#{@link}"{@link AuctionItem}> constructor
     *
     * @param item       The <a href="#{@link}"{@link ItemStack}> the <a href="#{@link}"{@link AuctionItem}> represents
     * @param id         The auction id of the item
     * @param price      The price of the item
     * @param playerId   The <a href="#{@link}"{@link UUID}> of the item
     * @param time       The length of the item's auction in minutes (-1 if it isn't an auction)
     * @param infsell    Whether the item should be removed from the auction hosue upon being bought
     * @param playerName The name of the player selling (empty for displayname of actual player)
     */
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

        this.time = time < 0 ? -1 : time * 60;
        this.infsell = infsell;
        this.auction = time > 0;

        this.playerName = playerName;
    }

    /**
     * Gets the price of the item
     *
     * @return The price of the item
     */
    public long getPrice() {
        return price;
    }

    /**
     * Gets the id of the item
     *
     * @return The id of the item
     */
    public long getId() {
        return id;
    }

    /**
     * Gets the name of the item
     *
     * @return The name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the <a href="#{@link}"{@link ItemStack}> of the item
     *
     * @return The <a href="#{@link}"{@link ItemStack}> of the item
     */
    public ItemStack getItem() {
        return item;
    }

    /**
     * This sets the itemStack of this AuctionItem
     *
     * @param item The ItemStack to set
     */
    public void setItem(ItemStack item) {
        this.item = item;
    }

    /**
     * Gets the <a href="#{@link}"{@link UUID}> of player selling the item
     *
     * @return The <a href="#{@link}"{@link UUID}> of player selling the item
     */
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Gets the name of the player selling the item
     *
     * @return The name of the player selling the item
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets whether the item should be removed from the auction house upon being bought
     *
     * @return The name of the item
     */
    public boolean isInfsell() {
        return infsell;
    }

    /**
     * Gets whether the item is an auction
     *
     * @return Whether the item is an auction
     */
    public boolean isAuction() {
        return auction;
    }

    /**
     * Gets the length of the item's auction (-1 if it isn't an auction)
     *
     * @return The length of the item's auction (in seconds)
     */
    public long getTime() {
        return time;
    }

    /**
     * Decreases the time by one and returns the time
     *
     * @return The new value of time
     */
    public long decTime() {
        time--;
        return time;
    }

    /**
     * Returns a copy of the AuctionItem
     *
     * @return A copy of the AuctionItem
     */
    public AuctionItem clone() {
        try {
            return (AuctionItem) super.clone();
        } catch (CloneNotSupportedException ignored) {}
        return null;
    }
}
