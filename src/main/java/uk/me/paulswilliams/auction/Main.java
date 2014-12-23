package uk.me.paulswilliams.auction;

import org.jivesoftware.smack.SmackException;
import uk.me.paulswilliams.auction.userinterface.MainWindow;
import uk.me.paulswilliams.auction.userinterface.SnipersTableModel;
import uk.me.paulswilliams.auction.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
        XMPPAuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);

        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }



    private void addUserRequestListenerFor(final XMPPAuctionHouse auctionHouse) {
        ui.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));
                Auction auction = auctionHouse.auctionFor(itemId);
                notToBeGCd.add(auction);
                auction.addAuctionEventListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener(snipers)));
                auction.join();
            }
        });
    }

    private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    auctionHouse.disconnect();
                } catch (SmackException.NotConnectedException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }
}