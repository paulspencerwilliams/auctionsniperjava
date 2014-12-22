package uk.me.paulswilliams.auction;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import uk.me.paulswilliams.auction.tests.AuctionSniperDriver;

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