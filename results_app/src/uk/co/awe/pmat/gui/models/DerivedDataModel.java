package uk.co.awe.pmat.gui.models;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.swing.ComboBoxModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.GuiModel;
import uk.co.awe.pmat.deriveddata.DerivedData;
import uk.co.awe.pmat.deriveddata.DerivedDataParser;
import uk.co.awe.pmat.deriveddata.Function;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.deriveddata.ParseException;
import uk.co.awe.pmat.deriveddata.ParserValues.Property;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Graph;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.axis.Axis;
import uk.co.awe.pmat.db.axis.AxisName;
import uk.co.awe.pmat.db.axis.AxisType;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.gui.events.EventHub;
import uk.co.awe.pmat.gui.events.EventHub.EventType;
import uk.co.awe.pmat.gui.events.EventListener;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;
import uk.co.awe.pmat.utils.ChangeEventSupport;
import uk.co.awe.pmat.utils.Pair;

/**
 * The model behind the derived data panel which is used to create new data to
 * plot from existing data. This is done via a free text field parsed by a
 * parser built with JavaCC. The resulting "fields" are created as a tree of
 * {@code DerivedData} objects, either constant values, function objects or
 * wrappers around the database entities.
 * 
 * @author AWE Plc copyright 2013
 */
public final class DerivedDataModel implements GuiModel, EventListener {

	private static final Logger LOG = LoggerFactory
			.getLogger(DerivedDataModel.class);

	private final ChangeEventSupport eventSupport = new ChangeEventSupport(this);
	private final EventHub eventHub;
	private final Map<String, Pair<String, DerivedData>> derivedData;
	private final Map<String, Function> functionMap;
	private final Map<String, Variable> variableMap;
	private final Map<String, Property> propertyMap;
	private final DefaultComboBoxModel<String> variableComboBoxModel;
	private final DefaultComboBoxModel<String> functionComboBoxModel;
	private final DefaultComboBoxModel<String> propertyComboBoxModel;
	private final DefaultComboBoxModel<String> newVarsComboBoxModel;
	private final Document variableFormulaDocument;

	/**
	 * Create a new {@code DerivedDataModel} with a reference to the underlying
	 * analysis model. During instantiation of this object the analysis
	 * functions directory will be scanned for all classes and these will be
	 * loaded ready to be used as functions in the parser.
	 * 
	 * @param analysisModel
	 *            the underlying analysis model.
	 */
	public DerivedDataModel(EventHub eventHub) {
        this.eventHub = eventHub;

        derivedData = new HashMap<>();
        variableMap = new HashMap<>();

        propertyMap = new HashMap<>();
        for (MetaData.Type type : MetaData.Type.values()) {
            propertyMap.put(type.displayName().replace(" ", ""),
                    new Property(type.name()));
        }

        functionMap = new HashMap<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            final String functionsDirectory = "uk/co/awe/pmat/deriveddata/functions";
            final String functionsPackage = functionsDirectory.replace("/", ".");

            final Enumeration<URL> urls = loader.getResources(functionsDirectory);
            if (!urls.hasMoreElements()) {
                throw new IOException("Failed to open derived data functions directory");
            }

            while (urls.hasMoreElements()) {
                final List<File> files = findFiles(urls, functionsDirectory);
                loadFiles(files, loader, functionsPackage);
            }
        } catch (IOException ex) {
            LOG.error("IOError whilst loading derived data functions", ex);
        }

        variableComboBoxModel = new DefaultComboBoxModel<>("Select variable...");
        functionComboBoxModel = new DefaultComboBoxModel<>("Select function...");
        propertyComboBoxModel = new DefaultComboBoxModel<>("Select property...");
        newVarsComboBoxModel = new DefaultComboBoxModel<>();
        variableFormulaDocument = new PlainDocument();

