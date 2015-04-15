package com.gk_software.tools.svnaccess.view.components;

/**
 * Represents a cookie node.
 */
public class FavoriteBean {

	/** The name of the cookie. */
	private String cookieName;

	/** The value of the cookie. */
	private int value;

	/** The timestamp of the cookie. */
	private long timestamp;

	/** The path to the node in the repository. */
	private String fullpath;

	/** The path to the repository. */
	private String svn;

	/** The indicator whether the node is selected. */
	private boolean selected = false;

	/**
	 * Creates a new instance.
	 */
	public FavoriteBean() {

	}

	/**
	 * Creates a new instance, sets the {@code cookieName} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param cookieName the name of the cookie
	 *
	 */
	public FavoriteBean(String cookieName) {
		super();
		this.cookieName = cookieName;
		this.fullpath = cookieName;
	}

	/**
	 * Creates a new instance, sets the variables according to the given values
	 * and initializes all the other variables.
	 *
	 * @param cookieName the name of the cookie
	 * @param value the value of the cookie
	 *
	 */
	public FavoriteBean(String cookieName, int value) {
		super();
		this.cookieName = cookieName;
		this.value = value;
		String[] split = cookieName.split("##");
		this.svn = split[1];
		this.fullpath = split[2];
	}

	/**
	 * Creates a new instance, sets the variables according to the given values
	 * and initializes all the other variables.
	 *
	 * @param cookieName the name of the cookie
	 * @param value the value of the cookie
	 *
	 */
	public FavoriteBean(String cookieName, long value) {
		super();
		this.cookieName = cookieName;
		this.timestamp = value;
		String[] split = cookieName.split("##");
		this.svn = split[1];
		this.fullpath = split[2];
	}

	/**
	 * If the node is selected returns the path to the node in the repository else
	 * returns the string formed from the path to the repository, - and the path to
	 * the node in the repository.
	 *
	 * @return the add user button
	 *
	 */
	public String toString() {
		if (selected)
			return fullpath;
		else
			return svn + " - " + fullpath;
	}

	/**
	 * Returns the name of the cookie.
	 *
	 * @return the name of the cookie
	 *
	 */
	public String getCookieName() {
		return cookieName;
	}

	/**
	 * Sets the new name of the cookie.
	 *
	 * @param cookieName the new name of the cookie
	 *
	 */
	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	/**
	 * Returns the value of the cookie.
	 *
	 * @return the value of the cookie
	 *
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the new value of the cookie.
	 *
	 * @param value the new value of the cookie
	 *
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Returns the path to the node in the repository.
	 *
	 * @return the path to the node in the repository
	 *
	 */
	public String getFullpath() {
		return fullpath;
	}

	/**
	 * Sets the new path to the node in the repository.
	 *
	 * @param fullpath the new path to the node in the repository
	 *
	 */
	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

	/**
	 * Returns the path to the repository.
	 *
	 * @return the path to the repository
	 *
	 */
	public String getSvn() {
		return svn;
	}

	/**
	 * Returns the timestamp of the cookie.
	 *
	 * @return the timestamp of the cookie
	 *
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets the new timestamp of the cookie.
	 *
	 * @param timestamp the timestamp of the cookie
	 *
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the indicator whether the node is selected.
	 *
	 * @return the indicator whether the node is selected
	 *
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the new indicator whether the node is selected.
	 *
	 * @param selected the new indicator whether the node is selected
	 *
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Sets the new path to the repository.
	 *
	 * @param svn the new path to the repository
	 *
	 */
	public void setSvn(String svn) {
		this.svn = svn;
	}

	/**
	 * Returns the hashcode of this node.
	 *
	 * @return the hashcode of this node
	 *
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fullpath == null) ? 0 : fullpath.hashCode());
		result = prime * result + ((svn == null) ? 0 : svn.hashCode());
		return result;
	}

	/**
	 * Compares this node to the given object.
	 *
	 * @param obj the compared object
	 *
	 * @return true if they equal otherwise false
	 *
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FavoriteBean other = (FavoriteBean) obj;
		if (fullpath == null) {
			if (other.fullpath != null)
				return false;
		} else if (!fullpath.equals(other.fullpath))
			return false;
		if (svn == null) {
			if (other.svn != null)
				return false;
		} else if (!svn.equals(other.svn))
			return false;
		return true;
	}
}