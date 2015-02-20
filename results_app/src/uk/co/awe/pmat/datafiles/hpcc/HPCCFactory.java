package uk.co.awe.pmat.datafiles.hpcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.datafiles.DataConfig;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.datafiles.DataFileFactory;
import uk.co.awe.pmat.datafiles.FileChecker;
import uk.co.awe.pmat.db.ErrorType;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.utils.FileUtils;
import uk.co.awe.pmat.utils.RegExpFilenameFilter;

/**
 * A {@code DataFileFactory} used to load HPCC files.
 * 
 * @author AWE Plc copyright 2013
 */
public class HPCCFactory extends DataFileFactory {

	private static final Logger LOG = LoggerFactory
			.getLogger(HPCCFactory.class);

	private static final String FILE_TYPE = "HPCC File";
	private static final String FILE_REGEX = ".*\\.txt";
	private static final String START_SUMMARY = "Begin of Summary section.";
	private static final String END_SUMMARY = "End of Summary section.";
	private static final Pattern DATE_REGEX = Pattern
			.compile("Current time \\(\\d+\\) is (.*)");
	private static final Pattern LINE_REGEX = Pattern.compile("(\\S+)=(\\S+)");
	private static final String VERSION_MAJOR = "VersionMajor";
	private static final String VERSION_MINOR = "VersionMinor";
	private static final String VERSION_MICRO = "VersionMicro";
	private static final String VERSION_RELEASE = "VersionRelease";
	private static final String APPLICATION_NAME = "HPCC";
	private static final String DIVIDER_LINE = "########################################################################";
	private static final String END_OF_CHALLENGE = "End of HPC Challenge tests.";

	/**
	 * An {@code Enum} representation of all the parameters in the HPCC file
	 * that we want to store.
	 */
	private enum Parameters {
		sizeof_char, sizeof_short, sizeof_int, sizeof_long, sizeof_void_ptr, sizeof_size_t, sizeof_float, sizeof_double, sizeof_s64Int, sizeof_u64Int, sizeof_struct_double_double, CommWorldProcs, HPL_N, HPL_NB, HPL_nprow, HPL_npcol, HPL_depth, HPL_nbdiv, HPL_nbmin, HPL_cpfact, HPL_crfact, HPL_ctop, HPL_order, HPLMaxProcs, HPLMinProcs, DGEMM_N, PTRANS_residual, PTRANS_n, PTRANS_nb, PTRANS_nprow, PTRANS_npcol, MPIRandomAccess_LCG_TimeBound, MPIRandomAccess_LCG_Algorithm, MPIRandomAccess_N, MPIRandomAccess_TimeBound, MPIRandomAccess_Algorithm, RandomAccess_LCG_N, RandomAccess_N, STREAM_VectorSize, STREAM_Threads, FFT_N, MPIFFT_Procs, FFTEnblk, FFTEnp, FFTEl2size, M_OPENMP, omp_get_num_threads, omp_get_max_threads, omp_get_num_procs, MemProc, MemSpec, MemVal, CPS_HPCC_FFT_235, CPS_HPCC_FFTW_ESTIMATE, CPS_HPCC_MEMALLCTR, CPS_HPL_USE_GETPROCESSTIMES, CPS_RA_SANDIA_NOPT, CPS_RA_SANDIA_OPT2, CPS_USING_FFTW,
	}

	/**
	 * An {@code Enum} representation of all the HPPC results we want to store.
	 */
	private enum Results {
		MPI_Wtick, HPL_Tflops, HPL_time, HPL_eps, HPL_RnormI, HPL_Anorm1, HPL_AnormI, HPL_Xnorm1, HPL_XnormI, HPL_BnormI, HPL_dMACH_EPS, HPL_dMACH_SFMIN, HPL_dMACH_BASE, HPL_dMACH_PREC, HPL_dMACH_MLEN, HPL_dMACH_RND, HPL_dMACH_EMIN, HPL_dMACH_RMIN, HPL_dMACH_EMAX, HPL_dMACH_RMAX, HPL_sMACH_EPS, HPL_sMACH_SFMIN, HPL_sMACH_BASE, HPL_sMACH_PREC, HPL_sMACH_MLEN, HPL_sMACH_RND, HPL_sMACH_EMIN, HPL_sMACH_RMIN, HPL_sMACH_EMAX, HPL_sMACH_RMAX, dweps, sweps, StarDGEMM_Gflops, StarSTREAM_Copy, StarSTREAM_Scale, StarSTREAM_Add, MPIFFT_N, MPIFFT_Gflops, MPIFFT_maxErr, PTRANS_GBs, PTRANS_time, MPIFFT_time0, MPIFFT_time1, MPIFFT_time2, MPIFFT_time3, MPIFFT_time4, MPIFFT_time5, MPIFFT_time6, StarSTREAM_Triad, SingleSTREAM_Copy, SingleSTREAM_Scale, SingleSTREAM_Add, SingleSTREAM_Triad, StarFFT_Gflops, SingleFFT_Gflops, SingleDGEMM_Gflops, MPIRandomAccess_LCG_N, MPIRandomAccess_LCG_time, MPIRandomAccess_LCG_CheckTime, MPIRandomAccess_LCG_Errors, MPIRandomAccess_LCG_ErrorsFraction, MPIRandomAccess_LCG_ExeUpdates, MPIRandomAccess_LCG_GUPs, MPIRandomAccess_time, MPIRandomAccess_CheckTime, MPIRandomAccess_Errors, MPIRandomAccess_ErrorsFraction, MPIRandomAccess_ExeUpdates, MPIRandomAccess_GUPs, StarRandomAccess_LCG_GUPs, SingleRandomAccess_LCG_GUPs, StarRandomAccess_GUPs, SingleRandomAccess_GUPs, MaxPingPongLatency_usec, RandomlyOrderedRingLatency_usec, MinPingPongBandwidth_GBytes, RandomlyOrderedRingBandwidth_GBytes, NaturallyOrderedRingBandwidth_GBytes, MinPingPongLatency_usec, AvgPingPongLatency_usec, MaxPingPongBandwidth_GBytes, AvgPingPongBandwidth_GBytes, NaturallyOrderedRingLatency_usec,
	}

