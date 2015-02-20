package uk.co.awe.pmat.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.dom.DOMElement;
import uk.co.awe.pmat.db.xml.XMLSerialisable;

/**
 * A data object class that holds the parameter and results set for a {@code
 * Run}.
 * 
 * @author AWE Plc copyright 2013
 */
public final class RunData implements XMLSerialisable {

	private final List<Value<?>> parameters;
	private final List<Value<Double>> results;

	/**
	 * Create a new {@code RunData}.
	 * 
	 * @param parameters
	 *            the parameters stored in this {@code RunData}.
	 * @param results
	 *            the results stored in this {@code RunData}.
	 */
	public RunData(List<Value<?>> parameters, List<Value<Double>> results) {
        this.parameters = new ArrayList<>(parameters);
        this.results = new ArrayList<>(results);
    }

	/**
	 * Returns the parameters stored in this {@code RunData}.
	 * 
	 * @return the parameters.
	 */
	public List<Value<?>> getParameters() {
		return Collections.unmodifiableList(parameters);
	}

	/**
	 * Returns the results stored in this {@code RunData}.
	 * 
	 * @return the results.
	 */
	public List<Value<Double>> getResults() {
		return Collections.unmodifiableList(results);
	}

	/**
	 * Add a parameter to the parameters stored in this {@code RunData}.
	 * 
	 * @param parameter
	 *            the parameter to add.
	 */
	public void addParameter(Value<?> parameter) {
		parameters.add(parameter);
	}

	public void addResult(Value<Double> result) {
		results.add(result);
	}

	/**
	 * Update the name of the parameter with the given index.
	 * 
	 * @param paramIdx
	 *            the parameter index.
	 * @param name
	 *            the new parameter name.
	 */
	public void updateParameterName(int paramIdx, String name) {
		parameters.set(paramIdx, parameters.get(paramIdx).updateName(name));
	}

	/**
	 * Return the parameter with the given name.
	 * 
	 * @param name
	 *            the name of the parameter.
	 * @return the parameter.
	 */
	public Value<?> getParameter(String name) {
		for (Value<?> parameter : parameters) {
			if (parameter.getName().equals(name)) {
				return parameter;
			}
		}
		return null;
	}

	@Override
	public Element toXML() {
		Element node = new DOMElement(getClass().getSimpleName());

		Element paramNode = new DOMElement(new QName("Parameters"));
		for (Value<?> param : parameters) {
			paramNode.add(param.toXML());
		}

		Element resultNode = new DOMElement(new QName("Results"));
		for (Value<Double> result : results) {
			resultNode.add(result.toXML());
		}

		node.add(paramNode);
		node.add(resultNode);
		return node;
	}

}
