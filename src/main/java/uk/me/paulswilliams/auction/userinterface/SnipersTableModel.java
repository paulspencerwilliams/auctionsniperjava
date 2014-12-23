package uk.me.paulswilliams.auction.userinterface;

import com.objogate.exception.Defect;
import uk.me.paulswilliams.auction.AuctionSniper;
import uk.me.paulswilliams.auction.SniperCollector;
import uk.me.paulswilliams.auction.SniperListener;
import uk.me.paulswilliams.auction.SniperSnapshot;
import uk.me.paulswilliams.auction.SniperState;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, SniperCollector {

    private static final String[] STATUS_TEXT = { "Joining", "Bidding", "Winning", "Lost", "Won"};
    private List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();
    private List<AuctionSniper> notToBeGCd = new ArrayList<AuctionSniper>();


    @Override
    public int getRowCount() { return snapshots.size(); }

    @Override
    public int getColumnCount() { return Column.values().length; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (newSnapshot.isForSameAs(snapshots.get(i))) {
                snapshots.set(i, newSnapshot);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        throw new Defect("No existing Sniper state for " + newSnapshot.itemId);
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public void addSniper(AuctionSniper sniper) {
        notToBeGCd.add(sniper);
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(new SwingThreadSniperListener(this));
    }

    private void addSniperSnapshot(SniperSnapshot sniperSnapshot) {
        snapshots.add(sniperSnapshot);
        int row = snapshots.size() -1;
        fireTableRowsInserted(row, row);
    }

}
