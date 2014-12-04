package uk.me.paulswilliams.auction.tests;

import org.jivesoftware.smack.SmackException;
import org.junit.After;
import org.junit.Test;
import uk.me.paulswilliams.auction.FakeAuctionServer;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
        auction.annouceClosed();
        application.showsSniperHasLostAuction();
    }

    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding();

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.annouceClosed();
        application.showsSniperHasLostAuction();
    }

    @After
    public void stopAuction() throws SmackException.NotConnectedException {
        auction.stop();
    }

    @After
    public void stopApplication() {
        application.stop();
    }
}

