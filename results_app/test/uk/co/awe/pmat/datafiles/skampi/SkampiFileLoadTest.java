package uk.co.awe.pmat.datafiles.skampi;

import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.Value;
import org.junit.Before;
import org.junit.Test;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.Rank;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class SkampiFileLoadTest {

    @Before
    public void setUp() {
        DatabaseManager.setDatabaseConnection(new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST)));
    }

    // <editor-fold defaultstate="collapsed" desc="Test: load_SkaMPI_version_5_0_4">
    @Test
    @SuppressWarnings("unchecked")
    public void load_SkaMPI_version_5_0_4() throws URISyntaxException, IOException, ParseException {
        URL url = ClassLoader.getSystemResource("SkaMPI-Version-5.0.4.txt");

        assertNotNull(url);

        SkampiFactory factory = new SkampiFactory();

        File file = new File(url.toURI());
        DataFile<?> skampiFile = factory.loadFile(file);

        assertThat(skampiFile.getApplicationName(), equalTo("SKaMPI v5.0.4 rev. 355"));

        Run run = skampiFile.asRun();
        
        assertThat(run.getFile(), equalTo(file.getAbsolutePath()));

        String date = "Thu May 20 15:03:54 2010";
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        assertThat(run.getRunDate(), equalTo(df.parse(date)));

        List<RunData> runData = run.getDataSets();

        assertThat(runData.size(), equalTo(15));

        RunData data = runData.get(0);

        List<Value<?>> params = data.getParameters();
        assertThat(params.size(), equalTo(2));
        assertThat(params.get(0), equalTo(new Value("Result Set", Rank.ALL_RANKS, "MPI_Allreduce-procs")));
        assertThat(params.get(1), equalTo(new Value("procs", Rank.UNKNOWN, 2)));

        List<Value<Double>> results = data.getResults();
        assertThat(results.size(), equalTo(3));

        List<String> resultNames = new ArrayList<>();
        List<Double> resultValues = new ArrayList<>();
        for (Value<Double> result : results) {
            resultNames.add(result.getName());
            resultValues.add(result.getValue());
        }
        assertThat(resultNames, equalTo(Arrays.asList("Time", "Time column 1", "Time column 2")));
        assertThat(resultValues, equalTo(Arrays.asList(18.9, 18.9, 18.1)));
    }// </editor-fold>

}