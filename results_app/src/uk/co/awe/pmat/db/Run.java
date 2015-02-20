package uk.co.awe.pmat.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.db.xml.XMLSerialisationException;
import uk.co.awe.pmat.utils.ArrayUtils;
import uk.co.awe.pmat.utils.StringUtils;

/**
 * The main data object class. This class represents a data collection which
 * contains meta-data about system state and application as well as all the
 * results data. This is a mainly immutable class with the exception that
 * additional {@code RunData} can be added post creation.
 *
 * @author AWE Plc copyright 2013
 */
public final class Run implements DatabaseObject<Run>, XMLSerialisable {
    
    private static final Logger LOG = LoggerFactory.getLogger(Run.class);

    public interface Column {
        Class<?> getColumnClass();
        String getColumnName();
    }
    
    private static class RunColumn implements Column {
        private final String name;
        private Class<?> cls;
        private final Object field;

        public RunColumn(String name, Class<?> cls, Object field) {
            this.name = name;
            this.cls = cls;
            this.field = field;
        }
        
        @Override
        public Class<?> getColumnClass() {
            return cls;
        }

        @Override
        public String getColumnName() {
            return name;
        }
    }
    
    /**
     * The names of the columns that should be used in a table displaying
     * {@code Run} objects.
     */
    public static final List<Column> TABLE_COLUMNS =
            ArrayUtils.asUnmodifiableList(
                (Column) new RunColumn("Run Date", Date.class, "runDate"),
                new RunColumn(MetaData.Type.APPLICATION.displayName(), MetaData.class, MetaData.Type.APPLICATION),
                new RunColumn(MetaData.Type.COMPILER.displayName(), MetaData.class, MetaData.Type.COMPILER),
                new RunColumn(MetaData.Type.MPI.displayName(), MetaData.class, MetaData.Type.MPI),
                new RunColumn(MetaData.Type.MACHINE.displayName(), MetaData.class, MetaData.Type.MACHINE),
                new RunColumn(MetaData.Type.OPERATING_SYSTEM.displayName(), MetaData.class, MetaData.Type.OPERATING_SYSTEM),
                new RunColumn(MetaData.Type.PROCESSOR.displayName(), MetaData.class, MetaData.Type.PROCESSOR),
                new RunColumn("Is Private", Boolean.class, "restricted")
            );
    
    public static final List<Column> VIEW_FIELDS =
            ArrayUtils.asUnmodifiableList(
                (Column) new RunColumn("Run ID", String.class, "runId"),
                new RunColumn("Run Date", Date.class, "runDate"),
                new RunColumn("Insertion Date", Date.class, "insertionDate"),
                new RunColumn(MetaData.Type.APPLICATION.displayName(), MetaData.class, MetaData.Type.APPLICATION),
                new RunColumn(MetaData.Type.COMPILER.displayName(), MetaData.class, MetaData.Type.COMPILER),
                new RunColumn(MetaData.Type.MPI.displayName(), MetaData.class, MetaData.Type.MPI),
                new RunColumn(MetaData.Type.MACHINE.displayName(), MetaData.class, MetaData.Type.MACHINE),
                new RunColumn(MetaData.Type.OPERATING_SYSTEM.displayName(), MetaData.class, MetaData.Type.OPERATING_SYSTEM),
                new RunColumn(MetaData.Type.PROCESSOR.displayName(), MetaData.class, MetaData.Type.PROCESSOR),
                new RunColumn("Creator", String.class, "creator"),
                new RunColumn("Is Private", Boolean.class, "restricted"),
                new RunColumn("File Name", String.class, "file"),
                new RunColumn("Tag", String.class, "tag"),
                new RunColumn("Flags", String.class, "flags")
            );
    
    private final String runId;
    private final Date runDate;
    private final Date insertionDate;
    private final Map<MetaData.Type, MetaData> metaMap = new EnumMap<>(MetaData.Type.class);
    private final String creator;
    private final boolean restricted;
    private final List<RunData> dataSets = new ArrayList<>();
    private final String file;
    private final String tag;
    private final List<String> flags = new ArrayList<>();
    
    // Checks for lazy loading flags and data sets.
    private boolean flagsLoaded = false;
    private boolean dataSetsLoaded = false;

