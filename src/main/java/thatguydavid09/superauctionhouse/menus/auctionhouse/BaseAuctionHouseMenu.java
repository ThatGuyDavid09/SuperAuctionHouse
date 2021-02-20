package thatguydavid09.superauctionhouse.menus.auctionhouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import thatguydavid09.superauctionhouse.AuctionItem;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static thatguydavid09.superauctionhouse.SuperAuctionHouse.getEconomy;
import static thatguydavid09.superauctionhouse.SuperAuctionHouse.placeholder;

public class BaseAuctionHouseMenu {
    private static final SuperAuctionHouse plugin = SuperAuctionHouse.getInstance();
    // List of all items
    private final static ArrayList<AuctionItem> allItems = new ArrayList<>(); // This needs to be backed up
    // Item to something
    private static final HashMap<UUID, List<AuctionItem>> itemsForPlayer = new HashMap<>(); // This needs to be created from backup
    private static final HashMap<ItemStack, AuctionItem> itemStackToAuctionItem = new HashMap<>(); // This needs to be created from backup
    // Other necessary stuff
    public static List<Player> playersFindingStuff = new ArrayList<>();
    // List to update every second
    public static List<Player> playersWithAHOpen = new ArrayList<>();

    private static Inventory baseAuctionHouse;
    private static long auctionId = 0; // This needs to be created from backup
    private static ItemStack sortItem;

