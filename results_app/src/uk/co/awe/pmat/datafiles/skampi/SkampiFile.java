package uk.co.awe.pmat.datafiles.skampi;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.db.Run;

/**
 * Represents a Run of SkaMPI, has extra fields not stored in the database
 * directly but needed by the class internally.
 *
 * @author AWE Plc copyright 2013
 */
public class SkampiFile extends DataFile<SkampiVersion> {

    private static final Logger LOG = LoggerFactory.getLogger(SkampiFile.class);
    private static final String APP_NAME_IN_DB = "SKaMPI v";

    private final List<String> instructionLines = new LinkedList<>();

    /**
     * Create a new {@code SkaMPI}.
     *
     * @param data the data object to populate from the file.
     * @param version the SkaMPI file version.
     */
    public SkampiFile(Run data, Enum<SkampiVersion> version) {
        super(data, APP_NAME_IN_DB + version, version, null);
    }

    /**
     * @return the version of SkaMPI that was run
     */
    public SkampiVersion getSkampiVersion() {
        return SkampiVersion.valueOf(getVersion().name());
    }

    /**
     * If SkaMPI has been run where the original script is written out with the
     * results this function returns those lines.
     *
     * @return instruction lines
     */
    public List<String> getInstructionLines() {
        return instructionLines;
    }

    /**
     * Set the instruction script that was used to run SkaMPI.
     *
     * @param instructLines the instructions lines used to run SkaMPI
     */
    public void addInstructionLines(Collection<String> instructLines) {
        instructionLines.addAll(instructLines);
    }

    /**
     * Add a single instruction line to the end of the instruction lines.
     *
     * @param instructLine the instruction line to add
     */
    public void addInstructionLine(String instructLine) {
        instructionLines.add(instructLine);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SkampiFile)) {
            return false;
        }
        final SkampiFile other = (SkampiFile) obj;
        if (instructionLines != other.getInstructionLines() && (instructionLines == null || !instructionLines.equals(other.getInstructionLines()))) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + (instructionLines != null ? instructionLines.hashCode() : 0);
        return hash;
    }

}
