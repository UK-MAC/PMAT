package uk.co.awe.pmat.datafiles.pmtm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.datafiles.DataConfig;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.datafiles.DataFileFactory;
import uk.co.awe.pmat.datafiles.FileChecker;
import uk.co.awe.pmat.db.Compiler;
import uk.co.awe.pmat.db.ErrorType;
import uk.co.awe.pmat.db.Machine;
import uk.co.awe.pmat.db.MetaData.Type;
import uk.co.awe.pmat.db.Mpi;
import uk.co.awe.pmat.db.OperatingSystem;
import uk.co.awe.pmat.db.Processor;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.utils.FileUtils;
import uk.co.awe.pmat.utils.RegExpFilenameFilter;

/**
 * A {@code DataFileFactory} used to load PMTM files.
 * 
 * @author AWE Plc copyright 2013
 */
public class PMTMFactory extends DataFileFactory {

	private static final Logger LOG = LoggerFactory
			.getLogger(PMTMFactory.class);

	private static final String FILE_TYPE = "PMTM File";
	private static final String FILE_REGEX = ".*\\.csv";

	private static final String FILE_HEADER = "Performance Modelling Timing File";
	private static final String FILE_VERSION = "PMTM Version";
	static final String OVERHEAD_PARAM_NAME = "Timer overhead for ";
	private static final String FILE_DELIMITER = "\\s*,\\s*";

	private static final String INTEGER_REGEX = "[+-]?[0-9]+";
	private static final String FLOAT_REGEX = "[+-]?(INF|[0-9]*\\.[0-9]+([Ee][+-]?[0-9]+)?)";

	@Override
	public RegExpFilenameFilter getFileFilter() {
		return new RegExpFilenameFilter(FILE_TYPE, FILE_REGEX);
	}

	@Override
	public FileChecker getFileChecker() {
		return new PMTMFileChecker();
	}

	/**
	 * Return an instance of a parameter from a scan stream based on the version
	 * of the PMTM library.
	 * 
	 * @param scanStream
	 *            the stream to get the data from.
	 * @param pmtmVersion
	 *            the version of the PMTM file we are reading.
	 * @return the parameter as an object.
	 * @throws IOException
	 *             on an invalid parameter read.
	 */
	private Value<?> getParameter(String line, PMTMVersion pmtmVersion)
			throws IOException {

		final String[] tokens = line.split(FILE_DELIMITER);

		if (tokens.length != 7) {
			throw new IOException("Invalid parameter line: " + line);
		}

		final Rank rank = getRank(tokens[2], pmtmVersion);
		final String name = tokens[4];
		final String valueString = tokens[6];

		final Value<?> param;
		if (valueString.matches(INTEGER_REGEX)) {
			param = new Value<Integer>(name, rank, Integer
					.parseInt(valueString));
		} else if (valueString.matches(FLOAT_REGEX)) {
			param = new Value<Double>(name, rank, Double
					.parseDouble(valueString));
		} else {
			param = new Value<String>(name, rank, valueString);
		}

		return param;
	}

	/**
	 * Return an instance of a timer from a scan stream based on the version of
	 * the PMTM library.
	 * 
	 * @param scanStream
	 *            the stream to get the data from
	 * @param pmtmVersion
	 *            the PMTM version that the stream is associated with
	 * @return the timer as an object
	 * @throws IOException
	 *             on bad read
	 */
	private Value<Double> getResult(String line, PMTMVersion pmtmVersion)
			throws IOException {

		final String[] tokens = line.split(FILE_DELIMITER);

		if ((tokens.length != 14 && pmtmVersion
				.isLaterThanOrEqualTo(PMTMVersion.V0_2_3))
				|| (tokens.length != 12 && pmtmVersion
						.isEarlierThan(PMTMVersion.V0_2_3))) {
			throw new IOException("Invalid result line: " + line);
		}

		final Rank rank = getRank(tokens[2], pmtmVersion);
		final String name = tokens[4];
		final String valueString = tokens[6];
		final String errorString = tokens[8];
		final String countString = tokens[11];

		// Pause count introduced in PMTM version 0.2.3.
		final String pauseString = pmtmVersion
				.isEarlierThan(PMTMVersion.V0_2_3) ? "0" : tokens[13];

		try {
			final Double value = parseDouble(valueString);
			final Double error = parseDouble(errorString);
			final Long count = parseLong(countString);
			final Long pause = parseLong(pauseString);

			final Value<Double> result = new Value<Double>(name, value, error,
					ErrorType.PMTM_STANDARD, rank, count, pause);

			return result;
		} catch (IOException ex) {
			throw new IOException("Invalid result line: " + line, ex);
		}
	}

