package uk.me.paulswilliams.auction;

import uk.me.paulswilliams.auction.userinterface.SwingThreadSniperListener;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private SniperSnapshot snapshot;
    private SniperListener listener;

    public AuctionSniper(String itemId, Auction auction) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                final int bid = price + increment;
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
                break;
        }
        notifyChange();
    }

    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    private void notifyChange() {
        listener.sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }

    public void addSniperListener(SniperListener sniperListener) {
        this.listener = sniperListener;
    }
}
