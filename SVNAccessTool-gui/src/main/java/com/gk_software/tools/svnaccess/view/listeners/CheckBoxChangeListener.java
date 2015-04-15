package com.gk_software.tools.svnaccess.view.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gk_software.core.client.web_vaadin.component.ui.checkbox.GkCheckBox;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.view.components.FavoriteBean;
import com.gk_software.tools.svnaccess.view.components.Repositories;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;

/**
 * Represents a listener of value changes in the checkboxes which represent user or
 * group rights.
 */
public class CheckBoxChangeListener implements ValueChangeListener {

	/** The instance of the {@code Logger} class. */
	private static final Log logger = LogFactory.getLog(CheckBoxChangeListener.class);

	/** The name of the user checkbox. */
	public static final String USER = "user";

	/** The name of the group checkbox. */
	public static final String GROUP = "group";

	/** The control class. */
	private IControl control;

	/** The string indicating if the checkbox is for user or group. */
	private String groupOrUser;

	/**
	 * Creates a new instance and sets the variables according to the given values.
	 *
	 * @param control the control class
	 * @param groupOrUser the string indicating if the checkbox is for user or group
	 *
	 */
	public CheckBoxChangeListener(IControl control, String groupOrUser) {
		this.control = control;
		this.groupOrUser = groupOrUser;

	}

	/**
	 * Handles the value change event.
	 *
	 * @param event the value change event
	 *
	 */
	public void valueChange(ValueChangeEvent event) {
		logger.info("Checkbox changed");
		GkCheckBox checkbox = (GkCheckBox) event.getProperty();
		String dataString = (String) checkbox.getData();
		String[] data = dataString.split(";");

		if (control.isDirectoryValid(control.getApplication().getDirectories()
				.getNodePath())) {
			logger.info("Timestamp is valid");
			if (groupOrUser.equals(GROUP)) {
				control.updateGroup(data[0], data[1]);
			} else if (groupOrUser.equals(USER)) {
				control.updateUser(data[0], data[1]);
			}
		} else {
			Repositories rep = control.getApplication().getRepositories();
			String svn = rep.getChosenRepo();
			String fullPath = ((FavoriteBean) control.getApplication()
					.getCurrentPath().getValue()).getFullpath();
			control.timestampNotValidNotification(svn, fullPath);
		}
		control.getApplication().getDirectories().refreshRowCache();
	}
}
