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
        auction.hasReceivedJoinRequestFromSniper();
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