	@Override
	protected DataFile<?> loadFile(File file, FileReader fileReader)
			throws IOException {

		final BufferedReader reader = new BufferedReader(fileReader);

		skipToSummary(reader);
		final Map<String, String> summary = processSummary(reader);

		final Date runDate = getDate(reader);
		final String runId = FileUtils.getMD5sum(file);
		final String applicationName;

		applicationName = getApplicationName(summary);

		final Collection<String> flags = Collections.emptyList();
		final Run data = new Run(runId, runDate, file.getAbsolutePath(), flags);
		try {
			List<Value<?>> parameters = getParameters(summary);
			List<Value<Double>> results = getResults(summary);

			data.addDataSet(new RunData(parameters, results));
		} catch (NoSuchElementException ex) {
			throw new HPCCException("Malformed HPCC file: end of summary "
					+ "section not found", ex);
		} catch (RuntimeException ex) {
			throw ex; // Catch everything except RuntimeExceptions
		} catch (Exception ex) {
			LOG.warn("Problem when reading a HPCC file. "
					+ "Rethrown as IO exception: " + ex.getMessage());
			throw new HPCCException("Problem when reading a HPCC file.", ex);
		}

		return new HPCCFile(data, applicationName, HPCCVersion.V1_4_1);
	}

	/**
	 * Process the summary map and extract all the required parameters.
	 * 
	 * @param summary
	 *            a map containing all the data in the HPCC summary.
	 * @return the list of extracted parameters.
	 * @throws HPCCException
	 *             if an expected parameter was not found in the given summary.
	 */
	private List<Value<?>> getParameters(Map<String, String> summary)
			throws HPCCException {
		final List<Value<?>> parameters = new ArrayList<Value<?>>();

		for (Parameters paramName : Parameters.values()) {
			String name = paramName.toString();
			String value = summary.get(paramName.toString());
			if (value == null) {
				throw new HPCCException("Parameter " + name + " not found");
			}
			try {
				int param = Integer.parseInt(value);
				LOG.info("Adding integer parameter " + name + " = " + param);
				parameters.add(new Value<Integer>(name, Rank.UNKNOWN, param));
			} catch (NumberFormatException ex) {
				LOG.info("Adding string parameter " + name + " = " + value);
				parameters.add(new Value<String>(name, Rank.UNKNOWN, value));
			}
		}

		return parameters;
	}

	/**
	 * Process the summary map and extract all the required results.
	 * 
	 * @param summary
	 *            a map containing all the data in the HPCC summary.
	 * @return the list of extracted results.
	 * @throws HPCCException
	 *             if an expected parameter was not found in the given summary.
	 */
	private List<Value<Double>> getResults(Map<String, String> summary)
			throws HPCCException {
		final List<Value<Double>> results = new ArrayList<Value<Double>>();

		for (Results resultName : Results.values()) {
			String name = resultName.toString();
			String value = summary.get(resultName.toString());
			if (value == null) {
				throw new HPCCException("Result " + name + " not found");
			}
			try {
				double result;
				if ("inf".equals(value)) {
					result = Double.MAX_VALUE;
				} else {
					result = Double.parseDouble(value);
				}
				LOG.info("Adding result " + name + " = " + result);
				results.add(new Value<Double>(name, result, 0., ErrorType.NONE,
						Rank.UNKNOWN, 1L, 0L));
			} catch (NumberFormatException ex) {
				throw new HPCCException("Non number found for result " + name,
						ex);
			}
		}

		return results;
	}

	@Override
	public FileChecker getFileChecker() {
		return new HPCCFileChecker();
	}

	@Override
	public RegExpFilenameFilter getFileFilter() {
		return new RegExpFilenameFilter(FILE_TYPE, FILE_REGEX);
	}

	@Override
	public DataConfig getRunConfigData() {
		return null;
	}

	@Override
	public JPanel getRunConfigPanel() {
		return null;
	}

