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
public final class Application implements MetaData {
    
    private static final LinkedHashMap<String, Class<?>> DATA_FIELDS;
    
    static {
        DATA_FIELDS = new LinkedHashMap<>();
        DATA_FIELDS.put("name", String.class);
        DATA_FIELDS.put("versionMajor", Integer.class);
        DATA_FIELDS.put("versionMinor", Integer.class);
        DATA_FIELDS.put("versionBuild", Integer.class);
        DATA_FIELDS.put("versionCode", String.class);
        DATA_FIELDS.put("description", String.class);
        DATA_FIELDS.put("dataPrivate", Boolean.class);
    }
    
    static LinkedHashMap<String, Class<?>> getDataFields() {
        return new LinkedHashMap<>(DATA_FIELDS);
    }

    private final String name;
    private final Integer versionMajor;
    private final Integer versionMinor;
    private final Integer versionBuild;
    private final String versionCode;
    private final String description;
    private final Boolean dataPrivate;

    /**
     * Create a new {@code Application}.
     * 
     * @param name the application name.
     * @param versionMajor the application major version.
     * @param versionMinor the application minor version.
     * @param versionBuild the application build version.
     * @param versionCode the application version code.
     * @param description a description of the application.
     * @param dataPrivate whether or not the application data is private to
     * this institution.
     */
    public Application(String name,
            Integer versionMajor,
            Integer versionMinor,
            Integer versionBuild,
            String versionCode,
            String description,
            Boolean dataPrivate) {
        this.name = name;
        this.versionMajor = versionMajor;
        this.versionMinor = versionMinor;
        this.versionBuild = versionBuild;
        this.versionCode = versionCode;
        this.description = description;
        this.dataPrivate = dataPrivate;
    }

    /**
     * Create a new {@code Application}.
     * 
     * @param fields a map of field names and values from which to populate this
     * application.
     */
    public Application(Map<String, Object> fields) {
        this.name = (String) fields.get("name");
        this.versionMajor = (Integer) fields.get("versionMajor");
        this.versionMinor = (Integer) fields.get("versionMinor");
        this.versionBuild = (Integer) fields.get("versionBuild");
        this.versionCode = (String) fields.get("versionCode");
        this.description = (String) fields.get("description");
        this.dataPrivate = (Boolean) fields.get("dataPrivate");
    }
    
    @Override
    public Object getData(String dataName) {
        switch (dataName) {
            case "name": return name;
            case "versionMajor": return versionMajor;
            case "versionMinor": return versionMinor;
            case "versionBuild": return versionBuild;
            case "versionCode": return versionCode;
            case "description": return description;
            case "dataPrivate": return dataPrivate;
            default:
                throw new IllegalArgumentException("Unknown data name " + dataName);
        }
    }

    @Override
    public Map<String, Object> getDataMap() {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("versionMajor", versionMajor);
        dataMap.put("versionMinor", versionMinor);
        dataMap.put("versionBuild", versionBuild);
        dataMap.put("versionCode", versionCode);
        dataMap.put("description", description);
        dataMap.put("dataPrivate", dataPrivate);
        return dataMap;
    }

    @Override
    public Type getType() {
        return Type.APPLICATION;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Application)) {
            return false;
        }
        final Application other = (Application) obj;
        if (!Objects.equals(this.name, other.name)) {
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
        if (!Objects.equals(this.versionCode, other.versionCode)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.dataPrivate, other.dataPrivate)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.name);
        hash = 89 * hash + Objects.hashCode(this.versionMajor);
        hash = 89 * hash + Objects.hashCode(this.versionMinor);
        hash = 89 * hash + Objects.hashCode(this.versionBuild);
        hash = 89 * hash + Objects.hashCode(this.versionCode);
        hash = 89 * hash + Objects.hashCode(this.description);
        hash = 89 * hash + Objects.hashCode(this.dataPrivate);
        return hash;
    }

    @Override
    public String displayName() {
        return String.format("%s v%d.%d.%d %s", name, versionMajor,
                versionMinor, versionBuild, versionCode);
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
