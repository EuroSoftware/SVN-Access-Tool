package com.gk_software.tools.svnaccess.data.mail;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlType;

/**
 * Represents the repos and groups that are ignored.
 */
@XmlType
public class Required {

	/** The list of ignored repos. */
	private List<String> repos = new ArrayList<String>();;

	/**
	 * Returns the list of ignored repos.
	 *
	 * @return the list of ignored repos
	 *
	 */
	public List<String> getRepo() {
		return repos;
	}

	/**
	 * Sets the new list of ignored repos.
	 *
	 * @param repo the new list of ignored repos
	 *
	 */
	public void setRepo(List<String> repo) {
		this.repos = repo;
	}

	/** The list of ignored groups. */
	private List<String> groups = new ArrayList<String>();

	/**
	 * Returns the list of ignored groups.
	 *
	 * @return the list of ignored groups
	 *
	 */
	public List<String> getGroup() {
		return groups;
	}

	/**
	 * Sets the new list of ignored groups.
	 *
	 * @param groups the new list of ignored groups
	 *
	 */
	public void setGroup(List<String> groups) {
		this.groups = groups;
	}

	/**
	 * Creates a new instance and initialize the {@code repos} variable according to the
	 * given value.
	 *
	 * @param repo the list of ignored repos
	 *
	 */
	public Required(List<String> repo) {
		super();
		this.repos = repo;
	}

	/**
	 * Creates a new instance.
	 */
	public Required() {
	}

}
