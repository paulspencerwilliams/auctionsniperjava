package uk.me.paulswilliams.auction.unittests.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import uk.me.paulswilliams.auction.AuctionEventListener;
import uk.me.paulswilliams.auction.xmpp.AuctionMessageTranslator;
import uk.me.paulswilliams.auction.xmpp.XMPPFailureReporter;

import static java.lang.String.format;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class AuctionMessageTranslatorTest {
    private static final Chat UNUSED_CHAT = null;
    private static final String SNIPER_ID = "sniper_id";
    private AuctionEventListener listener = mock(AuctionEventListener.class);
    private XMPPFailureReporter failureReporter = mock(XMPPFailureReporter.class);
    private AuctionMessageTranslator translator = new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);


    @Test
    public void notifiesAuctionClosedWhenClosedMessageReceived() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).auctionClosed();
    }

    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedOtherBidder() {
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: Someone else;");

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).currentPrice(192, 7, AuctionEventListener.PriceSource.FromOtherBidder);
    }


    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        Message message = new Message();
        message.setBody(format(
                "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; Bidder: %s;", SNIPER_ID));

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).currentPrice(192, 7, AuctionEventListener.PriceSource.FromSniper);
    }

    @Test
    public void notifiesAuctionFailedWhenBadMessageReceived() {
        Message message = new Message();
        String badMessage = "a bad message";
        message.setBody(badMessage);

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).auctionFailed();
        verify(failureReporter).cannotTranslateMessage(eq(SNIPER_ID), eq(badMessage), any(Exception.class));
    }

    @Test
    public void notifiesAuctionFailedWhenEventTypeMissing() {
        Message message = new Message();
        String badMessage = format(
                "SOLVersion: 1.1; CurrentPrice: 192; Increment: 7; Bidder: %s;", SNIPER_ID);
        message.setBody(badMessage);

        translator.processMessage(UNUSED_CHAT, message);

        verify(listener, times(1)).auctionFailed();
        verify(failureReporter).cannotTranslateMessage(eq(SNIPER_ID), eq(badMessage),(any(Exception.class)));
    }

}
