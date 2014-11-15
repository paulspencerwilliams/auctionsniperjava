package uk.me.paulswilliams.auction;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class Main {

    public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
    public static final String SNIPER_STATUS_NAME = "sniper";
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main (String ...args) throws Exception {
        Main main = new Main();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}
