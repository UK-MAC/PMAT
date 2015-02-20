package uk.co.awe.pmat.datafiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The abstract base class of all run file checkers which are used to process
 * files and check whether they match the conditions for processing by the run
 * factories.
 * 
 * @author AWE Plc copyright 2013
 */
public abstract class FileChecker {

	private static final Logger LOG = LoggerFactory
			.getLogger(FileChecker.class);

	/**
	 * Checks a file to make sure it is readable and contains at least one non
	 * blank line.
	 * 
	 * @param file
	 *            The file to check
	 * @return {@code true} if file is valid, {@code false} otherwise
	 */
	public boolean checkFile(File file) {
		boolean goodFile = false;
		if (file.exists() && file.isFile() && file.canRead()) {
			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader(file));
				String line = in.readLine();
				while (line != null) {
					if (!line.isEmpty()) {
						goodFile = true;
					}
					line = in.readLine();
				}
			} catch (IOException ex) {
				LOG.debug("checkFile returned false for " + file + ": "
						+ ex.getMessage());
			} finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					LOG.debug("error closing reader for " + file + ": "
							+ ex.getMessage());
				}
			}
		}
		return goodFile;
	}

	/**
	 * Checks the header on the file.
	 * 
	 * @param file
	 *            The file for which we are checking the header
	 * @return {@code true} if the file has a valid header, {@code false}
	 *         otherwise
	 * @throws IOException
	 *             on an error reading the file
	 */
	public abstract boolean checkHeader(File file) throws IOException;
}