	private Double parseDouble(String string) throws IOException {
		if (!string.matches(FLOAT_REGEX)) {
			throw new IOException("Invalid double: " + string);
		}
		if (string.equals("INF")) {
			return Double.POSITIVE_INFINITY;
		}
		if (string.equals("-INF")) {
			return Double.NEGATIVE_INFINITY;
		}
		return Double.parseDouble(string);
	}

	private Long parseLong(String string) throws IOException {
		if (!string.matches(INTEGER_REGEX)) {
			throw new IOException("Invalid long: " + string);
		}
		return Long.parseLong(string);
	}

	/**
	 * Return an overhead counter time as an instance of a parameter from a scan
	 * stream based on the version of the PMTM library.
	 * 
	 * @param scanStream
	 *            The stream to get the data from
	 * @param pmtmVersion
	 *            The PMTM version that the stream is associated with
	 * @return The parameter as an object
	 * @throws IOException
	 *             on an invalid parameter read
	 */
	private Value<Double> getOverhead(String line, PMTMVersion pmtmVersion)
			throws IOException {

		final String[] tokens = line.split(FILE_DELIMITER);

		if (tokens.length != 10) {
			throw new IOException("Invalid overhead line: " + line);
		}

		final Rank rank = Rank.ALL_RANKS;
		final String name = OVERHEAD_PARAM_NAME + tokens[4];
		final String valueString = tokens[6];
		final String errorString = tokens[8];

		if (!valueString.matches(FLOAT_REGEX)) {
			throw new IOException("Invalid value in overhead line: " + line);
		}

		if (!errorString.matches(FLOAT_REGEX)) {
			throw new IOException("Invalid error in overhead line: " + line);
		}

		final Double value = Double.parseDouble(valueString);

		Double error = Double.parseDouble(errorString);
		if (pmtmVersion.isEarlierThan(PMTMVersion.V0_2_6)) {
			error = error / 10000; // Correct bug in v0.2.4 of PMTM.
		}

		final Value<Double> overhead = new Value<Double>(name, value, error,
				ErrorType.PMTM_STANDARD, rank, 10000L, 0L);

		return overhead;
	}

	/**
	 * Parse the rank string and return the rank.
	 * 
	 * @param rankString
	 *            the rank string.
	 * @param version
	 *            the version of the PMTM file we are reading.
	 * @return the rank.
	 * @throws IOException
	 *             on invalid rank string.
	 */
	private Rank getRank(String rankString, PMTMVersion version)
			throws IOException {
		final Rank rank;
		if (version.isLaterThanOrEqualTo(PMTMVersion.V2_0_0)) {
			rank = Rank.fromString(rankString);
		} else {
			switch (rankString) {
			case "all":
				rank = Rank.ALL_RANKS;
				break;
			case "average of all":
				rank = Rank.RANK_AVG;
				break;
			case "max of all":
				rank = Rank.RANK_MAX;
				break;
			case "min of all":
				rank = Rank.RANK_MIN;
				break;
			default:
				try {
					rank = Rank.valueOf(rankString);
				} catch (NumberFormatException ex) {
					throw new IOException("Bad rank found: " + rankString, ex);
				}
				break;
			}
		}
		return rank;
	}

