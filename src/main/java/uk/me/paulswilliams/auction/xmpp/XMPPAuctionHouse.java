package uk.me.paulswilliams.auction.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import uk.me.paulswilliams.auction.Auction;
import uk.me.paulswilliams.auction.AuctionHouse;

import java.io.IOException;

public class XMPPAuctionHouse implements AuctionHouse {
    public static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    private final XMPPConnection connection;

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws IOException, XMPPException, SmackException {
        ConnectionConfiguration connectionConfig = new
                ConnectionConfiguration(hostname, 5222);
        connectionConfig.setSecurityMode(
                ConnectionConfiguration.SecurityMode.required);
        System.setProperty("javax.net.ssl.trustStore",
                "certificates/akeystore.jks");
        XMPPConnection connection = new XMPPTCPConnection(connectionConfig);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return new XMPPAuctionHouse(connection);
    }

    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }

    @Override
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, auctionId(itemId, connection));
    }

    public void disconnect() throws SmackException.NotConnectedException {
        connection.disconnect();
    }

    private String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT,
                itemId,
                connection.getServiceName());
    }
}
