package uk.me.paulswilliams.auction.drivers;

import uk.me.paulswilliams.auction.Main;
import uk.me.paulswilliams.auction.MainWindow;
import uk.me.paulswilliams.auction.fakes.FakeAuctionServer;

import static java.lang.String.format;

public class ApplicationRunner {
    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "secr3t";
    private static final String STATUS_JOINING = "Joining";
    private static final String STATUS_LOST = "Lost";
    public static final String SNIPER_XMPP_ID =
            format("%s@%s/Auction", SNIPER_ID, FakeAuctionServer.XMPP_HOSTNAME);
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try
                {
                    Main.main(FakeAuctionServer.XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(STATUS_JOINING);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
    }

    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(MainWindow.STATUS_WON);
    }
}
