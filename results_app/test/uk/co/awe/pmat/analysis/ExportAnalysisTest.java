package uk.co.awe.pmat.analysis;

import java.util.Collections;
import uk.co.awe.pmat.utils.Pair;
import uk.co.awe.pmat.db.Graph;
import java.util.Date;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.db.Analysis;
import uk.co.awe.pmat.db.DerivedData;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.xml.XMLSerialisable;
import uk.co.awe.pmat.db.xml.XMLSerialiser;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class ExportAnalysisTest {

    public ExportAnalysisTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // <editor-fold defaultstate="collapsed" desc="test_serialise_of_analyses">
    @Test
    public void test_serialise_of_analyses_without_graphs_derived_data_retrictions_or_renames() throws IOException {
        String creator = "Test Creator";
        Date date = new Date();
        String notes = "Test Notes";
        boolean isPrivate = false;
        List<Graph> graphs = Collections.emptyList();
        List<DerivedData> derivedData = Collections.emptyList();
        List<Restriction> restrictions = Collections.emptyList();
        List<Pair<String, String>> renames = Collections.emptyList();

        List<XMLSerialisable> analyses = new ArrayList<>();
        analyses.add(new Analysis(creator, date, notes, isPrivate, graphs, derivedData, restrictions, renames));

        XMLSerialiser serialiser = new XMLSerialiser();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        serialiser.serialise(out, analyses);

        DateFormat df = new SimpleDateFormat(XMLSerialisable.DATE_FORMAT_STRING);
        String exp = String.format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<PMATExport version=\"2.1.0\">"
                + "<Analysis creator=\"%s\""
                + " date=\"%s\""
                + " dataPrivate=\"%s\">"
                + "<Notes>%s</Notes>"
                + "</Analysis></PMATExport>", creator, df.format(date), isPrivate, notes);

        String res = out.toString();
        assertThat(res, equalTo(exp));
    }// </editor-fold>

}