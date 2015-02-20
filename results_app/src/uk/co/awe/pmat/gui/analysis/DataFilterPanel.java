package uk.co.awe.pmat.gui.analysis;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.awe.pmat.db.DatabaseException;
import uk.co.awe.pmat.db.Rank;
import uk.co.awe.pmat.db.Restriction;
import uk.co.awe.pmat.db.Value;
import uk.co.awe.pmat.db.criteria.Category;
import uk.co.awe.pmat.db.criteria.Comparator;
import uk.co.awe.pmat.gui.models.DataFilterModel;
import uk.co.awe.pmat.gui.utils.DefaultComboBoxModel;

/**
 * A GUI panel to display and allow creation/editing of SelectCriteria. These
 * are stacked in the CriteriaListPanel to display a whole SelectCriteriaList.
 * 
 * @author AWE Plc copyright 2013
 */
public final class DataFilterPanel extends JPanel {

	private static final Logger LOG = LoggerFactory
			.getLogger(DataFilterPanel.class);

	private static final Icon CROSS_ICON = new ImageIcon(ClassLoader
			.getSystemResource("cross16.png"));
	private static final String CATEGORY_HEADER = "Select Category...";
	private static final String FIELD_HEADER = "Select Field...";
	private static final String VALUE_HEADER = "Select Value...";

	private final DataFilterModel model;
	private final DefaultComboBoxModel<Category> categoryModel;
	private final DefaultComboBoxModel<String> fieldModel;
	private final DefaultComboBoxModel<Rank> rankModel;
	private final DefaultComboBoxModel<Comparator> comparatorModel;
	private final DefaultComboBoxModel<Value<?>> valueModel;
	private final Action addAction;

	/**
	 * A helper class which is used to properly display {@code Value} objects in
	 * a combo box.
	 */
	private static final class ValueBoxRenderer implements ListCellRenderer {
		private final ListCellRenderer defaultRenderer;

		/**
		 * Create a new {@code ValueBoxRenderer}.
		 * 
		 * @param defaultRenderer
		 *            the default renderer which will do the actual work.
		 */
		ValueBoxRenderer(ListCellRenderer defaultRenderer) {
			this.defaultRenderer = defaultRenderer;
		}

		@Override
		@SuppressWarnings( { "rawtypes", "unchecked" })
		public Component getListCellRendererComponent(JList list, Object obj,
				int index, boolean isSelected, boolean cellHasFocus) {
			if (obj instanceof Value) {
				final Value<?> value = (Value<?>) obj;
				return defaultRenderer.getListCellRendererComponent(list, value
						.getValue(), index, isSelected, cellHasFocus);
			} else {
				return defaultRenderer.getListCellRendererComponent(list, obj,
						index, isSelected, cellHasFocus);
			}
		}
	}

	/**
	 * Create a new {@code DataFilterPanel}. This creates an incomplete panel
	 * with enabled fields, and an "add" button which becomes enabled when the
	 * fields are selected.
	 * 
	 * @param model
	 *            the model driving this panel.
	 * @param addAction
	 *            the action to perform to add a new panel to the list.
	 */
	@SuppressWarnings("unchecked")
    public DataFilterPanel(final DataFilterModel model, final Action addAction) {
        initComponents();

        this.model = model;
        categoryModel = new DefaultComboBoxModel<>(CATEGORY_HEADER, model.getCategories());
        fieldModel = new DefaultComboBoxModel<>(FIELD_HEADER);
        rankModel = new DefaultComboBoxModel<>();
        comparatorModel = new DefaultComboBoxModel<>();
        valueModel = new DefaultComboBoxModel<>(VALUE_HEADER);

        categoryModel.setSelectionAction(new CategorySelectedAction());
        fieldModel.setSelectionAction(new FieldSelectedAction());
        rankModel.setSelectionAction(new RankSelectedAction());
        comparatorModel.setSelectionAction(new ComparatorSelectedAction());
        valueModel.setSelectionAction(new ValueSelectedAction());

        categoryBox.setModel(categoryModel);
        fieldBox.setModel(fieldModel);
        rankBox.setModel(rankModel);
        comparatorBox.setModel(comparatorModel);
        valueBox.setModel(valueModel);
        valueBox.getRenderer();
        valueBox.setRenderer(new ValueBoxRenderer(valueBox.getRenderer()));

        this.addAction = addAction;
        addRemoveButton.setIcon(CROSS_ICON);
        addRemoveButton.setPreferredSize(new Dimension(
                CROSS_ICON.getIconWidth() + 10, CROSS_ICON.getIconHeight() + 10));
        addRemoveButton.setEnabled(false);
    }

