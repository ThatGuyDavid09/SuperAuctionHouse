package thatguydavid09.superauctionhouse.menus.auctionhouse;

import com.google.gson.InstanceCreator;
import org.bukkit.inventory.ItemStack;
import thatguydavid09.superauctionhouse.AuctionItem;

import java.lang.reflect.Type;

public class AuctionItemInstanceCreator implements InstanceCreator<AuctionItem> {
    private final AuctionItem auctionItem;

    public AuctionItemInstanceCreator(AuctionItem auctionItem) {
        this.auctionItem = auctionItem;
    }

    @Override
    public AuctionItem createInstance(Type type) {
        AuctionItem item = new AuctionItem(auctionItem);
        return item;
    }
}
