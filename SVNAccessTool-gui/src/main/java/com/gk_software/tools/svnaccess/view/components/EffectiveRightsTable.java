package com.gk_software.tools.svnaccess.view.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.component.ui.checkbox.GkCheckBox;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Rights;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalViewGroupsTree;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

/**
 * Represents a table containing the users with their rights to the currently chosen
 * node.
 */
public class EffectiveRightsTable extends Table {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(EffectiveRightsTable.class);

	/** The groups tree view window. */
	private ModalViewGroupsTree mvg;

	/** The map with user as key and its rights as value. */
	private Map<String, Rights> rights;

	/** The control class. */
	private IControl control;

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public EffectiveRightsTable(final IControl control) {
		logger.info("Parameters: control=" + control);
		this.control = control;
		mvg = new ModalViewGroupsTree(control);
		this.setSelectable(false);
		setSizeFull();
		setNullSelectionAllowed(true);
		this.addStyleName("effectiveRightsTable");

		logger.info("Creating container properties");
		/*
		 * Define the names and data types of columns. The "default value"
		 * parameter is meaningless here.
		 */
		this.addContainerProperty("NAME", Label.class, null);
		this.addContainerProperty("R", GkCheckBox.class, null);
		this.addContainerProperty("W", GkCheckBox.class, null);
		this.addContainerProperty("M", GkCheckBox.class, null);
		this.setPageLength(5);
		this.addListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (event.isDoubleClick()) {
					String group = (String) (event.getItemId());
					if (!group.startsWith("@")) {
						control.getApplication().showNotification(
								"There is no such a group");
						return;
					}
					try {
						Window win = mvg.getSubwindow(group.substring(1));

						if (win != null)
							if (win.getParent() != null) {
								// window is already showing
								getWindow().showNotification(
										"Window is already open");
							} else {
								// Open the subwindow by adding it to the parent
								// window
								// System.out.println("open window user");
								getWindow().addWindow(win);
								win.focus();

							}
					} catch (Exception e) {
						control.getApplication().showNotification(
								"There is no such a group");
						e.printStackTrace();
					}
				}
				// new ModalViewGroups(control);
			}
		});
		this.setColumnWidth("R", 10);
		this.setColumnWidth("W", 10);
		this.setColumnWidth("M", 10);
		this.setColumnExpandRatio("NAME", 1);

	}

	/**
	 * Checks if the users from the given map are already in the given map
	 * allWithRights. If they are and do not have modify rights then it gives
	 * it to them if they are not then it adds them with modify rights.
	 *
	 * @param allWithRights the map of all users with user as key and its rights as value
	 * @param map the map with users as key and its rights as value
	 *
	 */
	public void setRights(Map<String, Rights> allWithRights,
			Map<String, Rights> map) {
		logger.info("Parameters: userWithRights=" + allWithRights);
		this.rights = allWithRights;
		for (String key : map.keySet()) {
			Rights r = map.get(key);
			if (allWithRights.containsKey(key)) {
				Rights rr = allWithRights.get(key);
				if (!rr.isModify())
					rr.setModify(true);
			} else {
				allWithRights.put(key, new Rights("m"));
			}
		}
		update();
	}

	/**
	 * Returns the map with user as key and its rights as value.
	 *
	 * @return the map with user as key and its rights as value
	 *
	 */
	public Map<String, Rights> getUsersWithRights() {
		return rights;
	}

	/**
	 * Removes all users from the table and then it adds them and adds the style
	 * to which they where changed.
	 */
	private void update() {
		logger.info("Updating effective rights table");
		this.removeAllItems();

		Set<String> nameSet = rights.keySet();
		ArrayList<String> aaa = new ArrayList<String>(nameSet);
		Collections.sort(aaa);

		for (String name : aaa) {
			Label label = new Label(name);
			label.setDescription(name);
			Rights right = rights.get(name);
			GkCheckBox chR = new GkCheckBox();
			chR.setEnabled(false);
			chR.setStyleName("defaultRights");
			if (right.isRead()) {
				chR.setValue(true);
			} else {
				chR.setValue(false);
			}
			GkCheckBox chW = new GkCheckBox();
			chW.setEnabled(false);
			chW.setStyleName("defaultRights");
			if (right.isWrite()) {
				chW.setValue(true);
			} else {
				chW.setValue(false);
			}

			GkCheckBox chM = new GkCheckBox();
			chM.setEnabled(false);
			chM.setStyleName("defaultRights");
			if (right.isModify()) {
				chM.setValue(true);
			} else {
				chM.setValue(false);
			}

			this.addItem(new Object[] { label, chR, chW, chM }, name);

		}
		this.validate();

	}
}
