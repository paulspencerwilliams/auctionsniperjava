package uk.me.paulswilliams.auction;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import uk.me.paulswilliams.auction.userinterface.MainWindow;
import uk.me.paulswilliams.auction.userinterface.SnipersTableModel;

import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    private List<Auction> notToBeGCd = new ArrayList<Auction>();

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(final String ... args) throws Exception {
        Main main = new Main();
        XMPPConnection connection = connection(
                args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(connection);
        main.addUserRequestListenerFor(connection);
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }



    private void addUserRequestListenerFor(final XMPPConnection connection) {
        ui.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));
                Auction auction = new XMPPAuction(connection, itemId);
                notToBeGCd.add(auction);
                auction.addListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)));
                auction.join();
            }
        });
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
        connection.login(username, password, XMPPAuction.AUCTION_RESOURCE);
        return connection;
    }



}


