package uk.co.awe.pmat.gui.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ComboBoxModel;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.Application;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Compiler;
import uk.co.awe.pmat.db.Machine;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Mpi;
import uk.co.awe.pmat.db.OperatingSystem;
import uk.co.awe.pmat.db.Processor;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.gui.utils.DisplayTableModel;
import uk.co.awe.pmat.utils.DefaultHashMap;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * The model underpinning the {@code MetaDataDisplayPanel}. This is used to
 * display the data stored in the run meta data.
 *
 * @author AWE Plc copyright 2013
 */
public final class MetaDataDisplayModel {

    private static final Logger LOG = LoggerFactory.getLogger(MetaDataDisplayModel.class);

    private final Map<MetaData.Type, Map<Long, MetaData>> metaMaps
            = DefaultHashMap.enumMapOfMaps(MetaData.Type.class);
    private final Map<MetaData.Type, DisplayTableModel> tableModels
            = new EnumMap<>(MetaData.Type.class);

    /**
     * Returns a table model displaying all the stored meta-data of a given
     * type.
     *
     * @param type the type of the meta-data to display.
     * @return the table model.
     */
    public DefaultTableModel getTableModel(MetaData.Type type) {
        try {            
            final LinkedHashMap<String, Class<?>> columns = type.getFields();
            final List<String> columnNames = new ArrayList<>(columns.size());
            final List<Class<?>> columnTypes = new ArrayList<>(columns.values());

            for (String name : columns.keySet()) {
                columnNames.add(StringUtils.normaliseCamelCase(name));
            }

            final DisplayTableModel tableModel = new DisplayTableModel(columnNames, columnTypes);
            final Map<Long, MetaData> metaMap = metaMaps.get(type);

            final List<MetaData> metaData = DatabaseManager.getConnection().getAllMetaData(type);
            Collections.sort(metaData);

            Long id = 0L;
            for (MetaData data : metaData) {
                metaMap.put(id, data);
                final List<Object> row = new ArrayList<>(columns.size());
                for (String name : columns.keySet()) {
                    row.add(data.getData(name));
                }
                tableModel.addRow(id, row);
                ++id;
            }

            tableModels.put(type, tableModel);
            return tableModel;
        } catch (RuntimeException ex) {
            throw ex; // Catch everything except RuntimeExceptions
        } catch (Exception ex) {
            LOG.error("Unable to populate view table!", ex);
            return new DefaultTableModel();
        }
    }

    /**
     * Get the meta-data being displayed in a table, for a given row and type.
     *
     * @param type the type of the meta-data.
     * @param rowIdx the corresponding row in the table.
     * @return the meta-data.
     */
    public MetaData getSelectedMetaData(MetaData.Type type, int rowIdx) {
        Long id = tableModels.get(type).getId(rowIdx);
        return metaMaps.get(type).get(id);
    }

    /**
     * Get the list of distinct vendors for the meta-data type as a combo box.
     *
     * @param type the type of the meta-data.
     * @return a combo box model containing the list of vendors.
     * @throws DatabaseException if no connection to the database could be
     * established.
     */
    public ComboBoxModel<?> getVendorList(MetaData.Type type) throws DatabaseException {
        final List<String> vendors = DatabaseManager.getConnection().getMetaDataVendors(type);
        final String name = StringUtils.capitaliseWords(type.toString().toLowerCase());
        vendors.add(0, "New " + name + " vendor");
        return new DefaultComboBoxModel<>(vendors);
    }
}