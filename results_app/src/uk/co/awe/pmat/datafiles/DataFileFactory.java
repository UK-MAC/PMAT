package uk.co.awe.pmat.datafiles;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.utils.RegExpFilenameFilter;

/**
 * Superclass for factories which create a run object graph from a file.
 * 
 * @author AWE Plc copyright 2013
 */
public abstract class DataFileFactory {

	private static final Logger LOG = LoggerFactory
			.getLogger(DataFileFactory.class);

	/**
	 * Create a run object graph from a file reference.
	 * 
	 * @param fileToLoad
	 *            The file to load
	 * @return The Run that represents the loaded file
	 * @throws IOException
	 *             on an error with the file
	 */
	public DataFile<?> loadFile(File fileToLoad) throws IOException {

        final FileChecker fileChecker = getFileChecker();

        if (!fileChecker.checkFile(fileToLoad)) {
            throw new IOException("Unable to load file "
                    + fileToLoad.getAbsolutePath());
        }
        if (!fileChecker.checkHeader(fileToLoad)) {
            throw new InvalidHeaderException("Invalid header for file "
                    + fileToLoad.getAbsolutePath());
        }
        try (FileReader fileReader = new FileReader(fileToLoad)) {
            final DataFile<?> file = loadFile(fileToLoad, fileReader);
            if (file.getDataSets().isEmpty()) {
                throw new IOException("File appears to have no data in it.");
            }

            return file;
        }
    }

	/**
	 * Scan the given files for all valid files which can be loaded.
	 * 
	 * @param files
	 *            The files to scan
	 * @return A list of valid files
	 */
	public List<File> filterFiles(List<File> files) {

		List<File> filteredFiles = new ArrayList<File>();

		for (File file : files) {
			if (file.isDirectory()) {
				File[] subFiles = file.listFiles();
				filteredFiles.addAll(filterFiles(Arrays.asList(subFiles)));
			} else {
				try {
					if (getFileFilter().accept(file)
							&& getFileChecker().checkFile(file)
							&& getFileChecker().checkHeader(file)) {
						filteredFiles.add(file);
					}
				} catch (IOException ex) {
					LOG.debug("Invalid file " + file.getAbsolutePath(), ex);
				}
			}
		}

		return filteredFiles;
	}

	/**
	 * Load the given file and return an object containing the loaded data.
	 * 
	 * @param fileToLoad
	 *            the file to load.
	 * @param fileReader
	 *            the {@link FileReader} being used to read the file.
	 * @return The {@link DataFile} that represents the loaded file.
	 * @throws java.io.IOException
	 *             on an error with the file
	 */
	protected abstract DataFile<?> loadFile(File fileToLoad,
			FileReader fileReader) throws IOException;

	/**
	 * Return a FileChecker which can be used to test for valid run files.
	 * 
	 * @return A file checker
	 */
	public abstract FileChecker getFileChecker();

	/**
	 * Return a FilenameFilter which can be used to filter for valid run files.
	 * 
	 * @return A file name filter
	 */
	public abstract RegExpFilenameFilter getFileFilter();

	/**
	 * Return a RunConfigData class which encapsulates the run specific
	 * configuration data which can be displayed and edited via a
	 * RunConfigPanel.
	 * 
	 * @return A configuration data class
	 */
	public abstract DataConfig getRunConfigData();

	/**
	 * Return a RunConfigPanel which can be used to display and modify run
	 * specific configuration data.
	 * 
	 * @return A configuration panel
	 */
	public abstract JPanel getRunConfigPanel();

	/**
	 * Return a string representation of this factory which display what type of
	 * files this factory can process.
	 * 
	 * @return The string representation of the factory object
	 */
	@Override
	public abstract String toString();

}
