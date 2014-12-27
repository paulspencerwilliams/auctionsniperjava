package uk.me.paulswilliams.auction.supporting;

import uk.me.paulswilliams.auction.Main;
import uk.me.paulswilliams.auction.SniperState;
import uk.me.paulswilliams.auction.userinterface.MainWindow;

import static java.lang.String.format;
import static uk.me.paulswilliams.auction.supporting.FakeAuctionServer.XMPP_HOSTNAME;
import static uk.me.paulswilliams.auction.SniperState.*;
import static uk.me.paulswilliams.auction.userinterface.SnipersTableModel.textFor;

public class ApplicationRunner {

    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "secr3t";
    public static final String SNIPER_XMPP_ID =
            format("%s@%s/Auction", SNIPER_ID, FakeAuctionServer.XMPP_HOSTNAME);
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        startSniper();
        for (FakeAuctionServer auction: auctions) {
            bidForItem(auction, Integer.MAX_VALUE);
        }
    }

    private void bidForItem(FakeAuctionServer auction, int stopPrice) {
        final String itemId = auction.getItemId();
        driver.startBiddingFor(itemId, stopPrice);
        driver.showsSniperStatus(itemId, 0, 0, textFor(JOINING));
    }

    public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
        startSniper();
        bidForItem(auction, stopPrice);
    }

    private void startSniper() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void showsSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(LOST));
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(BIDDING));
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(WINNING)); }

    public void showsSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(WON)); }

    public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));
    }

    public void showsSniperHasFailed(FakeAuctionServer auction) {
        driver.showsSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.FAILED));
    }

    public void reportsInvalidMessage(FakeAuctionServer auction, String brokenMessage) {

    }
}
