//Package
package com.petboarding.Utilities;

//Imports
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class SortableTablePanel<T> extends JPanel {

    protected JTable table;
    protected DefaultTableModel model;

    protected int lastSortedModelColumn = -1;
    protected boolean ascending = true;

    protected abstract void sortByColumn(int modelColumn);
    protected abstract void updateColumnHeader();

    protected void attachSortingHeaderListener() {
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int viewColumn = table.columnAtPoint(e.getPoint());
                if (viewColumn < 0) return;

                int modelColumn = table.convertColumnIndexToModel(viewColumn);

                if (modelColumn == lastSortedModelColumn) {
                    ascending = !ascending;
                } else {
                    ascending = true;
                }

                lastSortedModelColumn = modelColumn;

                long start = System.nanoTime();
                sortByColumn(modelColumn);
                updateColumnHeader();
                long end = System.nanoTime();

                double ms = (end - start) / 1_000_000.0;
                String direction = ascending ? "ascending" : "descending";
                String colName = table.getColumnName(viewColumn);

                System.out.println("Sorted by " + colName + " (" + direction + ") in " + ms + " ms");
            }
        });
    }
}