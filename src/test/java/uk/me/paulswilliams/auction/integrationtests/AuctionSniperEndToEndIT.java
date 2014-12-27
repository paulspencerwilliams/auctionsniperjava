package uk.me.paulswilliams.auction.integrationtests;

import org.jivesoftware.smack.SmackException;
import org.junit.After;
import org.junit.Test;
import uk.me.paulswilliams.auction.supporting.ApplicationRunner;
import uk.me.paulswilliams.auction.supporting.FakeAuctionServer;

public class AuctionSniperEndToEndIT {
    private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
    private final FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction.annouceClosed();
        application.showsSniperHasLostAuction(auction, 0, 0);
    }

    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.annouceClosed();
        application.showsSniperHasLostAuction(auction, 1000, 1098);
    }

    @Test
    public void sniperWinsAuctionByBiddingHigher() throws Exception {
        auction.startSellingItem();

        application.startBiddingIn(auction);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);

        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(auction, 1098);

        auction.annouceClosed();
        application.showsSniperHasWonAuction(auction, 1098);
    }

    @Test
    public void sniperBidsForMultipleItems() throws Exception {
        auction.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction, auction2);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction2.reportPrice(500, 21, "other bidder");
        auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);

        application.hasShownSniperIsWinning(auction, 1098);
        application.hasShownSniperIsWinning(auction2, 521);

        auction.annouceClosed();
        auction2.annouceClosed();

        application.showsSniperHasWonAuction(auction, 1098);
        application.showsSniperHasWonAuction(auction2, 521);
    }

    @Test
    public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
        auction.startSellingItem();

        application.startBiddingWithStopPrice(auction, 1100);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1000, 98, "other bidder");
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(1197, 10, "third party");
        application.hasShownSniperIsLosing(auction, 1197, 1098);

        auction.reportPrice(1207, 10, "forth party");
        application.hasShownSniperIsLosing(auction, 1207, 1098);

        auction.annouceClosed();
        application.showsSniperHasLostAuction(auction, 1207, 1098);
    }

    @Test
    public void sniperReportsInvalidAuctionMessageAndStopsRespondingToEvents() throws Exception{
        String brokenMessage = "a broken message";
        auction.startSellingItem();
        auction2.startSellingItem();

        application.startBiddingIn(auction, auction2);
        auction.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);

        auction.reportPrice(500, 20, "other bidder");
        auction.hasReceivedBid(520, ApplicationRunner.SNIPER_XMPP_ID);

        auction.sendInvalidMessageContaining(brokenMessage);
        application.showsSniperHasFailed(auction);

        auction.reportPrice(520, 21, "other bidder");
        waitForAnotherAuctionEvent();

        application.reportsInvalidMessage(auction, brokenMessage);
        application.showsSniperHasFailed(auction);
    }

    private void waitForAnotherAuctionEvent() throws Exception {
        auction2.hasReceivedJoinRequestFrom(ApplicationRunner.SNIPER_XMPP_ID);
        auction2.reportPrice(600, 6, "other bidder");
        application.hasShownSniperIsBidding(auction2, 600, 606);
    }

    @After
    public void stopAuction() throws SmackException.NotConnectedException {
        auction.stop();
        auction2.stop();
    }

    @After
    public void stopApplication() {
        application.stop();
    }
}

