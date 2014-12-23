package uk.me.paulswilliams.auction;

public interface Auction {
    void bid(int amount);

    void join();

    void addAuctionEventListener(AuctionEventListener auctionEventListener);
}
