package uk.me.paulswilliams.auction;

import org.junit.After;
import org.junit.Test;
import uk.me.paulswilliams.auction.fakes.FakeAuctionServer;
import uk.me.paulswilliams.auction.helpers.ApplicationRunner;

public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFromSniper();
        auction.announceClosed();
        application.showsSniperHasLostAuction();
    }

    @After
    public void stopAuction() {
        auction.stop();
    }

    @After
    public void stopApplication() {

    }
}
