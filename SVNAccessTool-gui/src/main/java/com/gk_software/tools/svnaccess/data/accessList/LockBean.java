package com.gk_software.tools.svnaccess.data.accessList;

/**
 * Represents a locked node.
 */
public class LockBean {

	/** The name of the SVN the node belongs to. */
	private String svn;

	/** The path to the node. */
	private String fullpath;

	/** The name of the user who locked the node. */
	private String lockedBy;

	/** The indicator whether the user who locked the node can unlock it. */
	private boolean rightsToUnlock;

	/**
	 * Creates a new instance and initializes the variables according to the given
	 * parameters.
	 *
	 * @param svn the name of the SVN the node belongs to
	 * @param fullpath the path to the node
	 * @param lockedBy the name of the user who locked the node
	 *
	 */
	public LockBean(String svn, String fullpath, String lockedBy) {
		super();
		this.svn = svn;
		this.fullpath = fullpath;
		this.lockedBy = lockedBy;
	}

	/**
	 * Creates a new instance and initializes the variables according to the given
	 * parameters.
	 *
	 * @param svn the name of the SVN the node belongs to
	 * @param fullpath the path to the node
	 * @param lockedBy the name of the user who locked the node
	 * @param rightsToUnlock the indicator whether the user can unlock the node
	 *
	 */
	public LockBean(String svn, String fullpath, String lockedBy,
			boolean rightsToUnlock) {
		super();
		this.svn = svn;
		this.fullpath = fullpath;
		this.lockedBy = lockedBy;
		this.rightsToUnlock = rightsToUnlock;
	}

	/**
	 * Returns the name of the SVN the node belongs to.
	 *
	 * @return the name of the SVN the node belongs to
	 *
	 */
	public String getSvn() {
		return svn;
	}

	/**
	 * Sets the new name of the SVN the node belongs to.
	 *
	 * @param svn the new name of the SVN the node belongs to
	 *
	 */
	public void setSvn(String svn) {
		this.svn = svn;
	}

	/**
	 * Returns the path to the node.
	 *
	 * @return the path to the node
	 *
	 */
	public String getFullpath() {
		return fullpath;
	}

	/**
	 * Sets the new path to the node.
	 *
	 * @param fullpath the new path to the node
	 *
	 */
	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

	/**
	 * Returns the name of the user who locked the node.
	 *
	 * @return the name of the user who locked the node
	 *
	 */
	public String getLockedBy() {
		return lockedBy;
	}

	/**
	 * Sets the new name of the user who locked the node.
	 *
	 * @param lockedBy the new name of the user who locked the node
	 *
	 */
	public void setLockedBy(String lockedBy) {
		this.lockedBy = lockedBy;
	}

	/**
	 * Returns the indicator whether the user who locked the node can unlock it.
	 *
	 * @return the indicator whether the user who locked the node can unlock it
	 *
	 */
	public boolean isRightsToUnlock() {
		return rightsToUnlock;
	}

	/**
	 * Sets the new indicator whether the user who locked the node can unlock it.
	 *
	 * @param rightsToUnlock the new indicator whether the user can unlock the node
	 *
	 */
	public void setRightsToUnlock(boolean rightsToUnlock) {
		this.rightsToUnlock = rightsToUnlock;
	}
}