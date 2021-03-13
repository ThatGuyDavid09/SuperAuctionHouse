package thatguydavid09.superauctionhouse.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import thatguydavid09.superauctionhouse.AuctionItem;

public class PlayerBuyEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final AuctionItem item;
    private final Player buyer;
    private boolean isCancelled;

    public PlayerBuyEvent(AuctionItem boughtItem, Player buyer) {
        item = boughtItem;
        this.buyer = buyer;
        isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return buyer;
    }

    public AuctionItem getItem() {
        return item;
    }
}