	/**
	 * A helper class which is used to contain the PMTM meta data as the file is
	 * read.
	 */
	static class PmtmMeta {
		String date = null;
		String time = null;
		String runID = null;
		Integer nprocs = null;
		String applicationName = null;
		String tag = null;
		OperatingSystem os = null;
		Compiler compiler = null;
		Mpi mpi = null;
		Machine machine = null;
		Processor processor = null;
	}

	/**
	 * Read from the given {@code Scanner}, parsing and storing the PMTM meta
	 * data.
	 * 
	 * @param scanStream
	 *            the {@code Scanner} to read from.
	 * @param pmtmMeta
	 *            the meta data object to save the parsed data into.
	 * @param pmtmVersion
	 *            the version of the PMTM file we are reading.
	 * @return {@code true} if the line read was the last line of the meta data,
	 *         or {@code false} otherwise.
	 */
	void processMetaData(String line, PMTMVersion version, PmtmMeta pmtmMeta)
			throws IOException {

		final String[] tokens = line.split(FILE_DELIMITER);
		LOG.info("Loading line " + line);

		switch (tokens[0]) {
		case "Application":
			pmtmMeta.applicationName = getStringFromTokens(tokens, 2);
			break;
		case "Date":
			pmtmMeta.date = getStringFromTokens(tokens, 2);
			break;
		case "Time":
			pmtmMeta.time = getStringFromTokens(tokens, 2);
			break;
		case "Run ID":
			pmtmMeta.runID = getStringFromTokens(tokens, 2);
			break;
		case "NProcs":
			pmtmMeta.nprocs = getIntegerFromTokens(tokens, 2);
			break;
		case "Tag":
			pmtmMeta.tag = getStringFromTokens(tokens, 2);
			break;
		case "OS": // OS and System are synonymous
		case "System":
			try {
				pmtmMeta.os = new OperatingSystem(
						getStringFromTokens(tokens, 3), getStringFromTokens(
								tokens, 2), getVersionPartFromTokens(tokens, 4,
								0), getVersionPartFromTokens(tokens, 4, 1),
						getVersionPartFromTokens(tokens, 4, 2),
						getVersionPartFromTokens(tokens, 4, 3),
						getStringFromTokens(tokens, 5));
			} catch (IOException ex) {
				LOG.debug("Failed to read System", ex);
			}
			break;
		case "Compiler":
			try {
				pmtmMeta.compiler = new Compiler(
						getStringFromTokens(tokens, 3), getStringFromTokens(
								tokens, 2), getVersionPartFromTokens(tokens, 4,
								0), getVersionPartFromTokens(tokens, 4, 1),
						getVersionPartFromTokens(tokens, 4, 2));
			} catch (IOException ex) {
				LOG.debug("Failed to read Compiler", ex);
			}
			break;
		case "MPI":
			try {
				pmtmMeta.mpi = new Mpi(getStringFromTokens(tokens, 3),
						getStringFromTokens(tokens, 2),
						getVersionPartFromTokens(tokens, 4, 0),
						getVersionPartFromTokens(tokens, 4, 1),
						getVersionPartFromTokens(tokens, 4, 2));
			} catch (IOException ex) {
				LOG.debug("Failed to read MPI", ex);
			}
			break;
		case "Machine":
			// For Machine in PMTM version < 2.2 there is no vendor.
			if (version.isEarlierThan(PMTMVersion.V2_2_0)) {
				pmtmMeta.machine = new Machine(getStringFromTokens(tokens, 2),
						null);
			} else {
				pmtmMeta.machine = new Machine(getStringFromTokens(tokens, 3),
						getStringFromTokens(tokens, 2));
			}
			break;
		case "Processor":
			// Processor was incorrectly output in PMTM version 2.3.0 and 2.2.1
			if (version != PMTMVersion.V2_3_0 && version != PMTMVersion.V2_2_1) {
				pmtmMeta.processor = new Processor(getStringFromTokens(tokens,
						3), // name
						getStringFromTokens(tokens, 2), // vendor
						getStringFromTokens(tokens, 4), // architecture
						getIntegerFromTokens(tokens, 6), // cores
						getIntegerFromTokens(tokens, 7), // threads per core
						getClockSpeedFromTokens(tokens, 5) // clock speed
				);
			}
			break;
		default:
			throw new IOException("Unknown meta data line: " + line);
		}
	}

