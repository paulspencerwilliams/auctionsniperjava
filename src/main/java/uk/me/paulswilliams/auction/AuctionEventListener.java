package uk.me.paulswilliams.auction;

import java.util.EventListener;

public interface AuctionEventListener extends EventListener {
    void auctionFailed();

    enum PriceSource { FromSniper, FromOtherBidder;}

    void auctionClosed();

    void currentPrice(int price, int increment, PriceSource priceSource);
}
