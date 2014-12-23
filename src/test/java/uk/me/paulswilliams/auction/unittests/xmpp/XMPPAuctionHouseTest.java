package uk.me.paulswilliams.auction.unittests.xmpp;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.me.paulswilliams.auction.Auction;
import uk.me.paulswilliams.auction.AuctionEventListener;
import uk.me.paulswilliams.auction.supporting.FakeAuctionServer;
import uk.me.paulswilliams.auction.xmpp.XMPPAuctionHouse;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;
import static uk.me.paulswilliams.auction.supporting.ApplicationRunner.*;
import static uk.me.paulswilliams.auction.supporting.FakeAuctionServer.XMPP_HOSTNAME;

public class XMPPAuctionHouseTest {

    private FakeAuctionServer auctionServer = new FakeAuctionServer("item-54321");
    private XMPPAuctionHouse auctionHouse;

    @Before
    public void openConnection() throws Exception {
        auctionHouse = XMPPAuctionHouse.connect(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
    }

    @After
    public void closeConnection() throws Exception{
        auctionHouse.disconnect();
    }

    @Before
    public void startAuction() throws XMPPException, IOException, SmackException {
        auctionServer.startSellingItem();
    }

    @After
    public void stopAuction() throws SmackException.NotConnectedException {
        auctionServer.stop();
    }

    @Test
    public void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = auctionHouse.auctionFor(auctionServer.getItemId());
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auction.join();
        auctionServer.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auctionServer.annouceClosed();
        assertTrue("should have been closed", auctionWasClosed.await(2, TimeUnit.SECONDS));
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource priceSource) {
                //not implemented
            }
        };
    }

}