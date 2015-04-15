package com.gk_software.tools.svnaccess.view.components;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.vaadin.ui.TwinColSelect;

/**
 * Represents the twin column select component used for admin settings.
 */
public class TwinColumnSelect extends TwinColSelect {

	/** The instance of this class. */
	private TwinColumnSelect l = this;

	/** The selected values. */
	private String selected = "";

	/** The listener for the changes in the columns. */
	ValueChangeListener listener = new ValueChangeListener() {
		public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
			selected = "";
			if (!event.getProperty().toString().equals("[]")) {
				String p = event.getProperty().toString();
				String[] a = p.substring(1, p.length() - 1).split(",");
				for (int i = 0; i < a.length; i++) {
					if (a[i].trim().length() != 0)
						selected += a[i].trim() + ";";
				}
			}
		}
	};

	/**
	 * Creates a new instance, sets the variables according to the given values and
	 * initializes all the other variables.
	 *
	 * @param list the list of users or groups
	 * @param left the caption of the left column
	 * @param right the caption of the right column
	 * @param groups indicator if the list contains groups or users
	 *
	 */
	public TwinColumnSelect(List<String> list, String left, String right,
			boolean groups) {
		java.util.Collections.sort(list, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				Collator collator = Collator
						.getInstance(new Locale("cs", "CZ"));
				return collator.compare(o1, o2);

			}
		});

		for (String user : list) {
			if (groups)
				l.addItem("@" + user);
			else
				l.addItem(user);

		}
		l.addListener(listener);
		l.setSizeFull();
		l.setNullSelectionAllowed(true);
		l.setMultiSelect(true);
		l.setImmediate(true);
		l.setLeftColumnCaption(left);
		l.setRightColumnCaption(right);
		l.commit();
	}

	/**
	 * Adds the given values from the defualt values list to the left column.
	 *
	 * @param defaultList the list of the default values
	 *
	 */
	public void setDefaultValues(List<String> defaultList) {
		l.setValue(defaultList);
		selected = "";
		for (String s : defaultList)
			selected += s + ";";
	}

	/**
	 * Returns the selected values.
	 *
	 * @return the selected values
	 *
	 */
	public String getSelected() {
		return selected;
	}
}