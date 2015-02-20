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
public final class Processor implements MetaData {

    private static final LinkedHashMap<String, Class<?>> DATA_FIELDS;
    
    static {
        DATA_FIELDS = new LinkedHashMap<>();
        DATA_FIELDS.put("name", String.class);
        DATA_FIELDS.put("vendor", String.class);
        DATA_FIELDS.put("architecture", String.class);
        DATA_FIELDS.put("cores", Integer.class);
        DATA_FIELDS.put("threadsPerCore", Integer.class);
        DATA_FIELDS.put("clockSpeed", Integer.class);
    }
    
    static LinkedHashMap<String, Class<?>> getDataFields() {
        return new LinkedHashMap<>(DATA_FIELDS);
    }
    
    private final String name;
    private final String vendor;
    private final String architecture;
    private final Integer cores;
    private final Integer threadsPerCore;
    private final Integer clockSpeed;

    /**
     * Create a new {@code Processor}.
     * 
     * @param name the name of the processor.
     * @param vendor the vendor of the processor.
     * @param architecture the processor architecture.
     * @param cores the number of core of the architecture.
     * @param threadsPerCore the number of hardware threads per core on the
     * architecture.
     * @param clockSpeed the core clock speed of the architecture.
     */
    public Processor(String name, String vendor, String architecture, Integer cores, Integer threadsPerCore, Integer clockSpeed) {
        this.name = name;
        this.vendor = vendor;
        this.architecture = architecture;
        this.cores = cores;
        this.threadsPerCore = threadsPerCore;
        this.clockSpeed = clockSpeed;
    }

    /**
     * Create a new {@code Processor}.
     * 
     * @param valueMap a map of field names and values from which to populate
     * this processor.
     */
    public Processor(Map<String, Object> valueMap) {
        this(
            (String) valueMap.get("name"),
            (String) valueMap.get("vendor"),
            (String) valueMap.get("architecture"),
            (Integer) valueMap.get("cores"),
            (Integer) valueMap.get("threadsPerCore"),
            (Integer) valueMap.get("clockSpeed")
        );
    }

    @Override
    public Object getData(String dataName) {
        switch (dataName) {
            case "name": return name;
            case "vendor": return vendor;
            case "architecture": return architecture;
            case "cores": return cores;
            case "threadsPerCore": return threadsPerCore;
            case "clockSpeed": return clockSpeed;
            default:
                throw new IllegalArgumentException("Unknown data name " + dataName);
        }
    }

    @Override
    public Map<String, Object> getDataMap() {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", name);
        dataMap.put("vendor", vendor);
        dataMap.put("architecture", architecture);
        dataMap.put("cores", cores);
        dataMap.put("threadsPerCore", threadsPerCore);
        dataMap.put("clockSpeed", clockSpeed);
        return dataMap;
    }

    @Override
    public Type getType() {
        return Type.PROCESSOR;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Processor)) {
            return false;
        }
        final Processor other = (Processor) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.vendor, other.vendor)) {
            return false;
        }
        if (!Objects.equals(this.architecture, other.architecture)) {
            return false;
        }
        if (!Objects.equals(this.cores, other.cores)) {
            return false;
        }
        if (!Objects.equals(this.threadsPerCore, other.threadsPerCore)) {
            return false;
        }
        if (!Objects.equals(this.clockSpeed, other.clockSpeed)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.vendor);
        hash = 41 * hash + Objects.hashCode(this.architecture);
        hash = 41 * hash + Objects.hashCode(this.cores);
        hash = 41 * hash + Objects.hashCode(this.threadsPerCore);
        hash = 41 * hash + Objects.hashCode(this.clockSpeed);
        return hash;
    }

    @Override
    public String displayName() {
        String longname;
        longname = vendor + " " + name;
        longname += architecture != null ? " " + architecture : "";
        longname += clockSpeed != null ? " " + clockSpeed + " MHz" : "";
        longname += cores != null ? " " + cores + " cores" : "";
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