	private String getStringFromTokens(String[] tokens, int index) {
		return tokens.length > index ? tokens[index] : null;
	}

	private Integer getIntegerFromTokens(String[] tokens, int index) {
		return tokens.length > index ? Integer.parseInt(tokens[index]) : null;
	}

	private Integer getVersionPartFromTokens(String[] tokens, int index,
			int versionPartIndex) throws IOException {
		return getVersionPart(getStringFromTokens(tokens, index),
				versionPartIndex);
	}

	private Integer getClockSpeedFromTokens(String[] tokens, int index) {
		if (index > tokens.length) {
			return null;
		} else {
			Pattern CLOCK_RE = Pattern
					.compile("([0-9]+)(\\.[0-9]+)?(GHz|MHz)?");
			Matcher matcher = CLOCK_RE.matcher(tokens[index]);
			if (matcher.matches()) {
				if (matcher.group(2) == null) {
					return Integer.parseInt(matcher.group(1));
				} else {
					if (matcher.group(3) == null
							|| matcher.group(3).equals("MHz")) {
						return Integer.parseInt(matcher.group(1));
					} else {
						return (int) (Double.parseDouble(matcher.group(1)
								+ matcher.group(2)) * 1000);
					}
				}
			} else {
				return null;
			}
		}
	}

	/**
	 * Split a version string into parts and return the requested part as an
	 * {@code Integer}. If no value was found for the given index return {@code
	 * null}.
	 * 
	 * @param versionStr
	 *            the version string to split.
	 * @param idx
	 *            the index of the request part of the split version.
	 * @return the index of the version part required.
	 */
	private Integer getVersionPart(String versionStr, int idx)
			throws IOException {
		if (versionStr == null) {
			return 0;
		}
		final String[] vTokens = versionStr.split("[.-]");
		try {
			return idx < vTokens.length ? Integer.valueOf(vTokens[idx].trim())
					: 0;
		} catch (NumberFormatException ex) {
			throw new IOException("Illegal version string: " + versionStr);
		}
	}

