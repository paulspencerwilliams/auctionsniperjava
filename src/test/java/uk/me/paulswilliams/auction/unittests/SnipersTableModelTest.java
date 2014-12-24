package uk.me.paulswilliams.auction.unittests;


import com.objogate.exception.Defect;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import uk.me.paulswilliams.auction.AuctionSniper;
import uk.me.paulswilliams.auction.userinterface.Column;
import uk.me.paulswilliams.auction.SniperSnapshot;
import uk.me.paulswilliams.auction.userinterface.SnipersTableModel;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class SnipersTableModelTest {

    private TableModelListener listener = mock(TableModelListener.class);
    private AuctionSniper sniper = new AuctionSniper("item id", null);
    private SnipersTableModel model = new SnipersTableModel();

    @Before
    public void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    public void setsSniperValuesInColumns(){
        SniperSnapshot bidding = sniper.getSnapshot().bidding(555, 666);

        model.sniperAdded(sniper);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
        verify(listener, times(1)).tableChanged(argThat(aChangeInRow(0)));
    }

    @Test
    public void notifiesListenersWhenAddingASniper() {
        model.sniperAdded(sniper);
        verify(listener, times(1)).tableChanged(argThat(anInsertionAtRow(0)));
        assertThat(model.getRowCount(), equalTo(1));
        assertRowMatchesSnapshot(0, sniper.getSnapshot());
    }

    @Test
    public void holdsSnipersInAdditionOrder() {
        model.sniperAdded(new AuctionSniper("item 0", null));
        model.sniperAdded(new AuctionSniper("item 1", null));

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    @Test(expected = Defect.class)
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        model.sniperAdded(sniper);
        model.sniperStateChanged(SniperSnapshot.joining("another item id"));
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
        assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
        assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
        assertEquals(SnipersTableModel.textFor(snapshot.state), cellValue(row, Column.SNIPER_STATE));
    }

    private Matcher<TableModelEvent> anInsertionAtRow(final int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> aChangeInRow(int i) {
        return samePropertyValuesAs(new TableModelEvent(model, TableModelEvent.UPDATE));
    }

    private Object cellValue(int rowIndex, Column column) {
        return model.getValueAt(rowIndex, column.ordinal());
    }

    @Test
    public void setUpColumnsHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    private Matcher<TableModelEvent> aRowChangedEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0));
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        final int columnIndex = column.ordinal();

        assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
    }

}