package uk.co.awe.pmat.analysis.functions;

import uk.co.awe.pmat.deriveddata.functions.Ifeq;
import java.util.Map;
import uk.co.awe.pmat.db.Value;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.db.DataGrid;
import org.junit.Test;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.deriveddata.ParserValues.Constant;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import uk.co.awe.pmat.db.Rank;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Tests for {@link Ifeq}.
 *
 * @author AWE Plc copyright 2013
 */
public class IfeqTest {

    static Map<String, Value<?>> EMPTY_MAP = Collections.emptyMap();

    public IfeqTest() {
    }

    // <editor-fold defaultstate="collapsed" desc="ifeq_should_return_1_if_the_given_two_equal_constants">
    @Test
    public void ifeq_should_return_1_if_the_given_two_equal_constants() throws InvalidArgumentsException, DerivedDataException {

        Constant<Integer> intConst1 = new Constant<>(2);
        Constant<Integer> intConst2 = new Constant<>(2);
        Ifeq ifeq = new Ifeq();
        ifeq.bind(intConst1, intConst2);

        List<DataGrid.Row> rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(2, 2)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(1.0));

        Constant<Double> dblConst1 = new Constant<>(1.234);
        Constant<Double> dblConst2 = new Constant<>(1.234);
        ifeq = new Ifeq();
        ifeq.bind(dblConst1, dblConst2);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(1.234, 1.234)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(1.0));

        Constant<String> strConst1 = new Constant<>("test");
        Constant<String> strConst2 = new Constant<>("test");
        ifeq = new Ifeq();
        ifeq.bind(strConst1, strConst2);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(test, test)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(1.0));

    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ifeq_should_return_0_if_the_given_two_unequal_constants">
    @Test
    public void ifeq_should_return_0_if_the_given_two_unequal_constants() throws InvalidArgumentsException, DerivedDataException {

        Constant<Integer> intConst1 = new Constant<>(1);
        Constant<Integer> intConst2 = new Constant<>(2);
        Ifeq ifeq = new Ifeq();
        ifeq.bind(intConst1, intConst2);

        List<DataGrid.Row> rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(1, 2)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(0.0));

        Constant<Double> dblConst1 = new Constant<>(4.321);
        Constant<Double> dblConst2 = new Constant<>(1.234);
        ifeq = new Ifeq();
        ifeq.bind(dblConst1, dblConst2);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(4.321, 1.234)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(0.0));

        Constant<String> strConst1 = new Constant<>("testA");
        Constant<String> strConst2 = new Constant<>("testB");
        ifeq = new Ifeq();
        ifeq.bind(strConst1, strConst2);

        rows = new ArrayList<>();
        rows.add(new DataGrid.Row(null, EMPTY_MAP, null));
        dataGrid = new DataGrid("", "", rows);

        name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(testA, testB)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(0.0));
        
    }// </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="ifeq_should_return_0_if_the_given_two_unequal_constants">
    @Test
    public void ifeq_should_return_1_for_all_rows_that_one_variable_equals_a_constant() throws InvalidArgumentsException, DerivedDataException {

        // Integer -------------------------------------------------------------

        Variable variable = new Variable(Variable.Type.PARAMETER, "test");
        Constant<Integer> intConst = new Constant<>(2);
        Ifeq ifeq = new Ifeq();
        ifeq.bind(variable, intConst);

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

        String name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(test, 2)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(0.0));
        assertThat((Double) rows.get(1).getyValue(name).getValue(), equalTo(1.0));
        assertThat((Double) rows.get(2).getyValue(name).getValue(), equalTo(0.0));

        // Double --------------------------------------------------------------

        variable = new Variable(Variable.Type.PARAMETER, "test");
        Constant<Double> dblConst = new Constant<>(1.234);
        ifeq = new Ifeq();
        ifeq.bind(variable, dblConst);

        rows = new ArrayList<>();

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 1.234));
        rows.add(new DataGrid.Row(null, yValues, null));

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 1.234));
        rows.add(new DataGrid.Row(null, yValues, null));

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 4.321));
        rows.add(new DataGrid.Row(null, yValues, null));

        dataGrid = new DataGrid("", "", rows);

        name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(test, 1.234)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(1.0));
        assertThat((Double) rows.get(1).getyValue(name).getValue(), equalTo(1.0));
        assertThat((Double) rows.get(2).getyValue(name).getValue(), equalTo(0.0));

        // String --------------------------------------------------------------

        variable = new Variable(Variable.Type.PARAMETER, "test");
        Constant<String> strConst = new Constant<>("123");
        ifeq = new Ifeq();
        ifeq.bind(variable, strConst);

        rows = new ArrayList<>();

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 1.0));
        rows.add(new DataGrid.Row(null, yValues, null));

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 2.0));
        rows.add(new DataGrid.Row(null, yValues, null));

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 6.0));
        rows.add(new DataGrid.Row(null, yValues, null));

        dataGrid = new DataGrid("", "", rows);

        name = ifeq.evaluate(dataGrid);

        assertThat(name, equalTo("ifeq(test, 123)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(0.0));
        assertThat((Double) rows.get(1).getyValue(name).getValue(), equalTo(0.0));
        assertThat((Double) rows.get(2).getyValue(name).getValue(), equalTo(0.0));

    }// </editor-fold>

}