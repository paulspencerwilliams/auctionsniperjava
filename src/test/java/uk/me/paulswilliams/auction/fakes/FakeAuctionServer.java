package uk.me.paulswilliams.auction.fakes;

import java.io.IOException;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import uk.me.paulswilliams.auction.Main;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.me.paulswilliams.auction.Main.BID_COMMAND_FORMAT;

public class FakeAuctionServer {
	public static final String XMPP_HOSTNAME = "localhost";
	private static final String ITEM_ID_AS_LOGIN = "auction-%s";
	private static final String AUCTION_PASSWORD = "secr3t";
	private static final String AUCTION_RESOURCE = "Auction";

	private String itemId;
	private XMPPConnection connection;
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

	public void hasReceivedJoinRequestFrom(String sniperId) throws
			InterruptedException {
		receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
	}

	private void receivesAMessageMatching(String sniperId, Matcher<? super
			String> messageMatcher) throws InterruptedException {
		messageListener.receivesAMessage(messageMatcher);
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}

	public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
		receivesAMessageMatching(sniperId, equalTo(format(
				BID_COMMAND_FORMAT, bid)));
	}

	public void announceClosed() throws SmackException.NotConnectedException, XMPPException {
		currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
	}

	public void stop() throws SmackException.NotConnectedException {
		connection.disconnect();
	}

	public String getItemId() {
		return itemId;
	}

	public void reportPrice(int price, int increment, String bidder) throws XMPPException, SmackException.NotConnectedException {
		currentChat.sendMessage(
				String.format("SOLVersion: 1.1; Event: PRICE; "
								+ "CurrentPrice: %d; Increment: %d;"
								+ "Bidder: %s;",
								price, increment, bidder));
	}


}
