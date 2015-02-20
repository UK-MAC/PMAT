package uk.co.awe.pmat.gui.utils;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JTable;

/**
 * An implementation of the {@link java.awt.event.MouseListener} interface which
 * allows for the monitoring of clicks on table rows and triggers an
 * {@link Action} when it detects a "double click".
 *
 * @author AWE Plc copyright 2013
 */
public final class DoubleClickListener extends MouseAdapter {

    private static final long DOUBLE_CLICK_INTERVAL = 500; // ms

    private final JTable table;
    private final Action doubleClickAction;

    private final Map<Integer, Long> lastClicked = new HashMap<>();

    /**
     * Create a new {@code DoubleClickListener}.
     *
     * @param table the table whose rows we are monitoring.
     * @param doubleClickAction the action to perform on a double click.
     */
    public DoubleClickListener(JTable table, Action doubleClickAction) {
        this.table = table;
        this.doubleClickAction = doubleClickAction;
    }

    @Override
    public void mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1) {
            final int tableRow = table.rowAtPoint(evt.getPoint());
            final long clickTime = System.currentTimeMillis();

            if (lastClicked.containsKey(tableRow)
                    && (clickTime - lastClicked.get(tableRow)) < DOUBLE_CLICK_INTERVAL) {
                ActionEvent ae = new ActionEvent(table,
                    ActionEvent.ACTION_PERFORMED, "doubleClicked");
                doubleClickAction.actionPerformed(ae);
                lastClicked.put(tableRow, 0L);
            } else {
                lastClicked.put(tableRow, clickTime);
            }
        }
    }

}
