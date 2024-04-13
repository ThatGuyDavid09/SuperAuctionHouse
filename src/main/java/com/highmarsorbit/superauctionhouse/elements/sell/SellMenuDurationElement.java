package com.highmarsorbit.superauctionhouse.elements.sell;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.inventories.SellItemMenu;
import com.highmarsorbit.superauctionhouse.elements.BaseElement;
import com.highmarsorbit.superauctionhouse.util.ChatUtils;
import com.highmarsorbit.superauctionhouse.util.DurationUtils;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import fr.cleymax.signgui.SignGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellMenuDurationElement extends BaseElement {
    private SignGUI signGui;
    private SellItemMenu sellMenu;
    public SellMenuDurationElement(char character, InventoryGui gui, SellItemMenu sellMenu) {
        super(character, gui);
        this.sellMenu = sellMenu;

        createSignGui();

        element = new DynamicGuiElement(character, () -> {
            return new StaticGuiElement(character, new ItemStack(Material.CLOCK),
                    click -> {
                        gui.playClickSound();
//                        gui.close();
                        signGui.open((Player) click.getWhoClicked());
                        return true;
                    },
                    ChatUtils.RESET + "Set duration: " + ChatColor.AQUA + DurationUtils.formatDuration(sellMenu.duration, false),
                    " ",
                    ChatUtils.RESET + ChatColor.YELLOW + "Click to set the auction's duration!");
        });
    }

    private void createSignGui() {
        signGui = new SignGUI(SuperAuctionHouse.getInstance().getSignGuiManager(),
                state -> {
                    String stringDuration = state.getLines()[0].strip();
                    try {
                        sellMenu.duration = DurationUtils.fromString(stringDuration);
                    } catch (IllegalArgumentException ignored) {
//                        Bukkit.getLogger().warning(ignored.toString());
                    }
                    // Needed to run task synchronously as otherwise Bukkit throws an error. Why does this work in
                    // AuctionTextSortElement but not here?
                    Bukkit.getScheduler().runTask(SuperAuctionHouse.getInstance(), () -> {
                        sellMenu.getGui().close();
                        sellMenu = new SellItemMenu(sellMenu);
                        sellMenu.open(false);
                        sellMenu.updateConfirmElement();
                        sellMenu.drawInventory();
                    });
                })
                .withLines(
                        "",
                        "^^^^^^^^^^^^^^^^",
                        "Enter duration",
                        ""
                );
    }
}
