package thatguydavid09.superauctionhouse.menus.buy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouseMenu;

public class BuyMenu {
    public static ItemStack confirm;
    public static ItemStack cancel;
    public static Inventory buyMenu = null;

    private final ItemStack item;
    private final Player player;


    public BuyMenu(ItemStack item, Player player) {
        this.item = item;
        this.player = player;

        if (buyMenu == null) {
            createBuyMenuTemplate();
        }
    }

    private void createBuyMenuTemplate() {
        buyMenu = Bukkit.createInventory(null, 9, "Confirm purchase");
        for (int i = 0; i < 9; i++) {
            buyMenu.setItem(i, SuperAuctionHouse.placeholder);
        }

        // Create confirm item
        confirm = new ItemStack(Material.GREEN_CONCRETE, 1);
        ItemMeta meta = confirm.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Confirm purchase");
        confirm.setItemMeta(meta);
        buyMenu.setItem(2, confirm);

        // Create cancel item
        cancel = new ItemStack(Material.RED_CONCRETE, 1);
        meta = cancel.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Cancel purchase");
        cancel.setItemMeta(meta);
        buyMenu.setItem(6, cancel);
    }

    public void openBuyMenu() {
        player.closeInventory();
        player.openInventory(buyMenu);
    }

    public void confirmPurchase() {
        if (BaseAuctionHouseMenu.banks.get(player) == null) {
            BaseAuctionHouseMenu.setMoney(player, 0L);
        }
        if (item.getItemMeta().getPersistentDataContainer().get(BaseAuctionHouseMenu.priceKey, PersistentDataType.LONG) > BaseAuctionHouseMenu.getMoney(player)) {
            player.sendMessage(ChatColor.RED + "You don't have enough money to do that!");
            cancelPurchase();
        } else {
            BaseAuctionHouseMenu.removeMoney(player, item.getItemMeta().getPersistentDataContainer().get(BaseAuctionHouseMenu.priceKey, PersistentDataType.LONG));
            BaseAuctionHouseMenu.addMoney(player, item.getItemMeta().getPersistentDataContainer().get(BaseAuctionHouseMenu.priceKey, PersistentDataType.LONG));
            BaseAuctionHouseMenu.removeItem(item, player);
            BaseAuctionHouseMenu.giveItemToPlayer(item, player);
        }
        player.closeInventory();
    }

    public void cancelPurchase() {
        player.closeInventory();
        AuctionHouseCommand.auctionHousesByPlayer.get(player).openAuctionHouse();
    }
}
