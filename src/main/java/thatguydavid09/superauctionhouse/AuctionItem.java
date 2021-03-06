package thatguydavid09.superauctionhouse;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionItem {
    private final String playerName;
    private final boolean infsell;
    private final boolean isAuction;
    private final long id;
    private long price;
    private final String name;
    private final UUID playerId;
    private long time;
    private ItemStack item;
    private UUID currentBidderId = null;
    private String currentBidderName = null;

    /**
     * <a href="#{@link}"{@link AuctionItem}> constructor
     *
     * @param item       The <a href="#{@link}"{@link ItemStack}> the <a href="#{@link}"{@link AuctionItem}> represents
     * @param id         The auction id of the item
     * @param price      The price of the item
     * @param playerId   The <a href="#{@link}"{@link UUID}> of the item
     * @param time       The length of the item's auction in seconds (-1 if it isn't an auction)
     * @param infsell    Whether the item should be removed from the auction hosue upon being bought
     * @param playerName The name of the player selling (empty for displayname of actual player)
     */
    public AuctionItem(ItemStack item, long id, long price, UUID playerId, long time, boolean infsell, boolean isAuction, String playerName) {
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
        this.isAuction = isAuction;

        this.playerName = playerName;
    }

    /**
     * This is a cloning constructor
     *
     * @param item The AuctionItem to be cloned
     */
    public AuctionItem(AuctionItem item) {
        this.item = item.getItem();
        this.id = item.getId();
        this.price = item.getPrice();
        this.name = item.getName();
        this.playerId = item.getPlayerId();

        this.time = item.getTime();
        this.infsell = item.isInfsell();
        this.isAuction = item.isAuction();

        this.playerName = item.getPlayerName();

        this.currentBidderName = item.getCurrentBidderName();
        this.currentBidderId = item.getCurrentBidderId();
    }

    /**
     * This sets all information regarding the current bidder of the item
     * @param player The player bidding on the item
     */
    public void setBidder(Player player) {
        currentBidderId = player.getUniqueId();
        currentBidderName = player.getDisplayName();
    }

    /**
     * This sets all information regarding the current bidder of the item
     * @param playerId The UUID of the player bidding on the item
     * @param playerName The name of the player bidding on the item
     */
    public void setBidder(UUID playerId, String playerName) {
        currentBidderId = playerId;
        currentBidderName = playerName;
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
        return isAuction;
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
        if (time > 0) {
            time--;
        }

        if (time == -1 && isAuction) {
            time = -2;
        }
        return time;
    }

    /**
     * This sets the price of the item to a certain value
     * @param price The price to be set to
     * @return The modified price of the item
     */
    public long setPrice(long price) {
        this.price += price;
        return this.price;
    }

    /**
     * This returns the UUID of the current bidder
     * @return The UUID of the current bidder
     */
    public UUID getCurrentBidderId() {
        return currentBidderId;
    }

    /**
     * This returns the name of the current bidder
     * @return The name of the current bidder
     */
    public String getCurrentBidderName() {
        return currentBidderName;
    }

    /**
     * Return whether the item is expired.
     * @return Whether the item is expired.
     */
    public boolean isExpired() {
        return isAuction && time <= 0;
    }
}
