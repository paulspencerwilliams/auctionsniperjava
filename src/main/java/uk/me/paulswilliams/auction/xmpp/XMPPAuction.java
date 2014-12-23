package uk.me.paulswilliams.auction.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import uk.me.paulswilliams.auction.Announcer;
import uk.me.paulswilliams.auction.Auction;
import uk.me.paulswilliams.auction.AuctionEventListener;

public final class XMPPAuction implements Auction {
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Join;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Bid; Price: %d;";

    private final String itemId;
    Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;

    public XMPPAuction(XMPPConnection connection, String itemId) {
        this.itemId = itemId;
        chat = ChatManager.getInstanceFor(connection).createChat(
                itemId, null);
        chat.addMessageListener(
                new AuctionMessageTranslator(
                        connection.getUser(),
                        auctionEventListeners.announce()));
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
    }

    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}