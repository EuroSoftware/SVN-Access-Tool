package com.gk_software.tools.svnaccess.view.components;

import java.util.Map;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.component.ui.checkbox.GkCheckBox;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.bussiness.impl.ActionEnum.Actions;
import com.gk_software.tools.svnaccess.bussiness.impl.Change;
import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.Rights;
import com.gk_software.tools.svnaccess.view.listeners.CheckBoxChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;

/**
 * Represents the table containing users for the selected node.
 */
public class UsersTable extends Table {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(UsersTable.class);


	/** The map with user as key and its rights as value. */
	private Map<String, Rights> usersWithRights;

	/** The control class. */
	private IControl control;

	/** The listener for the checkbox changes. */
	private ValueChangeListener checkBoxListener;


	/** The indicator if the user can modify checkboxes. */
	private boolean canModifyCheckboxes = false;


	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public UsersTable(IControl control) {
		logger.info("Parameters: control=" + control);
		this.control = control;
		this.checkBoxListener = new CheckBoxChangeListener(control,
				CheckBoxChangeListener.USER);
		this.setSelectable(true);
		this.setMultiSelect(true);
		this.setSizeFull();
		setNullSelectionAllowed(true);
		this.addStyleName("userTable");

		logger.info("Creating container properties");
		/*
		 * Define the names and data types of columns. The "default value"
		 * parameter is meaningless here.
		 */
		this.addContainerProperty("USERS", Label.class, null);
		this.addContainerProperty("R", GkCheckBox.class, null);
		this.addContainerProperty("W", GkCheckBox.class, null);
		this.addContainerProperty("M", GkCheckBox.class, null);
		this.setPageLength(10);

		this.setColumnExpandRatio("USERS", 1);
		this.setColumnWidth("R", 10);
		this.setColumnWidth("W", 10);
		this.setColumnWidth("M", 10);

	}

	/**
	 * Sets the new map with user as key and its rights as value.
	 *
	 * @param usersWithRights the new map with user as key and its rights as value
	 *
	 */
	public void setUsers(Map<String, Rights> usersWithRights) {
		logger.info("Parameters: userWithRights=" + usersWithRights);
		this.usersWithRights = usersWithRights;
	}

	/**
	 * Returns the map with user as key and its rights as value.
	 *
	 * @return the map with user as key and its rights as value
	 *
	 */
	public Map<String, Rights> getUsersWithRights() {
		return usersWithRights;
	}

	/**
	 * Removes all users from the table and then adds them and also adds the style to
	 * which they where changed.
	 */
	public void update() {
		logger.info("Updating user table");
		this.removeAllItems();

		Node lastChosenNode = control.getApplication().getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = control.getApplication().getRepositories().getChosenRepo();
		Changes changes = control.getChanges().get(svn + ":" + fullpath);

		if (changes == null)
			changes = new Changes();

		Map<String, Change> userChanges = changes.getUserChangesDeepCopy();
		if (usersWithRights != null)
			// users that are in list, but it has to checked whether are changed
			for (String userName : usersWithRights.keySet()) {
				String labelStyle = "default";
				String rStyle = "defaultRights";
				String wStyle = "defaultRights";
				String mStyle = "defaultRights";
				Change change = userChanges.get(userName);
				if (change != null) { // if it is null I dont have to change its
										// style
					if (change.getAction() == Actions.REMOVED) {
						labelStyle = "deadItem";
						rStyle = "deadItem";
						wStyle = "deadItem";
						mStyle = "deadItem";
					} else if (change.getAction() == Actions.UPDATED) {
						if (change.getRights().isRead()) {
							rStyle = "changedRights";
						}
						if (change.getRights().isWrite()) {
							wStyle = "changedRights";
						}
						if (change.getRights().isModify()) {
							mStyle = "changedRights";
						}
					}

					// now I have to remove this item from copy of changes
					userChanges.remove(userName);
				}
				Rights right = (Rights) usersWithRights.get(userName);
				Rights changedRights = null;
				if (change != null) {
					changedRights = change.getRights();
				}

				Label label = new Label(userName);
				label.setDescription(userName);
				if (!labelStyle.equals("default"))
					label.addStyleName(labelStyle);

				GkCheckBox r = new GkCheckBox();
				if (!canModifyCheckboxes)
					r.setEnabled(false);

				// when there is change for this and its for read right
				if (changedRights != null && changedRights.isRead()) {
					r.setValue(!right.isRead());
					if (right.isRead()) {
						rStyle = "removedRights";
					}
				} else {
					r.setValue(right.isRead());
				}
				r.addStyleName(rStyle);
				r.setData(userName + ";r");
				r.addListener(checkBoxListener);
				r.setImmediate(true);

				GkCheckBox w = new GkCheckBox();
				if (!canModifyCheckboxes)
					w.setEnabled(false);
				// when there is change for this and its for write right
				if (changedRights != null && changedRights.isWrite()) {
					w.setValue(!right.isWrite());
					if (right.isWrite()) {
						wStyle = "removedRights";
					}
				} else {
					w.setValue(right.isWrite());
				}

				w.addStyleName(wStyle);
				w.setData(userName + ";w");
				w.addListener(checkBoxListener);
				w.setImmediate(true);

				GkCheckBox m = new GkCheckBox();
				if (!canModifyCheckboxes)
					m.setEnabled(false);
				// when there is change for this and its for read modify
				if (changedRights != null && changedRights.isModify()) {
					m.setValue(!right.isModify());
					if (right.isModify()) {
						mStyle = "removedRights";
					}
				} else {
					m.setValue(right.isModify());
				}

				m.addStyleName(mStyle);
				m.setData(userName + ";m");
				m.addListener(checkBoxListener);
				m.setImmediate(true);

				this.addItem(new Object[] { label, r, w, m }, userName);

			}

		// there should remain only newly added users
		if (userChanges != null)
			for (String userName : userChanges.keySet()) {
				Rights right = userChanges.get(userName).getRights();
				Label label = new Label(userName);
				label.setDescription(userName);
				label.addStyleName("addedItem");

				GkCheckBox r = new GkCheckBox();
				if (right.isRead())
					r.setValue(true);
				r.addStyleName("addedRight");
				r.setData(userName + ";r");
				r.addListener(checkBoxListener);
				r.setImmediate(true);

				GkCheckBox w = new GkCheckBox();
				if (right.isWrite())
					w.setValue(true);
				w.addStyleName("addedRight");
				w.setData(userName + ";w");
				w.addListener(checkBoxListener);
				w.setImmediate(true);

				GkCheckBox m = new GkCheckBox();
				if (right.isModify())
					m.setValue(true);
				m.addStyleName("addedRight");
				m.setData(userName + ";m");
				m.addListener(checkBoxListener);
				m.setImmediate(true);
				this.addItem(new Object[] { label, r, w, m }, userName);
			}
		control.cleanChanges(svn + ":" + fullpath);
		control.getApplication().getDirectories().refreshRowCache();
	}

	/**
	 * Returns the indicator if the user can modify checkboxes.
	 *
	 * @return the indicator if the user can modify checkboxes
	 *
	 */
	public boolean isCanModifyCheckboxes() {
		return canModifyCheckboxes;
	}

	/**
	 * Sets the new indicator if the user can modify checkboxes.
	 *
	 * @param canModifyCheckboxes the new indicator if the user can modify checkboxes
	 *
	 */
	public void setCanModifyCheckboxes(boolean canModifyCheckboxes) {
		this.canModifyCheckboxes = canModifyCheckboxes;
	}

}
