package thatguydavid09.superauctionhouse.menus.buy;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import thatguydavid09.superauctionhouse.AuctionItem;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;
import thatguydavid09.superauctionhouse.menus.auctionhouse.PlayerAuctionHouse;

public class BuyMenu {
    private static ItemStack confirm;
    private static ItemStack cancel;
    private static Inventory buyMenuTemplate = null;
    private final AuctionItem item;
    private final Player player;
    private Inventory buyMenu;


    public BuyMenu(AuctionItem item, Player player) {
        this.item = item;
        this.player = player;

        if (buyMenuTemplate == null) {
            createBuyMenuTemplate();
        }

        this.buyMenu = Bukkit.createInventory(null, 9, SuperAuctionHouse.getInstance().getConfig().getString("auctionhouse.names.buymenu"));
        this.buyMenu.setContents(buyMenuTemplate.getContents());
        buyMenu.setItem(4, item.getItem());

    }

    private void createBuyMenuTemplate() {
        buyMenuTemplate = Bukkit.createInventory(null, 9, "Confirm purchase");
        for (int i = 0; i < 9; i++) {
            buyMenuTemplate.setItem(i, SuperAuctionHouse.placeholder);
        }

        // Create confirm item
        confirm = new ItemStack(Material.GREEN_CONCRETE, 1);
        ItemMeta meta = confirm.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm purchase");
        confirm.setItemMeta(meta);
        buyMenuTemplate.setItem(2, confirm);

        // Create cancel item
        cancel = new ItemStack(Material.RED_CONCRETE, 1);
        meta = cancel.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Cancel purchase");
        cancel.setItemMeta(meta);
        buyMenuTemplate.setItem(6, cancel);
    }

    public void openBuyMenu() {
        player.closeInventory();
        player.openInventory(buyMenu);
    }

    public void confirmPurchase() {
        if (!BaseAuctionHouseMenu.hasMoney(player, item.getPrice())) {
            player.sendMessage(ChatColor.RED + "You don't have enough money to do that!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            cancelPurchaseNoSound();
        } else if (!BaseAuctionHouseMenu.getAllItems().contains(item)) {
            player.sendMessage(ChatColor.RED + "It seems that item has already been purchased!");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            cancelPurchaseNoSound();
        } else {
            BaseAuctionHouseMenu.removeMoney(player, item.getPrice());
            BaseAuctionHouseMenu.addMoney(player, item.getPrice());
            BaseAuctionHouseMenu.removeItem(item);
            BaseAuctionHouseMenu.giveItemToPlayer(PlayerAuctionHouse.removeLore(item).getItem(), player);

            // Make name look nicer
            String name = item.getName();
            if (name.equals(item.getItem().getType().toString())) {
                name = WordUtils.capitalizeFully(name.replace("_", " "));
            }

            player.sendMessage(ChatColor.GREEN + "You have purchased " + ChatColor.GOLD + item.getItem().getAmount() + " " + name + ChatColor.GREEN + " for " + ChatColor.GOLD + item.getPrice() + ChatColor.GREEN + " " + ((item.getPrice() == 1) ? SuperAuctionHouse.getEconomy().currencyNameSingular() : SuperAuctionHouse.getEconomy().currencyNamePlural()) + "!");
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
        }
        player.closeInventory();
    }

    public void cancelPurchase() {
        AuctionHouseCommand.getAuctionHouse(player).openAuctionHouse(true);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
    }

    public void cancelPurchaseNoSound() {
        AuctionHouseCommand.getAuctionHouse(player).openAuctionHouse(true);
    }

    public Inventory getBuyMenu() {
        return buyMenu;
    }
}
