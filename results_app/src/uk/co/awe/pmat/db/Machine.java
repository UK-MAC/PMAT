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
public final class Machine implements MetaData {

    private static final LinkedHashMap<String, Class<?>> DATA_FIELDS;
    
    static {
        DATA_FIELDS = new LinkedHashMap<>();
        DATA_FIELDS.put("name", String.class);
        DATA_FIELDS.put("vendor", String.class);
    }
    
    static LinkedHashMap<String, Class<?>> getDataFields() {
        return new LinkedHashMap<>(DATA_FIELDS);
    }

    private final String name;
    private final String vendor;

    /**
     * Create a a new {@code Machine}.
     * 
     * @param name the machine name.
     * @param vendor the machine vendor.
     */
    public Machine(String name, String vendor) {
        this.name = name;
        this.vendor = vendor;
    }

    /**
     * Create a new {@code Machine}.
     * 
     * @param valueMap a map of field names and values from which to populate
     * this machine.
     */
    public Machine(Map<String, Object> valueMap) {
        this(
            (String) valueMap.get("name"),
            (String) valueMap.get("vendor")
        );
    }
    
    @Override
    public Object getData(String dataName) {
        switch (dataName) {
            case "name": return name;
            case "vendor": return vendor;
            default:
                throw new IllegalArgumentException("Unknown data name " + dataName);
        }
    }

    @Override
    public Map<String, Object> getDataMap() {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("vendor", vendor);
        return dataMap;
    }

    @Override
    public Type getType() {
        return Type.MACHINE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Machine)) {
            return false;
        }
        final Machine other = (Machine) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public String displayName() {
        return name;
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
