package uk.me.paulswilliams.auction.tests;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;
import uk.me.paulswilliams.auction.*;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource.FromOtherBidder;
import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource.FromSniper;
import static uk.me.paulswilliams.auction.SniperState.LOST;
import static uk.me.paulswilliams.auction.SniperState.WINNING;
import static uk.me.paulswilliams.auction.SniperState.WON;

public class AuctionSniperTest {
    private static final String ITEM_ID = "54321";
    private SniperListener sniperListener = mock(SniperListener.class);
    private Auction auction = mock(Auction.class);
    private AuctionSniper sniper = new AuctionSniper(auction, sniperListener, ITEM_ID);

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed();
        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(isASniperThatIs(LOST)));
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        sniper.currentPrice(price, increment, FromOtherBidder);

        verify(auction, times(1)).bid(price + increment);
        verify(sniperListener, atLeastOnce()).sniperStateChanged(
                argThat(equalTo(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING))));
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        sniper.currentPrice(123, 12, FromOtherBidder);

        sniper.currentPrice(135, 45, FromSniper);

        verify(sniperListener, atLeastOnce()).sniperStateChanged(
                argThat(equalTo(new SniperSnapshot(ITEM_ID, 135, 135, WINNING))));
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(isASniperThatIs(LOST)));
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();

        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(isASniperThatIs(WON)));
    }

    private Matcher<SniperSnapshot> isASniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(
                equalTo(state), "sniper that is ", "was") {

            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }



}
