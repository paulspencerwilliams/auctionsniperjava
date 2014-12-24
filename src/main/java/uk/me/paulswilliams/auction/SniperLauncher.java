package uk.me.paulswilliams.auction;

import uk.me.paulswilliams.auction.xmpp.XMPPAuctionHouse;

public class SniperLauncher implements UserRequestListener {
    private final XMPPAuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(SniperCollector snipers, XMPPAuctionHouse auctionHouse) {
        this.auctionHouse = auctionHouse;
        this.collector = snipers;
    }

    @Override
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item);
        AuctionSniper sniper = new AuctionSniper(item, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }

}
