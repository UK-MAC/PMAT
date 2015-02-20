package uk.co.awe.pmat.datafiles.skampi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
import uk.co.awe.pmat.utils.DefaultHashMap;
import uk.co.awe.pmat.utils.FileUtils;
import uk.co.awe.pmat.utils.RegExpFilenameFilter;
import uk.co.awe.pmat.utils.StringUtils;


/**
 * A {@code DataFileFactory} used to load SkaMPI files.
 *
 * @author AWE Plc copyright 2013
 */
public class SkampiFactory extends DataFileFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SkampiFactory.class);

    private static final String SKAMPI_RUN_ID = "SKAMPI-";
    private static final String FILE_TYPE = "SkaMPI Results File";
    private static final String FILE_REGEX = ".*\\.(out|txt)";

    // Constants representing strings in the file
    private static final String COMMENT_LINE_START = "#";
    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy";
    private static final String DATE_TIME_END = "at";
    private static final String DATE_TIME_START = "Started";
    private static final String INSTRUCTION_LINE_START = "#>";
    private static final String VERSION_END = "Version";
    private static final String VERSION_START = "SKaMPI";
    private static final String BEGIN_RESULT_START = "begin";
    private static final String BEGIN_RESULT_END = "result";
    private static final String END_RESULT_START = "end";
    private static final String END_RESULT_END = "result";

    // Constants representing names in the database
    private static final String RESULT_SET_NAME_PARAM_NAME = "Result Set";
    private static final String MAIN_RESULT_NAME = "Time";
    private static final String COLUMN_N_RESULT_NAME = "Time column ";
    
    private static final String INTEGER_REGEX = "[+-]?[0-9]+";
    private static final String FLOAT_REGEX = "[+-]?[0-9]*\\.[0-9]+([Ee][+-]?[0-9]+)?";

    // For each results set map the variable name to the created name table parameter
    private final Map<Integer, ResultSetParams> runResultSetParams
            = new HashMap<>();

    // List of result set names
    private final Map<Integer, List<String>> runResultSetNames
            = DefaultHashMap.mapOfLists();
    private final Map<Run, Integer> runIds = new HashMap<>();

    private final DataConfig runConfigData;
    private final JPanel runConfigPanel;

    private int skampiRunId = 0;

    private void processResult(String resultSetName, String line, List<RunData> runDataSet) throws SkampiException {
        
        if (resultSetName ==  null) {
            throw new SkampiException("Null result set name");
        }
        
        final List<Value<?>> paramters = new ArrayList<>();
        final List<Value<Double>> results = new ArrayList<>();
        final String[] tokens = line.split("(\\s|=)+");
        
        if (tokens.length < 7) {
            throw new SkampiException("Malformed results line: " + line);
        }
        
        paramters.add(new Value<>(RESULT_SET_NAME_PARAM_NAME, Rank.ALL_RANKS, resultSetName));
        
        final String paramName = tokens[0];
        final String paramValue = tokens[1];
        
        if (paramValue.matches(INTEGER_REGEX)) {
            paramters.add(new Value<>(paramName, Rank.UNKNOWN, Integer.parseInt(paramValue)));
        } else if (paramValue.matches(FLOAT_REGEX)) {
            paramters.add(new Value<>(paramName, Rank.UNKNOWN, Double.parseDouble(paramValue)));
        } else {
            paramters.add(new Value<>(paramName, Rank.UNKNOWN, paramValue));
        }
        
        String resultName = MAIN_RESULT_NAME;
        String resultValue = tokens[3];
        String resultError = tokens[4];
        String resultCount = tokens[5];
        
        if (!resultValue.matches(FLOAT_REGEX)
                || !resultError.matches(FLOAT_REGEX)
                || !resultCount.matches(INTEGER_REGEX)) {
            throw new SkampiException("Malformed results line: " + line);
        }
        
        results.add(new Value<>(resultName, Double.parseDouble(resultValue),
                Double.parseDouble(resultError), ErrorType.SKAMPI_STANDARD,
                Rank.UNKNOWN, Long.parseLong(resultCount), 0L));
     
        int idx = 0;
        while (idx + 6 < tokens.length) {
            resultName = COLUMN_N_RESULT_NAME + (idx + 1);
            resultValue = tokens[idx + 6];
            if (!resultValue.matches(FLOAT_REGEX)) {
                throw new SkampiException("Malformed results line: " + line);
            }
            results.add(new Value<>(resultName, Rank.UNKNOWN, Double.parseDouble(resultValue)));
            ++idx;
        }
        
        runDataSet.add(new RunData(paramters, results));
    }

    /**
     * A mapping for holding the parameters in each SkaMPI "results set" and
     * any renaming of said parameters.
     */
    public static class ResultSetParams
            extends HashMap<String, Map<String, String>> { }

    /**
     * Create a new {@code SkampiFactory}.
     */
    public SkampiFactory() {
        SkampiConfigPanelModel model = new SkampiConfigPanelModel(this);
        runConfigData = model;
        runConfigPanel = new SkampiConfigPanel(model);
    }

    /**
     * Create an extension which indicates which duplicate this is.
     *
     * For instance this could be added to give dup, dup(1) dup(2) etc
     * @param count the duplicate number
     * @return the duplicate number as a formatted string which could be added
     * to the original string
     */
    protected static String getResultNameDuplicateExtension(int count) {
        return " (" + count + ")";
    }

    @Override
    public FileChecker getFileChecker() {
        return new SkampiFileChecker(COMMENT_LINE_START, VERSION_START);
    }

    @Override
    public RegExpFilenameFilter getFileFilter() {
        return new RegExpFilenameFilter(FILE_TYPE, FILE_REGEX);
    }

    /**
     * Get the parameters associated with a given {@code Data} id.
     *
     * @param id the result set id.
     * @return the parameters.
     */
    public ResultSetParams getResultSetParams(Integer id) {
        return runResultSetParams.get(id);
    }

    /**
     * Load the given SkaMPI file and return an object containing the loaded
     * data.
     *
     * @param fileToLoad the file to load.
     * @param fileReader the {@link FileReader} being used to read the file.
     * @return The {@link DataFile} that represents the loaded file.
     * @throws java.io.IOException on an error with the file
     */
    @Override
    public DataFile<SkampiVersion> loadFile(File fileToLoad, FileReader fileReader) throws IOException {
        
        final BufferedReader reader = new BufferedReader(fileReader);

        final List<RunData> dataSets = new ArrayList<>();
        final List<String> instructionLines = new ArrayList<>();

        Date runDate = null;
        SkampiVersion version = null;
        String resultSetName = null;
        boolean inResultSet = false;
        
        String line;
        line = reader.readLine();
        
        while (line != null) {
            LOG.info("Reading line \"" + line + "\"");

            final String[] tokens = line.split("\\s+");
                
            switch (tokens[0]) {
                case COMMENT_LINE_START:
                    if (tokens.length == 6 && tokens[1].equals(VERSION_START) && tokens[2].equals(VERSION_END)) {
                        final List<String> versionStrings = Arrays.asList(tokens[3], tokens[4], tokens[5]);
                        String versionString = "";
                        try {
                            versionString = StringUtils.joinStrings(versionStrings, " ");
                            version = SkampiVersion.getFromString(versionString);
                        } catch (IllegalArgumentException ex) {
                            throw new IOException("Version of SKaMPI is unknown, version found \""
                                    + versionString + "\"");
                        }
                    }
                    if (tokens.length == 8 && tokens[1].equals(DATE_TIME_START) && tokens[2].equals(DATE_TIME_END)) {
                        String dateString = "";
                        try {
                            final DateFormat df = new SimpleDateFormat(DATE_FORMAT);
                            final List<String> dateStrings = Arrays.asList(tokens[3], tokens[4], tokens[5], tokens[6], tokens[7]);
                            dateString = StringUtils.joinStrings(dateStrings, " ");
                            runDate = df.parse(dateString);
                        } catch (ParseException ex) {
                            throw new IOException("Date not in correct format should be "
                                    + DATE_FORMAT + " is \"" + dateString + "\"");
                        }
                    }
                    if (tokens.length == 4 && tokens[1].equals(BEGIN_RESULT_START) && tokens[2].equals(BEGIN_RESULT_END)) {
                        resultSetName = tokens[3].trim();
                        if (!resultSetName.matches("\"[^\"]+\"")) {
                            throw new SkampiException("Malformed result set name: " + resultSetName);
                        }
                        // Trim the leading and trailing quotes.
                        resultSetName = resultSetName.substring(1, resultSetName.length() - 1);
                        inResultSet = true;
                    }
                    if (tokens.length == 4 && tokens[1].equals(END_RESULT_START) && tokens[2].equals(END_RESULT_END)) {
                        resultSetName = null;
                        inResultSet = false;
                    }
                    break;
                case INSTRUCTION_LINE_START:
                    instructionLines.add(line);
                    break;
                default:
                    if (inResultSet) {
                        processResult(resultSetName, line, dataSets);
                    }
            }
            
            line = reader.readLine();
        }

        if (runDate == null) {
            runDate = new Date(0L);
        }

        if (version == null) {
            throw new SkampiException("Version not found");
        }

        final String runId = FileUtils.getMD5sum(fileToLoad);
        final Collection<String> flags = Collections.emptyList();
        final Run run = new Run(runId, runDate, fileToLoad.getAbsolutePath(), flags);
        run.addAllDataSets(dataSets);
        runIds.put(run, skampiRunId);
        ++skampiRunId;

        return new SkampiFile(run, version);
    }

    /**
     * Returns the date from the scan stream.
     *
     * Assumes "# Started at" has been removed.
     *
     * @param scanStream the stream to read the date and time from.
     * @return the time and date the run was started a.t
     * @throws java.io.IOException on bad read.
     */
    protected static Date getRunDate(Scanner scanStream) throws IOException {
        String dateTime = scanStream.nextLine().trim();
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            return df.parse(dateTime);
        } catch (ParseException ex) {
            throw new IOException("Date not in correct format should be "
                    + DATE_FORMAT + " is \"" + dateTime + "\"");
        }
    }

    /**
     * Returns the names of the result sets for the given {@code Data} id.
     *
     * @param id the id of the {@code Data} we are processing.
     * @return the list of the result set names from the last load of a SkaMPI
     * file.
     */
    public List<String> getResultSetNames(Integer id) {
        return runResultSetNames.get(id);
    }

    /**
     * Return a collection of all the variable names that were used in a result
     * set.
     *
     * @param id the id of the {@code Data} we are processing.
     * @param resultSetName the name of the result set for which to return the
     * names.
     * @return the variable names that were found in the results set.
     */
    public Collection<String> getVariablesForResultSet(Integer id, String resultSetName) {
        return runResultSetParams.get(id).get(resultSetName).values();
    }

    /**
     * Get the id associated with the given {@code Data}.
     *
     * @param data the data.
     * @return the id.
     */
    public Integer getRunId(Run data) {
        return runIds.get(data);
    }

    @Override
    public String toString() {
        return FILE_TYPE + " (" + FileUtils.regexToGlob(FILE_REGEX) + ")";
    }

    @Override
    public DataConfig getRunConfigData() {
        return runConfigData;
    }

    @Override
    public JPanel getRunConfigPanel() {
        return runConfigPanel;
    }
}
/**
 * An {@code IOException} subclass corresponding to exceptions that might occur
 * whilst reading an SkaMPI file.
 *
 * @author Hollcombe (Tessella plc)
 */
class SkampiException extends IOException {

    /**
     * Create a new {@code SkampiException}.
     *
     * @param msg the exception message, retrievable via {@link #getMessage()}.
     */
    SkampiException(String msg) {
        super("Malformed SkaMPI file: " + msg);
    }

    /**
     * Create a new {@code SkampiException}.
     *
     * @param msg the exception message, retrievable via {@link #getMessage()}.
     * @param cause the nested cause of this {@code HPCCException}.
     */
    SkampiException(String msg, Throwable cause) {
        super("Malformed SkaMPI file: " + msg, cause);
    }
}
