package uk.me.paulswilliams.auction;

public class AuctionSniper implements AuctionEventListener {
    private final Item item;
    private final Auction auction;
    private SniperSnapshot snapshot;
    private SniperListener listener;

    public AuctionSniper(Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.identifier);
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                final int bid = price + increment;
                if (item.allowsBid(bid)) {
                    auction.bid(bid);
                    snapshot = snapshot.bidding(price, bid);
                }
                else{
                    snapshot = snapshot.losing(price);
                }
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