	/**
	 * Create a new {@code DataFilterPanel}. This creates a completed panel with
	 * disabled fields and a "remove" panel button.
	 * 
	 * @param model
	 *            the model driving this panel.
	 * @param restriction
	 *            the restriction to display in this panel.
	 * @param removeAction
	 *            the action to perform when the Add/Remove button is clicked.
	 */
	@SuppressWarnings("unchecked")
    public DataFilterPanel(final DataFilterModel model,
            final Restriction restriction, final Action removeAction) {
        initComponents();

        this.model = model;
        categoryModel = new DefaultComboBoxModel<>(restriction.getCategory());
        fieldModel = new DefaultComboBoxModel<>(restriction.getField());
        rankModel = new DefaultComboBoxModel<>(restriction.getRank());
        comparatorModel = new DefaultComboBoxModel<>(restriction.getComparator());
        valueModel = new DefaultComboBoxModel<Value<?>>(restriction.getValue());

        categoryBox.setModel(categoryModel);
        fieldBox.setModel(fieldModel);
        rankBox.setModel(rankModel);
        comparatorBox.setModel(comparatorModel);
        valueBox.setModel(valueModel);
        valueBox.setRenderer(new ValueBoxRenderer(valueBox.getRenderer()));

        categoryBox.setEnabled(false);
        fieldBox.setEnabled(false);
        rankBox.setEnabled(false);
        comparatorBox.setEnabled(false);
        valueBox.setEnabled(false);

        this.addAction = null;
        addRemoveButton.setAction(removeAction);
        addRemoveButton.setIcon(CROSS_ICON);
        addRemoveButton.setPreferredSize(new Dimension(
                CROSS_ICON.getIconWidth() + 10, CROSS_ICON.getIconHeight() + 10));
        addRemoveButton.setEnabled(true);
    }

	/**
	 * Returns the {@code Category} selected by this panel.
	 * 
	 * @return the selected {@code Category}.
	 */
	Category getSelectedCategory() {
		return categoryModel.getSelectedElement();
	}

	/**
	 * Returns the field selected by this panel.
	 * 
	 * @return the selected field.
	 */
	String getSelectedField() {
		return fieldModel.getSelectedElement();
	}

	/**
	 * Returns the {@code Rank} selected by this panel.
	 * 
	 * @return the selected {@code Rank}.
	 */
	Rank getSelectedRank() {
		return rankModel.getSelectedElement();
	}

	/**
	 * Returns the {@code Comparator} selected by this panel.
	 * 
	 * @return the selected {@code Comparator}.
	 */
	Comparator getSelectedCompartor() {
		return comparatorModel.getSelectedElement();
	}

	/**
	 * Returns the {@code Value} selected by this panel.
	 * 
	 * @return the selected {@code Value}.
	 */
	Value<?> getSelectedValue() {
		return valueModel.getSelectedElement();
	}

	/**
	 * The {@link Action} to perform when a category combo box item is selected.
	 */
	private final class CategorySelectedAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (categoryModel.hasSelectedElement()) {
				fieldModel.removeAllElements();
				try {
					final List<String> fields = model.getFields(categoryModel
							.getSelectedElement());
					fieldModel.addAllElements(fields);
				} catch (DatabaseException ex) {
					LOG.error("Failed to get fields from database", ex);
				}
			}
		}
	}

	/**
	 * The {@link Action} to perform when a field combo box item is selected.
	 */
	private final class FieldSelectedAction extends AbstractAction {
		@Override
        public void actionPerformed(ActionEvent e) {
            if (fieldModel.hasSelectedElement()) {
                rankModel.removeAllElements();
                final Category category = categoryModel.getSelectedElement();
                final String field = fieldModel.getSelectedElement();
                final List<Rank> ranks = new ArrayList<>(model.getRanks(category, field));
                Collections.sort(ranks);
                ranks.add(0, Rank.ANY_RANK);
                rankModel.addAllElements(ranks);
            }
        }
	}

	/**
	 * The {@link Action} to perform when a rank combo box item is selected.
	 */
	private final class RankSelectedAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (rankModel.hasSelectedElement()) {
				comparatorModel.removeAllElements();
				comparatorModel.addAllElements(model.getComparators());
			}
		}
	}

	/**
	 * The {@link Action} to perform when a comparator combo box item is
	 * selected.
	 */
	private final class ComparatorSelectedAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (comparatorModel.hasSelectedElement()) {
				valueModel.removeAllElements();
				final Category category = categoryModel.getSelectedElement();
				final String field = fieldModel.getSelectedElement();
				final Rank rank = rankModel.getSelectedElement();
				try {
					List<Value<?>> values = model.getValues(category, field,
							rank);
					valueModel.addAllElements(values);
				} catch (DatabaseException ex) {
					LOG.error("Failed to get values from database", ex);
				}
			}
		}
	}

	/**
	 * The {@link Action} to perform when a value combo box item is selected.
	 */
	private final class ValueSelectedAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (valueModel.hasSelectedElement()) {
				addAction.actionPerformed(null);
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialise the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		java.awt.GridBagConstraints gridBagConstraints;

		setLayout(new java.awt.GridBagLayout());
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.8;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		add(categoryBox, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.8;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		add(fieldBox, gridBagConstraints);

		rankBox.setLightWeightPopupEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.3;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		add(rankBox, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 0.3;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		add(comparatorBox, gridBagConstraints);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
		add(valueBox, gridBagConstraints);

		addRemoveButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		addRemoveButton.setRequestFocusEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		add(addRemoveButton, gridBagConstraints);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private final javax.swing.JButton addRemoveButton = new javax.swing.JButton();
	private final javax.swing.JComboBox<Category> categoryBox = new javax.swing.JComboBox<Category>();
	private final javax.swing.JComboBox<Comparator> comparatorBox = new javax.swing.JComboBox<Comparator>();
	private final javax.swing.JComboBox<String> fieldBox = new javax.swing.JComboBox<String>();
	private final javax.swing.JComboBox<Rank> rankBox = new javax.swing.JComboBox<Rank>();
	private final javax.swing.JComboBox<Value<?>> valueBox = new javax.swing.JComboBox<Value<?>>();
	// End of variables declaration//GEN-END:variables

}
