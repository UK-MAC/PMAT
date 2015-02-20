package uk.co.awe.pmat.datafiles.skampi;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.datafiles.DataConfig;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import uk.co.awe.pmat.utils.DefaultHashMap;
import uk.co.awe.pmat.utils.DefaultHashMap.Creator;

/**
 * The model driving the SkaMPI results files configuration panel. This is used
 * to provide per file configuration when adding SkaMPI files to the database.
 *
 * @author AWE Plc copyright 2013
 */
public final class SkampiConfigPanelModel implements DataConfig {

    private static final Logger LOG = LoggerFactory.getLogger(SkampiConfigPanelModel.class);

    private static final String COMM_BETWEEN_PARAM_NAME_IN_DB = "communication between";
    private static final String BORDER_TITLE = "Data Specific Configuration";
    private static final String NEW_PARAM_NAME = "Create new parameter...";

    /**
     * A helper class used to hold per file status.
     */
    private final class RunConfig {
        private final DefaultComboBoxModel<String> commBetweenModel
                = new DefaultComboBoxModel<>();
        private final DefaultComboBoxModel<String> resultSetsModel
                = new DefaultComboBoxModel<>();
        private final DefaultComboBoxModel<String> variableRenamesModel
                = new DefaultComboBoxModel<>();

        private final Map<String, DefaultComboBoxModel<String>> resultSetVariablesModels
                = new HashMap<>();
        private final Map<String, Map<String, String>> resultSetVariableRenames
                = DefaultHashMap.mapOfHashMaps();
                //= new HashMap<String, Map<String, String>>();

        /**
         * Create a new {@code RunConfig}.
         */
        RunConfig() {
            variableRenamesModel.setSelectionAction(variableRenamesAction);
            resultSetsModel.setSelectionAction(resultSetsAction);
        }
    }

    private final Map<Integer, RunConfig> runConfigs
            = new DefaultHashMap<>(new Creator<RunConfig>() {
        @Override
        public RunConfig create() {
            return new RunConfig();
        }
    });

    private final Map<String, String> parameters = new HashMap<>();
    private final List<String> userNameChanges = new ArrayList<>();

