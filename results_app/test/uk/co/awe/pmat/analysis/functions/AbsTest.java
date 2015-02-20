package uk.co.awe.pmat.analysis.functions;

import uk.co.awe.pmat.deriveddata.functions.Abs;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.deriveddata.ParserValues.Constant;
import uk.co.awe.pmat.deriveddata.ParserValues.Property;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Value;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Tests for {@link Abs}.
 * 
 * @author AWE Plc copyright 2013
 */
public class AbsTest {

    static Map<String, Value<?>> EMPTY_MAP = Collections.emptyMap();

    public AbsTest() {
    }

    // <editor-fold defaultstate="collapsed" desc="the_abs_of_a_positive_number_constant_is_the_same_number">
    @Test
    public void the_abs_of_a_positive_number_constant_is_the_same_number() throws InvalidArgumentsException, DerivedDataException {

        Constant<Integer> intConst = new Constant<>(1);
        Abs abs = new Abs();
        abs.bind(intConst);

        List<DataGrid.Row> rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = abs.evaluate(dataGrid);

        assertThat(name, equalTo("abs(1)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(1.));
        }

        Constant<Double> dblConst = new Constant<>(1.);
        abs = new Abs();
        abs.bind(dblConst);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = abs.evaluate(dataGrid);

        assertThat(name, equalTo("abs(1.0)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(1.));
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_abs_of_a_negative_number_constant_is_that_number_made_postive">
    @Test
    public void the_abs_of_a_negative_number_constant_is_that_number_made_postive() throws InvalidArgumentsException, DerivedDataException {

        Constant<Integer> intConst = new Constant<>(-1);
        Abs abs = new Abs();
        abs.bind(intConst);

        List<DataGrid.Row> rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = abs.evaluate(dataGrid);

        assertThat(name, equalTo("abs(-1)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(1.));
        }

        Constant<Double> dblConst = new Constant<>(-1.);
        abs = new Abs();
        abs.bind(dblConst);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = abs.evaluate(dataGrid);

        assertThat(name, equalTo("abs(-1.0)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(1.));
        }
        
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_abs_of_a_string_should_through_an_exception">
    @Test(expected=InvalidArgumentsException.class)
    public void the_abs_of_a_string_should_through_an_exception() throws InvalidArgumentsException, DerivedDataException {

        Constant<String> strConst = new Constant<>("test");
        Abs abs = new Abs();
        abs.bind(strConst);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_abs_of_a_variable_makes_the_column_of_that_variable_positive">
    @Test
    public void the_abs_of_a_variable_makes_the_column_of_that_variable_positive() throws InvalidArgumentsException, DerivedDataException {

        Variable var = new Variable(Variable.Type.RESULT, "test");
        Abs abs = new Abs();
        abs.bind(var);

        List<DataGrid.Row> rows = new ArrayList<>();
        Map<String, Value<?>> yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, -2.5));
        rows.add(new DataGrid.Row(null, yValues, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = abs.evaluate(dataGrid);

        assertThat(name, equalTo("abs(test)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(2.5));
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_abs_of_a_property_should_through_an_exception">
    @Test(expected=InvalidArgumentsException.class)
    public void the_abs_of_a_property_should_through_an_exception() throws InvalidArgumentsException, DerivedDataException {

        Property property = new Property(MetaData.Type.MACHINE.name());
        Abs abs = new Abs();
        abs.bind(property);

    }// </editor-fold>
}