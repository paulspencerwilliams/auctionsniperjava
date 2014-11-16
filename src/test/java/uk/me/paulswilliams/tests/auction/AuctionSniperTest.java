package uk.me.paulswilliams.tests.auction;

import org.junit.Test;
import uk.me.paulswilliams.auction.Auction;
import uk.me.paulswilliams.auction.AuctionEventListener;
import uk.me.paulswilliams.auction.AuctionSniper;
import uk.me.paulswilliams.auction.SniperListener;

import static org.mockito.Mockito.*;
import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource.FromSniper;

public class AuctionSniperTest {
    private final SniperListener sniperListener = mock(SniperListener.class);
    private final Auction auction = mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(auction,
            sniperListener);


    @Test
    public void reportsLostWhenAuctionCloses() {
        sniper.auctionClosed();

        verify(sniperListener).sniperLost();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FromOtherBidder);

        verify(auction).bid(price + increment);
        verify(sniperListener, atLeastOnce()).sniperBidding();
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 45, FromSniper);

        verifyZeroInteractions(auction);
        verify(sniperListener, atLeastOnce()).sniperWinning();
    }
}