    /**
     * This creates all items for the auction house and creates the menu
     */
    public static void createAuctionHouse() {
        // Sets base auction house inventory
        // Make auction house inventory
        baseAuctionHouse = Bukkit.getServer().createInventory(null, 54, SuperAuctionHouse.getInstance().getConfig().getString("auctionhouse.names.auctionhouse"));

        // Set placeholder items
        baseAuctionHouse.setItem(47, placeholder);
        baseAuctionHouse.setItem(51, placeholder);

        // Set view auctions diamond
        ItemStack viewAuctions = new ItemStack(Material.DIAMOND, 1);
        ItemMeta itemMeta = viewAuctions.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "View your auctions");
        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "You currently have " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "outstanding ",
                ChatColor.BLUE + "auctions and " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "auctions ready to claim."));
        viewAuctions.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(45, viewAuctions);

        // Set view bids item
        ItemStack viewBids = new ItemStack(Material.GOLD_INGOT, 1);
        itemMeta = viewBids.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "View your bids");
        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "You currently have " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "outstanding ",
                ChatColor.BLUE + "bids and " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "bids ready to claim."));
        viewBids.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(46, viewBids);

        // Set the sort item
        sortItem = new ItemStack(Material.SUNFLOWER, 1);
        itemMeta = sortItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Sort by:");
        itemMeta.setLore(Collections.singletonList(ChatColor.BLUE + "Alphabetically, A-Z"));
        sortItem.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(49, sortItem);

        // Set the arrows
        ItemStack goBackArrow = new ItemStack(Material.ARROW, 1);
        ItemStack goForwardArrow = new ItemStack(Material.ARROW, 1);

        itemMeta = goBackArrow.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Previous page " + ChatColor.GRAY + "(Page 0/0)");
        goBackArrow.setItemMeta(itemMeta);

        itemMeta = goForwardArrow.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Next page " + ChatColor.GRAY + "(Page 0/0)");
        goForwardArrow.setItemMeta(itemMeta);

        // These are set later
        baseAuctionHouse.setItem(48, placeholder); // For back arrow
        baseAuctionHouse.setItem(50, placeholder); // For forward arrow

        // Set the search sign
        // Items
        ItemStack findSign = new ItemStack(Material.OAK_SIGN, 1);
        itemMeta = findSign.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Find an item");
        findSign.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(52, findSign);

        // Set the how to sell book
        ItemStack howToSell = new ItemStack(Material.BOOK, 1);
        itemMeta = howToSell.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "How do I sell an item?");
        itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Simply type " + ChatColor.AQUA + "/ah sell <price>",
                ChatColor.GREEN + "while holding the item you",
                ChatColor.GREEN + "want to sell!"));
        howToSell.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(53, howToSell);
    }

    /**
     * This adds an item to the auction house
     *
     * @param item          The <a href="#{@link}"{@link ItemStack}> to be added
     * @param sellingPlayer The <a href="#{@link}"{@link Player}> selling the item
     * @param price         The price of the item
     * @param time          The time the auction should last, -1 if it is not an auction
     * @param infsell       Whether the item should be removed from the auction house upon being bought
     */
    public static void addItem(ItemStack item, Player sellingPlayer, long price, long time, boolean infsell) {
        AuctionItem auctionItem = new AuctionItem(item.clone(), auctionId, price, sellingPlayer.getUniqueId(), time, infsell, sellingPlayer.getDisplayName());

        updateDictionaries(PlayerAuctionHouse.addLore(auctionItem));

        auctionId++;

        backUp(auctionItem, true);
    }

    /**
     * Adds an item to the auction house with a custom selling player name
     *
     * @param item          The <a href="#{@link}"{@link ItemStack}> to be added
     * @param sellingPlayer The <a href="#{@link}"{@link Player}> selling the item
     * @param price         The price of the item
     * @param playerName    The custom name of the player
     * @param time          The time the auction should last, -1 if it is not an auction
     * @param infsell       Whether the item should be removed from the auction house upon being bought
     */
    public static void addItem(ItemStack item, Player sellingPlayer, long price, String playerName, long time, boolean infsell) {
        AuctionItem auctionItem = new AuctionItem(item.clone(), auctionId, price, sellingPlayer.getUniqueId(), time, infsell, playerName);

        updateDictionaries(PlayerAuctionHouse.addLore(auctionItem));

        auctionId++;

        backUp(auctionItem, true);
    }

    /**
     * This adds an item to the auction house, but from its AuctionItem
     *
     * @param item   The <a href="#{@link}"{@link AuctionItem}> to add
     * @param backup Whether to back up the item
     */
    private static void addItem(AuctionItem item, boolean backup) {
        AuctionItem auctionItem = new AuctionItem(item.getItem(), item.getId(), item.getPrice(), item.getPlayerId(), item.getTime(), item.isInfsell(), item.getPlayerName());

        updateDictionaries(auctionItem);

        auctionId++;

        if (backup) {
            backUp(item, true);
        }
    }

    /**
     * This removes an item from the auction house
     *
     * @param auctionItem The <a href="#{@link}"{@link AuctionItem}> to remove
     */
    public static void removeItem(AuctionItem auctionItem) {
        unUpdateDictionaries(auctionItem);
        allItems.remove(auctionItem);

        backUp(auctionItem, false);

        if (auctionItem.isInfsell()) {
            AuctionItem item = new AuctionItem(auctionItem.getItem(), auctionItem.getId(), auctionItem.getPrice(), auctionItem.getPlayerId(), auctionItem.getTime(), auctionItem.isInfsell(), auctionItem.getPlayerName());
            addItem(item, true);
        }
    }

    /**
     * This removes an <a href="#{@link}"{@link ItemStack}> from the auction house
     *
     * @param item The <a href="#{@link}"{@link ItemStack}> to remove
     */
    public static void removeItem(ItemStack item) {
        allItems.removeIf(auctionItem -> auctionItem.getItem() == item);
    }

    /**
     * This updates various dictionaries about the current items
     *
     * @param item The <a href="#{@link}"{@link AuctionItem}> to update dictionaries with
     */
    private static void updateDictionaries(AuctionItem item) {
        // Update dictionaries
        if (!itemsForPlayer.containsKey(item.getPlayerId())) {
            itemsForPlayer.put(item.getPlayerId(), new ArrayList<>());
        }
        itemsForPlayer.get(item.getPlayerId()).add(item);

        allItems.add(item);
        itemStackToAuctionItem.put(item.getItem(), item);
    }

    /**
     * This removes an AuctionItem from various dictionaries
     *
     * @param item The AuctionItem to remove
     */
    private static void unUpdateDictionaries(AuctionItem item) {
        itemsForPlayer.get(item.getPlayerId()).remove(item);
        itemStackToAuctionItem.remove(item.getItem());
    }

    /**
     * This gives an <a href="#{@link}"{@link ItemStack}> to the Player
     *
     * @param item   The <a href="#{@link}"{@link ItemStack}> to give
     * @param player The <a href="#{@link}"{@link Player}> to give it to
     */
    public static void giveItemToPlayer(ItemStack item, Player player) {
        ItemMeta meta = item.getItemMeta();
        item.setItemMeta(meta);

        List<ItemStack> itemsToDrop = new ArrayList<>(player.getInventory().addItem(item).values());

        for (ItemStack itemStack : itemsToDrop) {
            player.getWorld().dropItem(player.getLocation().add(0, 1, 0), itemStack);
        }
    }

    /**
     * This removes all items from the auction house
     */
    public static void clearAuctionHouse() {
        allItems.clear();
        resetAuctionId();

        Connection connection = null;
        Statement statement;
        try {
            SuperAuctionHouse.openConnection();
            connection = SuperAuctionHouse.getConnection();
            statement = connection.createStatement();

            statement.execute("TRUNCATE TABLE auctionhouse");
        } catch (SQLException e) {
            SuperAuctionHouse.getInstance().getLogger().warning("Something has gone wrong with the database, see error log below");
            e.printStackTrace();
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    SuperAuctionHouse.getInstance().getLogger().warning("Something has gone wrong while closing the connection, see error log below");
                    e.printStackTrace();
                }
            }
        }
    }

    // The following deals with money

    /**
     * This gives a <a href="#{@link}"{@link Player}> money
     *
     * @param player The <a href="#{@link}"{@link Player}> to give money to
     * @param amount How much money to give
     */
    public static void addMoney(Player player, Long amount) {
        getEconomy().depositPlayer(player, amount);
    }

    /**
     * This removes money from a <a href="#{@link}"{@link Player}>
     *
     * @param player The <a href="#{@link}"{@link Player}> to remove money from
     * @param amount How much money to remove
     */
    public static void removeMoney(Player player, Long amount) {
        getEconomy().withdrawPlayer(player, amount);
    }

    /**
     * This returns how much money a <a href="#{@link}"{@link Player}> has
     *
     * @param player The <a href="#{@link}"{@link Player}> in question
     * @return The amount of money the player has
     */
    public static long getMoney(Player player) {
        return (long) getEconomy().getBalance(player);
    }

    /**
     * This checks if a <a href="#{@link}"{@link Player}> has above a certain amount of money
     *
     * @param player The <a href="#{@link}"{@link Player}> in question
     * @param amount The amount of money to check for
     * @return Whether the player has the amount of money specified
     */
    public static boolean hasMoney(Player player, long amount) {
        return getEconomy().has(player, amount);
    }

    /**
     * This returns the total number of items
     *
     * @return The number of items
     */
    public static long getNumOfItems() {
        return allItems.size();
    }

    /**
     * Returns a list of all the items currently in the auction house
     * Returns a list of all the items currently in the auction house
     *
     * @return A list of all the items in the auction house
     */
    public static ArrayList<AuctionItem> getAllItems() {
        return allItems;
    }

    /**
     * Converts a given <a href="#{@link}"{@link ItemStack}> to its corresponding <a href="#{@link}"{@link AuctionItem}>
     *
     * @param item The <a href="#{@link}"{@link ItemStack}> in question
     * @return Its corresponding <a href="#{@link}"{@link AuctionItem}>
     */
    public static AuctionItem itemStackToAuctionItem(ItemStack item) {
        return itemStackToAuctionItem.get(item);
    }

    /**
     * Returns an <a href="#{@link}"{@link Inventory}> of the auction house template
     *
     * @return The auction house template
     */
    public static Inventory getBaseAuctionHouse() {
        return baseAuctionHouse;
    }

    /**
     * Returns the <a href="#{@link}"{@link ItemStack}> corresponding to the sort item
     *
     * @return The <a href="#{@link}"{@link ItemStack}> corresponding to the sort item
     */
    public static ItemStack getSortItem() {
        return sortItem;
    }

    /**
     * This resets the auction id to 0
     */
    public static void resetAuctionId() {
        auctionId = 0;
    }

    /**
     * This backs up an item, either adding or removing it from the database
     *
     * @param item        The <a href="#{@link}"{@link AuctionItem}> to be backed up
     * @param addOrRemove Whether to add or to remove. True if add, false if remove
     */
    public static void backUp(AuctionItem item, boolean addOrRemove) {
        // addOrRemove should be true if an item is being added, false if it is being removed
        Connection connection = null;
        Statement statement;
        try {
            SuperAuctionHouse.openConnection();
            connection = SuperAuctionHouse.getConnection();
            statement = connection.createStatement();

            // Add item
            if (addOrRemove) {
                statement.executeUpdate("INSERT IGNORE INTO `auctionhouse`" +
                        "SET `auctionitem` = '" + encodeAuctionItem(item) + "'," +
                        "`auctionid` = " + item.getId() + ";");
            } else {
                statement.executeUpdate("DELETE FROM `auctionhouse`" +
                        "WHERE `auctionid` = + " + item.getId() + ";");
            }

        } catch (SQLException e) {
            plugin.getLogger().warning("Something has gone wrong with the database, see error log below");
            e.printStackTrace();
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    plugin.getLogger().warning("Something has gone wrong while closing the connection, see error log below");
                    e.printStackTrace();
                }
            }
        }

        plugin.getLogger().info("Auction house has been backed up!");
    }

    /**
     * This creates the auction house from a backup
     */
    public static void loadFromBackup() {
        createAuctionHouse();

        Connection connection = null;
        Statement statement;
        try {
            SuperAuctionHouse.openConnection();
            connection = SuperAuctionHouse.getConnection();
            statement = connection.createStatement();

            // Get auctionid
            statement.execute("USE " + SuperAuctionHouse.getDatabase() + ";");
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM auctionhouse;");
            rs.next();
            if (rs.getInt("COUNT(*)") != 0) {
                rs = statement.executeQuery("SELECT MAX(auctionid) FROM auctionhouse;");
                rs.next();
                auctionId = rs.getInt("MAX(auctionid)");
            } else {
                auctionId = 0;
            }
            // Load items
            rs = statement.executeQuery("SELECT auctionitem FROM auctionhouse;");
            while (rs.next()) {
                AuctionItem item = decodeAuctionItem(rs.getString("auctionitem"));
                addItem(item, false);
            }
            plugin.getLogger().info("Auctionhouse has been loaded from database!");

        } catch (SQLException e) {
            plugin.getLogger().warning("Something has gone wrong with the database, see error log below");
            e.printStackTrace();
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    plugin.getLogger().warning("Something has gone wrong while closing the connection, see error log below");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This encodes an <a href="#{@link}"{@link AuctionItem}> to a Base64 string
     *
     * @param item The <a href="#{@link}"{@link AuctionItem}> to be encoded
     * @return The Base64 string representing the item
     */
    public static String encodeAuctionItem(AuctionItem item) {
        ItemStack itemStack = item.getItem();
        ItemMeta meta = itemStack.getItemMeta();

        // Create NBT keys
        NamespacedKey id = new NamespacedKey(plugin, "id");
        NamespacedKey price = new NamespacedKey(plugin, "price");
        NamespacedKey playerId = new NamespacedKey(plugin, "playerId");
        NamespacedKey time = new NamespacedKey(plugin, "time");
        NamespacedKey infsell = new NamespacedKey(plugin, "infsell");
        NamespacedKey playerName = new NamespacedKey(plugin, "playerName");

        // Set NBT
        meta.getPersistentDataContainer().set(id, PersistentDataType.LONG, item.getId());
        meta.getPersistentDataContainer().set(price, PersistentDataType.LONG, item.getPrice());
        meta.getPersistentDataContainer().set(playerId, PersistentDataType.STRING, item.getPlayerId().toString());
        meta.getPersistentDataContainer().set(time, PersistentDataType.LONG, item.getTime());
        meta.getPersistentDataContainer().set(infsell, PersistentDataType.STRING, item.isInfsell() ? "true" : "false");
        meta.getPersistentDataContainer().set(playerName, PersistentDataType.STRING, item.getPlayerName());

        itemStack.setItemMeta(meta);
        // TODO Serialize itemstack
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos);
            boos.writeObject(itemStack);
            boos.flush();

            byte[] serialized = baos.toByteArray();
            String encoded = Base64.getEncoder().encodeToString(serialized);
            boos.close();
            return encoded;

        } catch (IOException e) {
            plugin.getLogger().severe(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return null;
        }
    }


    /**
     * This converts a base64 string to an <a href="#{@link}"{@link AuctionItem}>
     *
     * @param object The encoded item
     * @return The <a href="#{@link}"{@link AuctionItem}> the Base64 string represents
     */
    public static AuctionItem decodeAuctionItem(String object) {
        // TODO deserialize itemstack, get nbt, remove nbt, return AuctionItem
        try {
            byte[] deserialized = Base64.getDecoder().decode(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(deserialized);
            BukkitObjectInputStream bois = new BukkitObjectInputStream(bais);

            ItemStack item = (ItemStack) bois.readObject();
            ItemMeta meta = item.getItemMeta();
            bois.close();

            // Set NBT keys
            NamespacedKey id = new NamespacedKey(plugin, "id");
            NamespacedKey price = new NamespacedKey(plugin, "price");
            NamespacedKey playerId = new NamespacedKey(plugin, "playerId");
            NamespacedKey time = new NamespacedKey(plugin, "time");
            NamespacedKey infsell = new NamespacedKey(plugin, "infsell");
            NamespacedKey playerName = new NamespacedKey(plugin, "playerName");

            // Remove NBT from item
            ItemMeta metaCopy = meta.clone();
            PersistentDataContainer container = metaCopy.getPersistentDataContainer();
            container.remove(id);
            container.remove(price);
            container.remove(playerId);
            container.remove(time);
            container.remove(infsell);
            container.remove(playerName);

            item.setItemMeta(metaCopy);

            // Create AuctionItem
            PersistentDataContainer nbt = meta.getPersistentDataContainer();
            return new AuctionItem(item, nbt.get(id, PersistentDataType.LONG), nbt.get(price, PersistentDataType.LONG),
                    UUID.fromString(nbt.get(playerId, PersistentDataType.STRING)), nbt.get(time, PersistentDataType.LONG), nbt.get(playerName, PersistentDataType.STRING).equals("true"),
                    nbt.get(playerName, PersistentDataType.STRING));

        } catch (IOException | ClassNotFoundException e) {
            plugin.getLogger().severe(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

}