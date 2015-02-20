package uk.co.awe.pmat.analysis.functions;

import uk.co.awe.pmat.deriveddata.functions.Pow;
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
 * Tests for {@link Pow}.
 *
 * @author AWE Plc copyright 2013
 */
public class PowTest {

    static Map<String, Value<?>> EMPTY_MAP = Collections.emptyMap();

    public PowTest() {
    }

    // <editor-fold defaultstate="collapsed" desc="the_pow_of_a_positive_number_constant_is_the_powe_of_that_number">
    @Test
    public void the_pow_of_a_positive_number_constant_is_the_powe_of_that_number() throws InvalidArgumentsException, DerivedDataException {

        Constant<Integer> intConst1 = new Constant<>(2);
        Constant<Integer> intConst2 = new Constant<>(3);
        Pow pow = new Pow();
        pow.bind(intConst1, intConst2);

        List<DataGrid.Row> rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = pow.evaluate(dataGrid);

        assertThat(name, equalTo("pow(2, 3)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(Math.pow(2, 3)));
        }

        Constant<Double> dblConst1 = new Constant<>(1.1);
        Constant<Double> dblConst2 = new Constant<>(2.2);
        pow = new Pow();
        pow.bind(dblConst1, dblConst2);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = pow.evaluate(dataGrid);

        assertThat(name, equalTo("pow(1.1, 2.2)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        for (DataGrid.Row row : dataGrid.getRows()) {
            assertThat((Double) row.getyValue(name).getValue(), equalTo(Math.pow(1.1, 2.2)));
        }

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_pow_of_a_string_should_through_an_exception">
    @Test(expected=InvalidArgumentsException.class)
    public void the_pow_of_a_string_should_through_an_exception() throws InvalidArgumentsException, DerivedDataException {

        Constant<String> strConst = new Constant<>("test");
        Pow pow = new Pow();
        pow.bind(strConst);

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_pow_of_a_variable_should_set_that_variables_values_to_pow_of_the_value">
    @Test
    public void the_pow_of_a_variable_should_set_that_variables_values_to_pow_of_the_value() throws InvalidArgumentsException, DerivedDataException {

        Variable var = new Variable(Variable.Type.RESULT, "test");
        Constant<Double> exp = new Constant<>(2.0);
        Pow pow = new Pow();
        pow.bind(var, exp);

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

        String name = pow.evaluate(dataGrid);

        assertThat(name, equalTo("pow(test, 2.0)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(Math.pow(1.0, 2.0)));
        assertThat((Double) rows.get(1).getyValue(name).getValue(), equalTo(Math.pow(2.0, 2.0)));
        assertThat((Double) rows.get(2).getyValue(name).getValue(), equalTo(Math.pow(6.0, 2.0)));

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="the_pow_of_a_property_should_through_an_exception">
    @Test(expected=InvalidArgumentsException.class)
    public void the_pow_of_a_property_should_through_an_exception() throws InvalidArgumentsException, DerivedDataException {

        Property property = new Property(MetaData.Type.MACHINE.name());
        Pow pow = new Pow();
        pow.bind(property);

    }// </editor-fold>
}