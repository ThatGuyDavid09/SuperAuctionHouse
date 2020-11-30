package thatguydavid09.superauctionhouse.menus.auctionhouse;

import com.google.common.base.Strings;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.collections.ListUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
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
    public static List<Player> playersFindingStuff = new ArrayList<>();
    public static HashMap<Player, List<ItemStack>> stashes = new HashMap<>(); // This needs to be backed up
    private static Inventory baseAuctionHouse;
    // Items
    private static ItemStack findSign = null;
    private static ItemStack sortItem = null;
    private static ItemStack viewAuctions = null;
    private static ItemStack viewBids = null;
    private static ItemStack goBackArrow = null;
    private static ItemStack goForwardArrow = null;
    private static ItemStack howToSell = null;
    // Other necessary stuff
    private static SuperAuctionHouse plugin = SuperAuctionHouse.getInstance();
    public static final NamespacedKey auctionIdKey = new NamespacedKey(plugin, "id");
    private static long auctionId = 0; // This needs to be created from backup
    // Item to something
    private static BiMap<Player, List<AuctionItem>> itemsForPlayer = HashBiMap.create(); // This needs to be created from backup
    private static HashMap<ItemStack, AuctionItem> itemStackToAuctionItem = new HashMap<>(); // This needs to be created from backup
    // List of all items
    private static List<AuctionItem> allItems = new ArrayList<>(); // This needs to be backed up

    // TODO add way to back up ah
    public static void createAuctionHouse() {
        // Sets base auction house inventory
        // Make auction house inventory
        baseAuctionHouse = Bukkit.getServer().createInventory(null, 54, "Auction House");

        // Set placeholder items
        baseAuctionHouse.setItem(47, placeholder);
        baseAuctionHouse.setItem(51, placeholder);

        // Set view auctions diamond
        viewAuctions = new ItemStack(Material.DIAMOND, 1);
        ItemMeta itemMeta = viewAuctions.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "View your auctions");
        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "You currently have " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "outstanding ",
                ChatColor.BLUE + "auctions and " + ChatColor.YELLOW + "0 " + ChatColor.BLUE + "auctions ready to claim."));
        viewAuctions.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(45, viewAuctions);

        // Set view bids item
        viewBids = new ItemStack(Material.GOLD_INGOT, 1);
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
        itemMeta.setLore(Arrays.asList(ChatColor.BLUE + "Alphabetically, A-Z"));
        sortItem.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(49, sortItem);

        // Set the arrows
        goBackArrow = new ItemStack(Material.ARROW, 1);
        goForwardArrow = new ItemStack(Material.ARROW, 1);

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
        findSign = new ItemStack(Material.OAK_SIGN, 1);
        itemMeta = findSign.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "Find an item");
        findSign.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(52, findSign);

        // Set the how to sell book
        howToSell = new ItemStack(Material.BOOK, 1);
        itemMeta = howToSell.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GOLD + "How do I sell an item?");
        itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Simply type " + ChatColor.AQUA + "/ah sell <price>",
                ChatColor.GREEN + "while holding the item you",
                ChatColor.GREEN + "want to sell!"));
        howToSell.setItemMeta(itemMeta);

        baseAuctionHouse.setItem(53, howToSell);
    }

    public static void addItem(ItemStack item, Player sellingPlayer, long price) {
        ItemStack itemToAdd = item.clone();

        // Add correct lore
        ItemStack itemWithLore = addLore(itemToAdd, sellingPlayer, price);

        ItemMeta itemMeta = itemWithLore.getItemMeta();
        itemMeta.getPersistentDataContainer().set(auctionIdKey, PersistentDataType.LONG, auctionId);
        itemWithLore.setItemMeta(itemMeta);

        AuctionItem auctionItem = new AuctionItem(itemWithLore, auctionId, price, sellingPlayer);

        updateDictionaries(auctionItem, sellingPlayer);

        auctionId++;

        backUp();
    }

    public static void removeItem(AuctionItem item, Player seller) {
        unUpdateDictionaries(item, seller);
        allItems.remove(item);

        backUp();
    }

    private static void updateDictionaries(AuctionItem item, Player sellingPlayer) {
        // Update dictionaries
        if (!itemsForPlayer.containsKey(sellingPlayer)) {
            itemsForPlayer.put(sellingPlayer, new ArrayList<>());
        }
        itemsForPlayer.get(sellingPlayer).add(item);

        allItems.add(item);
        itemStackToAuctionItem.put(item.getItem(), item);
    }

    private static void unUpdateDictionaries(AuctionItem item, Player seller) {
        itemsForPlayer.get(seller).remove(item);
        itemStackToAuctionItem.remove(item.getItem());
    }

    public static void giveItemToPlayer(ItemStack item, Player player) {
        ItemStack itemToGive = removeLore(item);
        ItemMeta meta = itemToGive.getItemMeta();
        meta.getPersistentDataContainer().remove(auctionIdKey);
        itemToGive.setItemMeta(meta);

        List<ItemStack> itemsToAddToStash = new ArrayList<>(player.getInventory().addItem(itemToGive).values());
        if (itemsToAddToStash.size() != 0) {
            player.sendMessage(ChatColor.RED + "An item couldn't be added to your inventory, so it was put into your stash. Type /ah stash to get all items in your stash!");
            if (!stashes.containsKey(player)) {
                stashes.put(player, new ArrayList<>());
            }
            stashes.get(player).addAll(itemsToAddToStash);
        }
    }

    private static ItemStack addLore(ItemStack item, Player sellingPlayer, long price) {
        ItemStack itemToRet = item.clone();
        ItemMeta meta = itemToRet.getItemMeta();
        if (meta.getLore() != null) {
            meta.setLore(ListUtils.union(meta.getLore(), Arrays.asList("", ChatColor.GRAY + "+------------------+", "", ChatColor.GREEN + "Sold by " + ChatColor.GOLD + sellingPlayer.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + price)));
        } else {
            meta.setLore(Arrays.asList(ChatColor.GREEN + "Sold by " + ChatColor.GOLD + sellingPlayer.getDisplayName() + ChatColor.GREEN + " for " + ChatColor.GOLD + price));
        }
        itemToRet.setItemMeta(meta);
        return itemToRet;
    }

    private static ItemStack removeLore(ItemStack item) {
        ItemStack itemToRet = item.clone();
        ItemMeta meta = itemToRet.getItemMeta();
        List<String> lore = meta.getLore();
        lore.remove(lore.size() - 1);

        if (lore.size() > 0) {
            for (int i = 0; i <= 2; i++) {
                lore.remove(lore.size() - 1);
            }
        }

        meta.setLore(lore);
        itemToRet.setItemMeta(meta);

        return itemToRet;
    }


    // This removes all items from ah
    public static void clearAuctionHouse() {
        allItems.clear();
        itemsForPlayer = HashBiMap.create();
        auctionId = 0;
    }

    // The following deals with money
    public static void addMoney(Player player, Long amount) {
        getEconomy().depositPlayer(player, amount);
    }

    public static void removeMoney(Player player, Long amount) {
        getEconomy().withdrawPlayer(player, amount);
    }

    public static long getMoney(Player player) {
        return (long) getEconomy().getBalance(player);
    }

    public static boolean hasMoney(Player player, long amount) {
        return getEconomy().has(player, amount);
    }

    public static String deriveName(ItemStack item) {
        if (item.getItemMeta().hasDisplayName() && !Strings.isNullOrEmpty(item.getItemMeta().getDisplayName())) {
            return ChatColor.stripColor(item.getItemMeta().getDisplayName()) + " " + item.getItemMeta().getPersistentDataContainer().get(auctionIdKey, PersistentDataType.LONG);
        } else {
            return ChatColor.stripColor(item.getType().toString()) + " " + item.getItemMeta().getPersistentDataContainer().get(auctionIdKey, PersistentDataType.LONG);
        }
    }

    public static long getNumOfItems() {
        return allItems.size();
    }

    public static List<AuctionItem> getAllItems() {
        return allItems;
    }

    public static AuctionItem itemStackToAuctionItem(ItemStack item) {
        return itemStackToAuctionItem.get(item);
    }

    public static Inventory getBaseAuctionHouse() {
        return baseAuctionHouse;
    }

    public static ItemStack getFindSign() {
        return findSign;
    }

    public static void resetAuctionId() {
        auctionId = 0;
    }

    public static void backUp() {
        Connection connection = null;
        Statement statement;
        try {
            SuperAuctionHouse.openConnection();
            connection = SuperAuctionHouse.getConnection();
            statement = connection.createStatement();

            statement.executeQuery("USE " + SuperAuctionHouse.database + ";");

            // Bask up allitems
            statement.executeUpdate("TRUNCATE TABLE auctionhouse;");
            statement.executeUpdate("INSERT IGNORE INTO `auctionhouse`" +
                    "SET `auctionitem` = '" + toBase64(allItems.toArray()) + "'," +
                    "`auctionid` = " + auctionId + ";");

            // Back up stashes
            statement.executeUpdate("TRUNCATE TABLE stashes;");
            Iterator stashIterator = stashes.entrySet().iterator();
            while (stashIterator.hasNext()) {
                Map.Entry itemsInStash = (Map.Entry) stashIterator.next();
                List<ItemStack> items = (List<ItemStack>) itemsInStash.getValue();

                statement.executeUpdate("INSERT IGNORE INTO `stashes`" +
                        "SET `player` = '" + toBase64((Object[]) itemsInStash.getKey()) + "'," +
                        "`items` = " + toBase64(items.toArray()) + ";");
            }
        } catch (SQLException | ClassNotFoundException e) {
            plugin.getLogger().warning("Something has gone wrong with the database, stack trace logged as error");
            e.printStackTrace();
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    plugin.getLogger().warning("Something has gone wrong with the database, stack trace logged as error");
                    e.printStackTrace();
                }
            }
        }

        plugin.getLogger().info("Auction house has been backed up!");
    }

    public static void loadFromBackup() {
        Connection connection = null;
        Statement statement;
        try {
            SuperAuctionHouse.openConnection();
            connection = SuperAuctionHouse.getConnection();
            statement = connection.createStatement();

            statement.executeQuery("USE " + SuperAuctionHouse.database + ";");

            // Get auctionid
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
            rs = statement.executeQuery("SELECT auctionitem FROM auctionhouse LIMIT 1;");
            while (rs.next()) {
                for (AuctionItem item : Arrays.asList((AuctionItem[]) fromBase64(rs.getString("auctionitem")))) {
                    addItem(item.getItem(), item.getPlayer(), item.getPrice());
                }
            }

            // Load stashes
            rs = statement.executeQuery("SELECT * FROM stashes;");
            while (rs.next()) {
                Player player = (Player) fromBase64(rs.getString("player"))[0];
                ItemStack[] item = (ItemStack[]) fromBase64(rs.getString("item"));

                stashes.put(player, Arrays.asList(item));
            }
        } catch (SQLException | ClassNotFoundException | IOException e) {
            plugin.getLogger().warning("Something has gone wrong with the database, stack trace logged as error");
            e.printStackTrace();
        } finally {
            // Close connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    plugin.getLogger().warning("Something has gone wrong with the database, stack trace logged as error");
                    e.printStackTrace();
                }
            }
        }

        plugin.getLogger().info("Auction house has been loaded!");
    }

    public static Object[] fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    public static String toBase64(Object[] objects) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(objects.length);

            // Save every element in the list
            for (int i = 0; i < objects.length; i++) {
                dataOutput.writeObject(objects[i]);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
}