package com.gk_software.tools.svnaccess.data.mail;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represent a user who logs into the app.
 */
@XmlType
public class User {
	
	/** The name of the user. */
	private String name;
	
	/** The required indicator. */
	private Required required = new Required();

	/**
	 * Returns the name of the user.
	 *
	 * @return the name of the user
	 *
	 */
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	/**
	 * Sets the new name of the user.
	 *
	 * @param name the new name of the user
	 *
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Creates a new instance.
	 */
	public User() {
	}
	
	/**
	 * Returns this instance.
	 *
	 * @return this instance
	 *
	 */
	public User getUser(){
		return this;
	}

	/**
	 * Returns the required indicator.
	 *
	 * @return the required indicator
	 *
	 */
	public Required getRequired() {
		return required;
	}

	/**
	 * Sets the new required indicator.
	 *
	 * @param notRequired the new required indicator
	 *
	 */
	public void setRequired(Required notRequired) {
		this.required = notRequired;
	}

}