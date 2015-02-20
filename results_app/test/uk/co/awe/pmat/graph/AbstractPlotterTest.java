package uk.co.awe.pmat.graph;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import uk.co.awe.pmat.db.axis.Axis;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class AbstractPlotterTest {

    public AbstractPlotterTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // -- setting_image_size_should_set_the_height_and_width_of_the_plotter
    // -- a_small_increment_in_size_should_not_result_in_the_height_and_width_of_the_plotter_being_set

    // <editor-fold defaultstate="collapsed" desc="write_plot_data_writes_the_data_out_in_space_separated_columns_with_dashes_for_nulls">
    @Test
    public void write_plot_data_writes_the_data_out_in_tab_separated_columns_with_dashes_for_nulls() throws IOException {

        AbstractPlotter plotter = new AbstractPlotter() {
            @Override public void runPlotter() throws IOException {}
            @Override public boolean testPlotter() { return true; }
            @Override public File getPlotFile() { return null; }
            @Override
            public void writeCommandFile(List<? extends Plottable> plottables,
                    GraphConfig graphConfig,
                    boolean forExport,
                    boolean forTeX,
                    File commandFile) throws IOException {}
        };

        Plottable plottable = new Plottable() {
            @Override
            public Object[][] getTableData() {
                return new Object[][] {
                    new Object[] { 1, 0.1,  0.2  },
                    new Object[] { 2, 0.3,  null },
                    new Object[] { 3, null, 0.3  },
                };
            }
            @Override public Axis getXAxis() { return null; }
            @Override public Axis getYAxis() { return null; }
            @Override public Collection<? extends PlottableLine> getPlottableLines() { return null; }
        };

        StringWriter writer = new StringWriter();

        plotter.writePlotData(plottable, writer);

        String exp = "1\t0.1\t0.2\n2\t0.3\t-\n3\t-\t0.3\n";
        assertThat(writer.toString(), equalTo(exp));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="write_plot_data_only_output_lines_that_are_different_to_the_previous_line">
    @Test
    public void write_plot_data_only_output_lines_that_are_different_to_the_previous_line() throws IOException {

        AbstractPlotter plotter = new AbstractPlotter() {
            @Override public void runPlotter() throws IOException {}
            @Override public boolean testPlotter() { return true; }
            @Override public File getPlotFile() { return null; }
            @Override
            public void writeCommandFile(List<? extends Plottable> plottables,
                    GraphConfig graphConfig,
                    boolean forExport,
                    boolean forTeX,
                    File commandFile) throws IOException {}
        };

        Plottable plottable = new Plottable() {
            @Override
            public Object[][] getTableData() {
                return new Object[][] {
                    new Object[] { 1, 0.1,  0.2  },
                    new Object[] { 1, 0.1,  0.2 },
                    new Object[] { 3, null, 0.3  },
                    new Object[] { 3, null, 0.3  },
                    new Object[] { 4, null, 0.3  },
                };
            }
            @Override public Axis getXAxis() { return null; }
            @Override public Axis getYAxis() { return null; }
            @Override public Collection<? extends PlottableLine> getPlottableLines() { return null; }
        };

        StringWriter writer = new StringWriter();

        plotter.writePlotData(plottable, writer);

        String exp = "1\t0.1\t0.2\n3\t-\t0.3\n4\t-\t0.3\n";
        assertThat(writer.toString(), equalTo(exp));
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="write_command_file_without_extra_parameters_defaults_to_not_for_export_not_for_tex_and_null_command_file">
    @Test
    public void write_command_file_without_extra_parameters_defaults_to_not_for_export_not_for_tex_and_null_command_file() throws IOException {

        AbstractPlotter plotter = new AbstractPlotter() {
            @Override public void runPlotter() throws IOException {}
            @Override public boolean testPlotter() { return true; }
            @Override public File getPlotFile() { return null; }
            @Override
            public void writeCommandFile(List<? extends Plottable> plottables,
                    GraphConfig graphConfig,
                    boolean forExport,
                    boolean forTeX,
                    File commandFile) throws IOException {
                assertThat(forTeX, equalTo(false));
                assertNull(commandFile);
            }
        };

        plotter.writeCommandFile(null, null);
        
    }// </editor-fold>

}