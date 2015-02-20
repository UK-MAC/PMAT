package uk.co.awe.pmat.analysis.functions;

import uk.co.awe.pmat.deriveddata.functions.Max;
import java.util.Map;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.deriveddata.ParserValues.Constant;
import uk.co.awe.pmat.db.DataGrid;
import org.junit.Test;
import uk.co.awe.pmat.deriveddata.ParserValues.Property;
import uk.co.awe.pmat.db.MetaData;
import uk.co.awe.pmat.db.Rank;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class MaxTest {

    static Map<String, Value<?>> EMPTY_MAP = Collections.emptyMap();

    public MaxTest() {
    }

    // <editor-fold defaultstate="collapsed" desc="the_max_of_a_constant_number_is_that_number">
    @Test
    public void the_max_of_a_constant_number_is_that_number() throws InvalidArgumentsException, DerivedDataException {
        
        Constant<Integer> intConst = new Constant<>(2);
        Max max = new Max();
        max.bind(intConst);

        List<DataGrid.Row> rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = max.evaluate(dataGrid);

        assertThat(name, equalTo("max(2)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(2.));
        }

        Constant<Double> dblConst = new Constant<>(1.234);
        max = new Max();
        max.bind(dblConst);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = max.evaluate(dataGrid);

        assertThat(name, equalTo("max(1.234)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(1.234));
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_max_of_a_string_constant_should_through_an_exception">
    @Test(expected=InvalidArgumentsException.class)
    public void the_max_of_a_string_constant_should_through_an_exception() throws InvalidArgumentsException, DerivedDataException {

        Constant<String> strConst = new Constant<>("test");
        Max max = new Max();
        max.bind(strConst);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_max_of_a_variable_should_be_the_average_of_all_the_values_of_the_variable">
    @Test
    public void the_max_of_a_variable_should_be_the_average_of_all_the_values_of_the_variable() throws InvalidArgumentsException, DerivedDataException {

        Variable variable = new Variable(Variable.Type.RESULT, "test");
        Max max = new Max();
        max.bind(variable);

        List<DataGrid.Row> rows = new ArrayList<>();

        Map<String, Value<?>> yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 1.));
        rows.add(new DataGrid.Row(null, yValues, null));

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 2.));
        rows.add(new DataGrid.Row(null, yValues, null));

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 6.));
        rows.add(new DataGrid.Row(null, yValues, null));

        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = max.evaluate(dataGrid);

        assertThat(name, equalTo("max(test)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(6.));
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_max_of_a_property_should_through_an_exception">
    @Test(expected=InvalidArgumentsException.class)
    public void the_max_of_a_property_should_through_an_exception() throws InvalidArgumentsException, DerivedDataException {

        Property property = new Property(MetaData.Type.MACHINE.name());
        Max max = new Max();
        max.bind(property);

    }// </editor-fold>
}