package com.gk_software.tools.svnaccess.bussiness.impl;

import com.gk_software.tools.svnaccess.bussiness.impl.ActionEnum.Actions;
import com.gk_software.tools.svnaccess.data.accessList.Rights;

/**
 * Represents one change of a user or a group.
 */
public class Change {

	/** The change action. */
	private ActionEnum.Actions action;

	/** The rights of the user/group. */
	private Rights rights;

	/**
	 * Creates a new instance and initializes the variables according to the
	 * given values.
	 *
	 * @param added the change action
	 * @param rights the rights of the user/group
	 *
	 */
	public Change(Actions added, String rights) {
		this.action = added;
		this.rights = new Rights(rights);
	}

	/**
	 * Sets new rights according to the given parameter.
	 *
	 * @param rights the new rights
	 *
	 */
	public void setRights(String rights) {
		this.rights = new Rights(rights);
	}

	/**
	 * Returns the rights of the user/group.
	 *
	 * @return the rights of the user/group
	 *
	 */
	public Rights getRights() {
		return rights;
	}

	/**
	 * Returns the change action.
	 *
	 * @return the change action
	 *
	 */
	public ActionEnum.Actions getAction() {
		return action;
	}

	/**
	 * Sets new action according to the given parameter.
	 *
	 * @param action the new action
	 *
	 */
	public void setAction(ActionEnum.Actions action) {
		this.action = action;
	}

	/**
	 * Returns whether the rights are empty.
	 *
	 * @return true if the rights are empty otherwise false
	 *
	 */
	public boolean isEmpty() {
		if (rights.toString().length() == 0)
			return true;
		else
			return false;
	}

	/**
	 * Returns the string representation of this class.
	 *
	 * @return the string representation of this class
	 *
	 */
	public String toString() {
		return action.toString() + " " + rights.toString();
	}
}