    /**
     * Create a new {@code Run} object.
     *
     * @param runId the unique id stored against the run.
     * @param runDate the date the run was performed.
     * @param insertionDate the date the run was saved into the database.
     * @param metaData the meta-data stored alongside the run data.
     * @param creator the name of the user who imported this run.
     * @param restricted whether the run data is private to this institution.
     */
    public Run(String runId,
            Date runDate,
            Date insertionDate,
            Collection<MetaData> metaData,
            String creator,
            boolean restricted,
            String file,
            String tag) {
        this.runId = runId;
        this.runDate = (Date) runDate.clone();
        this.insertionDate = insertionDate == null
                ? null : (Date) insertionDate.clone();
        this.creator = creator;
        this.restricted = restricted;
        for (MetaData meta : metaData) {
            if (meta != null) {
                metaMap.put(meta.getType(), meta);
            }
        }
        this.file = file;
        this.tag = tag;
    }

    /**
     * Create a new {@code Run} object.
     *
     * @param runId the unique id stored against the run.
     * @param runDate the data the run was performed.
     * @param file the file from which this run was created.
     * @param flags the build flags associated with this run.
     */
    @SuppressWarnings("unchecked")
    public Run(String runId, Date runDate, String file, Collection<String> flags) {
        this(runId, runDate, null, Collections.EMPTY_LIST, null, false, file, null);
        addFlags(flags);
    }
    
    public boolean isComplete() {
        boolean complete = (runDate != null
                && insertionDate != null
                && creator != null
                && runId != null
                && file != null);
        for (MetaData.Type type : MetaData.Type.values()) {
            if (metaMap.get(type) == null) {
                LOG.debug("Run is incomplete, missing " + type);
                complete = false;
                break;
            }
        }
        return complete;
    }

    /**
     * Create a new {@code Run} object as a copy of this {@code Run} but with
     * the given fields updated.
     *
     * @param newCreator the name of the user who imported this run.
     * @param newRestricted whether the run data is private to this institution.
     * @return a copy of this {@code Run}, updated with the given information.
     */
    public Run update(String newCreator, boolean newRestricted, String newTag) {
        Run newRun = new Run(runId, runDate, insertionDate, metaMap.values(),
                newCreator, newRestricted, file, newTag);
        newRun.addAllDataSets(dataSets);
        newRun.addFlags(flags);
        return newRun;
    }
    
    public Run update(String newFile) {
        Run newRun = new Run(runId, runDate, insertionDate, metaMap.values(),
                creator, restricted, newFile, tag);
        newRun.addAllDataSets(dataSets);
        newRun.addFlags(flags);
        return newRun;
    }

    /**
     * Get this {@code Run} as a row that can be displayed in a table.
     *
     * @return this {@code Run} as a table row.
     */
    public List<Object> asTableRow() {
        final List<Object> data = new ArrayList<>();
        for (Column column : TABLE_COLUMNS) {
            data.add(getColumnData(column));
        }
        return Collections.unmodifiableList(data);
    }
    
    public Object getColumnData(Column column) {
        if (!(column instanceof RunColumn)) {
            throw new IllegalArgumentException("Invalid column " + column);
        }
        final Object field = ((RunColumn) column).field;
        if (field instanceof MetaData.Type) {
            final MetaData.Type type = (MetaData.Type) field;
            return metaMap.get(type);
        } else if (field instanceof String) {
            switch ((String) field) {
                case "runDate": return runDate.clone();
                case "runId": return runId;
                case "insertionDate": return insertionDate.clone();
                case "creator": return creator;
                case "restricted": return restricted;
                case "file": return file;
                case "tag": return tag;
                case "flags": return StringUtils.joinStrings(flags, ", ");
                default:
                    throw new IllegalArgumentException("Invalid field name " + field);
            }
        } else {
            throw new IllegalArgumentException("Invalid field " + field);
        }
    }

    /**
     * Add a {@code RunData} to the collection of data sets stored against this
     * {@code Run}.
     *
     * @param dataSet the data set to add.
     */
    public void addDataSet(RunData dataSet) {
        dataSets.add(dataSet);
    }
    
    public void addFlags(Collection<String> flags) {
        this.flags.addAll(flags);
    }

    public void loadFlags() throws DatabaseException {
        if (!flagsLoaded) {
            flags.addAll(DatabaseManager.getConnection().getFlags(this));
            flagsLoaded = true;
        }
    }
    
