package uk.co.awe.pmat.gui.graph;

import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.Constants;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.utils.FileUtils;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * A panel to display the table of data used in the plotting of the graph, one
 * table per data file written.
 * 
 * @author AWE Plc copyright 2013
 */
public final class GraphDataPanel extends JPanel {

	private static final Logger LOG = LoggerFactory
			.getLogger(GraphDataPanel.class);

	private static final String DEFAULT_FILENAME = Constants.Plot.Data.DEFAULT_FILENAME;
	private static final String CSV_EXTENSION = Constants.Plot.Data.CSV_EXT;

	private final Configuration configuration;

	/**
	 * Create a new {@code GraphDataPanel}.
	 * 
	 * @param dataModel
	 *            the table model containing the data to display.
	 */
	public GraphDataPanel(TableModel dataModel, Configuration configuration) {
		initComponents();

		this.configuration = configuration;
		graphDataTable.setModel(dataModel);
	}

	/**
	 * Save the displayed table data to a file.
	 */
	private void saveData() {

        final String lastFile = configuration.getProperty(Configuration.Key.SAVE_CSV_FILE_PATH);
        if (lastFile != null) {
            saveDataFileChooser.setSelectedFile(new File(lastFile));
        }
        
        final int chooserReturn = saveDataFileChooser.showSaveDialog(this);
        if (chooserReturn != JFileChooser.APPROVE_OPTION) { return; }

        File dataFile = saveDataFileChooser.getSelectedFile();
        if (dataFile != null) {
            configuration.setProperty(Configuration.Key.SAVE_CSV_FILE_PATH, dataFile.getAbsolutePath());
        } else {
            return;
        }
        
        dataFile = FileUtils.getSaveAsFile(dataFile, DEFAULT_FILENAME, CSV_EXTENSION);

        int rowCount = graphDataTable.getRowCount();
        int colCount = graphDataTable.getColumnCount();
        
        final List<String> lines = new ArrayList<>(rowCount + 1);
        
        final List<String> columnNames = new ArrayList<>(colCount);
        for (int colIdx = 0; colIdx < colCount; ++colIdx) {
            columnNames.add(graphDataTable.getColumnName(colIdx));
        }
        lines.add(StringUtils.joinStrings(columnNames, ","));
                
        for (int rowIdx = 0; rowIdx < rowCount; ++rowIdx) {
            StringBuilder row = new StringBuilder();
            String delim = "";
            for (int colIdx = 0; colIdx < colCount; ++colIdx) {
                row.append(delim);
                Object value = graphDataTable.getValueAt(rowIdx, colIdx);
                row.append(value != null ? value.toString() : "");
                delim = ",";
            }
            lines.add(row.toString());
        }

        Writer out = null;

        try {
            out = new BufferedWriter(new FileWriter(dataFile));

            for (String row : lines) {
                out.write(row);
                out.write("\n");
            }
        } catch (IOException ex) {
            ExceptionDialog.showExceptionDialog(ex, "Failed to write data file");
        } finally {
            try {
                if (out != null) { out.close(); }
            } catch (IOException ex) {
                ExceptionDialog.showExceptionDialog(ex, "Failed to close data file");
            }
        }
    }

	/**
	 * This method is called from within the constructor to initialise the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		saveDataMenuItem.setText("Save Data as CSV");
		saveDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				saveDataMenuItemActionPerformed(evt);
			}
		});
		saveDataPopupMenu.add(saveDataMenuItem);

		setLayout(new javax.swing.BoxLayout(this,
				javax.swing.BoxLayout.LINE_AXIS));

		graphDataTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				graphDataTableMouseClicked(evt);
			}
		});
		scrollPanel.setViewportView(graphDataTable);

		add(scrollPanel);
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * The function called when the data table is clicked.
	 * 
	 * @param evt
	 *            the click event.
	 */
	private void graphDataTableMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_graphDataTableMouseClicked
		if (evt.getButton() == MouseEvent.BUTTON3) {
			saveDataPopupMenu.show(this, evt.getX(), evt.getY());
		}
	}// GEN-LAST:event_graphDataTableMouseClicked

	/**
	 * The function called when the "Save Data" menu item is selected.
	 * 
	 * @param evt
	 *            the selection event.
	 */
	private void saveDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveDataMenuItemActionPerformed
		saveData();
	}// GEN-LAST:event_saveDataMenuItemActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JTable graphDataTable = new javax.swing.JTable();
	private final javax.swing.JFileChooser saveDataFileChooser = new javax.swing.JFileChooser();
	private final javax.swing.JMenuItem saveDataMenuItem = new javax.swing.JMenuItem();
	private final javax.swing.JPopupMenu saveDataPopupMenu = new javax.swing.JPopupMenu();
	private final javax.swing.JScrollPane scrollPanel = new javax.swing.JScrollPane();
	// End of variables declaration//GEN-END:variables

}
