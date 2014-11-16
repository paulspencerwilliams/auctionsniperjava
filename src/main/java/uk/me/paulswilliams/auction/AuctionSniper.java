package uk.me.paulswilliams.auction;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final SniperListener sniperListener;

    public AuctionSniper(final Auction auction, final SniperListener
            sniperListener) {
        this.auction = auction;
        this.sniperListener = sniperListener;
    }

    @Override
    public void auctionClosed() {
        sniperListener.sniperLost();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource
            priceSource) {
        switch (priceSource) {
            case FromSniper:
                sniperListener.sniperWinning();
                break;
            case FromOtherBidder:
                auction.bid(price + increment);
                sniperListener.sniperBidding();
                break;
        }

    }
}
