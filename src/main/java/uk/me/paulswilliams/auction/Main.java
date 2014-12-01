package uk.me.paulswilliams.auction;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import javax.swing.SwingUtilities;
import java.io.IOException;

public class Main {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;
    private static final String AUCTION_RESOURCE = "Auction";
    private static final String ITEM_ID_AS_LOGIN = "auction-%s";
    private static final String AUCTION_ID_FORMAT
            = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {

            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    public static void main(final String ... args) throws Exception {
        Main main = new Main();
        main.joinAuction(connection(
                        args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
                        args[ARG_ITEM_ID]);
    }

    private void joinAuction(XMPPConnection connection, String itemId)
            throws IOException, XMPPException, SmackException {
        Chat chat = ChatManager.getInstanceFor(connection).createChat(
                auctionId(itemId, connection),
                new MessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                ui.showStatus(MainWindow.STATUS_LOST);
                            }
                        });
                    }
                }
        );
        chat.sendMessage(new Message());
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(AUCTION_ID_FORMAT,
                itemId,
                connection.getServiceName());
    }

    private static XMPPConnection connection(String hostname, String username, String password)
            throws IOException, XMPPException, SmackException {
        ConnectionConfiguration connectionConfig = new
                ConnectionConfiguration(hostname, 5222);
        connectionConfig.setSecurityMode(
                ConnectionConfiguration.SecurityMode.required);
        System.setProperty("javax.net.ssl.trustStore",
                "certificates/akeystore.jks");
        XMPPConnection connection = new XMPPTCPConnection(connectionConfig);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }
}
