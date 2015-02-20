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
public final class OperatingSystem implements MetaData {
    
    private static final LinkedHashMap<String, Class<?>> DATA_FIELDS;
    
    static {
        DATA_FIELDS = new LinkedHashMap<>();
        DATA_FIELDS.put("name", String.class);
        DATA_FIELDS.put("vendor", String.class);
        DATA_FIELDS.put("versionMajor", Integer.class);
        DATA_FIELDS.put("versionMinor", Integer.class);
        DATA_FIELDS.put("versionBuild", Integer.class);
        DATA_FIELDS.put("versionBuildMinor", Integer.class);
        DATA_FIELDS.put("kernel", String.class);
    }
    
    static LinkedHashMap<String, Class<?>> getDataFields() {
        return new LinkedHashMap<>(DATA_FIELDS);
    }

    private final String name;
    private final String vendor;
    private final Integer versionMajor;
    private final Integer versionMinor;
    private final Integer versionBuild;
    private final Integer versionBuildMinor;
    private final String kernel;

    /**
     * Create a new {@code OperatingSystem}.
     * 
     * @param name the name of the operating system.
     * @param vendor the vendor of the operating system.
     * @param versionMajor the major version of the operating system.
     * @param versionMinor the minor version of the operating system.
     * @param versionBuild the build version of the operating system.
     * @param versionBuildMinor the minor build version of the operating system.
     * @param kernel the operating system kernel name and version.
     */
    public OperatingSystem(String name, String vendor, Integer versionMajor, Integer versionMinor, Integer versionBuild, Integer versionBuildMinor, String kernel) {
        this.name = name;
        this.vendor = vendor;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.versionBuild = versionBuild;
        this.versionBuildMinor = versionBuildMinor;
        this.kernel = kernel;
    }

    /**
     * Create a new {@code OperatingSystem}.
     * 
     * @param valueMap a map of field names and values from which to populate
     * this operating system.
     */
    public OperatingSystem(Map<String, Object> valueMap) {
        this(
            (String) valueMap.get("name"),
            (String) valueMap.get("vendor"),
            (Integer) valueMap.get("versionMajor"),
            (Integer) valueMap.get("versionMinor"),
            (Integer) valueMap.get("versionBuild"),
            (Integer) valueMap.get("versionBuildMinor"),
            (String) valueMap.get("kernel")
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
            case "versionBuildMinor": return versionBuildMinor;
            case "kernel": return kernel;
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
        dataMap.put("versionBuildMinor", versionBuildMinor);
        dataMap.put("kernel", kernel);
        return dataMap;
    }

    @Override
    public Type getType() {
        return Type.OPERATING_SYSTEM;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OperatingSystem)) {
            return false;
        }
        final OperatingSystem other = (OperatingSystem) obj;
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
        if (!Objects.equals(this.versionBuildMinor, other.versionBuildMinor)) {
            return false;
        }
        if (!Objects.equals(this.kernel, other.kernel)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.vendor);
        hash = 89 * hash + Objects.hashCode(this.versionMajor);
        hash = 89 * hash + Objects.hashCode(this.versionMinor);
        hash = 89 * hash + Objects.hashCode(this.versionBuild);
        hash = 89 * hash + Objects.hashCode(this.versionBuildMinor);
        hash = 89 * hash + Objects.hashCode(this.kernel);
        return hash;
    }

    @Override
    public String displayName() {
        String longname;
        longname = vendor + " " + name;
        longname += versionMajor != null ? " " + versionMajor : "";
        longname += versionMinor != null ? "." + versionMinor : "";
        longname += versionBuild != null ? "." + versionBuild : "";
        longname += versionBuildMinor != null ? "-" + versionBuildMinor : "";
        longname += kernel != null ? " " + kernel : "";
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
