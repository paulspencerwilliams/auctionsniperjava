package uk.me.paulswilliams.auction.xmpp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import uk.me.paulswilliams.auction.Auction;
import uk.me.paulswilliams.auction.AuctionHouse;
import uk.me.paulswilliams.auction.Item;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.apache.commons.io.FilenameUtils.getFullPath;

public class XMPPAuctionHouse implements AuctionHouse {
    public static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    private static final String LOG_FILE_NAME = "auction-sniper.log";
    private static final java.lang.String LOGGER_NAME = "auction-sniper";
    private static XMPPFailureReporter failureReporter;

    private final XMPPConnection connection;

    public static XMPPAuctionHouse connect(String hostname, String username, String password) throws IOException, XMPPException, SmackException, XMPPAuctionException {
        ConnectionConfiguration connectionConfig = new
                ConnectionConfiguration(hostname, 5222);
        connectionConfig.setSecurityMode(
                ConnectionConfiguration.SecurityMode.required);
        System.setProperty("javax.net.ssl.trustStore",
                "certificates/akeystore.jks");
        XMPPConnection connection = new XMPPTCPConnection(connectionConfig);
        failureReporter = new LoggingXMPPFailureReporter(makeLogger());
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return new XMPPAuctionHouse(connection);
    }

    private static Logger makeLogger() throws XMPPAuctionException {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }

    private static FileHandler simpleFileHandler() throws XMPPAuctionException {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        }
        catch (Exception e) {
            throw new XMPPAuctionException("Could not create logger FileHandler" + getFullPath(LOG_FILE_NAME), e);
        }
    }

    public XMPPAuctionHouse(XMPPConnection connection) {
        this.connection = connection;
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, auctionId(item.identifier, connection), failureReporter);
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
