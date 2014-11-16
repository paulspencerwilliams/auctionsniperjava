package uk.me.paulswilliams.tests.auction;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static uk.me.paulswilliams.auction.AuctionEventListener.PriceSource;

import uk.me.paulswilliams.auction.AuctionEventListener;
import uk.me.paulswilliams.auction.AuctionMessageTranslator;

public class AuctionMessageTranslatorTest {
    public static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "sniper id";
    private final AuctionEventListener listener = mock(AuctionEventListener
            .class);
    private final AuctionMessageTranslator translator =
            new AuctionMessageTranslator(SNIPER_ID, listener);


    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder () {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; " +
                "Increment: 7; Bidder: Someone else;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).currentPrice(192, 7, PriceSource.FromOtherBidder);
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; " +
                "Increment: 7; Bidder: " + SNIPER_ID + ";");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener).currentPrice(192, 7, PriceSource.FromSniper);
    }
}
