package uk.co.awe.pmat.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMCDATA;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.utils.ArrayUtils;
import uk.co.awe.pmat.utils.Pair;

/**
 * The analysis persistence class. This is an immutable class containing the
 * analysis state, which can be saved to and loaded from the database or XML
 * files.
 *
 * @author AWE Plc copyright 2013
 */
public final class Analysis implements DatabaseObject<Analysis>, XMLSerialisable {

    private static final int MAX_NOTES_CHARS_SHOWN = 20;

    /**
     * The names of the columns that should be used in a table displaying
     * {@code Analysis} objects.
     */
    public static final List<String> TABLE_COLUMN_NAMES = ArrayUtils.asUnmodifiableList(
        "Creator",
        "Date",
        "Notes"
    );

    /**
     * The types of the columns that should be used in a table displaying
     * {@code Analysis} objects.
     */
    @SuppressWarnings("unchecked")
    public static final List<Class<?>> TABLE_COLUMN_TYPES = ArrayUtils.asUnmodifiableList(
        (Class<?>) String.class,
        Date.class,
        String.class
    );

    private final String creator;
    private final Date date;
    private final String notes;
    private final boolean dataPrivate;
    private final List<Graph> graphs = new ArrayList<>();
    private final List<DerivedData> derivedData = new ArrayList<>();
    private final List<Restriction> restrictions = new ArrayList<>();
    private final List<Pair<String, String>> renames = new ArrayList<>();

    /**
     * Create a new {@code Analysis}.
     *
     * @param creator the analysis creator.
     * @param date the analysis creation date.
     * @param notes the analysis notes.
     * @param dataPrivate whether the analysis is private.
     * @param graphs the analysis graphs.
     * @param derivedData the analysis derived data.
     * @param restrictions the analysis restrictions.
     * @param renames the analysis renames.
     */
    public Analysis(String creator,
            Date date,
            String notes,
            boolean dataPrivate,
            List<Graph> graphs,
            List<DerivedData> derivedData,
            List<Restriction> restrictions,
            List<Pair<String, String>> renames) {
        this.creator = creator;
        this.date = (Date) date.clone();
        this.notes = notes;
        this.dataPrivate = dataPrivate;
        this.graphs.addAll(graphs);
        this.derivedData.addAll(derivedData);
        this.restrictions.addAll(restrictions);
        this.renames.addAll(renames);
    }

    /**
     * Returns the creator of the analysis.
     *
     * @return the creator.
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Returns the date the analysis was performed.
     *
     * @return the analysis date.
     */
    public Date getDate() {
        return (Date) date.clone();
    }

    /**
     * Returns any notes stored with the analysis.
     *
     * @return the analysis notes.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Return whether the analysis is private.
     *
     * @return {@code true} if the analysis is private, {@code false} otherwise.
     */
    public boolean isDataPrivate() {
        return dataPrivate;
    }

    /**
     * Return any notes stored with the analysis, trim to a fixed amount. If
     * the notes go beyond this amount then they will be terminated with an
     * ellipsis (...).
     *
     * @return the trimmed notes.
     */
    private String getNotesTrimmed() {
        if (notes != null && notes.length() > MAX_NOTES_CHARS_SHOWN) {
            return notes.substring(0, MAX_NOTES_CHARS_SHOWN) + "...";
        }
        return notes;
    }

    /**
     * Get this {@code Analysis} as a row that can be displayed in a table.
     *
     * @return this {@code Analysis} as a table row.
     */
    public List<Object> asTableRow() {
        return ArrayUtils.asUnmodifiableList(
                (Object) getCreator(),
                getDate(),
                getNotesTrimmed());
    }

    @Override
    public String toString() {
        return String.format("%s: %s %s", creator, date.toString(), getNotesTrimmed());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Analysis)) {
            return false;
        }
        final Analysis other = (Analysis) obj;
        if ((this.creator == null) ? (other.creator != null) : !this.creator.equals(other.creator)) {
            return false;
        }
        if (this.date != other.date && (this.date == null || !this.date.equals(other.date))) {
            return false;
        }
        if ((this.notes == null) ? (other.notes != null) : !this.notes.equals(other.notes)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.creator != null ? this.creator.hashCode() : 0);
        hash = 53 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 53 * hash + (this.notes != null ? this.notes.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Analysis other) {
        final int creatorCmp = creator.compareTo(other.creator);
        if (creatorCmp == 0) {
            return date.compareTo(other.date);
        }
        return creatorCmp;
    }

    /**
     * Return the graphs stored in this analysis.
     *
     * @return the analysis graphs.
     */
    public Collection<Graph> getGraphs() {
        return Collections.unmodifiableList(graphs);
    }

    /**
     * Return the derived data stored in this analysis.
     *
     * @return the analysis derived data.
     */
    public Collection<DerivedData> getDerivedData() {
        return Collections.unmodifiableList(derivedData);
    }

    /**
     * Return the restrictions stored in this analysis.
     *
     * @return the analysis restrictions.
     */
    public Collection<Restriction> getRestrictions() {
        return Collections.unmodifiableList(restrictions);
    }

    /**
     * Return the label renames stored in this analysis.
     *
     * @return the analysis label renames.
     */
    public Collection<Pair<String, String>> getLabelRenames() {
        return Collections.unmodifiableList(renames);
    }

    @Override
    public Element toXML() {
        final Element node = new DOMElement(Analysis.class.getSimpleName());

        DateFormat df = new SimpleDateFormat(DATE_FORMAT_STRING);

        node.add(new DOMAttribute(new QName("creator"), creator));
        node.add(new DOMAttribute(new QName("date"), df.format(date)));

        if (notes != null) {
            Element notesEl = new DOMElement(new QName("Notes"));
            notesEl.add(new DOMCDATA(notes));
            node.add(notesEl);
        }

        node.add(new DOMAttribute(new QName("dataPrivate"), Boolean.toString(dataPrivate)));
        for (Graph axis : graphs) {
            node.add(axis.toXML());
        }
        for (DerivedData data : derivedData) {
            node.add(data.toXML());
        }
        for (Restriction restriction : restrictions) {
            node.add(restriction.toXML());
        }
        for (Pair<String, String> rename : renames) {
            Element renameNode = new DOMElement(new QName("Rename"));
            renameNode.add(new DOMAttribute(new QName("from"), rename.getFirst()));
            renameNode.add(new DOMAttribute(new QName("to"), rename.getSecond()));
            node.add(renameNode);
        }

        return node;
    }

}
