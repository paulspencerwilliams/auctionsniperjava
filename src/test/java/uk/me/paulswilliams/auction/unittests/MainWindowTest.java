package uk.me.paulswilliams.auction.unittests;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import uk.me.paulswilliams.auction.userinterface.MainWindow;
import uk.me.paulswilliams.auction.userinterface.SnipersTableModel;
import uk.me.paulswilliams.auction.UserRequestListener;
import uk.me.paulswilliams.auction.supporting.AuctionSniperDriver;

import static org.hamcrest.CoreMatchers.equalTo;

public class MainWindowTest {

    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe
                = new ValueMatcherProbe<String>(equalTo("an item-id"), "join request");
        mainWindow.addUserRequestListener(
                new UserRequestListener() {
                    public void joinAuction(String itemId) {
                        buttonProbe.setReceivedValue(itemId);
                    }
                });

        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }

}