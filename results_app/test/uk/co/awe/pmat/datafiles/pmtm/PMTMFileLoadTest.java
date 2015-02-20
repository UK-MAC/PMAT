package uk.co.awe.pmat.datafiles.pmtm;

import uk.co.awe.pmat.Configuration;
import uk.co.awe.pmat.db.jdbc.JdbcDatabaseConnection;
import uk.co.awe.pmat.db.jdbc.JdbcProperties;
import uk.co.awe.pmat.db.RunData;
import uk.co.awe.pmat.db.MetaData;
import java.util.Collections;
import java.util.Arrays;
import java.util.Set;
import uk.co.awe.pmat.db.Value;
import java.util.List;
import uk.co.awe.pmat.db.Run;
import java.text.DateFormat;
import java.text.ParseException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.Before;
import org.junit.Test;
import uk.co.awe.pmat.datafiles.DataFile;
import uk.co.awe.pmat.db.DatabaseManager;
import uk.co.awe.pmat.db.MetaData.Type;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class PMTMFileLoadTest {

    @Before
    public void setUp() {
        DatabaseManager.setDatabaseConnection(new JdbcDatabaseConnection(new Configuration(new JdbcProperties(), Configuration.Mode.TEST)));
    }

    // <editor-fold defaultstate="collapsed" desc="Test: load_PMTM_version_0_2_6">
    @Test
    public void load_PMTM_version_0_2_6() throws URISyntaxException, IOException, ParseException {
        URL url = ClassLoader.getSystemResource("PMTM-Version-0.2.6.csv");
        
        assertNotNull(url);

        PMTMFactory factory = new PMTMFactory();

        File file = new File(url.toURI());
        DataFile<?> pmtmFile = factory.loadFile(file);

        assertThat(pmtmFile.getApplicationName(), equalTo("Chimaera v1.6.1"));

        Run run = pmtmFile.asRun();
        
        assertThat(run.getFile(), equalTo(file.getAbsolutePath()));
        
        String date = "22-04-2010 20:30";
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        assertThat(run.getRunDate(), equalTo(df.parse(date)));

        List<RunData> runData = run.getDataSets();

        assertThat(runData.size(), equalTo(1));

        RunData data = runData.get(0);

        List<Value<?>> params = data.getParameters();
        assertThat(params.size(), equalTo(35));

        Set<String> paramNames = new HashSet<>();
        for (Value<?> param : params) {
            paramNames.add(param.getName());
        }
        List<String> pNames = new ArrayList<>(paramNames);
        Collections.sort(pNames);
        assertThat(pNames, equalTo(Arrays.asList("cells per tile", "meshx", "meshy", "meshz")));

        List<Value<Double>> results = data.getResults();
        assertThat(results.size(), equalTo(35));

        Set<String> resultNames = new HashSet<>();
        for (Value<Double> result : results) {
            resultNames.add(result.getName());
        }
        List<String> rNames = new ArrayList<>(resultNames);
        Collections.sort(rNames);
        assertThat(rNames, equalTo(Arrays.asList(
                "Timer overhead for pause-continue",
                "Timer overhead for start-stop",
                "W Tile Calculation Time")));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Test: load_PMTM_version_1_0_0">
    @Test
    public void load_PMTM_version_1_0_0() throws URISyntaxException, IOException, ParseException {
        URL url = ClassLoader.getSystemResource("PMTM-Version-1.0.0.csv");

        assertNotNull(url);

        PMTMFactory factory = new PMTMFactory();

        File file = new File(url.toURI());
        DataFile<?> pmtmFile = factory.loadFile(file);

        assertThat(pmtmFile.getApplicationName(), equalTo("DL_Poly v2.17.2"));

        Run run = pmtmFile.asRun();
        
        assertThat(run.getFile(), equalTo(file.getAbsolutePath()));

        String date = "23-03-2011 18:03";
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        assertThat(run.getRunDate(), equalTo(df.parse(date)));

        assertThat(run.getRunId(), equalTo("20110323-180317-0000020189"));

        List<RunData> runData = run.getDataSets();

        assertThat(runData.size(), equalTo(1));

        RunData data = runData.get(0);

        List<Value<?>> params = data.getParameters();
        assertThat(params.size(), equalTo(13));

        Set<String> paramNames = new HashSet<>();
        for (Value<?> param : params) {
            paramNames.add(param.getName());
        }
        List<String> pNames = new ArrayList<>(paramNames);
        Collections.sort(pNames);
        assertThat(pNames, equalTo(Arrays.asList("PE Count", "eps", "fac", "tol")));

        List<Value<Double>> results = data.getResults();
        assertThat(results.size(), equalTo(13));

        Set<String> resultNames = new HashSet<>();
        for (Value<Double> result : results) {
            resultNames.add(result.getName());
        }
        List<String> rNames = new ArrayList<>(resultNames);
        Collections.sort(rNames);
        assertThat(rNames, equalTo(Arrays.asList(
                "Application Time",
                "Ewald 1 Time",
                "Inter Angle Three-Body-Force Time",
                "Timer overhead for pause-continue",
                "Timer overhead for start-stop")));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Test: load_PMTM_version_2_0_0">
    @Test
    public void load_PMTM_version_2_0_0() throws URISyntaxException, IOException, ParseException {
        URL url = ClassLoader.getSystemResource("PMTM-Version-2.0.0.csv");

        assertNotNull(url);

        PMTMFactory factory = new PMTMFactory();

        File file = new File(url.toURI());
        DataFile<?> pmtmFile = factory.loadFile(file);

        assertThat(pmtmFile.getApplicationName(), equalTo("CHIM"));

        Run run = pmtmFile.asRun();
        
        assertThat(run.getFile(), equalTo(file.getAbsolutePath()));

        String date = "27-05-2011 17:28";
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        assertThat(run.getRunDate(), equalTo(df.parse(date)));

        assertThat(run.getRunId(), equalTo("20110527-172810-0000016840"));

        MetaData os = (MetaData) run.getMetaData(Type.OPERATING_SYSTEM);
        assertThat(os.getData("vendor").toString(), equalTo("Red_Hat"));
        assertThat(os.getData("name").toString(), equalTo("RHEL"));
        assertThat(os.getData("versionMajor").toString(), equalTo("4"));
        assertThat(os.getData("versionMinor").toString(), equalTo("1"));
        assertThat(os.getData("versionBuild").toString(), equalTo("2"));
        assertThat(os.getData("versionBuildMinor").toString(), equalTo("46"));
        assertThat(os.getData("kernel").toString(), equalTo("Linux_2.6.18-128.1.6.el5.Bull.6"));

        MetaData comp = (MetaData) run.getMetaData(Type.COMPILER);
        assertThat(comp.getData("vendor").toString(), equalTo("Intel"));
        assertThat(comp.getData("name").toString(), equalTo("Intel"));
        assertThat(comp.getData("versionMajor").toString(), equalTo("11"));
        assertThat(comp.getData("versionMinor").toString(), equalTo("1"));
        assertThat(comp.getData("versionBuild").toString(), equalTo("0"));

        MetaData mpi = (MetaData) run.getMetaData(Type.MPI);
        assertThat(mpi.getData("vendor").toString(), equalTo("Intel"));
        assertThat(mpi.getData("name").toString(), equalTo("IntelMPI"));
        assertThat(mpi.getData("versionMajor").toString(), equalTo("4"));
        assertThat(mpi.getData("versionMinor").toString(), equalTo("0"));
        assertThat(mpi.getData("versionBuild").toString(), equalTo("3"));

        List<RunData> runData = run.getDataSets();

        assertThat(runData.size(), equalTo(1));

        RunData data = runData.get(0);

        List<Value<?>> params = data.getParameters();
        assertThat(params.size(), equalTo(5));

        Set<String> paramNames = new HashSet<>();
        for (Value<?> param : params) {
            paramNames.add(param.getName());
        }
        List<String> pNames = new ArrayList<>(paramNames);
        Collections.sort(pNames);
        assertThat(pNames, equalTo(Arrays.asList("PE Count", "max SN", "meshx", "meshy", "min SN")));

        List<Value<Double>> results = data.getResults();
        assertThat(results.size(), equalTo(14));

        Set<String> resultNames = new HashSet<>();
        for (Value<Double> result : results) {
            resultNames.add(result.getName());
        }
        List<String> rNames = new ArrayList<>(resultNames);
        Collections.sort(rNames);
        assertThat(rNames, equalTo(Arrays.asList(
                "PMTM_CHIMMAIN",
                "PMTM_CHIM_BVALUE",
                "PMTM_CHIM_WALLCLOCK",
                "Timer overhead for pause-continue",
                "Timer overhead for start-stop")));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Test: load_PMTM_version_2_1_1">
    @Test
    public void load_PMTM_version_2_1_1() throws URISyntaxException, IOException, ParseException {
        URL url = ClassLoader.getSystemResource("PMTM-Version-2.1.1.csv");

        assertNotNull(url);

        PMTMFactory factory = new PMTMFactory();

        File file = new File(url.toURI());
        DataFile<?> pmtmFile = factory.loadFile(file);

        assertThat(pmtmFile.getApplicationName(), equalTo("DL_Poly v2.17.2"));

        Run run = pmtmFile.asRun();
        
        assertThat(run.getFile(), equalTo(file.getAbsolutePath()));

        String date = "16-11-2011 18:27";
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        assertThat(run.getRunDate(), equalTo(df.parse(date)));

        assertThat(run.getRunId(), equalTo("20111116-182755-0000012999"));
        
        MetaData machine = (MetaData) run.getMetaData(Type.MACHINE);
        assertThat(machine.getData("name").toString(), equalTo("WILLOW"));

        MetaData os = run.getMetaData(Type.OPERATING_SYSTEM);
        assertThat(os.getData("vendor").toString(), equalTo("Red_Hat"));
        assertThat(os.getData("name").toString(), equalTo("RHEL"));
        assertThat(os.getData("versionMajor").toString(), equalTo("4"));
        assertThat(os.getData("versionMinor").toString(), equalTo("1"));
        assertThat(os.getData("versionBuild").toString(), equalTo("2"));
        assertThat(os.getData("versionBuildMinor").toString(), equalTo("46"));
        assertThat(os.getData("kernel").toString(), equalTo("Linux_2.6.18-128.1.6.el5.Bull.6"));

        MetaData comp = run.getMetaData(Type.COMPILER);
        assertThat(comp.getData("vendor").toString(), equalTo("Intel"));
        assertThat(comp.getData("name").toString(), equalTo("Intel"));
        assertThat(comp.getData("versionMajor").toString(), equalTo("12"));
        assertThat(comp.getData("versionMinor").toString(), equalTo("1"));
        assertThat(comp.getData("versionBuild").toString(), equalTo("0"));

        MetaData mpi = run.getMetaData(Type.MPI);
        assertThat(mpi.getData("vendor").toString(), equalTo("Intel"));
        assertThat(mpi.getData("name").toString(), equalTo("IntelMPI"));
        assertThat(mpi.getData("versionMajor").toString(), equalTo("0"));
        assertThat(mpi.getData("versionMinor").toString(), equalTo("0"));
        assertThat(mpi.getData("versionBuild").toString(), equalTo("0"));
        
        List<String> expFlags = Arrays.asList("-heap-arrays 64", "-xHOST", "-O3");
        assertThat(run.getFlags(), equalTo(expFlags));

        List<RunData> runData = run.getDataSets();

        assertThat(runData.size(), equalTo(1));

        RunData data = runData.get(0);

        List<Value<?>> params = data.getParameters();
        assertThat(params.size(), equalTo(9));

        Set<String> paramNames = new HashSet<>();
        for (Value<?> param : params) {
            paramNames.add(param.getName());
        }
        List<String> pNames = new ArrayList<>(paramNames);
        Collections.sort(pNames);
        assertThat(pNames, equalTo(Arrays.asList("PE Count", "eps", "fac")));

        List<Value<Double>> results = data.getResults();
        assertThat(results.size(), equalTo(16));

        Set<String> resultNames = new HashSet<>();
        for (Value<Double> result : results) {
            resultNames.add(result.getName());
        }
        List<String> rNames = new ArrayList<>(resultNames);
        Collections.sort(rNames);
        assertThat(rNames, equalTo(Arrays.asList(
                "Ewald 1 Time",
                "Inter Angle Three-Body-Force Time",
                "Timer overhead for pause-continue",
                "Timer overhead for start-stop")));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Test: load_PMTM_with_infities">
    @Test
    public void load_PMTM_with_infinities() throws URISyntaxException, IOException, ParseException {
        URL url = ClassLoader.getSystemResource("PMTM-Version-2.1.1-inf.csv");

        assertNotNull(url);

        PMTMFactory factory = new PMTMFactory();

        File file = new File(url.toURI());
        DataFile<?> pmtmFile = factory.loadFile(file);

        assertThat(pmtmFile.getApplicationName(), equalTo("DL_Poly v2.17.2"));

        Run run = pmtmFile.asRun();
        
        assertThat(run.getFile(), equalTo(file.getAbsolutePath()));

        String date = "16-11-2011 18:27";
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        assertThat(run.getRunDate(), equalTo(df.parse(date)));

        assertThat(run.getRunId(), equalTo("20111116-182755-0000012999"));
               
        List<String> expFlags = Arrays.asList("-heap-arrays 64", "-xHOST", "-O3");
        assertThat(run.getFlags(), equalTo(expFlags));

        List<RunData> runData = run.getDataSets();

        assertThat(runData.size(), equalTo(1));

        RunData data = runData.get(0);

        List<Value<?>> params = data.getParameters();
        assertThat(params.size(), equalTo(9));

        Set<String> paramNames = new HashSet<>();
        for (Value<?> param : params) {
            paramNames.add(param.getName());
        }
        List<String> pNames = new ArrayList<>(paramNames);
        Collections.sort(pNames);
        assertThat(pNames, equalTo(Arrays.asList("PE Count", "eps", "fac")));

        List<Value<Double>> results = data.getResults();
        assertThat(results.size(), equalTo(16));

        Set<String> resultNames = new HashSet<>();
        for (Value<Double> result : results) {
            resultNames.add(result.getName());
        }
        List<String> rNames = new ArrayList<>(resultNames);
        Collections.sort(rNames);
        assertThat(rNames, equalTo(Arrays.asList(
                "Ewald 1 Time",
                "Inter Angle Three-Body-Force Time",
                "Timer overhead for pause-continue",
                "Timer overhead for start-stop")));
    }// </editor-fold>
}