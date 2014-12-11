package uk.me.paulswilliams.auction;

import javax.swing.table.AbstractTableModel;

public class SnipersTableModel extends AbstractTableModel implements SniperListener{

    private static final String[] STATUS_TEXT = { "Joining", "Bidding", "Winning", "Lost", "Won"};
    private SniperSnapshot snapshot;


    @Override
    public int getRowCount() { return 1; }

    @Override
    public int getColumnCount() { return Column.values().length; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        this.snapshot = newSnapshot;
        fireTableRowsUpdated(0, 0);
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }
}
