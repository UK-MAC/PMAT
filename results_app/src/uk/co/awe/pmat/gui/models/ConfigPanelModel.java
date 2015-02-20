package uk.co.awe.pmat.gui.models;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.ComboBoxModel;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.ApplicationException;
import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.datafiles.ConfigData;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.MetaData.Type;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * The model behind the configuration panel which is used to allow the user to
 * set configuration data which is added to the runs before they are saved into
 * the database.
 *
 * @author AWE Plc copyright 2013
 */
public final class ConfigPanelModel implements ConfigData {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigPanelModel.class);

    private static final ComboBoxModel<MetaData> NULL_MODEL = new DefaultComboBoxModel<>();
    private static final int APP_GUESS_THRESHOLD = 3;

    private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);
    private final Map<Run, RunModels> runModelsMap = new HashMap<>();
    private final String defaultUser;
    private final List<Run> selectedRuns = new ArrayList<>();
    
    private class RunModels {
        private final Map<MetaData.Type, DefaultComboBoxModel<MetaData>> metaDataModels
                = new EnumMap<>(MetaData.Type.class);
        private String appNameSummary = null;
        private boolean dataIsPrivate = true;
        private String user = null;
        private String tag = null;
        
        private class MetaDataSelectionAction extends AbstractAction {

            private final MetaData.Type type;

            public MetaDataSelectionAction(Type type) {
                this.type = type;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                final MetaData selectedItem = metaDataModels.get(type).getSelectedElement();
                for (Run run : selectedRuns) {
                    final RunModels models = runModelsMap.get(run);
                    if (models != RunModels.this) {
                        models.metaDataModels.get(type).setSelectedElement(selectedItem);
                    }
                }
            }
        }

        RunModels() {
            for (MetaData.Type type : MetaData.Type.values()) {
                metaDataModels.put(type, new DefaultComboBoxModel<MetaData>("Select " + type.displayName()));
            }
            
            final DefaultComboBoxModel<MetaData> applicationsModel = metaDataModels.get(MetaData.Type.APPLICATION);
            applicationsModel.setSelectionAction(new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final MetaData selectedItem = metaDataModels
                            .get(MetaData.Type.APPLICATION).getSelectedElement();
                    final Boolean dataPrivate = (selectedItem == null)
                            ? false
                            : (Boolean) selectedItem.getData("dataPrivate");
                    for (Run run : selectedRuns) {
                        final RunModels models = runModelsMap.get(run);
                        if (models != RunModels.this) {
                            models.metaDataModels.get(MetaData.Type.APPLICATION)
                                    .setSelectedElement(selectedItem);
                            models.dataIsPrivate = dataPrivate;
                        }
                    }
                    dataIsPrivate = dataPrivate;
                    eventSupport.fireChangeEvent();
                }
            });
            
            for (MetaData.Type type : MetaData.Type.values()) {
                if (type == MetaData.Type.APPLICATION) { continue; }
                metaDataModels.get(type).setSelectionAction(new MetaDataSelectionAction(type));
            }
        }
    }

    /**
     * Creates a new {@code ConfigPanelModel}.
     *
     * @param config the application configuration.
     */
    public ConfigPanelModel(Configuration config) {
        defaultUser = config.getProperty(Configuration.Key.NAME_IN_DATABASE);
    }

    /**
     * Clear all the models behind the panel.
     */
    public void clear() {
        runModelsMap.clear();

        eventSupport.fireChangeEvent();
    }

    @Override
    public void loadData(List<DataFile<?>> dataFiles) throws DatabaseException {
        
        for (DataFile<?> dataFile : dataFiles) {
            final RunModels runModels = new RunModels();
            runModels.appNameSummary = dataFile.getApplicationName();
            runModels.dataIsPrivate = dataFile.getDataIsPrivate();
            runModels.user = dataFile.getUser() == null
                ? defaultUser
                : dataFile.getUser();
            runModels.tag = dataFile.getTag();
            
            final Run run = dataFile.asRun();
            
            for (MetaData.Type type : MetaData.Type.values()) {
                updateModel(run, type, runModels.metaDataModels.get(type));
            }
            
            
            final DefaultComboBoxModel<MetaData> appModel
                    = runModels.metaDataModels.get(MetaData.Type.APPLICATION);
            if (appModel.getSelectedElement() == null) {
                final String appName = dataFile.getApplicationName();
                final String fileName = new File(dataFile.asRun().getFile()).getName();
                guessApplication(appModel, fileName, appName);
            }
            
            runModelsMap.put(dataFile.asRun(), runModels);
        }
    }

    @Override
    public Run populateRunData(Run run) throws ApplicationException {
        
        final RunModels runModels = runModelsMap.get(run);
        
        for (MetaData.Type type : MetaData.Type.values()) {
            final MetaData metaData = runModels.metaDataModels.get(type).getSelectedElement();
            if (metaData == null) {
                throw new ApplicationException("No " + type.displayName() + " selected");
            }
            run.setMetaData(type, metaData);
        }
        return run.update(runModels.user, runModels.dataIsPrivate, runModels.tag);
    }

    /**
     * Returns the application name as loaded from the files.
     *
     * @return the application name.
     */
    public String getApplicationName() {
        if (!selectedRuns.isEmpty() && runModelsMap.containsKey(selectedRuns.get(0))) {
            return runModelsMap.get(selectedRuns.get(0)).appNameSummary; 
        }else {
            return null;
        }
    }
    
    public String getTag() {
        if (!selectedRuns.isEmpty() && runModelsMap.containsKey(selectedRuns.get(0))) {
            return runModelsMap.get(selectedRuns.get(0)).tag; 
        }else {
            return null;
        }
    }
    
    private void updateModel(Run run, MetaData.Type type, DefaultComboBoxModel<MetaData> model) throws DatabaseException {
        model.removeAllElements();
        final List<MetaData> meta = DatabaseManager.getConnection().getAllMetaData(type);
        if (meta.isEmpty()) {
            model.selectHeader();
        } else {
            Collections.sort(meta);
            model.addAllElements(meta);
            model.selectHeader();
        }
        model.setSelectedElement(run.getMetaData(type));
    }
    
    private void guessApplication(DefaultComboBoxModel<MetaData> appModel, String fileName, String appName) {
        
        int bestSoFar = 0;
        MetaData foundMatch = null;
        
        for (int idx = 0; idx < appModel.getSize(); ++idx) {
            final MetaData application = appModel.getElement(idx);
            int closeness = Math.max(
                    StringUtils.closeness(application.displayName(), fileName), 
                    StringUtils.closeness(application.displayName(), appName));
            if (closeness > APP_GUESS_THRESHOLD && closeness > bestSoFar) {
                foundMatch = application;
                bestSoFar = closeness;
            }
        }
        
        if (foundMatch != null) {
            appModel.setSelectedElement(foundMatch);
        }
    }

    /**
     * Add a {@code ChangeListener} to those to be notified on updates to the
     * model state.
     *
     * @param listener the change listener.
     */
    public void addChangeListener(ChangeListener listener) {
        eventSupport.addChangeListener(listener);
    }
    
    public ComboBoxModel<MetaData> getMetaDataModel(MetaData.Type type) {
        if (!selectedRuns.isEmpty() && runModelsMap.containsKey(selectedRuns.get(0))) {
            return runModelsMap.get(selectedRuns.get(0)).metaDataModels.get(type);
        } else {
            return NULL_MODEL;
        }
    }
    
    /**
     * Returns whether the run is set as private.
     *
     * @return {@code true} if the run should be private, {@code false}
     * otherwise.
     */
    public boolean isPrivate() {
        if (!selectedRuns.isEmpty() && runModelsMap.containsKey(selectedRuns.get(0))) {
            return runModelsMap.get(selectedRuns.get(0)).dataIsPrivate;
        } else {
            return false;
        }
    }

    /**
     * Returns the user being saved against the run.
     *
     * @return the user.
     */
    public String getUser() {
        if (!selectedRuns.isEmpty() && runModelsMap.containsKey(selectedRuns.get(0))) {
            return runModelsMap.get(selectedRuns.get(0)).user;
        } else {
            return null;
        }
    }
    
    public void setUser(String user) {
        for (Run run : selectedRuns) {
            runModelsMap.get(run).user = user;
        }
    }

    public void setTag(String tag) {
        for (Run run : selectedRuns) {
            runModelsMap.get(run).tag = tag;
        }
    }
    
    public void setSelectedRuns(List<Run> runs) {
        selectedRuns.clear();
        selectedRuns.addAll(runs);
        eventSupport.fireChangeEvent();
    }
}
