package uk.co.awe.pmat.datafiles.hpcc;

import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.TestUtils;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.Run;
import uk.co.awe.pmat.db.Value;
import org.junit.Before;
import org.junit.Test;
import uk.co.awe.pmat.db.DatabaseManager;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class HpccFileLoadTest {

    @Before
    public void setUp() {
        DatabaseManager.setDatabaseConnection(new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST)));
    }

    // <editor-fold defaultstate="collapsed" desc="load_PMTM_version_0_2_6">
    @Test
    public void load_PMTM_version_0_2_6() throws URISyntaxException, IOException, ParseException {
        URL url = ClassLoader.getSystemResource("HPCC-Version-1.4.1.txt");

        assertNotNull(url);

        HPCCFactory factory = new HPCCFactory();

        File file = new File(url.toURI());
        DataFile<?> hpccFile = factory.loadFile(file);

        assertThat(hpccFile.getApplicationName(), equalTo("HPCC v1.4.1 f"));

        Run run = hpccFile.asRun();
        
        assertThat(run.getFile(), equalTo(file.getAbsolutePath()));

        String date = "Fri Dec 10 18:02:35 2010";
        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        assertThat(run.getRunDate(), equalTo(df.parse(date)));

        List<RunData> runData = run.getDataSets();

        assertThat(runData.size(), equalTo(1));

        RunData data = runData.get(0);

        List<Value<?>> params = data.getParameters();
        assertThat(params.size(), equalTo(59));

        Set<String> paramNames = new HashSet<>();
        for (Value<?> param : params) {
            paramNames.add(param.getName());
        }
        List<String> pNames = new ArrayList<>(paramNames);
        Collections.sort(pNames);
        assertThat(pNames, TestUtils.startsWith(Arrays.asList(
                "CPS_HPCC_FFTW_ESTIMATE", "CPS_HPCC_FFT_235", "CPS_HPCC_MEMALLCTR")));

        List<Value<Double>> results = data.getResults();
        assertThat(results.size(), equalTo(83));

        Set<String> resultNames = new HashSet<>();
        for (Value<Double> result : results) {
            resultNames.add(result.getName());
        }
        List<String> rNames = new ArrayList<>(resultNames);
        Collections.sort(rNames);
        assertThat(rNames, TestUtils.startsWith(Arrays.asList(
                "AvgPingPongBandwidth_GBytes",
                "AvgPingPongLatency_usec",
                "HPL_Anorm1")));
    }// </editor-fold>

}