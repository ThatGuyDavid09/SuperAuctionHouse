package thatguydavid09.superauctionhouse.menus.auctionhouse;

import com.google.gson.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.JSONObject;
import thatguydavid09.superauctionhouse.AuctionItem;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static thatguydavid09.superauctionhouse.SuperAuctionHouse.getEconomy;
import static thatguydavid09.superauctionhouse.SuperAuctionHouse.placeholder;

public class BaseAuctionHouseMenu {
    // Other necessary stuff
    private static final SuperAuctionHouse plugin = SuperAuctionHouse.getInstance();
    // List of all items
    private final static ArrayList<AuctionItem> allItems = new ArrayList<>(); // This needs to be backed up
    public static List<Player> playersFindingStuff = new ArrayList<>();
    private static Inventory baseAuctionHouse;
    private static long auctionId = 0; // This needs to be created from backup
    private static ItemStack sortItem;
    // Item to something
    private static final HashMap<UUID, List<AuctionItem>> itemsForPlayer = new HashMap<>(); // This needs to be created from backup
    private static final HashMap<ItemStack, AuctionItem> itemStackToAuctionItem = new HashMap<>(); // This needs to be created from backup

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

        updateDictionaries(auctionItem);

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

        updateDictionaries(auctionItem);

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
     *
     * @return A list of all the items in the auction house
     */
    public static ArrayList<AuctionItem> getAllItems() {
        // FIXME Make this somehow not return a pointer to the original list
        // We must do this to prevent modifying the original list, as .addAll and the constructor method add pointers, not copies of the object.
//        List<AuctionItem> toReturn = new ArrayList<>();
//
//        for (AuctionItem item : allItems) {
//            toReturn.add(new AuctionItem(item));
//        }
//        return toReturn;
        return (ArrayList<AuctionItem>) allItems.clone();
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
                        "SET `auctionitem` = '" + auctionItemToJson(item).replaceAll("\"", "\\\\\"") + "'," +
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
                AuctionItem item = auctionItemFromJson(rs.getString("auctionitem"));
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
     * This encodes an <a href="#{@link}"{@link AuctionItem}> to a json string
     *
     * @param item The <a href="#{@link}"{@link AuctionItem}> to be converted
     * @return The json string representing the item
     */
    public static String auctionItemToJson(AuctionItem item) {
        return StringEscapeUtils.escapeSql(new JSONObject()
                .put("id", item.getId())
                .put("price", item.getPrice())
                .put("item", StringEscapeUtils.escapeSql(itemStackToJson(item.getItem())))
                .put("player", StringEscapeUtils.escapeSql(item.getPlayerId().toString()))
                .put("time", item.getTime())
                .put("infsell", item.isInfsell())
                .put("playerName", item.getPlayerName())
                .toString());
    }

    /**
     * This converts an <a href="#{@link}"{@link ItemStack}> to a json string
     *
     * @param item The <a href="#{@link}"{@link ItemStack}> to be converted
     * @return The json string representing the item
     */
    public static String itemStackToJson(ItemStack item) {
        String[] BYPASS_CLASS = {"CraftMetaBlockState", "CraftMetaItem"
                /*Glowstone Support*/, "GlowMetaItem"};

        Gson gson = new Gson();
        JsonObject itemJson = new JsonObject();


        itemJson.addProperty("type", item.getType().name());
        if (item.getAmount() != 1) itemJson.addProperty("amount", item.getAmount());


        if (item.hasItemMeta()) {
            JsonObject metaJson = new JsonObject();

            ItemMeta meta = item.getItemMeta();


            if (meta.hasDisplayName()) {
                metaJson.addProperty("displayname", meta.getDisplayName());
            }
            if (meta.hasLore()) {
                JsonArray lore = new JsonArray();
                meta.getLore().forEach(str -> lore.add(new JsonPrimitive(str)));
                metaJson.add("lore", lore);
            }
            if (meta.hasEnchants()) {
                JsonArray enchants = new JsonArray();
                meta.getEnchants().forEach((enchantment, integer) -> enchants.add(new JsonPrimitive(enchantment.getKey() + ":" + integer)));
                metaJson.add("enchants", enchants);
            }
            if (!meta.getItemFlags().isEmpty()) {
                JsonArray flags = new JsonArray();
                meta.getItemFlags().stream().map(ItemFlag::name).forEach(str -> flags.add(new JsonPrimitive(str)));
                metaJson.add("flags", flags);
            }

            for (String clazz : BYPASS_CLASS) {
                if (meta.getClass().getSimpleName().equals(clazz)) {
                    itemJson.add("item-meta", metaJson);
                    return gson.toJson(itemJson);
                }
            }

            if (meta instanceof SkullMeta) {
                SkullMeta skullMeta = (SkullMeta) meta;
                if (skullMeta.hasOwner()) {
                    JsonObject extraMeta = new JsonObject();
                    extraMeta.addProperty("owner", skullMeta.getOwningPlayer().getName());
                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof BannerMeta) {
                BannerMeta bannerMeta = (BannerMeta) meta;
                JsonObject extraMeta = new JsonObject();

                if (bannerMeta.numberOfPatterns() > 0) {
                    JsonArray patterns = new JsonArray();
                    bannerMeta.getPatterns()
                            .stream()
                            .map(pattern ->
                                    pattern.getColor().name() + ":" + pattern.getPattern().getIdentifier())
                            .forEach(str -> patterns.add(new JsonPrimitive(str)));
                    extraMeta.add("patterns", patterns);
                }

                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof EnchantmentStorageMeta) {
                EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
                if (esmeta.hasStoredEnchants()) {
                    JsonObject extraMeta = new JsonObject();
                    JsonArray storedEnchants = new JsonArray();
                    esmeta.getStoredEnchants().forEach((enchantment, integer) -> storedEnchants.add(new JsonPrimitive(enchantment.getKey() + ":" + integer)));
                    extraMeta.add("stored-enchants", storedEnchants);
                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof LeatherArmorMeta) {
                LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                JsonObject extraMeta = new JsonObject();
                extraMeta.addProperty("color", Integer.toHexString(lameta.getColor().asRGB()));
                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof BookMeta) {
                BookMeta bmeta = (BookMeta) meta;
                if (bmeta.hasAuthor() || bmeta.hasPages() || bmeta.hasTitle()) {
                    JsonObject extraMeta = new JsonObject();
                    if (bmeta.hasTitle()) {
                        extraMeta.addProperty("title", bmeta.getTitle());
                    }
                    if (bmeta.hasAuthor()) {
                        extraMeta.addProperty("author", bmeta.getAuthor());
                    }
                    if (bmeta.hasPages()) {
                        JsonArray pages = new JsonArray();
                        bmeta.getPages().forEach(str -> pages.add(new JsonPrimitive(str)));
                        extraMeta.add("pages", pages);
                    }
                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof PotionMeta) {
                PotionMeta pmeta = (PotionMeta) meta;
                if (pmeta.hasCustomEffects()) {
                    JsonObject extraMeta = new JsonObject();

                    JsonArray customEffects = new JsonArray();
                    pmeta.getCustomEffects().forEach(potionEffect -> customEffects.add(new JsonPrimitive(potionEffect.getType().getName()
                            + ":" + potionEffect.getAmplifier()
                            + ":" + potionEffect.getDuration() / 20)));
                    extraMeta.add("custom-effects", customEffects);

                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof FireworkEffectMeta) {
                FireworkEffectMeta femeta = (FireworkEffectMeta) meta;
                if (femeta.hasEffect()) {
                    FireworkEffect effect = femeta.getEffect();
                    JsonObject extraMeta = new JsonObject();

                    extraMeta.addProperty("type", effect.getType().name());
                    if (effect.hasFlicker()) extraMeta.addProperty("flicker", true);
                    if (effect.hasTrail()) extraMeta.addProperty("trail", true);

                    if (!effect.getColors().isEmpty()) {
                        JsonArray colors = new JsonArray();
                        effect.getColors().forEach(color ->
                                colors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                        extraMeta.add("colors", colors);
                    }

                    if (!effect.getFadeColors().isEmpty()) {
                        JsonArray fadeColors = new JsonArray();
                        effect.getFadeColors().forEach(color ->
                                fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                        extraMeta.add("fade-colors", fadeColors);
                    }

                    metaJson.add("extra-meta", extraMeta);
                }
            } else if (meta instanceof FireworkMeta) {
                FireworkMeta fmeta = (FireworkMeta) meta;

                JsonObject extraMeta = new JsonObject();

                extraMeta.addProperty("power", fmeta.getPower());

                if (fmeta.hasEffects()) {
                    JsonArray effects = new JsonArray();
                    fmeta.getEffects().forEach(effect -> {
                        JsonObject jsonObject = new JsonObject();

                        jsonObject.addProperty("type", effect.getType().name());
                        if (effect.hasFlicker()) jsonObject.addProperty("flicker", true);
                        if (effect.hasTrail()) jsonObject.addProperty("trail", true);

                        if (!effect.getColors().isEmpty()) {
                            JsonArray colors = new JsonArray();
                            effect.getColors().forEach(color ->
                                    colors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                            jsonObject.add("colors", colors);
                        }

                        if (!effect.getFadeColors().isEmpty()) {
                            JsonArray fadeColors = new JsonArray();
                            effect.getFadeColors().forEach(color ->
                                    fadeColors.add(new JsonPrimitive(Integer.toHexString(color.asRGB()))));
                            jsonObject.add("fade-colors", fadeColors);
                        }

                        effects.add(jsonObject);
                    });
                    extraMeta.add("effects", effects);
                }
                metaJson.add("extra-meta", extraMeta);
            } else if (meta instanceof MapMeta) {
                MapMeta mmeta = (MapMeta) meta;
                JsonObject extraMeta = new JsonObject();

                /* 1.11
                if(mmeta.hasLocationName()) {
                    extraMeta.addProperty("location-name", mmeta.getLocationName());
                }
                if(mmeta.hasColor()) {
                    extraMeta.addProperty("color", Integer.toHexString(mmeta.getColor().asRGB()));
                }*/
                extraMeta.addProperty("scaling", mmeta.isScaling());

                metaJson.add("extra-meta", extraMeta);
            }

            itemJson.add("item-meta", metaJson);
        }
        return gson.toJson(itemJson);
    }

    /**
     * This converts a json string to an <a href="#{@link}"{@link AuctionItem}>
     *
     * @param string The json string
     * @return The <a href="#{@link}"{@link AuctionItem}> the json represents
     */
    public static AuctionItem auctionItemFromJson(String string) {
        JSONObject json = new JSONObject(string.replaceAll("\"", "\\\""));
        return new AuctionItem(itemStackFromJson(json.getString("item")), json.getLong("id"), json.getLong("price"), UUID.fromString(json.getString("player")), json.getLong("time"), json.getBoolean("infsell"), json.getString("playerName"));
    }

    /**
     * This converts a json string to an <a href="#{@link}"{@link ItemStack}>
     *
     * @param string The json string
     * @return The <a href="#{@link}"{@link ItemStack}> the json represents
     */
    public static ItemStack itemStackFromJson(String string) {
        String[] BYPASS_CLASS = {"CraftMetaBlockState", "CraftMetaItem"
                /*Glowstone Support*/, "GlowMetaItem"};

        string = string.replaceAll("\"", "\\\"");

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(string);
        if (element.isJsonObject()) {
            JsonObject itemJson = element.getAsJsonObject();

            JsonElement typeElement = itemJson.get("type");
            JsonElement dataElement = itemJson.get("data");
            JsonElement amountElement = itemJson.get("amount");

            if (typeElement.isJsonPrimitive()) {

                String type = typeElement.getAsString();
                short data = dataElement != null ? dataElement.getAsShort() : 0;
                int amount = amountElement != null ? amountElement.getAsInt() : 1;

                ItemStack itemStack = new ItemStack(Material.getMaterial(type));
                itemStack.setDurability(data);
                itemStack.setAmount(amount);

                JsonElement itemMetaElement = itemJson.get("item-meta");
                if (itemMetaElement != null && itemMetaElement.isJsonObject()) {

                    ItemMeta meta = itemStack.getItemMeta();
                    JsonObject metaJson = itemMetaElement.getAsJsonObject();

                    JsonElement displaynameElement = metaJson.get("displayname");
                    JsonElement loreElement = metaJson.get("lore");
                    JsonElement enchants = metaJson.get("enchants");
                    JsonElement flagsElement = metaJson.get("flags");
                    if (displaynameElement != null && displaynameElement.isJsonPrimitive()) {
                        meta.setDisplayName(displaynameElement.getAsString());
                    }
                    if (loreElement != null && loreElement.isJsonArray()) {
                        JsonArray jarray = loreElement.getAsJsonArray();
                        List<String> lore = new ArrayList<>(jarray.size());
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) lore.add(jsonElement.getAsString());
                        });
                        meta.setLore(lore);
                    }
                    if (enchants != null && enchants.isJsonArray()) {
                        JsonArray jarray = enchants.getAsJsonArray();
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) {
                                String enchantString = jsonElement.getAsString();
                                if (enchantString.contains(":")) {
                                    try {
                                        String[] splitEnchant = enchantString.split(":");
                                        Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(splitEnchant[0]));
                                        int level = Integer.parseInt(splitEnchant[1]);
                                        if (enchantment != null && level > 0) {
                                            meta.addEnchant(enchantment, level, true);
                                        }
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            }
                        });
                    }
                    if (flagsElement != null && flagsElement.isJsonArray()) {
                        JsonArray jarray = flagsElement.getAsJsonArray();
                        jarray.forEach(jsonElement -> {
                            if (jsonElement.isJsonPrimitive()) {
                                for (ItemFlag flag : ItemFlag.values()) {
                                    if (flag.name().equalsIgnoreCase(jsonElement.getAsString())) {
                                        meta.addItemFlags(flag);
                                        break;
                                    }
                                }
                            }
                        });
                    }
                    for (String clazz : BYPASS_CLASS) {
                        if (meta.getClass().getSimpleName().equals(clazz)) {
                            return itemStack;
                        }
                    }

                    JsonElement extrametaElement = metaJson.get("extra-meta");

                    if (extrametaElement != null
                            && extrametaElement.isJsonObject()) {
                        try {
                            JsonObject extraJson = extrametaElement.getAsJsonObject();
                            if (meta instanceof SkullMeta) {
                                JsonElement ownerElement = extraJson.get("owner");
                                if (ownerElement != null && ownerElement.isJsonPrimitive()) {
                                    SkullMeta smeta = (SkullMeta) meta;
                                    smeta.setOwner(ownerElement.getAsString());
                                }
                            } else if (meta instanceof BannerMeta) {
                                JsonElement baseColorElement = extraJson.get("base-color");
                                JsonElement patternsElement = extraJson.get("patterns");
                                BannerMeta bmeta = (BannerMeta) meta;
                                if (baseColorElement != null && baseColorElement.isJsonPrimitive()) {
                                    try {
                                        Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                                                .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(baseColorElement.getAsString()))
                                                .findFirst();
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                                if (patternsElement != null && patternsElement.isJsonArray()) {
                                    JsonArray jarray = patternsElement.getAsJsonArray();
                                    List<Pattern> patterns = new ArrayList<>(jarray.size());
                                    jarray.forEach(jsonElement -> {
                                        String patternString = jsonElement.getAsString();
                                        if (patternString.contains(":")) {
                                            String[] splitPattern = patternString.split(":");
                                            Optional<DyeColor> color = Arrays.stream(DyeColor.values())
                                                    .filter(dyeColor -> dyeColor.name().equalsIgnoreCase(splitPattern[0]))
                                                    .findFirst();
                                            PatternType patternType = PatternType.getByIdentifier(splitPattern[1]);
                                            if (color.isPresent() && patternType != null) {
                                                patterns.add(new Pattern(color.get(), patternType));
                                            }
                                        }
                                    });
                                    if (!patterns.isEmpty()) bmeta.setPatterns(patterns);
                                }
                            } else if (meta instanceof EnchantmentStorageMeta) {
                                JsonElement storedEnchantsElement = extraJson.get("stored-enchants");
                                if (storedEnchantsElement != null && storedEnchantsElement.isJsonArray()) {
                                    EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
                                    JsonArray jarray = storedEnchantsElement.getAsJsonArray();
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) {
                                            String enchantString = jsonElement.getAsString();
                                            if (enchantString.contains(":")) {
                                                try {
                                                    String[] splitEnchant = enchantString.split(":");
                                                    Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(splitEnchant[0]));
                                                    int level = Integer.parseInt(splitEnchant[1]);
                                                    if (enchantment != null && level > 0) {
                                                        esmeta.addStoredEnchant(enchantment, level, true);
                                                    }
                                                } catch (NumberFormatException ignored) {
                                                }
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof LeatherArmorMeta) {
                                JsonElement colorElement = extraJson.get("color");
                                if (colorElement != null && colorElement.isJsonPrimitive()) {
                                    LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
                                    try {
                                        lameta.setColor(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                    } catch (NumberFormatException ignored) {
                                    }
                                }
                            } else if (meta instanceof BookMeta) {
                                JsonElement titleElement = extraJson.get("title");
                                JsonElement authorElement = extraJson.get("author");
                                JsonElement pagesElement = extraJson.get("pages");

                                BookMeta bmeta = (BookMeta) meta;
                                if (titleElement != null && titleElement.isJsonPrimitive()) {
                                    bmeta.setTitle(titleElement.getAsString());
                                }
                                if (authorElement != null && authorElement.isJsonPrimitive()) {
                                    bmeta.setAuthor(authorElement.getAsString());
                                }
                                if (pagesElement != null && pagesElement.isJsonArray()) {
                                    JsonArray jarray = pagesElement.getAsJsonArray();
                                    List<String> pages = new ArrayList<>(jarray.size());
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) pages.add(jsonElement.getAsString());
                                    });
                                    bmeta.setPages(pages);
                                }

                            } else if (meta instanceof PotionMeta) {
                                JsonElement customEffectsElement = extraJson.get("custom-effects");
                                if (customEffectsElement != null && customEffectsElement.isJsonArray()) {
                                    PotionMeta pmeta = (PotionMeta) meta;
                                    JsonArray jarray = customEffectsElement.getAsJsonArray();
                                    jarray.forEach(jsonElement -> {
                                        if (jsonElement.isJsonPrimitive()) {
                                            String enchantString = jsonElement.getAsString();
                                            if (enchantString.contains(":")) {
                                                try {
                                                    String[] splitPotions = enchantString.split(":");
                                                    PotionEffectType potionType = PotionEffectType.getByName(splitPotions[0]);
                                                    int amplifier = Integer.parseInt(splitPotions[1]);
                                                    int duration = Integer.parseInt(splitPotions[2]) * 20;
                                                    if (potionType != null) {
                                                        pmeta.addCustomEffect(new PotionEffect(potionType, amplifier, duration), true);
                                                    }
                                                } catch (NumberFormatException ignored) {
                                                }
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof FireworkEffectMeta) {
                                JsonElement effectTypeElement = extraJson.get("type");
                                JsonElement flickerElement = extraJson.get("flicker");
                                JsonElement trailElement = extraJson.get("trail");
                                JsonElement colorsElement = extraJson.get("colors");
                                JsonElement fadeColorsElement = extraJson.get("fade-colors");

                                if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {
                                    FireworkEffectMeta femeta = (FireworkEffectMeta) meta;

                                    FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeElement.getAsString());

                                    List<Color> colors = new ArrayList<>();
                                    if (colorsElement != null && colorsElement.isJsonArray())
                                        colorsElement.getAsJsonArray().forEach(colorElement -> {
                                            if (colorElement.isJsonPrimitive())
                                                colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                        });

                                    List<Color> fadeColors = new ArrayList<>();
                                    if (fadeColorsElement != null && fadeColorsElement.isJsonArray())
                                        fadeColorsElement.getAsJsonArray().forEach(colorElement -> {
                                            if (colorElement.isJsonPrimitive())
                                                fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                        });

                                    FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);

                                    if (flickerElement != null && flickerElement.isJsonPrimitive())
                                        builder.flicker(flickerElement.getAsBoolean());
                                    if (trailElement != null && trailElement.isJsonPrimitive())
                                        builder.trail(trailElement.getAsBoolean());

                                    if (!colors.isEmpty()) builder.withColor(colors);
                                    if (!fadeColors.isEmpty()) builder.withFade(fadeColors);

                                    femeta.setEffect(builder.build());
                                }
                            } else if (meta instanceof FireworkMeta) {
                                FireworkMeta fmeta = (FireworkMeta) meta;

                                JsonElement effectArrayElement = extraJson.get("effects");
                                JsonElement powerElement = extraJson.get("power");

                                if (powerElement != null && powerElement.isJsonPrimitive()) {
                                    fmeta.setPower(powerElement.getAsInt());
                                }

                                if (effectArrayElement != null && effectArrayElement.isJsonArray()) {

                                    effectArrayElement.getAsJsonArray().forEach(jsonElement -> {
                                        if (jsonElement.isJsonObject()) {

                                            JsonObject jsonObject = jsonElement.getAsJsonObject();

                                            JsonElement effectTypeElement = jsonObject.get("type");
                                            JsonElement flickerElement = jsonObject.get("flicker");
                                            JsonElement trailElement = jsonObject.get("trail");
                                            JsonElement colorsElement = jsonObject.get("colors");
                                            JsonElement fadeColorsElement = jsonObject.get("fade-colors");

                                            if (effectTypeElement != null && effectTypeElement.isJsonPrimitive()) {

                                                FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeElement.getAsString());

                                                List<Color> colors = new ArrayList<>();
                                                if (colorsElement != null && colorsElement.isJsonArray())
                                                    colorsElement.getAsJsonArray().forEach(colorElement -> {
                                                        if (colorElement.isJsonPrimitive())
                                                            colors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                    });

                                                List<Color> fadeColors = new ArrayList<>();
                                                if (fadeColorsElement != null && fadeColorsElement.isJsonArray())
                                                    fadeColorsElement.getAsJsonArray().forEach(colorElement -> {
                                                        if (colorElement.isJsonPrimitive())
                                                            fadeColors.add(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                                    });

                                                FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);

                                                if (flickerElement != null && flickerElement.isJsonPrimitive())
                                                    builder.flicker(flickerElement.getAsBoolean());
                                                if (trailElement != null && trailElement.isJsonPrimitive())
                                                    builder.trail(trailElement.getAsBoolean());

                                                if (!colors.isEmpty()) builder.withColor(colors);
                                                if (!fadeColors.isEmpty()) builder.withFade(fadeColors);

                                                fmeta.addEffect(builder.build());
                                            }
                                        }
                                    });
                                }
                            } else if (meta instanceof MapMeta) {
                                MapMeta mmeta = (MapMeta) meta;

                                JsonElement scalingElement = extraJson.get("scaling");
                                if (scalingElement != null && scalingElement.isJsonPrimitive()) {
                                    mmeta.setScaling(scalingElement.getAsBoolean());
                                }

                                /* 1.11
                                JsonElement locationNameElement = extraJson.get("location-name");
                                if(locationNameElement != null && locationNameElement.isJsonPrimitive()) {
                                    mmeta.setLocationName(locationNameElement.getAsString());
                                }
                                JsonElement colorElement = extraJson.get("color");
                                if(colorElement != null && colorElement.isJsonPrimitive()) {
                                    mmeta.setColor(Color.fromRGB(Integer.parseInt(colorElement.getAsString(), 16)));
                                }*/
                            }
                        } catch (Exception e) {
                            return null;
                        }
                    }
                    itemStack.setItemMeta(meta);
                }
                return itemStack;
            } else return null;
        } else return null;
    }
}