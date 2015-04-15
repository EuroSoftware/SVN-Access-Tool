package com.gk_software.tools.svnaccess.data.accessList;

/**
 * Represent the rights read from the access list and the changes made by the logged
 * users in the application.
 */
public class Rights {

	/** The R right. */
	private boolean read = false;

	/** The W right. */
	private boolean write = false;

	/** The M right. */
	private boolean modify = false;

	/**
	 * Creates a new instance and initializes the rights variables by parsing the given
	 * rights string.
	 *
	 * @param rights the rights string
	 *
	 */
	public Rights(String rights) {
		for (int i = 0; i < rights.length(); i++) {
			char c = rights.charAt(i);
			if (c == 'r')
				read = true;
			if (c == 'w')
				write = true;
			if (c == 'm')
				modify = true;
		}
	}

	/**
	 * Creates a new instance and initializes the variables according to the given
	 * parameters.
	 *
	 * @param read the R right
	 * @param write the W right
	 * @param modify the M right
	 *
	 */
	public Rights(boolean read, boolean write, boolean modify) {
		this.read = read;
		this.write = write;
		this.modify = modify;
	}

	/**
	 * Returns the R right.
	 *
	 * @return the R right
	 *
	 */
	public boolean isRead() {
		return read;
	}

	/**
	 * Returns the W right.
	 *
	 * @return the W right
	 *
	 */
	public boolean isWrite() {
		return write;
	}

	/**
	 * Returns the M right.
	 *
	 * @return the M right
	 *
	 */
	public boolean isModify() {
		return modify;
	}

	/**
	 * Sets the new R right.
	 *
	 * @param read the new R right
	 *
	 */
	public void setRead(boolean read) {
		this.read = read;
	}

	/**
	 * Sets the new W right.
	 *
	 * @param write the new W right
	 *
	 */
	public void setWrite(boolean write) {
		this.write = write;
	}

	/**
	 * Sets the new M right.
	 *
	 * @param modify the new M right
	 *
	 */
	public void setModify(boolean modify) {
		this.modify = modify;
	}

	/**
	 * Returns rights in the standard format format.
	 *
	 * @return the M right
	 *
	 */
	public String printACL() {
		String output = "";
		if (isRead())
			output += "r";
		if (isWrite())
			output += "w";
		return output;
	}

	/**
	 * Returns rights in the viewSVN format.
	 *
	 * @return the M right
	 *
	 */
	public String printViewSvnACL() {
		String output = "";
		if (isRead())
			output += "r";
		return output;
	}

	/**
	 * Returns the string representation of this class.
	 *
	 * @return the string representation of this class
	 *
	 */
	@Override
	public String toString() {
		String output = "";
		if (isRead())
			output += "r";
		if (isWrite())
			output += "w";
		if (isModify())
			output += "m";
		return output;
	}
}