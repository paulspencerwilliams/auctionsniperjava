package uk.me.paulswilliams.auction;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

import static java.lang.String.format;

public class FakeAuctionServer {
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String AUCTION_PASSWORD = "secr3t";

    private final String itemId;
    private final XMPPConnection connection;
    private Chat currentChat;
    private SingleMessageListener messageListener = new SingleMessageListener();

    public FakeAuctionServer(String itemId) {
        this.itemId = itemId;
        ConnectionConfiguration connectionConfig = new ConnectionConfiguration(XMPP_HOSTNAME, 5222);
        connectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        System.setProperty("javax.net.ssl.trustStore", "certificates/akeystore.jks");
        this.connection = new XMPPTCPConnection(connectionConfig);
    }

    public void startSellingItem() throws IOException, XMPPException, SmackException {
        connection.connect();
        connection.login(format(ITEM_ID_AS_LOGIN, itemId),
                AUCTION_PASSWORD, AUCTION_RESOURCE);

        ChatManager.getInstanceFor(this.connection).addChatListener(
                new ChatManagerListener() {
                    @Override
                    public void chatCreated(Chat chat, boolean createdLocally) {
                        currentChat = chat;
                        chat.addMessageListener(messageListener);
                    }
                }
        );
    }

    public String getItemId() {
        return itemId;
    }

    public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
        messageListener.receivesAMessage();
    }

    public void annouceClosed() throws SmackException.NotConnectedException {
        currentChat.sendMessage(new Message());
    }

    public void stop() throws SmackException.NotConnectedException {
        connection.disconnect();
    }
}
