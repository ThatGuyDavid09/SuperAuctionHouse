package com.highmarsorbit.superauctionhouse.elements.sell;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.elements.BaseElement;
import com.highmarsorbit.superauctionhouse.inventories.SellItemMenu;
import com.highmarsorbit.superauctionhouse.util.AuctionType;
import com.highmarsorbit.superauctionhouse.util.ChatUtils;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import fr.cleymax.signgui.SignGUI;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SellMenuSellerNameElement extends BaseElement {
    private SellItemMenu sellMenu;
    private AnvilGUI.Builder builder;

    public SellMenuSellerNameElement(char character, InventoryGui gui, SellItemMenu sellMenu) {
        super(character, gui);
        this.sellMenu = sellMenu;

        createAnvilGui();

        element = new DynamicGuiElement(character, () -> {
            return new StaticGuiElement(character, getPlayerHead(),
                    click -> {
                        gui.playClickSound();
//                        gui.close();
                        builder.open((Player) click.getWhoClicked());
                        return true;
                    },
                    ChatUtils.RESET + "Set seller name: " + ChatColor.RESET + sellMenu.sellerName,
                    " ",
                    ChatUtils.RESET + ChatColor.YELLOW + "Click to change who the seller appears as!");
        });
    }

    private ItemStack getPlayerHead() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(sellMenu.getHolder().getUniqueId()));
        head.setItemMeta(skullMeta);
        return head;
    }

    private void createAnvilGui() {
        builder = new AnvilGUI.Builder()
                .onClose(state -> {
                    Bukkit.getScheduler().runTask(SuperAuctionHouse.getInstance(), () -> {
                        sellMenu.sellerName = state.getText();
                        sellMenu.getGui().close(false);
                        sellMenu.getGui().playClickSound();
                        sellMenu.drawInventory();

//                        sellMenu = new SellItemMenu(sellMenu);
                        sellMenu.open(false);
                    });
                })
                .onClick((slot, state) -> {
                    if (slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    return List.of(AnvilGUI.ResponseAction.close());
                })
                .preventClose()
                .title("Input seller name:")
                .text(sellMenu.sellerName)
                .plugin(SuperAuctionHouse.getInstance());
    }
}
