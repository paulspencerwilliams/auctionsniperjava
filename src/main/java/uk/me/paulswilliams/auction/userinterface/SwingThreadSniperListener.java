package uk.me.paulswilliams.auction.userinterface;

import uk.me.paulswilliams.auction.SniperListener;
import uk.me.paulswilliams.auction.SniperSnapshot;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {

    private final SniperListener snipers;

    public SwingThreadSniperListener(SniperListener snipers) {
        this.snipers = snipers;
    }

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
