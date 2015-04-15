package com.gk_software.tools.svnaccess.view.components.GroupsTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group.
 */
public class GroupBean {

	/** The name of the group. */
	private String name;

	/** The list of the group's children. */
	private List<GroupBean> children;

	/** The parent of the group. */
	private GroupBean parent;

	/** The indicator if the group is selected. */
	private boolean selected;

	/**
	 * Creates a new instance, sets the {@code name} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param name the name of the group
	 *
	 */
	public GroupBean(String name) {
		this.name = name;
		this.children = new ArrayList<GroupBean>();
	}

	/**
	 * Returns the indicator if the group is selected.
	 *
	 * @return the indicator if the group is selected
	 *
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets the new indicator if the group is selected.
	 *
	 * @param selected the new indicator if the group is selected
	 *
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Returns the parent of the group.
	 *
	 * @return the parent of the group
	 *
	 */
	public GroupBean getParent() {
		return parent;
	}

	/**
	 * Returns the list of the group's children.
	 *
	 * @return the list of the group's children
	 *
	 */
	public List<GroupBean> getChildren() {
		return children;
	}

	/**
	 * Sets the new list of the group's children.
	 *
	 * @param children the new list of the group's children
	 *
	 */
	public void setChildren(List<GroupBean> children) {
		this.children = children;
	}

	/**
	 * Returns the name of the cookie.
	 *
	 * @return the name of the cookie
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the new name of the cookie.
	 *
	 * @param cookieName the new name of the cookie
	 *
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the string representation of this class.
	 *
	 * @return the string representation of this class
	 *
	 */
	@Override
	public String toString() {
		return "GroupBean [name=" + name + "]";
	}

	/**
	 * Sets the new parent of the group.
	 *
	 * @param parent the new parent of the group
	 *
	 */
	public void setParent(GroupBean node) {
		this.parent = node;

	}

	/**
	 * Adds the given child to the list of children.
	 *
	 * @param gg the added child
	 *
	 */
	public void addChild(GroupBean gg) {
		children.add(gg);

	}

	/**
	 * Checks if the list of children contains the given child.
	 *
	 * @param bean the searched child
	 *
	 * @return true if it does otherwise false
	 *
	 */
	public boolean containsChildren(GroupBean bean) {
		if(children==null)
			children = new ArrayList<GroupBean>();
		for (GroupBean ch : children) {
			if (ch.getName().equals(bean.getName()))
				return true;
		}
		return false;
	}

	/**
	 * Returns the full path to this group from the root group.
	 *
	 * @return the full path to this group from the root group
	 *
	 */
	public String getFullGroupPath() {
		String pathAll = name;

		GroupBean pom = parent;
		while (pom != null) {
			pathAll = pom.name + "/" + pathAll;
			pom = pom.parent;
		}
		return pathAll.replace("//", "/");
	}

	/**
	 * Returns the child with the same name as the given group.
	 *
	 * @param p the searched group
	 *
	 * @return the child with the same name as the given group
	 *
	 */
	public GroupBean getCh(GroupBean p) {
		for (GroupBean ch : children) {
			if (ch.getName().equals(p.getName()))
				return ch;
		}
		return null;
	}
}
