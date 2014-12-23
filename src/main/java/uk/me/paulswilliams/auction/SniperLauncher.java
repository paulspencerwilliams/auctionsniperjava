package uk.me.paulswilliams.auction;

import uk.me.paulswilliams.auction.userinterface.SnipersTableModel;
import uk.me.paulswilliams.auction.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.util.ArrayList;

class SniperLauncher implements UserRequestListener {
    private final XMPPAuctionHouse auctionHouse;
    private final SnipersTableModel snipers;
    private ArrayList<Auction> notToBeGCd = new ArrayList<Auction>();

    public SniperLauncher(SnipersTableModel snipers, XMPPAuctionHouse auctionHouse) {
        this.auctionHouse = auctionHouse;
        this.snipers = snipers;
    }

    @Override
    public void joinAuction(String itemId) {
        snipers.addSniper(SniperSnapshot.joining(itemId));
        Auction auction = auctionHouse.auctionFor(itemId);
        notToBeGCd.add(auction);
        auction.addAuctionEventListener(new AuctionSniper(itemId, auction, new SwingThreadSniperListener()));
        auction.join();
    }

    public class SwingThreadSniperListener implements SniperListener {

        @Override
        public void sniperStateChanged(final SniperSnapshot snapshot) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    snipers.sniperStateChanged(snapshot);
                }
            });
        }

    }
}
