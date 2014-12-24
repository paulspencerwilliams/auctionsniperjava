package uk.me.paulswilliams.auction.unittests;

import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.junit.Test;
import uk.me.paulswilliams.auction.Item;
import uk.me.paulswilliams.auction.SniperPortfolio;
import uk.me.paulswilliams.auction.userinterface.MainWindow;
import uk.me.paulswilliams.auction.userinterface.SnipersTableModel;
import uk.me.paulswilliams.auction.UserRequestListener;
import uk.me.paulswilliams.auction.supporting.AuctionSniperDriver;

import static org.hamcrest.CoreMatchers.equalTo;

public class MainWindowTest {

    private final SnipersTableModel tableModel = new SnipersTableModel();
    private SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow mainWindow = new MainWindow(portfolio);
    private AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<Item> itemProbe
                = new ValueMatcherProbe<Item>(equalTo(new Item("an item-id", 789)), "item request");
        mainWindow.addUserRequestListener(
                new UserRequestListener() {
                    public void joinAuction(Item item) {
                        itemProbe.setReceivedValue(item);
                    }
                });

        driver.startBiddingFor("an item-id", 789 );
        driver.check(itemProbe);
    }

}