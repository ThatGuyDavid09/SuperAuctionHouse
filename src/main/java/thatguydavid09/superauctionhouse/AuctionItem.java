package thatguydavid09.superauctionhouse;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuctionItem implements ConfigurationSerializable {
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
        this.auction = item.isAuction();

        this.playerName = item.getPlayerName();
    }

    public AuctionItem() {
        item = null;
        id = -1;
        price = -1;
        name = "";
        playerId = null;
        time = -100;
        infsell = false;
        auction = false;
        playerName = "";

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
     * Creates a Map representation of this class.
     *
     * @return Map containing the current state of this class
     */
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("item", item.serialize());
        map.put("id", id);
        map.put("price", price);
        map.put("playerId", playerId);
        map.put("time", time);
        map.put("infsell", infsell);
        map.put("playerName", playerName);

        return map;
    }

    /**
     * Returns the item represented by the map
     *
     * @return The object contained in this map
     */
    public AuctionItem deserialize(Map<String, Object> map) {
        return new AuctionItem(ItemStack.deserialize((Map<String, Object>) map.get("item")), (Long) map.get("id"), (Long) map.get("price"), (UUID) map.get("playerId"),
                (Long) map.get("time"), (Boolean) map.get("infsell"), (String) map.get("playerName"));
    }
}
