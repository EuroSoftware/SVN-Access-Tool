package com.gk_software.tools.svnaccess.data.mail;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents the mail settings.
 */
@XmlRootElement
public class MailSettings {
	
	/** The user to whom the mail belongs. */
	private User user;

	/**
	 * Creates a new instance and initializes the variable {@code user} according
	 * to the given value.
	 * 
	 * @param user the user to whom the mail belongs
	 * 
	 */
	public MailSettings(User user) {
		super();
		this.user = user;
	}

	/**
	 * Returns the user to whom the mail belongs.
	 *
	 * @return the user to whom the mail belongs
	 *
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the new user to whom the mail belongs.
	 *
	 * @param user the new user to whom the mail belongs
	 *
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Creates a new instance.
	 */
	public MailSettings() {
	}
}
