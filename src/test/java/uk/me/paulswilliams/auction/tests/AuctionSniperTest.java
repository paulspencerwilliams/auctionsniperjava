package uk.me.paulswilliams.auction.tests;

import org.junit.Test;
import uk.me.paulswilliams.auction.Auction;
import uk.me.paulswilliams.auction.AuctionSniper;
import uk.me.paulswilliams.auction.SniperListener;

import static org.mockito.Mockito.*;

public class AuctionSniperTest {
    private SniperListener sniperListener = mock(SniperListener.class);
    private Auction auction = mock(Auction.class);
    private AuctionSniper sniper = new AuctionSniper(auction, sniperListener);

    @Test
    public void reportsLostWhenAuctionCloses() {
        sniper.auctionClosed();
        verify(sniperListener, times(1)).sniperLost();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment);

        verify(auction, times(1)).bid(price + increment);
        verify(sniperListener, atLeastOnce()).sniperBidding();
    }
}
