package thatguydavid09.superauctionhouse.menus.bid;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import thatguydavid09.superauctionhouse.AuctionItem;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;

public class BidMenu {
    public static ItemStack upBid1Item = null;
    public static ItemStack upBid2Item = null;
    public static ItemStack upBid3Item = null;
    public static ItemStack upBid4Item = null;
    public static ItemStack upBid5Item = null;
    public static ItemStack upBid6Item = null;
    public static ItemStack upBidCustomItem = null;
    public static ItemStack confirmItem = null;
    public static ItemStack closeItem = null;
    private static Inventory bidMenuTemplate = null;
    private final AuctionItem item;
    private final Player player;
    private final Inventory bidMenu;
    private long bid = 0;

    public BidMenu(AuctionItem item, Player player) {
        this.item = item;
        this.player = player;

        if (closeItem == null) {
            createItems();
        }

        if (bidMenuTemplate == null) {
            createBidMenuTemplate();
        }

        this.bidMenu = Bukkit.createInventory(null, 54, SuperAuctionHouse.getInstance().getConfig().getString("auctionhouse.names.bidmenu"));
        this.bidMenu.setContents(bidMenuTemplate.getContents());
        updateItems();
        bidMenu.setItem(13, item.getItem());
    }

    private void createItems() {
        long minBidBalance = SuperAuctionHouse.getInstance().getConfig().getInt("auctionhouse.minbidinterval");
        upBid1Item = new ItemStack(Material.IRON_NUGGET);
        ItemMeta meta = upBid1Item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Increase bid by " + ChatColor.GOLD + minBidBalance * 1);
        upBid1Item.setItemMeta(meta);

        upBid2Item = new ItemStack(Material.IRON_INGOT);
        meta = upBid2Item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Increase bid by " + ChatColor.GOLD + minBidBalance * 5);
        upBid2Item.setItemMeta(meta);

        upBid3Item = new ItemStack(Material.IRON_BLOCK);
        meta = upBid3Item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Increase bid by " + ChatColor.GOLD + minBidBalance * 10);
        upBid3Item.setItemMeta(meta);

        upBid4Item = new ItemStack(Material.GOLD_NUGGET);
        meta = upBid4Item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Increase bid by " + ChatColor.GOLD + minBidBalance * 100);
        upBid4Item.setItemMeta(meta);

        upBid5Item = new ItemStack(Material.GOLD_INGOT);
        meta = upBid5Item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Increase bid by " + ChatColor.GOLD + minBidBalance * 500);
        upBid5Item.setItemMeta(meta);

        upBid6Item = new ItemStack(Material.GOLD_BLOCK);
        meta = upBid6Item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Increase bid by " + ChatColor.GOLD + minBidBalance * 1000);
        upBid6Item.setItemMeta(meta);

        upBidCustomItem = new ItemStack(Material.OAK_SIGN);
        meta = upBidCustomItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Custom bid");
        upBidCustomItem.setItemMeta(meta);

        confirmItem = new ItemStack(Material.GREEN_CONCRETE);
        updateConfirmItem();

        closeItem = new ItemStack(Material.BARRIER);
        meta = closeItem.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Close menu");
        closeItem.setItemMeta(meta);
    }

    private void createBidMenuTemplate() {
        bidMenuTemplate = Bukkit.createInventory(null, 54, SuperAuctionHouse.getInstance().getConfig().getString("auctionhouse.names.bidmenu"));

        bidMenuTemplate.setItem(28, upBid1Item);
        bidMenuTemplate.setItem(29, upBid2Item);
        bidMenuTemplate.setItem(30, upBid3Item);
        bidMenuTemplate.setItem(31, upBid4Item);
        bidMenuTemplate.setItem(32, upBid5Item);
        bidMenuTemplate.setItem(33, upBid6Item);
        bidMenuTemplate.setItem(34, upBidCustomItem);

        bidMenuTemplate.setItem(40, confirmItem);
        bidMenuTemplate.setItem(49, closeItem);

        for (int i = 0; i < bidMenuTemplate.getSize(); i++) {
            if (bidMenuTemplate.getItem(i) == null) {
                bidMenuTemplate.setItem(i, SuperAuctionHouse.empty);
            }
        }
    }

    public void confirmBid() {
        // TODO make sure to call the custom event here
        // Check for enough money
        if (SuperAuctionHouse.getEconomy().getBalance(player) >= bid) {
            AuctionItem item = BaseAuctionHouse.getAllItems().get(BaseAuctionHouse.getAllItems().indexOf(this.item));
            item.setPrice(this.bid);
            item.setBidder(player);
            player.sendMessage(SuperAuctionHouse.getPrefix() + net.md_5.bungee.api.ChatColor.GREEN + "Bid has been placed!");
            player.closeInventory();
        } else {
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You don't have enough money to do that!");
        }
    }

    public void refreshInventory() {
        updateItems();
        player.openInventory(bidMenu);
    }

    public Inventory getBidMenu() {
        return bidMenu;
    }

    public long getBid() {
        return bid;
    }

    public long setBid(long bid) {
        this.bid = Math.max(bid, SuperAuctionHouse.getInstance().getConfig().getInt("auctionhouse.minbidinterval"));
        return this.bid;
    }

    public long increaseBid(long bidIncrease) {
        this.bid += bidIncrease;
        updateItems();
        refreshInventory();
        return this.bid;
    }

    private void updateItems() {
        if (bid <= 0) {
            bidMenu.setItem(40, SuperAuctionHouse.empty);
        } else {
            updateConfirmItem();
            bidMenu.setItem(40, confirmItem);
        }
    }

    private void updateConfirmItem() {
        ItemMeta meta = confirmItem.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm bid: " + ChatColor.GOLD + (item.getPrice() + bid) + " " + SuperAuctionHouse.getEconomy().currencyNamePlural());
        confirmItem.setItemMeta(meta);
    }

    public AuctionItem getItem() {
        return item;
    }

    public Player getPlayer() {
        return player;
    }

    public void openBidMenu() {
        player.closeInventory();
        player.openInventory(bidMenu);
    }
}
