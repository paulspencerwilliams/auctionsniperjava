package uk.me.paulswilliams.auction;

import uk.me.paulswilliams.auction.userinterface.SwingThreadSniperListener;
import uk.me.paulswilliams.auction.xmpp.XMPPAuctionHouse;

public class SniperLauncher implements UserRequestListener {
    private final XMPPAuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(SniperCollector snipers, XMPPAuctionHouse auctionHouse) {
        this.auctionHouse = auctionHouse;
        this.collector = snipers;
    }

    @Override
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }

}