    private final Action variableRenamesAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addVariableNameChange();
            eventSupport.fireChangeEvent();
        }
    };

    private final Action resultSetsAction = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            updateResultSetVariables(selectedData);
        }
    };

    private final SkampiFactory skampiFactory;
    private final ChangeEventSupport eventSupport;
    
    private Integer selectedData;
    private List<String> variables;

    /**
     * Creates a new {@code SkampiConfigPanelModel}.
     * 
     * @param skampiFactory the factory used to load the run data from the
     * files.
     */
    public SkampiConfigPanelModel(SkampiFactory skampiFactory) {
        this.skampiFactory = skampiFactory;

        eventSupport =  new ChangeEventSupport(this);
    }

    /**
     * Add a variable rename option to those provided and perform a variable
     * rename using this new option.
     *
     * @param name the name change.
     */
    public void addUserVariableNameChange(String name) {
        userNameChanges.add(name);
        final RunConfig runConfig = getRunConfig(selectedData);
        runConfig.variableRenamesModel.insertElementAt(name, 0);
        runConfig.variableRenamesModel.setSelectedElement(name);
        addVariableNameChange();
    }

    /**
     * Returns the run config associated with the given run id.
     *
     * @param runId the run id.
     * @return the run config.
     */
    private RunConfig getRunConfig(Integer runId) {
        return runConfigs.get(runId);
    }

    /**
     * Set a variable rename based on the currently selected values of the
     * result set variable and result set renames models.
     */
    private void addVariableNameChange() {
        final String resultSetName = getSelectedResultSet(selectedData);
        final RunConfig runConfig = getRunConfig(selectedData);
        
        final Map<String, String> nameChanges =
                runConfig.resultSetVariableRenames.get(resultSetName);

        String param = getSelectedResultSetVariable(selectedData, resultSetName);

        final String changeTo = getRunConfig(selectedData).variableRenamesModel
                .getSelectedElement();
        if (changeTo != null && !changeTo.equals(NEW_PARAM_NAME)) {
            LOG.debug("Adding parameter name change for resultSet "
                    + resultSetName + " from " + param + " to " + changeTo);
            nameChanges.put(param, changeTo);
        }
    }

    /**
     * Return the border title for the SkaMPI config panel based on the
     * currently selected file.
     *
     * @return the border title.
     */
    public String getBorderTitle() {
        if (selectedData == null) {
            return BORDER_TITLE;
        } else {
            // Guard against separator being interpreted as an escape char in
            // Windows.
            String fileSeperator = File.separatorChar == '\\' ? "\\\\" : File.separator;

            String[] path = new String[]{""};
                    //selectedData..getAbsolutePath().split(fileSeperator);
            StringBuilder title  = new StringBuilder(BORDER_TITLE + ": ");
            if (path.length < 4) {
                for (String name : path) {
                    title.append(File.separator).append(name);
                }
            } else {
                title.append("...");
                for (int i = path.length - 3; i < path.length; i++) {
                    title.append(File.separator).append(path[i]);
                }
            }
            return title.toString();
        }
    }

    /**
     * Returns the combo box model used to display the different "Comm Between"
     * choices in the SkaMPI config panel.
     *
     * @return the comm between model.
     */
    public DefaultComboBoxModel<String> getCommBetweenModel() {
        if (selectedData != null) {
            return getRunConfig(selectedData).commBetweenModel;
        } else {
            return new DefaultComboBoxModel<>();
        }
    }

    /**
     * Returns the combo box model used to display the different result sets
     * loaded in the currently selected SkaMPI run file.
     *
     * @return the results sets model.
     */
    public DefaultComboBoxModel<String> getResultSetsModel() {
        if (selectedData != null) {
            return getRunConfig(selectedData).resultSetsModel;
        } else {
            return new DefaultComboBoxModel<>();
        }
    }

    /**
     * Returns the combo box model used to display the different variable
     * rename options.
     *
     * @return the result change to model.
     */
    public DefaultComboBoxModel<String> getVariableRenamesModel() {
        if (selectedData != null) {
            return getRunConfig(selectedData).variableRenamesModel;
        } else {
            return new DefaultComboBoxModel<>();
        }
    }

    /**
     * Returns the combo box model used to display the different variables
     * in the currently selected result set.
     *
     * @return the result set variables model.
     */
    public DefaultComboBoxModel<String> getResultSetVariables() {
        if (selectedData != null) {
            DefaultComboBoxModel<String> model = getRunConfig(selectedData)
                    .resultSetVariablesModels.get(getResultSetsModel().getSelectedElement());
            return model != null ? model : new DefaultComboBoxModel<String>();
        } else {
            return new DefaultComboBoxModel<>();
        }
    }

    @Override
    public void clear() {
        selectedData = null;

        eventSupport.fireChangeEvent();
    }

    @Override
    public void loadRuns(List<Run> runs) throws DatabaseException {
        Integer id = skampiFactory.getRunId(runs.get(0));
        selectedData = id;

        updateCommBetweenModel(id);
        updateResultSetVariables(id);
        updateResultSetsModel(id);
        updateVariableRenamesModel(id);

        eventSupport.fireChangeEvent();
    }

    @Override
    public void populateRun(Run run) {
        final Integer id = skampiFactory.getRunId(run);

        final Map<String, Map<String, String>> resultSetChanges
                = getRunConfig(id).resultSetVariableRenames;

        // Do replaces and add parameter for communications.
        for (RunData dataSet : run.getDataSets()) {

            final String resultSetName = dataSet.getParameter("Result Set")
                    .getValue().toString();
            final Map<String, String> nameChanges = resultSetChanges.get(resultSetName);
            final List<Value<?>> params = dataSet.getParameters();

            int paramIdx = 0;
            for (Value<?> param : params) {
                String name = param.getName();
                if (nameChanges.containsKey(name)) {
                    dataSet.updateParameterName(paramIdx, nameChanges.get(name));
                }
                ++paramIdx;
            }

            final String commBetween = getSelectedCommBetween(id);
            if (commBetween != null && !commBetween.isEmpty()) {
                dataSet.addParameter(new Value<>(COMM_BETWEEN_PARAM_NAME_IN_DB, Rank.ALL_RANKS, commBetween));
            }
        }
    }

    /**
     * Returns the value of the currently selected "Comm between" for the given
     * run id, or the empty string if not "Comm between" is selected.
     *
     * @param runId the run id.
     * @return the selected "Comm between" value.
     */
    private String getSelectedCommBetween(Integer runId) {
        DefaultComboBoxModel<String> commBetween = getRunConfig(runId).commBetweenModel;
        if (commBetween == null) { return ""; }
        return commBetween.getSelectedElement();
    }

    /**
     * Returns the value of the selected result set for the given run id.
     *
     * @param runId the run id.
     * @return the selected result set.
     */
    private String getSelectedResultSet(Integer runId) {
        return getRunConfig(runId).resultSetsModel.getSelectedElement();
    }

    /**
     * Returns the value of the selected result set variable for the given run
     * id and result set, or {@code null} if no variable has been selected for
     * the given inputs.
     *
     * @param runId the run id.
     * @param resultSetName the result set.
     * @return the selected variable.
     */
    private String getSelectedResultSetVariable(Integer runId, String resultSetName) {
        final RunConfig runConfig = getRunConfig(runId);
        if (runConfig.resultSetVariablesModels.get(resultSetName) == null) {
            return null;
        }
        String paramName = runConfig.resultSetVariablesModels.get(resultSetName)
                .getSelectedElement();
        return parameters.get(paramName);
    }

    /**
     * Update the "Comm between" combo box model for the given run id, clearing
     * it and then adding the parameters in the database with the "Comm between"
     * parameter name.
     *
     * @param runId the run id.
     * @throws DatabaseException if an error occurs getting the "Comm between"
     * parameters from the database.
     */
    private void updateCommBetweenModel(Integer runId) throws DatabaseException {

        final DefaultComboBoxModel<String> comboBoxModel
                = getRunConfig(runId).commBetweenModel;

        final List<Value<?>> commList = DatabaseManager.getConnection().getValues(Restriction.NONE,
                Category.PARAMETER, COMM_BETWEEN_PARAM_NAME_IN_DB, Rank.ANY_RANK);
        final List<String> commNames = new ArrayList<>(commList.size());
        for (Value<?> comm : commList) {
            commNames.add(comm.getValue().toString());
        }

        Collections.sort(commNames);

        final String selectedElement = comboBoxModel.getSelectedElement();
        comboBoxModel.removeAllElements();
        comboBoxModel.addAllElements(commNames);
        if (selectedElement != null) {
            comboBoxModel.setSelectedElement(selectedElement);
        }
    }

    /**
     * Updates the result set variables combo box models for the given run id
     * and currently selected result set, setting each variable to have a combo
     * box model with the variables loaded in the given result set.
     *
     * @param runId the run id.
     */
    private void updateResultSetVariables(Integer runId) {

        String resultSetName = getSelectedResultSet(runId);
        if (resultSetName != null) {

            final Map<String, DefaultComboBoxModel<String>> resultSetModels
                    = getRunConfig(runId).resultSetVariablesModels;

            DefaultComboBoxModel<String> comboBoxModel = resultSetModels.get(resultSetName);
            if (comboBoxModel == null) {
                comboBoxModel = new DefaultComboBoxModel<>();
                resultSetModels.put(resultSetName, comboBoxModel);
            }

            comboBoxModel.removeAllElements();
            for (String name : skampiFactory.getVariablesForResultSet(runId, resultSetName)) {
                parameters.put(name, name);
                comboBoxModel.addElement(name);
            }
        }
    }

    /**
     * Updates the result sets model for the given run id, clearing it and then
     * adding all the results sets loaded.
     *
     * @param runId the run id.
     */
    private void updateResultSetsModel(Integer runId) {

        final DefaultComboBoxModel<String> comboBoxModel
                = getRunConfig(selectedData).resultSetsModel;
        final Map<String, Map<String, String>> resultSetNameChanges
                = getRunConfig(runId).resultSetVariableRenames;

        comboBoxModel.removeAllElements();

        // Update table model with all result sets and create empty entries in
        // map of new names.
        List<String> names = skampiFactory.getResultSetNames(runId);
        for (String resultSet : names) {
            comboBoxModel.addElement(resultSet);
            if (!resultSetNameChanges.containsKey(resultSet)) {
                resultSetNameChanges.put(resultSet, new HashMap<String, String>());
            }
        }
        
        comboBoxModel.setSelectedElement(names.get(0));
    }

    /**
     * Updates the variable renames model for the given run id, clearing it and
     * then adding all the parameter names from the database as well as the user
     * variable renames that have been used before.
     *
     * @param runId the run id.
     * @throws DatabaseException if an error occurs getting the parameter names
     * from the database.
     */
    private void updateVariableRenamesModel(Integer runId) throws DatabaseException {

        final DefaultComboBoxModel<String> comboBoxModel
                = getRunConfig(selectedData).variableRenamesModel;

        if (variables == null) {
            variables = DatabaseManager.getConnection().getFields(Restriction.NONE, Category.PARAMETER);
        }

        // Add any user entered names.
        List<String> commList = new ArrayList<>(variables);
        commList.addAll(userNameChanges);
        Collections.sort(commList);

        comboBoxModel.removeAllElements();
        comboBoxModel.addElement(NEW_PARAM_NAME);
        comboBoxModel.addAllElements(commList);
        comboBoxModel.setSelectedElement(getChangeToValueThatHasBeenSet(runId));
    }

    /**
     * Return the value a user set the change to value for the current result
     * set and variable, will return null if this hasn't been selected.
     *
     * @param runId the run id.
     * @return the value to use instead of the variable name, or {@code null} if
     * not set.
     */
    private String getChangeToValueThatHasBeenSet(Integer runId) {
        final String resultSetName = getSelectedResultSet(runId);
        if (resultSetName != null) {
            final String resultSetVariable = getSelectedResultSetVariable(runId, resultSetName);
            if (resultSetVariable != null) {
                if (!getRunConfig(runId).resultSetVariableRenames.containsKey(resultSetName)) {
                    return null;
                }
                return getRunConfig(runId).resultSetVariableRenames
                        .get(resultSetName).get(resultSetVariable);
            }
        }
        return null;
    }

    /**
     * Add a {@code ChangeListener} to those to be informed when this model
     * changes.
     *
     * @param listener the change listener.
     */
    public void addChangeListener(ChangeListener listener) {
        eventSupport.addChangeListener(listener);
    }

    /**
     * Returns whether the new variable rename panel should be shown.
     *
     * @return {@code true} if the panel should be shown, {@code false}
     * otherwise.
     */
    public boolean isNewVariablePanelShown() {
        return NEW_PARAM_NAME.equals(
                getRunConfig(selectedData).variableRenamesModel.getSelectedElement());
    }
}
