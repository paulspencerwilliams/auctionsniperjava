package uk.me.paulswilliams.auction.fakes;

import eu.geekplace.javapinning.JavaPinning;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.parsing.ExceptionLoggingCallback;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.sasl.SaslException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

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

        try {
			ConnectionConfiguration connectionConfig = new ConnectionConfiguration(XMPP_HOSTNAME, 5222);
			connectionConfig.setSecurityMode(ConnectionConfiguration.SecurityMode.required);

			System.setProperty("javax.net.debug", "all");

			SSLContext pinnedContext = JavaPinning.forPin("SHA256:533C5B11B4AD3EAFCB9BC121C1352AC141A81678AD5F22124A195980708AAD67");
			connectionConfig.setCustomSSLContext(pinnedContext);


            this.connection = new XMPPTCPConnection(connectionConfig);
            this.connection.connect();
            this.connection.login(format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("connection failed");
        }

	}

	public void startSellingItem() throws XMPPException, IOException, SmackException {

		// connection.getChatManager().addChatListener(
		// new ChatManagerListener() {
		// @Override
		// public void chatCreated(Chat chat, boolean createdLocally) {
		// currentChat = chat;
		// chat.addMessageListener(messageListener);
		// }
		// }
		// );
	}

	public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
		messageListener.receivesAMessage();
	}

	public void announceClosed() throws XMPPException {
		// currentChat.sendMessage(new Message());
	}

	public void stop() {
		// connection.disconnect();
	}

	public String getItemId() {
		return itemId;
	}
}