        receiveEvent(null);
    }

	/**
	 * For each files load that file as a class in the given package.
	 * 
	 * @param files
	 *            The files to load
	 * @param loader
	 *            The class loader used to load the files
	 * @param functionsPackage
	 *            The Java package these classes exist in
	 * @throws IOException
	 *             If an error occurs loading the files
	 */
	private void loadFiles(final List<File> files,
            ClassLoader loader,
            final String functionsPackage) throws IOException {
        if (files == null) {
            throw new IOException("Cannot open derived data functions directory");
        }
        for (File file : files) {
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            String clsName = file.getName().replace(".class", "");
            try {
                Class<?> cls = loader.loadClass(functionsPackage + "." + clsName);
                if (!cls.isMemberClass() && !cls.isAnonymousClass()) {
                    LOG.info("Adding derived data function " + cls);
                    functionMap.put(clsName, (Function) cls.newInstance());
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                LOG.error("Failed to load derived data function " + clsName, ex);
            }
        }
    }

	/**
	 * Scan the given URLs looking for files. The URL may be inside a Jar.
	 * 
	 * @param urls
	 *            The URLs to scan
	 * @param functionsDirectory
	 *            The directory we are looking for
	 * @return The found files
	 * @throws IOException
	 *             if an error occurs whilst scanning the URLs
	 */
	private List<File> findFiles(final Enumeration<URL> urls,
            final String functionsDirectory) throws IOException {
        final List<File> files;
        final URL nextEl = urls.nextElement();
        LOG.info("Loading derived data functions from " + nextEl.getFile());
        if (nextEl.getProtocol().equals("jar")) {
            final String urlFile = nextEl.getFile();
            final String jarFile = urlFile.substring(urlFile.indexOf(":") + 1,
                    urlFile.indexOf("!"));
            files = new ArrayList<>();
            for (Enumeration<JarEntry> entries = new JarFile(jarFile).entries();
                    entries.hasMoreElements();) {
                final JarEntry entry = entries.nextElement();
                final String parent = new File(entry.getName()).getParent();
                if (functionsDirectory.equals(parent)) {
                    files.add(new File(entry.getName()));
                }
            }
        } else {
            File[] filesArr = new File(nextEl.getFile()).listFiles();
            files = Arrays.asList(filesArr);
        }
        return files;
    }

	/**
	 * Return the text that was used to generate a given {@code DerivedData}
	 * object.
	 * 
	 * @param name
	 *            The name of the {@code DerivedData}
	 * @return The text that was parsed to create it
	 */
	public String getDerivedDataEquation(String name) {
		if (derivedData.get(name) == null) {
			return null;
		}
		return derivedData.get(name).getFirst();
	}

	/**
	 * Return a the {@code DerivedData} object with the given name.
	 * 
	 * @param name
	 *            The name of the {@code DerivedData}
	 * @return The {@code DerivedData}
	 */
	public DerivedData getDerivedData(String name) {
		if (derivedData.get(name) == null) {
			return null;
		}
		return derivedData.get(name).getSecond();
	}

	/**
	 * Get the names of the all the {@code DerivedData} objects currently
	 * stored.
	 * 
	 * @return The {@code DerivedData} names
	 */
	public Collection<String> getDerivedDataNames() {
		return Collections.unmodifiableSet(derivedData.keySet());
	}

	/**
	 * Create a parser which will parse the text read from the given reader.
	 * 
	 * @param reader
	 *            The reader from which the parser will read
	 * @return A derived data parser
	 */
	public DerivedDataParser getParser(Reader reader) {
		DerivedDataParser parser = new DerivedDataParser(reader);
		parser.setFunctions(functionMap);
		parser.setVariables(variableMap);
		parser.setProperties(propertyMap);
		return parser;
	}

	/**
	 * Return the model behind the "Insert Property" combo box.
	 * 
	 * @return The combo box model
	 */
	public ComboBoxModel<String> getPropertyComboBoxModel() {
		return propertyComboBoxModel;
	}

	/**
	 * Return the model behind the "Insert Variable" combo box.
	 * 
	 * @return The combo box model
	 */
	public ComboBoxModel<String> getVariableComboBoxModel() {
		return variableComboBoxModel;
	}

	/**
	 * Return the model behind the "Insert Function" combo box.
	 * 
	 * @return The combo box model
	 */
	public ComboBoxModel<String> getFunctionComboBoxModel() {
		return functionComboBoxModel;
	}

	/**
	 * Return the model behind the "New Variable" combo box.
	 * 
	 * @return The combo box model
	 */
	public ComboBoxModel<String> getNewVarsComboBoxModel() {
		return newVarsComboBoxModel;
	}

	/**
	 * Return the model behind the equation text field.
	 * 
	 * @return The document model
	 */
	public Document getVariableFormulaDocument() {
		return variableFormulaDocument;
	}

	/**
	 * Remove the derived data field with the given name.
	 * 
	 * @param varName
	 *            The name of the derived data field to remove
	 */
	public void removeDerivedData(String varName) {
		derivedData.remove(varName);
		eventHub.notifyEvent(EventHub.EventType.DERIVED_DATA);
		updateSelectionBox(newVarsComboBoxModel, getDerivedDataNames());
	}

	/**
	 * Set the derived data field with the given name to have the parsed and
	 * unparsed values provided.
	 * 
	 * @param varName
	 *            The name of the derived data field to set
	 * @param equation
	 *            The unparsed equation
	 * @param result
	 *            The parsed equation
	 */
	public void setDerivedData(String varName, String equation, DerivedData result) {
        derivedData.put(varName, new Pair<>(equation, result));
        eventHub.notifyEvent(EventHub.EventType.DERIVED_DATA);
        updateSelectionBox(newVarsComboBoxModel, getDerivedDataNames());
    }

	@Override
	public void receiveEvent(EventType eventType) {
		variableMap.clear();
		try {
			for (String param : DatabaseManager.getConnection().getFields(
					Restriction.NONE, Category.PARAMETER)) {
				variableMap.put(param, new Variable(Variable.Type.PARAMETER,
						param));
			}

			for (String result : DatabaseManager.getConnection().getFields(
					Restriction.NONE, Category.RESULT)) {
				variableMap.put(result, new Variable(Variable.Type.RESULT,
						result));
			}
		} catch (DatabaseException ex) {
			LOG.error("Error populating derived data lists", ex);
		}

		updateSelectionBox(variableComboBoxModel, variableMap.keySet());
		updateSelectionBox(functionComboBoxModel, functionMap.keySet());
		updateSelectionBox(propertyComboBoxModel, propertyMap.keySet());
	}

	/**
	 * Update the given combo model box using the given items.
	 * 
	 * @param model
	 *            the combo box model to update.
	 * @param items
	 *            the items to update the model with.
	 */
	private void updateSelectionBox(
            final DefaultComboBoxModel<String> model,
            final Collection<String> items) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                model.removeAllElements();
                final List<String> sortedList = new ArrayList<>(items);
                Collections.sort(sortedList);
                for (String varName : sortedList) {
                    model.addElement(varName);
                }

                model.selectHeader();
                try {
                    variableFormulaDocument.remove(0, variableFormulaDocument.getLength());
                } catch (BadLocationException ex) {
                    LOG.error("Invalid range when clearing text", ex);
                }
            }
        });
    }

	@Override
	public void addChangeListener(ChangeListener listener) {
		eventSupport.addChangeListener(listener);
	}

	/**
	 * Return persistence objects for each {@code DerivedData} object defined.
	 * 
	 * @return the derived data persistence objects.
	 */
	public List<uk.co.awe.pmat.db.DerivedData> getAllDerivedData() {
        final List<uk.co.awe.pmat.db.DerivedData> dData = new ArrayList<>();
        for (Map.Entry<String, Pair<String, DerivedData>> entry
                : derivedData.entrySet()) {
            final String name = entry.getKey();
            final String equation = entry.getValue().getFirst();
            dData.add(new uk.co.awe.pmat.db.DerivedData(name, equation));
        }
        return dData;
    }

	/**
	 * Add a new {@code DerivedData} object for each derived data persistence
	 * object given.
	 * 
	 * @param dData
	 *            the derived data persistence objects.
	 */
	public void updateFromAnalysis(Iterable<uk.co.awe.pmat.db.DerivedData> dData) {
        try {
            for (uk.co.awe.pmat.db.DerivedData data : dData) {
                final String equation = data.getEquation();
                DerivedData dd = getParser(new StringReader(equation)).parse();
                derivedData.put(data.getName(), new Pair<>(equation, dd));
            }
        } catch (ParseException | InvalidArgumentsException ex) {
            LOG.error("Failed to load analysis", ex);
        }
    }

	/**
	 * Create a collections of {@code Graph}s using the given collection where
	 * derived axes are updated such that they contain valid references to
	 * derived data functions.
	 * 
	 * @param graphs
	 *            the graphs to update.
	 * @return the updated graphs.
	 */
	public Collection<Graph> updateDerivedDataAxes(Collection<Graph> graphs) {
        final List<Graph> newGraphs = new ArrayList<>();

        for (Graph graph : graphs) {
            final Axis xAxis = graph.getxAxis();
            final Axis yAxis = graph.getyAxis();

            final Axis newXAxis;
            final Axis newYAxis;

            if (xAxis.getType() == AxisType.DERIVED) {
                final String name = xAxis.displayName();
                final DerivedData dData = derivedData.get(name).getSecond();
                newXAxis = Axis.newDerivedAxis(AxisName.X1, name, dData);
            } else {
                newXAxis = xAxis;
            }

            if (yAxis.getType() == AxisType.DERIVED) {
                final String name = yAxis.displayName();
                final DerivedData dData = derivedData.get(name).getSecond();
                newYAxis = Axis.newDerivedAxis(AxisName.Y1, name, dData);
            } else {
                newYAxis = yAxis;
            }

            newGraphs.add(graph.updateAxes(newXAxis, newYAxis));
        }

        return newGraphs;
    }

	/**
	 * Return all the {@code DerivedData} defined paired with the name it name.
	 * 
	 * @return the derived data.
	 */
	public Collection<Pair<String, DerivedData>> getDerivedData() {
        final Collection<Pair<String, DerivedData>> data
                = new ArrayList<>(derivedData.size());
        for (Map.Entry<String, Pair<String, DerivedData>> entry : derivedData.entrySet()) {
            data.add(new Pair<>(entry.getKey(), entry.getValue().getSecond()));
        }
        return data;
    }
}
