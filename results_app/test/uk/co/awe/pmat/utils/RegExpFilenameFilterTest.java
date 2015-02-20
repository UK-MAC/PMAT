package uk.co.awe.pmat.utils;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class RegExpFilenameFilterTest {

    public RegExpFilenameFilterTest() {
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

    // <editor-fold defaultstate="collapsed" desc="directories_are_always_accepted_by_a_filename_filter">
    @Test
    public void directories_are_always_accepted_by_a_filename_filter() {
        
        RegExpFilenameFilter filter = new RegExpFilenameFilter(null);
        
        File dir = new File("/tmp");
        assertTrue(dir.isDirectory());
        
        assertTrue(filter.accept(dir));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="files_that_match_the_regex_are_accepted_by_the_filter">
    @Test
    public void files_that_match_the_regex_are_accepted_by_the_filter() {

        String regex = "[abc]{3}[123]{3}[def]{3}.txt";
        RegExpFilenameFilter filter = new RegExpFilenameFilter(regex);

        final String name = "abc123def.txt";
        assertTrue(name.matches(regex));

        File file = new File("/tmp") {
            @Override
            public File getParentFile() {
                return new File("/tmp");
            }
            @Override
            public String getName() {
                return name;
            }
            @Override
            public boolean isDirectory() {
                return false;
            }
        };

        assertTrue(filter.accept(file));
        assertTrue(filter.accept(null, file.getName()));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="a_null_or_empty_file_name_is_never_accepted">
    @Test
    public void a_null_or_empty_file_name_is_never_accepted() {

        RegExpFilenameFilter filter = new RegExpFilenameFilter(null);

        assertFalse(filter.accept(null, null));
        assertFalse(filter.accept(null, ""));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="creating_a_filter_with_a_description_will_mean_the_filter_will_return_that_description">
    @Test
    public void creating_a_filter_with_a_description_will_mean_the_filter_will_return_that_description() {

        String desc = "Test Desc";
        RegExpFilenameFilter filter = new RegExpFilenameFilter(desc, null);

        assertThat(filter.getDescription(), equalTo(desc));
    }// </editor-fold>

}