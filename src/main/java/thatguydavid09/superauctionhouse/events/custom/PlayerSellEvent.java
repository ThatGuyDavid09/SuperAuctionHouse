package thatguydavid09.superauctionhouse.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import thatguydavid09.superauctionhouse.AuctionItem;

public class PlayerSellEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private final AuctionItem item;
    private final Player seller;
    private boolean isCancelled;

    public PlayerSellEvent(AuctionItem soldItem, Player seller) {
        item = soldItem;
        this.seller = seller;
        isCancelled = false;
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

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getSeller() {
        return seller;
    }

    public AuctionItem getSoldItem() {
        return item;
    }
}
