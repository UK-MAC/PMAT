package uk.co.awe.pmat.analysis;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class which can be used to load all classes from a given jar which are
 * implementations of the {@code AnalysisMethod} interface.
 * 
 * @author AWE Plc copyright 2013
 * @see AnalysisMethod
 */
public final class AnalysisMethodJarLoader extends URLClassLoader {

	private static final Logger LOG = LoggerFactory
			.getLogger(AnalysisMethodJarLoader.class);

	private final List<Class<AnalysisMethod>> analysisMethods = new ArrayList<Class<AnalysisMethod>>();

	/**
	 * Create a new {@code AnalysisMethodJarLoader} and load all classes in the
	 * given jar file that implement the {@code AnalysisMethod} interface. All
	 * class files within the jar are queried and any that match the interface
	 * are recorded.
	 * 
	 * @param file
	 *            The jar file
	 * @throws IOException
	 *             if an I/O error occurs opening the jar file
	 */
	public AnalysisMethodJarLoader(File file) throws IOException {
		super(new URL[] { file.toURI().toURL() });

		JarFile jarFile = new JarFile(file);
		List<String> classNames = new ArrayList<String>();

		for (Enumeration<JarEntry> entries = jarFile.entries(); entries
				.hasMoreElements();) {

			String name = entries.nextElement().getName();
			if (name.endsWith(".class")) {
				// Convert path into package name.
				classNames.add(name.substring(0, name.length() - 6).replace(
						"/", "."));
			}
		}

		for (String className : classNames) {
			try {
				Class<?> cls = loadClass(className);
				boolean isAnalysisClass = false;
				for (Class<?> inter : cls.getInterfaces()) {
					if (inter.equals(AnalysisMethod.class)) {
						isAnalysisClass = true;
						break;
					}
				}
				if (isAnalysisClass) {
					LOG.info("Adding analysis method class " + className);
					@SuppressWarnings("unchecked")
					Class<AnalysisMethod> analysis = (Class<AnalysisMethod>) cls;
					analysisMethods.add(analysis);
				}
			} catch (ClassNotFoundException ex) {
				LOG.debug("Failed to load class in jar: " + className, ex);
			}
		}
	}

	/**
	 * Return the loaded {@code AnalysisMethod} classes.
	 * 
	 * @return A list of classes
	 */
	public List<Class<AnalysisMethod>> getAnalysisMethods() {
		return Collections.unmodifiableList(analysisMethods);
	}

}
