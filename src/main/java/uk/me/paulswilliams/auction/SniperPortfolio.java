package uk.me.paulswilliams.auction;

import java.util.ArrayList;
import java.util.List;

public class SniperPortfolio implements SniperCollector {
    private final Announcer<PortfolioListener> announcer = Announcer.to(PortfolioListener.class);
    private List<AuctionSniper> snipers = new ArrayList<AuctionSniper>();

    public void addPortfolioListener(PortfolioListener listener) {
        announcer.addListener(listener);
    }

    @Override
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        announcer.announce().sniperAdded(sniper);
    }
}
