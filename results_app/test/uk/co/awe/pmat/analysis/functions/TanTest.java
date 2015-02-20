package uk.co.awe.pmat.analysis.functions;

import uk.co.awe.pmat.deriveddata.functions.Tan;
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
 *
 * @author AWE Plc copyright 2013
 */
public class TanTest {

    static Map<String, Value<?>> EMPTY_MAP = Collections.emptyMap();

    public TanTest() {
    }

    // <editor-fold defaultstate="collapsed" desc="the_tan_of_a_positive_number_constant_is_the_tangent_of_that_number">
    @Test
    public void the_tan_of_a_positive_number_constant_is_the_tangent_of_that_number() throws InvalidArgumentsException, DerivedDataException {

        Constant<Integer> intConst = new Constant<>(2);
        Tan tan = new Tan();
        tan.bind(intConst);

        List<DataGrid.Row> rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = tan.evaluate(dataGrid);

        assertThat(name, equalTo("tan(2)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(Math.tan(2)));
        }

        Constant<Double> dblConst = new Constant<>(1.234);
        tan = new Tan();
        tan.bind(dblConst);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = tan.evaluate(dataGrid);

        assertThat(name, equalTo("tan(1.234)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(Math.tan(1.234)));
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_tan_of_a_string_should_through_an_exception">
    @Test(expected=InvalidArgumentsException.class)
    public void the_tan_of_a_string_should_through_an_exception() throws InvalidArgumentsException, DerivedDataException {

        Constant<String> strConst = new Constant<>("test");
        Tan tan = new Tan();
        tan.bind(strConst);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_tan_of_a_variable_should_set_that_variables_values_to_tan_of_the_value">
    @Test
    public void the_tan_of_a_variable_should_set_that_variables_values_to_tan_of_the_value() throws InvalidArgumentsException, DerivedDataException {

        Variable var = new Variable(Variable.Type.RESULT, "test");
        Tan tan = new Tan();
        tan.bind(var);

        List<DataGrid.Row> rows = new ArrayList<>();

        Map<String, Value<?>> yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 1.0));
        rows.add(new DataGrid.Row(null, yValues, null));
        
        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 2.0));
        rows.add(new DataGrid.Row(null, yValues, null));
        
        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 6.0));
        rows.add(new DataGrid.Row(null, yValues, null));

        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = tan.evaluate(dataGrid);

        assertThat(name, equalTo("tan(test)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(Math.tan(1.0)));
        assertThat((Double) rows.get(1).getyValue(name).getValue(), equalTo(Math.tan(2.0)));
        assertThat((Double) rows.get(2).getyValue(name).getValue(), equalTo(Math.tan(6.0)));

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_tan_of_a_property_should_through_an_exception">
    @Test(expected=InvalidArgumentsException.class)
    public void the_tan_of_a_property_should_through_an_exception() throws InvalidArgumentsException, DerivedDataException {

        Property property = new Property(MetaData.Type.MACHINE.name());
        Tan tan = new Tan();
        tan.bind(property);

    }// </editor-fold>
}