package com.gk_software.tools.svnaccess.view.components;

import java.util.Map;

import com.gk_software.core.client.web_vaadin.component.ui.checkbox.GkCheckBox;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.bussiness.impl.ActionEnum.Actions;
import com.gk_software.tools.svnaccess.bussiness.impl.Change;
import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.Rights;
import com.gk_software.tools.svnaccess.view.listeners.CheckBoxChangeListener;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalViewGroupsTree;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;

/**
 * Represents the table containing groups for the selected node.
 */
public class GroupsTable extends Table {

	/** The map with group as key and its rights as value. */
	private Map<String, Rights> groupsWithRights;

	/** The control class. */
	private IControl control;

	/** The listener for the checkbox changes. */
	private ValueChangeListener checkBoxListener;

	/** The groups tree view window. */
	private ModalViewGroupsTree mvg;

	/** The indicator if the group can modify checkboxes. */
	private boolean canModifyCheckboxes = false;

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public GroupsTable(final IControl control) {

		this.control = control;
		this.mvg = new ModalViewGroupsTree(control);
		this.checkBoxListener = new CheckBoxChangeListener(control,
				CheckBoxChangeListener.GROUP);
		this.setSelectable(true);
		this.setMultiSelect(true);
		setSizeFull();
		setNullSelectionAllowed(true);
		this.addStyleName("groupTable");
		this.addListener(new ItemClickListener() {

			@Override
			public void itemClick(ItemClickEvent event) {
				// TODO Auto-generated method stub
				if (event.isDoubleClick()) {

					try {
						Window win = mvg.getSubwindow(((String) event
								.getItemId()).substring(1));

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
					}
				}
				// new ModalViewGroups(control);
			}
		});
		/*
		 * Define the names and data types of columns. The "default value"
		 * parameter is meaningless here.
		 */
		this.addContainerProperty("GROUPS", Label.class, null);
		this.addContainerProperty("R", GkCheckBox.class, null);
		this.addContainerProperty("W", GkCheckBox.class, null);
		this.addContainerProperty("M", GkCheckBox.class, null);

		// Show just three rows because they are so high.
		this.setPageLength(3);

		this.setColumnExpandRatio("GROUPS", 1);
		this.setColumnWidth("R", 10);
		this.setColumnWidth("W", 10);
		this.setColumnWidth("M", 10);
	}

	/**
	 * Sets the new map with group as key and its rights as value.
	 *
	 * @param groupsWithRights the new map with group as key and its rights as value
	 *
	 */
	public void setGroups(Map<String, Rights> groupsWithRights) {
		this.groupsWithRights = groupsWithRights;
	}

	/**
	 * Returns the map with group as key and its rights as value.
	 *
	 * @return the map with group as key and its rights as value
	 *
	 */
	public Map<String, Rights> getGroupsWithRights() {
		return groupsWithRights;
	}

	/**
	 * Removes all groups from the table and then adds them and also adds the style to
	 * which they where changed.
	 */
	public void update() {
		this.removeAllItems();

		Node lastChosenNode = control.getApplication().getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = control.getApplication().getRepositories().getChosenRepo();
		Changes changes = control.getChanges().get(svn + ":" + fullpath);

		if (changes == null)
			changes = new Changes();

		Map<String, Change> groupChanges = changes.getGroupsChangesDeepCopy();

		// users that are in list, but it has to checked whether are changed
		if (groupsWithRights != null)
			for (String groupName : groupsWithRights.keySet()) {
				String labelStyle = "default";
				String rStyle = "defaultRights";
				String wStyle = "defaultRights";
				String mStyle = "defaultRights";
				Change change = groupChanges.get(groupName);
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
					groupChanges.remove(groupName);
				}

				Rights right = (Rights) groupsWithRights.get(groupName);
				Rights changedRights = null;
				if (change != null) {
					changedRights = change.getRights();
				}

				Label label = new Label(groupName);
				label.setDescription(groupName);
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
				r.setData(groupName + ";r");
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
				w.setData(groupName + ";w");
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
				m.setData(groupName + ";m");
				m.addListener(checkBoxListener);
				m.setImmediate(true);

				this.addItem(new Object[] { label, r, w, m }, groupName);

			}

		// there should remain only newly added groups
		if (groupChanges != null)
			for (String groupName : groupChanges.keySet()) {
				Rights right = groupChanges.get(groupName).getRights();
				Label label = new Label(groupName);
				label.setDescription(groupName);
				label.addStyleName("addedItem");

				GkCheckBox r = new GkCheckBox();
				if (right.isRead())
					r.setValue(true);
				r.addStyleName("addedRight");
				r.setData(groupName + ";r");
				r.addListener(checkBoxListener);
				r.setImmediate(true);

				GkCheckBox w = new GkCheckBox();
				if (right.isWrite())
					w.setValue(true);
				w.addStyleName("addedRight");
				w.setData(groupName + ";w");
				w.addListener(checkBoxListener);
				w.setImmediate(true);

				GkCheckBox m = new GkCheckBox();
				if (right.isModify())
					m.setValue(true);
				m.addStyleName("addedRight");
				m.setData(groupName + ";m");
				m.addListener(checkBoxListener);
				m.setImmediate(true);

				this.addItem(new Object[] { label, r, w, m }, groupName);
			}
		control.cleanChanges(svn + ":" + fullpath);
		control.getApplication().getDirectories().refreshRowCache();
	}

	/**
	 * Returns the indicator if the group can modify checkboxes.
	 *
	 * @return the indicator if the group can modify checkboxes
	 *
	 */
	public boolean isCanModifyCheckboxes() {
		return canModifyCheckboxes;
	}

	/**
	 * Sets the new indicator if the group can modify checkboxes.
	 *
	 * @param canModifyCheckboxes the new indicator if the group can modify checkboxes
	 *
	 */
	public void setCanModifyCheckboxes(boolean canModifyCheckboxes) {
		this.canModifyCheckboxes = canModifyCheckboxes;
	}
}
