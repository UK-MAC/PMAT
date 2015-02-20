package uk.co.awe.pmat.datafiles.hpcc;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.datafiles.FileChecker;

/**
 * A {@code FileChecker} which is used to check the validity of HPCC files
 * before they are loaded.
 * 
 * @author AWE Plc copyright 2013
 */
public class HPCCFileChecker extends FileChecker {

	private static final Logger LOG = LoggerFactory
			.getLogger(HPCCFileChecker.class);

	private static final String LINE1 = "########################################################################";
	private static final String LINE2 = "This is the DARPA/DOE HPC Challenge Benchmark version";

	@Override
	public boolean checkHeader(final File file) throws IOException {

		final Scanner scanner = new Scanner(file);

		boolean isValid = false;

		try {
			if (!scanner.hasNext()) {
				throw new IOException("Empty file.");
			}
			String line = scanner.nextLine();
			if (line.equals(LINE1)) {
				line = scanner.nextLine();
				if (line.startsWith(LINE2)) {
					isValid = true;
				} else {
					LOG.debug("Invalid HPCC file. Expecting " + LINE2
							+ ", found " + line);
				}
			} else {
				LOG.debug("Invalid HPCC file. Expecting " + LINE1 + ", found "
						+ line);
			}
		} catch (IllegalStateException ex) {
			throw new IOException("Scanner closed on checkHeader.", ex);
		}

		return isValid;
	}

}
