package uk.me.paulswilliams.auction.unittests;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.me.paulswilliams.auction.Auction;
import uk.me.paulswilliams.auction.AuctionSniper;
import uk.me.paulswilliams.auction.Item;
import uk.me.paulswilliams.auction.SniperListener;
import uk.me.paulswilliams.auction.SniperSnapshot;
import uk.me.paulswilliams.auction.SniperState;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.*;
import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource.FromOtherBidder;
import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource.FromSniper;
import static uk.me.paulswilliams.auction.SniperState.*;

public class AuctionSniperTest {
    private static final String ITEM_ID = "54321";
    private SniperListener sniperListener = mock(SniperListener.class);
    private Auction auction = mock(Auction.class);
    private Item item = new Item(ITEM_ID, 567);
    private AuctionSniper sniper = new AuctionSniper(item, auction);

    @Before
    public void addSniperListener() {
        sniper.addSniperListener(sniperListener);
    }

    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        sniper.auctionClosed();
        verify(sniperListener, atLeastOnce()).sniperStateChanged(argThat(isASniperThatIs(LOST)));
    }

    @Test
    @Ignore
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
