package uk.co.awe.pmat.analysis.functions;

import uk.co.awe.pmat.deriveddata.functions.Find;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import uk.co.awe.pmat.deriveddata.DerivedDataException;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Value;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import uk.co.awe.pmat.deriveddata.InvalidArgumentsException;
import uk.co.awe.pmat.deriveddata.ParserValues.Constant;
import uk.co.awe.pmat.deriveddata.ParserValues.Property;
import uk.co.awe.pmat.deriveddata.ParserValues.Variable;
import uk.co.awe.pmat.db.MetaData;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author AWE Plc copyright 2013
 */
public class FindTest {

    public FindTest() {
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

    // <editor-fold defaultstate="collapsed" desc="find_should_find_the_first_value_where_the_property_matches_the_constant">
    @Test
    public void find_should_find_the_first_value_where_the_property_matches_the_constant() throws InvalidArgumentsException, DerivedDataException {

        Variable variable = new Variable(Variable.Type.RESULT, "test");
        Property property = new Property(MetaData.Type.MACHINE.name());
        Constant<String> value = new Constant<>("MachineC");
        Find find = new Find();
        find.bind(variable, property, value);

        List<DataGrid.Row> rows = new ArrayList<>();

        Map<String, Value<?>> yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 1.));
        DataGrid.Row row = new DataGrid.Row(null, yValues, null);
        row.addyValue(MetaData.Type.MACHINE.asFieldName(), new Value<>(MetaData.Type.MACHINE.name(), Rank.UNKNOWN, "MachineA"));
        rows.add(row);

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 2.));
        row = new DataGrid.Row(null, yValues, null);
        row.addyValue(MetaData.Type.MACHINE.asFieldName(), new Value<>(MetaData.Type.MACHINE.name(), Rank.UNKNOWN, "MachineB"));
        rows.add(row);

        yValues = new HashMap<>();
        yValues.put("test", new Value<>("test", Rank.UNKNOWN, 6.));
        row = new DataGrid.Row(null, yValues, null);
        row.addyValue(MetaData.Type.MACHINE.asFieldName(), new Value<>(MetaData.Type.MACHINE.name(), Rank.UNKNOWN, "MachineC"));
        rows.add(row);
        
        DataGrid dataGrid = new DataGrid("", "", rows);

        String name = find.evaluate(dataGrid);

        assertThat(name, equalTo("find(test, machine, MachineC)"));
        assertThat(dataGrid.getRows().size(), equalTo(3));

        rows = dataGrid.getRows();
        assertThat((Double) rows.get(0).getyValue(name).getValue(), equalTo(6.));
        assertThat((Double) rows.get(1).getyValue(name).getValue(), equalTo(6.));
        assertThat((Double) rows.get(2).getyValue(name).getValue(), equalTo(6.));
        
    }// </editor-fold>

}