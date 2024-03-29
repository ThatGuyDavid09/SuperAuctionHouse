package com.highmarsorbit.superauctionhouse.elements;

import com.highmarsorbit.superauctionhouse.SuperAuctionHouse;
import com.highmarsorbit.superauctionhouse.inventories.AuctionBrowserMenu;
import com.highmarsorbit.superauctionhouse.util.AuctionSortState;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import fr.cleymax.signgui.SignGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AuctionTextSortElement extends BaseElement {
//    private AnvilGUI.Builder inputGui;
    private SignGUI signGui;
    private AuctionBrowserMenu ahRef;
    public AuctionTextSortElement(char character, InventoryGui gui, AuctionBrowserMenu ahRef) {
        super(character, gui);
        this.ahRef = ahRef;

        createSignGui();
        element = new DynamicGuiElement(character, () -> new StaticGuiElement(character, new ItemStack(Material.OAK_SIGN),
                click -> {
                    gui.playClickSound();
//                    inputGui.open((Player) click.getWhoClicked());
                    signGui.open((Player) click.getWhoClicked());
                    return true;
                },
                ChatColor.RESET + "" + ChatColor.BLUE + "Search with text: " + ChatColor.AQUA + ahRef.sortState.textFilter,
                " ",
                ChatColor.RESET + "" + ChatColor.YELLOW + "Click to search items by title!"));
    }

    private void createSignGui() {
        signGui = new SignGUI(SuperAuctionHouse.getInstance().getSignGuiManager(),
                state -> {
//                    Bukkit.getLogger().info(String.valueOf(state.getLines().getClass()));
//                    Bukkit.getLogger().info(String.valueOf(state.getPlayer().getClass()));
//                    Bukkit.getLogger().info(String.valueOf(state.getLocation().getClass()));
//                    ahRef.getGui().playClickSound();
//                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 1F);
                    ahRef.sortState.textFilter = state.getLines()[0].strip();
                    AuctionSortState newState = ahRef.sortState;
                    ahRef = new AuctionBrowserMenu(state.getPlayer(), SuperAuctionHouse.getMessages().getMessage("ah_title"));
                    ahRef.sortState = newState;
                    ahRef.recreateElements();
                    ahRef.recreateTextSortElement();
                    ahRef.open(false);
//                    ahRef.recreateElements();
//                    ahRef.open(false);
//                    InventoryGui.get(state.getPlayer()).draw();
                })
                .withLines(
                        "",
                        "^^^^^^^^^^^^^^^^",
                        "Enter the text you",
                        "wish to search by"
                );
//        inputGui = new AnvilGUI.Builder()
//                .onClose(stateSnapshot -> {
//                    Bukkit.getLogger().info("TEST filtertext " + stateSnapshot.getText());
//                    Player player = stateSnapshot.getPlayer();
//                    ahRef.getGui().playClickSound();
////                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1F, 1F);
//                    ahRef.sortState.textFilter = stateSnapshot.getText().replace("\u200B", "").strip();
//                    ahRef.recreateAuctionGroupElement();
//                    ahRef.open(stateSnapshot.getPlayer());
//                })
//                .onClick((slot, stateSnapshot) -> {
//                    if (slot == AnvilGUI.Slot.OUTPUT) {
//                        return List.of(AnvilGUI.ResponseAction.close());
//                    } else {
//                        return Collections.emptyList();
//                    }
//                })
//                // Zero width space to prevent GUI from displaying word "paper" in input slot
//                .text("\u200B")
//                .title("Input search term...")
//                .plugin(SuperAuctionHouse.getInstance());
    }
}
