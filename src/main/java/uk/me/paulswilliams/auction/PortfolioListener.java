package uk.me.paulswilliams.auction;

import java.util.EventListener;

public interface PortfolioListener extends EventListener {
    void sniperAdded(AuctionSniper sniper);
}
