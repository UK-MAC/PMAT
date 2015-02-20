package uk.co.awe.pmat.gui.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeListener;
import uk.co.awe.pmat.GuiModel;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;
import uk.co.awe.pmat.gui.ExceptionDialog;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.utils.ChangeEventSupport;

/**
 * Model underpinning the {@code DataFilterPanel} and the data restrictions to
 * apply.
 *
 * @author AWE Plc copyright 2013
 */
public final class DataFilterModel implements GuiModel {

    private final EventHub eventHub;
    private final List<Restriction> restrictions = new ArrayList<>();
    private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);

    /**
     * Creates a new {@code DataFilterModel}.
     * 
     * @param analysisModel the model underpinning the analysis.
     */
    public DataFilterModel(EventHub eventHub) {
        this.eventHub = eventHub;
    }

    /**
     * Returns the different restriction categories available.
     *
     * @return the available restriction categories.
     */
    public List<Category> getCategories() {
        return Arrays.asList(Category.values());
    }

    /**
     * Returns the different restriction field available for the given category.
     *
     * @param category the restriction category.
     * @return the available restriction fields.
     * @throws DatabaseException if an error occurs querying the database.
     */
    public List<String> getFields(Category category) throws DatabaseException {
        List<String> fields = DatabaseManager.getConnection().getFields(restrictions, category);
        Collections.sort(fields);

        return fields;
    }

    /**
     * Returns the different restriction ranks available.
     *
     * @return the available restriction ranks.
     */
    public List<Rank> getRanks(Category category, String field) {
        try {
            return DatabaseManager.getConnection().getRanks(restrictions, category, field);
        } catch (DatabaseException ex) {
            ExceptionDialog.showDatabaseExceptionDialog(ex);
            return Collections.emptyList();
        }
    }

    /**
     * Returns the different restriction comparators available.
     *
     * @return the available restriction comparators.
     */
    public List<Comparator> getComparators() {
        return Arrays.asList(Comparator.values());
    }

    /**
     * Returns the different restriction values available for the given
     * category, field and rank.
     *
     * @param category the restriction category.
     * @param field the restriction field.
     * @param rank the restriction rank.
     * @return the available restriction values.
     * @throws DatabaseException if an error occurs querying the database.
     */
    public List<Value<?>> getValues(Category category, String field, Rank rank) throws DatabaseException {
        List<Value<?>> values = DatabaseManager.getConnection().getValues(restrictions, category, field, rank);
        Collections.sort(values);

        return values;
    }

    /**
     * Returns all the restriction currently defined in the model.
     *
     * @return the restrictions.
     */
    public List<Restriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    /**
     * Removes the restriction with the given index.
     *
     * @param index the restriction index.
     */
    public void removeRestriction(int index) {
        restrictions.remove(index);
        eventHub.notifyEvent(EventHub.EventType.FILTERS);
        eventSupport.fireChangeEvent();
    }

    /**
     * Adds a restriction to restrict against the given category, field, rank,
     * comparator and value.
     *
     * @param category the restriction category.
     * @param field the restriction field.
     * @param rank the restriction rank.
     * @param comparator the restriction comparator.
     * @param value the restriction value.
     */
    public void addRestriction(Category category, String field, Rank rank, Comparator comparator, Value<?> value) {
        restrictions.add(DatabaseManager.getConnection().newRestriction(category, field, rank, comparator, value));
        eventHub.notifyEvent(EventHub.EventType.FILTERS);
        eventSupport.fireChangeEvent();
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        eventSupport.addChangeListener(listener);
    }

    /**
     * Returns the all the result names for datasets that match the defined
     * restrictions.
     *
     * @return the result names.
     * @throws DatabaseException if an error occurs querying the database.
     */
    public List<String> getFilteredResultNames() throws DatabaseException {
        return DatabaseManager.getConnection().getFields(restrictions, Category.RESULT);
    }

    /**
     * Returns the all the parameter names for datasets that match the defined
     * restrictions.
     *
     * @return the parameter names.
     * @throws DatabaseException if an error occurs querying the database.
     */
    public List<String> getFilteredParameterNames() throws DatabaseException {
        return DatabaseManager.getConnection().getFields(restrictions, Category.PARAMETER);
    }

    /**
     * Add all the given restrictions to those currently defined.
     *
     * @param restricts the restrictions to add.
     */
    public void addAllRestrictions(Collection<Restriction> restricts) {
        restrictions.addAll(restricts);
    }
    
}
