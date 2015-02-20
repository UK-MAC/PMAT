package uk.co.awe.pmat.deriveddata;

import java.util.List;
import uk.co.awe.pmat.db.DataGrid;
import uk.co.awe.pmat.db.Value;

/**
 * A container class which holds the different types of operators which can be
 * used in building up the object tree from parsing the derived data equation,
 * i.e. '+', '-', '/', etc. These operators fall into two categories: unary and
 * binary operators and abstract classes exist for both these types to implement
 * common functionality.
 * 
 * @author AWE Plc copyright 2013
 */
public final class Operators {

	/**
	 * This class cannot be instantiated.
	 */
	private Operators() {
	}

	/**
	 * The abstract class which should be inherited from by all Unary Operators.
	 */
	public abstract static class UnaryOp extends AbstractFunction {
		@Override
		public void bind(DerivedData... args) throws InvalidArgumentsException {
			super.bind(new Class<?>[][] { new Class<?>[] { Double.class,
					Integer.class }, }, args);
		}

		@Override
		public Class<?>[] returnTypes() {
			return new Class<?>[] { Double.class };
		}

		@Override
		protected String apply(DataGrid dataGrid, List<String> argCols) {
			if (argCols.size() != 1) {
				throw new IllegalStateException(
						"Wrong number of columns passed to UnaryOp: "
								+ argCols.size());
			}
			final String name = name() + argCols.get(0);
			for (DataGrid.Row row : dataGrid.getRows()) {
				final Value<Double> val = Value.doubleValue(row
						.getyValue(argCols.get(0)));
				row.addyValue(name, doOp(val));
			}
			return name;
		}

		/**
		 * Apply the operator to the given argument.
		 * 
		 * @param val
		 *            the argument to apply the operator to.
		 * @return the result.
		 */
		protected abstract Value<Double> doOp(Value<Double> val);

		/**
		 * Return the name of the operator.
		 * 
		 * @return the operator name.
		 */
		protected abstract String name();
	}

	/**
	 * The abstract class which should be inherited from by all Binary
	 * Operators.
	 */
	public abstract static class BinaryOp extends AbstractFunction {
		@Override
		public void bind(DerivedData... args) throws InvalidArgumentsException {
			super.bind(new Class<?>[][] {
					new Class<?>[] { Double.class, Integer.class },
					new Class<?>[] { Double.class, Integer.class }, }, args);
		}

		@Override
		public Class<?>[] returnTypes() {
			return new Class<?>[] { Double.class };
		}

		@Override
		protected String apply(DataGrid dataGrid, List<String> argCols) {
			if (argCols.size() != 2) {
				throw new IllegalStateException(
						"Wrong number of columns passed to BinaryOp: "
								+ argCols.size());
			}
			final String name = argCols.get(0) + name() + argCols.get(1);
			for (DataGrid.Row row : dataGrid.getRows()) {
				final Value<Double> lhs = Value.doubleValue(row
						.getyValue(argCols.get(0)));
				final Value<Double> rhs = Value.doubleValue(row
						.getyValue(argCols.get(1)));
				row.addyValue(name, doOp(lhs, rhs));
			}
			return name;
		}

		/**
		 * Apply the operator to the given arguments.
		 * 
		 * @param lhs
		 *            the left hand side argument of the operator.
		 * @param rhs
		 *            the right hand side argument of the operator.
		 * @return the result.
		 */
		protected abstract Value<Double> doOp(Value<Double> lhs,
				Value<Double> rhs);

		/**
		 * Return the name of the operator.
		 * 
		 * @return the operator name.
		 */
		protected abstract String name();
	}

	/**
	 * The binary addition operator '+'.
	 */
	public static class Add extends BinaryOp {
		@Override
		protected Value<Double> doOp(Value<Double> lhs, Value<Double> rhs) {
			final Double newVal = lhs.getValue() + rhs.getValue();
			return lhs.updateValue(newVal);
		}

		@Override
		protected String name() {
			return "+";
		}
	}

	/**
	 * The binary subtraction operator '-'.
	 */
	public static class Subtract extends BinaryOp {
		@Override
		protected Value<Double> doOp(Value<Double> lhs, Value<Double> rhs) {
			final Double newVal = lhs.getValue() - rhs.getValue();
			return lhs.updateValue(newVal);
		}

		@Override
		protected String name() {
			return "-";
		}
	}

	/**
	 * The binary division operator '/'.
	 */
	public static class Divide extends BinaryOp {
		@Override
		protected Value<Double> doOp(Value<Double> lhs, Value<Double> rhs) {
			final Double newVal = lhs.getValue() / rhs.getValue();
			return lhs.updateValue(newVal);
		}

		@Override
		protected String name() {
			return "/";
		}
	}

	/**
	 * The binary multiplication operator '*'.
	 */
	public static class Multiply extends BinaryOp {
		@Override
		protected Value<Double> doOp(Value<Double> lhs, Value<Double> rhs) {
			final Double newVal = lhs.getValue() * rhs.getValue();
			return lhs.updateValue(newVal);
		}

		@Override
		protected String name() {
			return "*";
		}
	}

	/**
	 * The unary negation operator '-'.
	 */
	public static class Negative extends UnaryOp {
		@Override
		protected Value<Double> doOp(Value<Double> val) {
			return val.updateValue(-val.getValue());
		}

		@Override
		protected String name() {
			return "-";
		}
	}

}