	@Override
	public String toString() {
		return FILE_TYPE + " (" + FileUtils.regexToGlob(FILE_REGEX) + ")";
	}

	/**
	 * Extract the HPCC application name from the summary map.
	 * 
	 * @param summary
	 *            a map containing all the data in the HPCC summary.
	 * @return the application name.
	 * @throws HPCCException
	 *             if the application information expected is not found.
	 */
	private String getApplicationName(Map<String, String> summary)
			throws HPCCException {

		String versionMajor;
		String versionMinor;
		String versionMicro;
		String versionRelease;

		if (summary.get(VERSION_MAJOR) == null) {
			throw new HPCCException("No version major found");
		} else {
			versionMajor = summary.get(VERSION_MAJOR);
		}
		if (summary.get(VERSION_MINOR) == null) {
			throw new HPCCException("No version minor found");
		} else {
			versionMinor = summary.get(VERSION_MINOR);
		}
		if (summary.get(VERSION_MICRO) == null) {
			throw new HPCCException("No version micro found");
		} else {
			versionMicro = summary.get(VERSION_MICRO);
		}
		if (summary.get(VERSION_RELEASE) == null) {
			throw new HPCCException("No version release found");
		} else {
			versionRelease = summary.get(VERSION_RELEASE);
		}
		return (APPLICATION_NAME + " v" + versionMajor + "." + versionMinor
				+ "." + versionMicro + " " + versionRelease);
	}

	/**
	 * Skip through the HPCC file until we get to the summary section.
	 * 
	 * @param scanStream
	 *            the HPCC file scanner.
	 * @throws IOException
	 *             if we get to the end of the file without finding the summary.
	 */
	private void skipToSummary(BufferedReader reader) throws IOException {
		String line;
		do {
			line = reader.readLine();
			if (line == null) {
				throw new HPCCException(
						"End of file encountered whilst skipping summary");
			}
		} while (!line.equals(START_SUMMARY));
	}

	/**
	 * Process the summary, reading each line "a=b" into a map where a is the
	 * key and b is the value.
	 * 
	 * @param scanStream
	 *            the HPCC file scanner.
	 * @return the map containing the processed summary.
	 * @throws IOException
	 *             if we read a line that match the expected pattern or we hit
	 *             the end of the file without getting to the end of the
	 *             summary.
	 */
	private Map<String, String> processSummary(BufferedReader reader)
			throws IOException {
		final Map<String, String> summary = new HashMap<String, String>();

		String line = reader.readLine();
		if (line == null) {
			throw new HPCCException(
					"End of file encountered whilst skipping summary");
		}

		while (!line.equals(END_SUMMARY)) {
			LOG.info("Processing line: " + line);
			final Matcher matcher = LINE_REGEX.matcher(line);
			if (!matcher.matches()) {
				throw new HPCCException("Invalid summary line " + line);
			} else {
				summary.put(matcher.group(1), matcher.group(2));
			}
			line = reader.readLine();
			if (line == null) {
				throw new HPCCException(
						"End of file encountered whilst skipping summary");
			}
		}
		return summary;
	}

	/**
	 * Process the date lines in the HPCC file and return the corresponding
	 * date.
	 * 
	 * @param scanStream
	 *            the HPCC file scanner.
	 * @return the date specified in the HPCC file.
	 * @throws IOException
	 *             if the date line is invalid.
	 */
	private Date getDate(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		if (line == null || !line.equals(DIVIDER_LINE)) {
			throw new HPCCException("Expecting " + DIVIDER_LINE + ", got "
					+ line);
		}
		line = reader.readLine();
		if (line == null || !line.equals(END_OF_CHALLENGE)) {
			throw new HPCCException("Expecting " + END_OF_CHALLENGE + ", got "
					+ line);
		}
		line = reader.readLine();
		String dateString = null;
		try {
			Matcher matcher = DATE_REGEX.matcher(line);
			if (!matcher.matches()) {
				throw new HPCCException("Invalid date line " + line);
			} else {
				DateFormat df = new SimpleDateFormat("EEE MMM dd hh:mm:ss yyyy");
				dateString = matcher.group(1);
				return df.parse(dateString);
			}
		} catch (ParseException ex) {
			throw new HPCCException("Invalid date " + dateString, ex);
		}
	}

}

/**
 * An {@code IOException} subclass corresponding to exceptions that might occur
 * whilst reading an HPCC file.
 * 
 * @author Hollcombe (Tessella plc)
 */
class HPCCException extends IOException {

	/**
	 * Create a new {@code HPCCException}.
	 * 
	 * @param msg
	 *            the exception message, retrievable via {@link #getMessage()}.
	 */
	HPCCException(String msg) {
		super("Malformed HPCC file: " + msg);
	}

	/**
	 * Create a new {@code HPCCException}.
	 * 
	 * @param msg
	 *            the exception message, retrievable via {@link #getMessage()}.
	 * @param cause
	 *            the nested cause of this {@code HPCCException}.
	 */
	HPCCException(String msg, Throwable cause) {
		super("Malformed HPCC file: " + msg, cause);
	}
}
