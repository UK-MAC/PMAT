/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.awe.pmat.datafiles.pmtm;

import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.datafiles.pmtm.PMTMFactory.PmtmMeta;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class ReadProcessorLineTest {
    
    public ReadProcessorLineTest() {
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
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void read_processor_line_with_integer_clock_speed_returns_that_clock_speed() throws IOException {
        PmtmMeta pmtmMeta = new PmtmMeta();
        new PMTMFactory().processMetaData("Processor, =, AMD, Opteron_2400, x86_64, 2800, 6, 1", PMTMVersion.V2_3_1, pmtmMeta);
        
        assertThat((int) pmtmMeta.processor.getData("clockSpeed"), equalTo(2800));
    }
    
    @Test
    public void read_processor_line_with_decimal_MHz_clock_speed_returns_that_truncated_to_integer() throws IOException {
        PmtmMeta pmtmMeta = new PmtmMeta();
        new PMTMFactory().processMetaData("Processor, =, AMD, Opteron_2400, x86_64, 2800.0MHz, 6, 1", PMTMVersion.V2_3_1, pmtmMeta);
        
        assertThat((int) pmtmMeta.processor.getData("clockSpeed"), equalTo(2800));
    }
    
    @Test
    public void read_processor_line_with_decimal_GHz_clock_speed_returns_that_times_1000_truncated_to_integer() throws IOException {
        PmtmMeta pmtmMeta = new PmtmMeta();
        new PMTMFactory().processMetaData("Processor, =, AMD, Opteron_2400, x86_64, 2.8GHz, 6, 1", PMTMVersion.V2_3_1, pmtmMeta);
        
        assertThat((int) pmtmMeta.processor.getData("clockSpeed"), equalTo(2800));
    }
}
