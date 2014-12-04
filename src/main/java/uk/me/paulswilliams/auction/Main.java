package uk.me.paulswilliams.auction;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Join;";
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: Bid; Price: %d;";
    private MainWindow ui;
    private Chat notToBeGCd;

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

        disconnectWhenUICloses(connection);
        final Chat chat = ChatManager.getInstanceFor(connection).createChat(
                auctionId(itemId, connection), null);
        this.notToBeGCd = chat;
        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(
                new AuctionMessageTranslator(
                        connection.getUser(),
                        new AuctionSniper(auction, new SniperStateDisplayer())));
        auction.join();
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    connection.disconnect();
                } catch (SmackException.NotConnectedException e1) {
                    e1.printStackTrace();
                }
            }
        });
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

    private static class XMPPAuction implements Auction {
        private final Chat chat;

        public XMPPAuction(Chat chat) {
            this.chat = chat;
        }

        @Override
        public void bid(int amount) {
            sendMessage(String.format(BID_COMMAND_FORMAT, amount));
        }

        @Override
        public void join() {
            sendMessage(JOIN_COMMAND_FORMAT);
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

    public class SniperStateDisplayer implements SniperListener {
        @Override
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }

        @Override
        public void sniperBidding() {
            showStatus(MainWindow.STATUS_BIDDING);

        }

        @Override
        public void sniperWinning() {
            showStatus(MainWindow.STATUS_WINNING);
        }

        @Override
        public void sniperWon() {
            showStatus(MainWindow.STATUS_WON);
        }

        private void showStatus(final String status) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ui.showStatus(status);
                }
            });
        }
    }
}
