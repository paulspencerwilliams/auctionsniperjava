package uk.me.paulswilliams.auction;

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
