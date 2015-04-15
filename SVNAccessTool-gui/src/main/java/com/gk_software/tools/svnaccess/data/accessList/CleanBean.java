package com.gk_software.tools.svnaccess.data.accessList;

/**
 * Represents a node which will be cleaned by the Cleaner.
 */
public class CleanBean {

	/** The name of the SVN the node belongs to. */
	private String SVN;

	/**
	 * Returns the name of the SVN the node belongs to.
	 *
	 * @return the name of the SVN the node belongs to
	 *
	 */
	public String getSVN() {
		return SVN;
	}

	/**
	 * Sets the new name of the SVN the node belongs to.
	 *
	 * @param sVN the new name of the SVN the node belongs to
	 *
	 */
	public void setSVN(String sVN) {
		SVN = sVN;
	}

	/**
	 * Creates a new instance and initializes the variable {@code sVN} according to the
	 * given value.
	 *
	 * @param sVN the name of the SVN the node belongs to
	 *
	 */
	public CleanBean(String sVN) {
		super();
		SVN = sVN;
	}

}