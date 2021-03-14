package thatguydavid09.superauctionhouse.events.generic;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import thatguydavid09.superauctionhouse.AuctionItem;
import thatguydavid09.superauctionhouse.SuperAuctionHouse;
import thatguydavid09.superauctionhouse.commands.AuctionHouseCommand;
import thatguydavid09.superauctionhouse.commands.PlayerCommands;
import thatguydavid09.superauctionhouse.menus.auctionhouse.AuctionHouseActions;
import thatguydavid09.superauctionhouse.menus.auctionhouse.BaseAuctionHouse;
import thatguydavid09.superauctionhouse.menus.bid.BidMenu;
import thatguydavid09.superauctionhouse.menus.bid.BidMenuActions;
import thatguydavid09.superauctionhouse.menus.buy.BuyMenu;
import thatguydavid09.superauctionhouse.menus.sell.SellMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class PreventItemRemoval implements Listener {
    public static HashMap<Player, BuyMenu> buyMenus = new HashMap<>();
    public static HashMap<Player, BidMenu> bidMenus = new HashMap<>();

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        FileConfiguration config = SuperAuctionHouse.getInstance().getConfig();
        // List of forbidden inventory titles
        List<String> forbiddenTitles = new ArrayList<>(Arrays.asList(config.getString("auctionhouse.names.auctionhouse"), config.getString("auctionhouse.names.buymenu"), config.getString("auctionhouse.names.sellmenu"), config.getString("auctionhouse.names.bidmenu")));


        if (event.getClickedInventory() != null && forbiddenTitles.contains(event.getView().getTitle()) && event.getRawSlot() < 54) {
            if (event.getClick() == ClickType.LEFT) {
                // Identify inventory as ah
                List<Inventory> auctionHousePage = AuctionHouseCommand.getAuctionHouse(player).getAuctionHouse();
                if (auctionHousePage.contains(event.getClickedInventory())) {
                    if (event.getRawSlot() == 50 && event.getCurrentItem().getType() == Material.ARROW) {
                        AuctionHouseActions.nextPage(player);
                        event.setCancelled(true);
                    } else if (event.getRawSlot() == 45 && event.getCurrentItem().getType() == Material.DIAMOND) {
                        AuctionHouseCommand.getOwnAuctionHouse(player).openAuctionHouse();
                        event.setCancelled(true);
                    } else if (event.getRawSlot() == 48 && event.getCurrentItem().getType() == Material.ARROW) {
                        AuctionHouseActions.previousPage(player);
                        event.setCancelled(true);
                    } else if (event.getRawSlot() == 49 && event.getCurrentItem().getType() == Material.SUNFLOWER) {
                        AuctionHouseActions.cycleSortMode(player);
                        event.setCancelled(true);
                    } else if (event.getRawSlot() == 52 && event.getCurrentItem().getType() == Material.OAK_SIGN) {
                        event.setCancelled(true);
                        AuctionHouseActions.find(player);
                    } else if (event.getRawSlot() <= 44) {
                        // AH item is clicked
                        if (event.getCurrentItem() != null) {
                            AuctionItem item = BaseAuctionHouse.itemStackToAuctionItem(event.getCurrentItem());
                            if (item.isAuction()) {
                                bidMenus.remove(player);
                                BidMenu confirm = new BidMenu(item, player);
                                bidMenus.put(player, confirm);
                                (player).playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                confirm.openBidMenu();
                            } else {
                                buyMenus.remove(player);
                                BuyMenu confirm = new BuyMenu(item, player);
                                buyMenus.put(player, confirm);
                                (player).playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                confirm.openBuyMenu();
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    } else {
                        event.setCancelled(true);
                    }
                } else if (AuctionHouseCommand.getOwnAuctionHouse(player).getAuctionHouse().contains(event.getClickedInventory())) {
                    // Identify as Own Auctions inventory
                    if (event.getRawSlot() == 50 && event.getCurrentItem().getType() == Material.ARROW) {
                        AuctionHouseActions.nextPage(player);
                        event.setCancelled(true);
                    } else if (event.getRawSlot() == 48 && event.getCurrentItem().getType() == Material.ARROW) {
                        AuctionHouseActions.previousPage(player);
                        event.setCancelled(true);
                    } else if (event.getRawSlot() == 49 && event.getCurrentItem().getType() == Material.SUNFLOWER) {
                        AuctionHouseActions.cycleSortMode(player);
                        event.setCancelled(true);
                    } else if (event.getRawSlot() <= 44) {
                        // TODO replace with claiming logic
                        // AH item is clicked
                        if (event.getCurrentItem() != null) {
                            AuctionItem item = BaseAuctionHouse.itemStackToAuctionItem(event.getCurrentItem());
                            if (item.isAuction()) {
                                bidMenus.remove(player);
                                BidMenu confirm = new BidMenu(item, player);
                                bidMenus.put(player, confirm);
                                (player).playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                confirm.openBidMenu();
                            } else {
                                buyMenus.remove(player);
                                BuyMenu confirm = new BuyMenu(item, player);
                                buyMenus.put(player, confirm);
                                (player).playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 2f);
                                confirm.openBuyMenu();
                            }
                        } else {
                            event.setCancelled(true);
                        }
                    } else {
                        event.setCancelled(true);
                    }
                } else if (buyMenus.size() != 0 && buyMenus.containsKey(player)) {
                    BuyMenu buyMenu = buyMenus.get(player);
                    // Identify inventory as BuyMenu and restrict to buyMenu
                    if (event.getRawSlot() >= 0 || event.getRawSlot() <= 8) {
                        if (event.getRawSlot() == 2) {
                            buyMenu.confirmPurchase();
                            event.setCancelled(true);
                        } else if (event.getRawSlot() == 6) {
                            buyMenu.cancelPurchase();
                            event.setCancelled(true);
                        } else {
                            event.setCancelled(true);
                        }
                    }
                    // Identify as sell menu inventory
                } else if (event.getInventory().getItem(29).getType() == Material.SUNFLOWER) {
                    SellMenu menu = PlayerCommands.sellMenuByPlayer.get(player);

                    // Handle price
                    if (event.getRawSlot() == 29 && event.getInventory().getItem(29).getType() == Material.SUNFLOWER) {
                        player.closeInventory();
                        SellMenu.playersEnteringPrice.put(player, -1L);
                        (player).spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Type the desired price of the item in chat! (Not negative and no decimals)").color(ChatColor.GREEN).create());
                        event.setCancelled(true);
                    }

                    // Handle setting custom name
                    if (event.getRawSlot() == 40 && event.getInventory().getItem(40).getType() == Material.PLAYER_HEAD) {
                        player.closeInventory();
                        SellMenu.playersEnteringName.put(player, "");
                        (player).spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Type the desired name to sell as in chat (Color codes work)").color(ChatColor.GREEN).create());
                        event.setCancelled(true);
                    }

                    // Handle setting time
                    if (event.getRawSlot() == 28 && menu.mode == 1) {
                        player.closeInventory();
                        SellMenu.playersEnteringTime.put(player, -1L);
                        (player).spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder("Type the desired auction time (in minutes)").color(ChatColor.GREEN).create());
                        event.setCancelled(true);
                    }

                    // Handle confirm sell
                    if (event.getRawSlot() == 31 && event.getInventory().getItem(31).getType() == Material.GREEN_CONCRETE) {
                        PlayerCommands.confirmSell(menu, player);
                        player.closeInventory();
                        PlayerCommands.sellMenuByPlayer.remove(player);
                    }

                    // Handle close
                    if (event.getRawSlot() == 49) {
                        player.closeInventory();
                        PlayerCommands.sellMenuByPlayer.remove(player);
                    }

                    // mode item clicked, change mode
                    if (event.getRawSlot() == 33) {
                        menu.incrementMode();
                        menu.refreshInventory();
                        event.setCancelled(true);
                    }

                    event.setCancelled(true);
                    // Identify inventory as bidding inventory
                } else if (bidMenus.size() != 0 && bidMenus.containsKey(player)) {
                    BidMenu menu = bidMenus.get(player);
                    event.setCancelled(true);
                    long minBidInterval = SuperAuctionHouse.getInstance().getConfig().getInt("auctionhouse.minbidinterval");
                    // If is close item
                    if (event.getRawSlot() == 49) {
                        AuctionHouseCommand.getAuctionHouse(player).reloadAuctionHouse();

                        // If is confirm item
                    } else if (event.getRawSlot() == 40 && event.getCurrentItem().getType() == Material.GREEN_CONCRETE) {
                        menu.confirmBid();

                        // If is bid increase 1
                    } else if (event.getRawSlot() == 28) {
                        menu.increaseBid(minBidInterval * 1);

                        // If is bin increase 2
                    } else if (event.getRawSlot() == 29) {
                        menu.increaseBid(minBidInterval * 5);

                        // If is bin increase 3
                    } else if (event.getRawSlot() == 30) {
                        menu.increaseBid(minBidInterval * 10);

                        // If is bin increase 4
                    } else if (event.getRawSlot() == 31) {
                        menu.increaseBid(minBidInterval * 100);

                        // If is bin increase 5
                    } else if (event.getRawSlot() == 32) {
                        menu.increaseBid(minBidInterval * 500);

                        // If is bin increase 7
                    } else if (event.getRawSlot() == 33) {
                        menu.increaseBid(minBidInterval * 1000);

                        // If it is custom bid set
                    } else if (event.getRawSlot() == 34) {
                        BidMenuActions.getCustomBid(player);
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}
