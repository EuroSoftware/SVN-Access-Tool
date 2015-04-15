package com.gk_software.tools.svnaccess.bussiness.impl;

/**
 * Represents the  user. It contains user name and information if the user is
 * admin or not.
 */
public class User {

	/** The name of the user. */
	private String userName;

	/** The indicator whether the user is admin. */
	private boolean admin;

	/**
	 * Creates a new instance and initializes the variable {@code username} according to
	 * the given value.
	 *
	 * @param userName the name of the created user
	 *
	 */
	public User(String userName) {
		this.userName = userName;
	}

	/**
	 * Creates a new instance and initializes the variables according the given values.
	 *
	 * @param userName the name of the created user
	 * @param admin indicator whether the user is admin
	 *
	 */
	public User(String userName, boolean admin) {
		this.userName = userName;
		this.admin = admin;
	}

	/**
	 * Returns the name of the user.
	 *
	 * @return the name of the user
	 *
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the new name of the user.
	 *
	 * @param userName the new name of the user
	 *
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Returns the indicator whether the user is admin.
	 *
	 * @return the indicator whether the user is admin
	 *
	 */
	public boolean isAdmin() {
		return admin;
	}

	/**
	 * Sets the new indicator whether the user is admin.
	 *
	 * @param admin the new indicator whether the user is admin
	 *
	 */
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

}
