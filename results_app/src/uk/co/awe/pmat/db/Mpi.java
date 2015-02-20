package uk.co.awe.pmat.db;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.dom4j.Element;
import uk.co.awe.pmat.db.xml.MetaDataXMLSerialiser;

/**
 *
 * @author AWE Plc copyright 2013
 */
public final class Mpi implements MetaData {

    private static final LinkedHashMap<String, Class<?>> DATA_FIELDS;
    
    static {
        DATA_FIELDS = new LinkedHashMap<>();
        DATA_FIELDS.put("name", String.class);
        DATA_FIELDS.put("vendor", String.class);
        DATA_FIELDS.put("versionMajor", Integer.class);
        DATA_FIELDS.put("versionMinor", Integer.class);
        DATA_FIELDS.put("versionBuild", Integer.class);
    }
    
    static LinkedHashMap<String, Class<?>> getDataFields() {
        return new LinkedHashMap<>(DATA_FIELDS);
    }

    private final String name;
    private final String vendor;
    private final Integer versionMajor;
    private final Integer versionMinor;
    private final Integer versionBuild;

    /**
     * Create a new {@code Mpi}.
     * 
     * @param name the name of the MPI.
     * @param vendor the vendor of the MPI.
     * @param versionMajor the major version of the MPI.
     * @param versionMinor the minor version of the MPI.
     * @param versionBuild the build version of the MPI.
     */
    public Mpi(String name, String vendor, Integer versionMajor, Integer versionMinor, Integer versionBuild) {
        this.name = name;
        this.vendor = vendor;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.versionBuild = versionBuild;
    }

    /**
     * Create a new {@code Mpi}.
     * 
     * @param valueMap a map of field names and values from which to populate
     * this MPI.
     */
    public Mpi(Map<String, Object> valueMap) {
        this(
            (String) valueMap.get("name"),
            (String) valueMap.get("vendor"),
            (Integer) valueMap.get("versionMajor"),
            (Integer) valueMap.get("versionMinor"),
            (Integer) valueMap.get("versionBuild")
        );
    }
    
    @Override
    public Object getData(String dataName) {
        switch (dataName) {
            case "name": return name;
            case "vendor": return vendor;
            case "versionMajor": return versionMajor;
            case "versionMinor": return versionMinor;
            case "versionBuild": return versionBuild;
            default:
                throw new IllegalArgumentException("Unknown data name " + dataName);
        }
    }

    @Override
    public Map<String, Object> getDataMap() {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("vendor", vendor);
        dataMap.put("versionMajor", versionMajor);
        dataMap.put("versionMinor", versionMinor);
        dataMap.put("versionBuild", versionBuild);
        return dataMap;
    }

    @Override
    public Type getType() {
        return Type.MPI;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Mpi)) {
            return false;
        }
        final Mpi other = (Mpi) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.vendor, other.vendor)) {
            return false;
        }
        if (!Objects.equals(this.versionMajor, other.versionMajor)) {
            return false;
        }
        if (!Objects.equals(this.versionMinor, other.versionMinor)) {
            return false;
        }
        if (!Objects.equals(this.versionBuild, other.versionBuild)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.name);
        hash = 83 * hash + Objects.hashCode(this.vendor);
        hash = 83 * hash + Objects.hashCode(this.versionMajor);
        hash = 83 * hash + Objects.hashCode(this.versionMinor);
        hash = 83 * hash + Objects.hashCode(this.versionBuild);
        return hash;
    }

    @Override
    public String displayName() {
        String longname;
        longname = name;
        longname += versionMajor != null ? " " + versionMajor : "";
        longname += versionMinor != null ? "." + versionMinor : "";
        longname += versionBuild != null ? "." + versionBuild : "";
        return longname;
    }

    @Override
    public int compareTo(MetaData other) {
        return displayName().compareToIgnoreCase(other.displayName());
    }

    @Override
    public Element toXML() {
        return MetaDataXMLSerialiser.toXML(this);
    }
    
}