    public void loadDataSets() throws DatabaseException {
        if (!dataSetsLoaded) {
            dataSets.addAll(DatabaseManager.getConnection().getDataSets(this));
            dataSetsLoaded = true;
        }
    }
    
    /**
     * Updates the {@code MetaData} stored in this run with the given type.
     * 
     * @param type the type of the {@code MetaData} to store.
     * @param metaData the {@code MetaData} to store.
     */
    public void setMetaData(MetaData.Type type, MetaData metaData) {
        metaMap.put(type, metaData);
    }

    /**
     * Returns the meta-data stored against this {@code Run} with the given
     * type.
     *
     * @param type the type of the meta-data to return.
     * @return the meta-data.
     */
    public MetaData getMetaData(MetaData.Type type) {
        return metaMap.get(type);
    }

    /**
     * Add all the given {@code RunData}s to the collections of data sets stored
     * against this {@code Run}.
     *
     * @param data the data sets to add.
     */
    public void addAllDataSets(Collection<RunData> data) {
        this.dataSets.addAll(data);
    }

    /**
     * Returns the date when the run was performed.
     *
     * @return the run date.
     */
    public Date getRunDate() {
        return (Date) runDate.clone();
    }
    
    public String getTag() {
        return tag;
    }
    
    public List<String> getFlags() {
        return Collections.unmodifiableList(flags);
    }

    /**
     * Returns the date when the run was inserted into the database.
     * 
     * @return the insertion date.
     */
    public Date getInsertionDate() {
        return insertionDate == null ? null : (Date) insertionDate.clone();
    }

    /**
     * Returns whether the data stored in this {@code Run} is private to this
     * institution.
     *
     * @return {@code true} is the data is private, {@code false} otherwise.
     */
    public boolean isRestricted() {
        return restricted;
    }

    /**
     * Returns the name of the user who entered this run data into the database.
     *
     * @return the user name.
     */
    public String getCreator() {
        return creator;
    }
    
    public String getFile() {
        return file;
    }

    /**
     * Returns all the data stored in this {@code Run}.
     *
     * @return the run data.
     */
    public List<RunData> getDataSets() {
        return Collections.unmodifiableList(dataSets);
    }

    /**
     * Returns the unique id stored with this {@code Run}.
     *
     * @return the run id.
     */
    public String getRunId() {
        return runId;
    }

    @Override
    public String toString() {
        return String.format("%s", runId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Run)) {
            return false;
        }
        final Run other = (Run) obj;
        if (runId != null) {
            return runId.equals(other.getRunId());
        }
        if (runDate != other.runDate && (runDate == null || !runDate.equals(other.runDate))) {
            return false;
        }
        if ((creator == null) ? (other.creator != null) : !creator.equals(other.creator)) {
            return false;
        }
        if (restricted != other.restricted) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (runId != null) {
            return runId.hashCode();
        }
        int hash = 7;
        hash = 23 * hash + (runDate != null ? runDate.hashCode() : 0);
        hash = 23 * hash + (creator != null ? creator.hashCode() : 0);
        hash = 23 * hash + (restricted ? 1 : 0);
        return hash;
    }

    @Override
    public int compareTo(Run other) {
        return runId.compareTo(other.getRunId());
    }

    @Override
    public Element toXML() {
        if (restricted) {
            throw new XMLSerialisationException("Private runs cannot be exported.");
        }
        DateFormat dateFormat =
                new SimpleDateFormat(DATE_FORMAT_STRING, Locale.UK);

        Element node = new DOMElement(getClass().getSimpleName());

        node.add(new DOMAttribute(new QName("user"), creator));
        node.add(new DOMAttribute(new QName("runDate"),
                dateFormat.format(runDate)));
        node.add(new DOMAttribute(new QName("runId"),
                runId == null ? XMLSerialisable.NULL : runId));

        for (String flag : flags) {
            Element flagNode = new DOMElement(new QName("Flag"));
            flagNode.add(new DOMAttribute(new QName("value"), flag));
            node.add(flagNode);
        }
        
        for (MetaData.Type type : MetaData.Type.values()) {
            node.add(metaMap.get(type).toXML());
        }
        
        for (RunData runData : dataSets) {
            node.add(runData.toXML());
        }
        
        return node;
    }

}
