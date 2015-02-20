package uk.co.awe.pmat.datafiles.pmtm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.datafiles.FileChecker;

/**
 * A {@code FileChecker} which is used to check the validity of PMTM files
 * before they are loaded.
 * 
 * @author AWE Plc copyright 2013
 */
public class PMTMFileChecker extends FileChecker {

	private static final Logger LOG = LoggerFactory
			.getLogger(PMTMFileChecker.class);
	private static final String FILE_HEADER = "Performance Modelling Timing File";

	@Override
    public boolean checkHeader(File file) throws IOException {

        try(final BufferedReader reader = new BufferedReader(new FileReader(file))) {
            final String firstLine = reader.readLine();

            if (firstLine == null) {
                throw new IOException("Empty file: " + file.getAbsolutePath());
            }

            if (firstLine.equals(FILE_HEADER)) {
                return true;
            } else {
                LOG.info(file.getAbsolutePath() + ": not recognised. Does not "
                        + "start with \'" + FILE_HEADER + "\', starts with \'"
                        + firstLine + "\'.");
                return false;
            }
        }

    }
}