	/**
	 * Load the given PMTM file and return an object containing the loaded data.
	 * 
	 * @param fileToLoad
	 *            the file to load.
	 * @param fileReader
	 *            the {@link FileReader} being used to read the file.
	 * @return The {@link DataFile} that represents the loaded file.
	 * @throws IOException
	 *             on an error with the file
	 */
	@Override
    protected DataFile<PMTMVersion> loadFile(File fileToLoad, FileReader fileReader) throws IOException {
        
        final BufferedReader reader = new BufferedReader(fileReader);
        
        String line = reader.readLine();
        if (line == null || !line.trim().equals(FILE_HEADER)) {
            throw new IOException("Invalid header found when loading PMTM file\n");
        }
        
        line = reader.readLine();
        
        PMTMVersion version = null;
        boolean inMetaSection = true;
        boolean fileEnded = false;
        String[] flags = null;
        
        final PmtmMeta pmtmMeta = new PmtmMeta();
        final List<Value<?>> parameters = new ArrayList<>();
        final List<Value<Double>> results = new ArrayList<>();
        
        while (line != null) {
            if (line.trim().isEmpty()) {
                line = reader.readLine();
                continue; // Ignore empty lines.
            }
            
            final String[] tokens = line.split(FILE_DELIMITER);
            
            switch (tokens[0]) {
                case "PMTM Version":
                    version = getVersion(line);
                    break;
                case "Application": // Fall through
                case "Date":        // Fall through
                case "Time":        // Fall through
                case "Run ID":      // Fall through
                case "NProcs":      // Fall through
                case "OS":          // Fall through
                case "System":      // Fall through
                case "Compiler":    // Fall through
                case "Machine":     // Fall through
                case "MPI":         // Fall through
                case "Processor":   // Fall through
                case "Tag":
                    if (fileEnded || !inMetaSection) {
                        throw new IOException("Meta data line found not in header: " + line);
                    }
                    processMetaData(line, version, pmtmMeta);
                    if (tokens[0].equals("Time")
                            && (version != null && version.isEarlierThan(PMTMVersion.V0_2))) {
                        inMetaSection = false; // Use Time as end of header marker for PMTM version 0.2 and before.
                    }
                    break;
                case "#Type":
                    inMetaSection = false;
                    break;
                case "Overhead":
                    if (fileEnded || inMetaSection) {
                        throw new IOException("Overhead line found in header: " + line);
                    }
                    results.add(getOverhead(line, version));
                    break;
                case "Parameter":
                    parameters.add(getParameter(line, version));
                    break;
                case "Timer":
                    results.add(getResult(line, version));
                    break;
                case "End of File":
                    fileEnded = true;
                    break;
                case "Flags":
                    if (tokens.length > 2) {
                        flags = Arrays.copyOfRange(tokens, 2, tokens.length);
                    }
                    break;
                case "Environ":
                    // TODO: Ignored for now.
                    break;
                default:
                    throw new IOException("Invalid line found in PMTM file: " + line);
            }
            line = reader.readLine();
        }
        
        if (version == null) {
            throw new IOException("Incomplete PMTM file: no PMTM version line found");
        }
        
        if (version.isLaterThanOrEqualTo(PMTMVersion.V0_2_7) && !fileEnded) {
            // PMTM files later than V0.2.7 should have "End of File" markers.
            throw new IOException("Incomplete PMTM file: end of file marker not found");
        }
        
        Date date;
        try {
            final DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            date = df.parse(pmtmMeta.date.trim() + " " + pmtmMeta.time.trim());
        } catch (NullPointerException | ParseException ex) {
            date = new Date(0L);
        }
        
        if (pmtmMeta.nprocs != null) {
            parameters.add(new Value<>("PE Count", Rank.UNKNOWN, pmtmMeta.nprocs));
        }
        
        if (pmtmMeta.runID == null) {
            pmtmMeta.runID = FileUtils.getMD5sum(fileToLoad);
        }
        
        final List<String> flagList;
        if (flags != null) {
            flagList = Arrays.asList(flags);
        } else {
            flagList = Collections.emptyList();
        }
        
        final Run run = new Run(pmtmMeta.runID, date, fileToLoad.getAbsolutePath(), flagList);
        
        run.setMetaData(Type.COMPILER, pmtmMeta.compiler);
        run.setMetaData(Type.MACHINE, pmtmMeta.machine);
        run.setMetaData(Type.MPI, pmtmMeta.mpi);
        run.setMetaData(Type.OPERATING_SYSTEM, pmtmMeta.os);
        run.setMetaData(Type.PROCESSOR, pmtmMeta.processor);
        
        run.addDataSet(new RunData(parameters, results));
        
        return new PMTMFile(run, pmtmMeta.applicationName, version, pmtmMeta.tag);
    }

	/**
	 * Returns the version of PMTM used to create this file.
	 * 
	 * @param line
	 *            the version line to parse.
	 * @return {@code Enum} Describing the library version
	 * @throws IOException
	 *             on bad read of the data
	 */
	protected static PMTMVersion getVersion(String line) throws IOException {

		final String[] tokens = line.split(FILE_DELIMITER);

		if (tokens.length != 3) {
			throw new IOException("Invalid version line " + line);
		}

		if (!tokens[0].equals(FILE_VERSION)) {
			throw new IOException("Invalid version line " + line);
		}

		try {
			return PMTMVersion.getFromString(tokens[2].trim());
		} catch (IllegalArgumentException ex) {
			throw new IOException("Version of PMTM is unknown, version "
					+ "found \"" + tokens[2] + "\"");
		}
	}

	@Override
	public String toString() {
		return FILE_TYPE + " (" + FileUtils.regexToGlob(FILE_REGEX) + ")";
	}

	@Override
	public DataConfig getRunConfigData() {
		return null;
	}

	@Override
	public JPanel getRunConfigPanel() {
		return null;
	}

}
