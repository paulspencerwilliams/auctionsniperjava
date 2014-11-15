package uk.me.paulswilliams.auction.fakes;

import java.io.IOException;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import static java.lang.String.format;

public class FakeAuctionServer {
	public static final String XMPP_HOSTNAME = "localhost";

	private static final String ITEM_ID_AS_LOGIN = "arthur";

	private static final String AUCTION_PASSWORD = "secr3t";

	private static final String AUCTION_RESOURCE = "Auction";

	private XMPPConnection connection;

	private String itemId;

	private Chat currentChat;

	private final SingleMessageListener messageListener = new SingleMessageListener();

	public FakeAuctionServer(String itemId) {
		this.itemId = itemId;
		ConnectionConfiguration connectionConfig = new ConnectionConfiguration(XMPP_HOSTNAME, 5222);
		connectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
		System.setProperty("javax.net.ssl.trustStore", "certificates/akeystore.jks");
		this.connection = new XMPPTCPConnection(connectionConfig);
	}

	public void startSellingItem() throws XMPPException, IOException, SmackException {
		this.connection.connect();
		this.connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);

		ChatManager chatManager = ChatManager.getInstanceFor(this.connection);
		chatManager.addChatListener(new ChatManagerListener() {
			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				currentChat = chat;
				chat.addMessageListener(messageListener);
			}
		});
	}

	public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
		messageListener.receivesAMessage();
	}

	public void announceClosed() throws SmackException.NotConnectedException {
		currentChat.sendMessage(new Message());
	}

	public void stop() throws SmackException.NotConnectedException {
		connection.disconnect();
	}

	public String getItemId() {
	return itemId;
	}
}
