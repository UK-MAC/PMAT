package uk.co.awe.pmat.analysis.functions;

import uk.co.awe.pmat.deriveddata.functions.Or;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Test;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.Value;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Tests for {@link Cos}.
 *
 * @author AWE Plc copyright 2013
 */
public class OrTest {

    public OrTest() {
    }

    // <editor-fold defaultstate="collapsed" desc="the_or_function_should_return_the_first_argument_if_it_is_not_null">
    @Test
    public void the_or_function_should_return_the_first_argument_if_it_is_not_null() throws InvalidArgumentsException, DerivedDataException {

        Variable var1 = new Variable(Variable.Type.PARAMETER, "var1");
        Variable var2 = new Variable(Variable.Type.PARAMETER, "var2");
        Or cos = new Or();
        cos.bind(var1, var2);

        List<DataGrid.Row> rows = new ArrayList<>();
        Map<String, Value<?>> yValues = new HashMap<>();
        yValues.put("var1", Value.valueOf("Integer", "2"));
        yValues.put("var2", Value.valueOf("Integer", "3"));
        rows.add(new DataGrid.Row(null, yValues, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = cos.evaluate(dataGrid);
        Value expVal = Value.valueOf("Integer", "2");
        
        assertThat(name, equalTo("or(var1, var2)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));
        assertThat(dataGrid.getRows().get(0).getyValue(name), equalTo(expVal));
    }// </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="the_or_function_should_return_the_second_argument_if_the_first_argument_is_null">
    @Test
    public void the_or_function_should_return_the_second_argument_if_the_first_argument_is_null() throws InvalidArgumentsException, DerivedDataException {

        Variable var1 = new Variable(Variable.Type.PARAMETER, "var1");
        Variable var2 = new Variable(Variable.Type.PARAMETER, "var2");
        Or cos = new Or();
        cos.bind(var1, var2);

        List<DataGrid.Row> rows = new ArrayList<>();
        Map<String, Value<?>> yValues = new HashMap<>();
        yValues.put("var1", null);
        yValues.put("var2", Value.valueOf("Integer", "3"));
        rows.add(new DataGrid.Row(null, yValues, null));
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = cos.evaluate(dataGrid);
        Value expVal = Value.valueOf("Integer", "3");
        
        assertThat(name, equalTo("or(var1, var2)"));
        assertThat(dataGrid.getRows().size(), equalTo(1));
        assertThat(dataGrid.getRows().get(0).getyValue(name), equalTo(expVal));
    }// </editor-fold>

}