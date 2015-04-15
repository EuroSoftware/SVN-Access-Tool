package com.gk_software.tools.svnaccess.data.accessList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gk_software.tools.svnaccess.bussiness.impl.Change;
import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Represents a node with rights for users/groups from the access list.
 */
public class Node {

	/** The name of the user who locked the node. */
	private String personLocked;

	/** The indicator whether the node is locked. */
	private boolean locked;

	/** The list of all the changes in the locked node. */
	private Changes changes;

	/** The indicator whether the node was already checked on SVN. The node is checked
	 *  on the SVN when the user expands the parent node. This operation saves a lot of time
	 *  while building the access tree.
	 */
	private boolean checkOnSVN;

	/** The indicator whether the node has been deleted. */
	private boolean deleted;

	/**	The name of the node. */
	private String name;

	/** The name of the SVN the node belongs to. */
	private String svn;

	/** The map of the rights of the groups assigned to this node. */
	private Map<String, Rights> groups;

	/** The map of the rights of the users assigned to this node. */
	private Map<String, Rights> users;

	/** The list of the children of this node. */
	private List<Node> children;

	/** The parent node. */
	private Node parent;

	/** The indicator if the node has disabled inheritance. */
	private boolean inheritance;

	/** The timestamp of the node creation/change. */
	private long timestamp;

	/** The name of the responsible person. */
	private String responsiblePerson;

	/**
	 * Creates a new instance and initializes the variables according to the given values.
	 *
	 * @param name the name of the node
	 * @param parent the parent node
	 *
	 */
	public Node(String name, Node parent) {
		this.name = name;
		this.parent = parent;
		this.children = new ArrayList<Node>();
		this.groups = new HashMap<String, Rights>();
		this.users = new HashMap<String, Rights>();
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Returns the indicator whether the node was already checked on SVN.
	 *
	 * @return the indicator whether the node was already checked on SVN
	 *
	 */
	public boolean isCheckOnSVN() {
		return checkOnSVN;
	}

	/**
	 * Sets the new indicator whether the node was already checked on SVN.
	 *
	 * @param checkOnSVN the new indicator whether the node was already checked on SVN
	 *
	 */
	public void setCheckOnSVN(boolean checkOnSVN) {
		this.checkOnSVN = checkOnSVN;
	}

	/**
	 * Applies the made changes on the corresponding objects.
	 */
	public void applyChangesLock() {
		setTimestamp(System.currentTimeMillis());

		if (changes.isInheritance()) {
			this.reverseInheritance();
		}

		for (String group : changes.getGroupChanges().keySet()) {
			Change groupCh = changes.getGroup(group);
			switch (groupCh.getAction()) {
			case ADDED:
				addGroup(group, groupCh.getRights().toString());
				break;
			case UPDATED:
				updateGroup(group, groupCh.getRights());
				break;
			case REMOVED:
				break;
			default:
				break;
			}
		}
		for (String user : changes.getUserChanges().keySet()) {
			Change userCh = changes.getUser(user);
			switch (userCh.getAction()) {
			case ADDED:
				addUser(user, userCh.getRights().toString());
				break;
			case UPDATED:
				updateUser(user, userCh.getRights());
				break;
			case REMOVED:
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Removes the made changes from the corresponding objects and unlocks the node.
	 */
	public void removeChangesUnlock() {
		setTimestamp(System.currentTimeMillis());
		if (changes != null) {
			if (changes.isInheritance()) {
				this.reverseInheritance();
			}

			for (String group : changes.getGroupChanges().keySet()) {
				Change groupCh = changes.getGroup(group);
				switch (groupCh.getAction()) {
				case ADDED:
					removeGroup(group);
					break;
				case UPDATED:
					updateGroup(group, groupCh.getRights());
					break;
				case REMOVED:
					break;
				default:
					break;
				}
			}
			for (String user : changes.getUserChanges().keySet()) {
				Change userCh = changes.getUser(user);
				switch (userCh.getAction()) {
				case ADDED:
					removeUser(user);
					break;
				case UPDATED:
					updateUser(user, userCh.getRights());
					break;
				case REMOVED:
					break;
				default:
					break;
				}
			}
		}
		locked = false;
		personLocked = "";
		changes = null;
	}

	/**
	 * Reverses the state of the indicator if the node has disabled inheritance.
	 */
	public void reverseInheritance() {
		this.inheritance = !inheritance;
	}

	/**
	 * Returns the indicator if the node has disabled inheritance.
	 *
	 * @return the indicator if the node has disabled inheritance
	 *
	 */
	public boolean isInheritance() {
		return inheritance;
	}

	/**
	 * Checks if the directory has canceled inheritance or contains *= for canceling
	 * the inheritance.
	 *
	 * @return true if the directory has canceled inheritance otherwise false
	 */
	public boolean isInheritanceAbsolute() {
		if (groups.containsKey("*") || inheritance)
			return true;
		return false;
	}

	/**
	 * Checks if the node with the given name is a child of this node.
	 *
	 * @param name the name of the searched node
	 *
	 * @return true if the node is a child otherwise false
	 */
	public boolean isChildExist(String name) {
		for (Node s : children) {
			if (name.equals(s.getName()))
				return true;
		}
		return false;
	}

	/**
	 * Adds the given node to the list of children.
	 *
	 * @param child the added node
	 */
	public void addChild(Node child) {
		if (isLocked()) {
			child.setLocked(true);
			child.setPersonLocked(personLocked);
		}
		children.add(child);
	}

	/**
	 * Adds the given group rights to the groups map.
	 *
	 * @param groupName the name of the added group
	 * @param rights the rights of the added group
	 *
	 */
	public void addGroup(String groupName, String rights) {
		groups.put(groupName, new Rights(rights));
	}

	/**
	 * Adds the given user rights to the users map.
	 *
	 * @param userName the name of the added user
	 * @param rights the rights of the added user
	 *
	 */
	public void addUser(String userName, String rights) {
		users.put(userName, new Rights(rights));
	}

	/**
	 * Reverses the rights of the user with the given username according to the given
	 * rights.
	 *
	 * @param userName the name of the user whose rights are reversed
	 * @param rights the changed rights
	 *
	 */
	public void updateUser(String userName, Rights rights) {
		Rights user = users.get(userName);
		if (rights.isModify())
			user.setModify(!user.isModify());
		if (rights.isRead())
			user.setRead(!user.isRead());
		if (rights.isWrite())
			user.setWrite(!user.isWrite());
	}

	/**
	 * Reverses the rights of the group with the given name according to the given
	 * rights.
	 *
	 * @param groupName the name of the group whose rights are reversed
	 * @param rights the changed rights
	 *
	 */
	public void updateGroup(String groupName, Rights rights) {
		Rights group = groups.get(groupName);
		if (rights.isModify())
			group.setModify(!group.isModify());
		if (rights.isRead())
			group.setRead(!group.isRead());
		if (rights.isWrite())
			group.setWrite(!group.isWrite());
	}

	/**
	 * Removes the user with the given username from the users map.
	 *
	 * @param userName the name of the user to be removed
	 *
	 */
	public void removeUser(String userName) {
		users.remove(userName);
	}

	/**
	 * Removes the group with the given name from the users map.
	 *
	 * @param groupName the name of the group to be removed
	 *
	 */
	public void removeGroup(String groupName) {
		groups.remove(groupName);
	}

	/**
	 * Sets new rights to the given user.
	 *
	 * @param user the user whose rights are changed
	 * @param rights the new rights
	 *
	 */
	public void changeRightsUser(String user, String rights) {
		this.users.put(user, new Rights(rights));
	}

	/**
	 * Sets new rights to the given group.
	 *
	 * @param group the group whose rights are changed
	 * @param rights the new rights
	 *
	 */
	public void changeRightsGroup(String group, String rights) {
		this.groups.put(group, new Rights(rights));
	}

	/**
	 * Returns the path to the node.
	 *
	 * @return the path to the node
	 */
	public String getFullPath() {
		String pathAll = name;

		Node pom = parent;
		while (pom != null) {
			pathAll = pom.name + "/" + pathAll;
			pom = pom.parent;
		}
		return pathAll.replace("//", "/");
	}

	/**
	 * Returns the R/W rights in standard format. M rights are ignored.
	 *
	 * @return the R/W rights in standard format or null if there are none
	 *
	 * @throws Exception rights retrieval failed
	 *
	 */
	public String saveNode() throws Exception {
		if (groups.size() == 0 && users.size() == 0 && !inheritance)
			return null;
		String rights = "";

		if (isInheritance()) {
			rights += "* = \n";
			for (String s : Arrays.asList(((String) (Constants.getProperties()
					.get("inheritance_free"))).split(";"))) {
				if (s.trim().length() != 0)
					if (!groups.containsKey(s)) {
						groups.put(s, new Rights(ViewInformation.getInstance()
								.getRightsInheritanceFree(s, "", "", this)));
					}
			}
		}
		List<String> ar = new ArrayList<String>(groups.keySet());
		Collections.sort(ar);

		for (String k : ar) {
			Rights a = groups.get(k);
			// if (a.isRead() || a.isWrite())
			rights += k + " = " + a.printACL() + "\n";
		}
		ar = new ArrayList<String>(users.keySet());
		Collections.sort(ar);

		for (String k : ar) {
			Rights a = users.get(k);
			// if (a.isRead() || a.isWrite())
			rights += k + " = " + a.printACL() + "\n";
		}
		if (rights.length() == 0)
			return null;

		return rights;
	}

	/**
	 * Returns the R rights in viewSVN format. M rights are ignored.
	 *
	 * @return the R rights in viewSVN format or null if there are none
	 *
	 */
	public String saveNodeViewSVN() {
		String rights = "";

		if (name.equals("/")) {
			rights += "* = \n";
		}
		if (groups.size() == 0 && users.size() == 0 && !inheritance) {
			if(rights.equals("")) {
				return null;
			} else {
				return rights;
			}
		}

		if (isInheritance() && rights.equals("")) {
			rights += "* = \n";
		}
		List<String> ar = new ArrayList<String>(groups.keySet());
		Collections.sort(ar);

		for (String k : ar) {
			Rights a = groups.get(k);
			// if (a.isRead() || a.isWrite())
			rights += k + " = " + a.printViewSvnACL() + "\n";
		}
		ar = new ArrayList<String>(users.keySet());
		Collections.sort(ar);

		for (String k : ar) {
			Rights a = users.get(k);
			// if (a.isRead() || a.isWrite())
			rights += k + " = " + a.printViewSvnACL() + "\n";
		}
		if (rights.length() == 0)
			return null;

		return rights;
	}

	/**
	 * Returns all the rights in standard format if the M right exists.
	 *
	 * @return all the rights in standard format or null if there is no M right
	 */
	public String saveModifyNode() {
		String rights = "";
		if (locked) {
			rights = saveLockChanges();
		}
		if (groups.size() == 0 && users.size() == 0)
			return null;

		List<String> ar = new ArrayList<String>(groups.keySet());
		Collections.sort(ar);

		for (String k : ar) {
			Rights a = groups.get(k);
			if (a.isModify())
				rights += k + " = " + a + "\n";
		}
		ar = new ArrayList<String>(users.keySet());
		Collections.sort(ar);

		for (String k : ar) {
			Rights a = users.get(k);
			if (a.isModify())
				rights += k + " = " + a + "\n";
		}

		if (rights.length() == 0)
			return null;
		return rights;
	}

	/**
	 * Returns the changes in rights along with the name of the person who made them.
	 *
	 * @return the changes in rights or null if there are none
	 *
	 */
	private String saveLockChanges() {
		String rights = "";
		if (changes == null
				|| (changes.getGroupChanges().size() == 0
						&& changes.getUserChanges().size() == 0 && !changes.isInheritance()))
			return "";

		rights += "# lockedBy = " + personLocked + "\n";
		if (changes.isInheritance()) {
			rights += "# inh\n";
		}
		List<String> ar = new ArrayList<String>(changes.getGroupChanges().keySet());
		Collections.sort(ar);

		for (String k : ar) {
			Change a = changes.getGroupChanges().get(k);
			// if (a.isRead() || a.isWrite())
			rights += "# " + k + "," + a.getRights() + "," + a.getAction() + "\n";
		}
		ar = new ArrayList<String>(changes.getUserChanges().keySet());
		Collections.sort(ar);

		for (String k : ar) {
			Change a = changes.getUserChanges().get(k);
			// if (a.isRead() || a.isWrite())
			rights += "# " + k + "," + a.getRights() + "," + a.getAction() + "\n";

		}
		return rights;

	}

	/**
	 * Returns the map of the rights of the groups assigned to this node.
	 *
	 * @return the map of the rights of the groups assigned to this node
	 *
	 */
	public Map<String, Rights> getGroups() {
		return groups;
	}

	/**
	 * Returns the timestamp of the node creation/change.
	 *
	 * @return the timestamp of the node creation/change
	 *
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the map of the rights of the users assigned to this node.
	 *
	 * @return the map of the rights of the users assigned to this node
	 *
	 */
	public Map<String, Rights> getUsers() {
		return users;
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
	 * Returns the list of the children of this node.
	 *
	 * @return the list of the children of this node
	 *
	 */
	public List<Node> getChildren() {
		return children;
	}

	/**
	 * Returns the name of the node.
	 *
	 * @return the name of the node
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the parent node.
	 *
	 * @return the parent node
	 *
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * Returns the list of all the changes in the locked node.
	 *
	 * @return the list of all the changes in the locked node
	 *
	 */
	public Changes getChanges() {
		return changes;
	}

	/**
	 * Sets the new map of the rights of the groups assigned to this node.
	 *
	 * @param groups the new map of the rights of the groups assigned to this node
	 *
	 */
	public void setGroups(Map<String, Rights> groups) {
		this.groups = groups;
	}

	/**
	 * Sets the new timestamp of the node creation/change.
	 *
	 * @param timestamp the new timestamp of the node creation/change
	 *
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Sets the new name of the SVN the node belongs to.
	 *
	 * @param inheritance the new name of the SVN the node belongs to
	 *
	 */
	public void setInheritance(boolean inheritance) {
		this.inheritance = inheritance;
	}

	/**
	 * Sets the new map of the rights of the users assigned to this node.
	 *
	 * @param users the map of the rights of the users assigned to this node
	 *
	 */
	public void setUsers(Map<String, Rights> users) {
		this.users = users;
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
	 * Sets the new name of the node.
	 *
	 * @param name the new name of the node
	 *
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the new parent node.
	 *
	 * @param parent the new parent node
	 *
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	/**
	 * Sets the new list of the children of this node.
	 *
	 * @param children the new list of the children of this node
	 *
	 */
	public void setChildren(List<Node> children) {
		this.children = children;
	}

	/**
	 * Sets the new list of all the changes in the locked node.
	 *
	 * @param changes the new list of all the changes in the locked node
	 *
	 */
	public void setChanges(Changes changes) {
		this.changes = changes;
	}

	/**
	 * Returns the string representation of this class.
	 *
	 * @return the string representation of this class
	 *
	 */
	@Override
	public String toString() {
		return this.name + " " + this.timestamp;
	}

	/**
	 * Returns the name of the responsible person.
	 *
	 * @return the name of the responsible person
	 *
	 */
	public String getResponsiblePerson() {
		return responsiblePerson;
	}

	/**
	 * Returns the string formed from "Responsible" and the name of the responsible
	 * person.
	 *
	 * @return the created string
	 *
	 */
	public String getRepositoryDescription() {
		if (responsiblePerson != null && responsiblePerson.length() != 0)
			return "Responsible: " + responsiblePerson;
		return "Responsible person isn't set.";
	}

	/**
	 * Sets the new name of the responsible person.
	 *
	 * @param responsiblePerson the new name of the responsible person
	 *
	 */
	public void setResponsiblePerson(String responsiblePerson) {
		this.responsiblePerson = responsiblePerson;
	}

	/**
	 * Returns the indicator whether the node is locked.
	 *
	 * @return the indicator whether the node is locked
	 *
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Sets the new indicator whether the node is locked.
	 *
	 * @param locked the new indicator whether the node is locked
	 *
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	/**
	 * Returns the name of the user who locked the node. It also checks if the parent
	 * is locked and if it is it returns an empty string
	 *
	 * @return the name of the user who locked the node or empty string
	 *
	 */
	public String getPersonLocked() {
		if (getParent() != null) {
			if (getParent().isLocked())
				return "";
			else
				return personLocked;
		}
		return personLocked;
	}

	/**
	 * Returns the name of the user who locked the node without checking the parent.
	 *
	 * @return the name of the user who locked the node
	 *
	 */
	public String getPersonLockedWithoutCheck() {
		return personLocked;
	}

	/**
	 * Sets the new name of the user who locked the node.
	 *
	 * @param personLocked the new name of the user who locked the node
	 *
	 */
	public void setPersonLocked(String personLocked) {
		this.personLocked = personLocked;
	}

	/**
	 * Removes the node on the given path from the list of children.
	 *
	 * @param fullPath the path to the node in the repository
	 *
	 */
	public void removeChildren(String fullPath) {
		Node toRemove = null;
		for (Node n : children) {
			if (n.getFullPath().equals(fullPath)) {
				toRemove = n;
			}
		}
		if (toRemove != null) {
			children.remove(toRemove);
		}
	}

	/**
	 * Sets the deleted state of the node on the given path to true.
	 *
	 * @param fullPath the path to the node in the repository
	 *
	 */
	public void markAsDeleted(String fullPath) {
		Node toRemove = null;
		for (Node n : children) {
			if (n.getFullPath().equals(fullPath)) {
				toRemove = n;
			}
		}
		if (toRemove != null) {
			int i = children.indexOf(toRemove);
			children.get(i).setDeleted(true);
		}
	}

	/**
	 * Checks if the deleted indicator is true. If not it also checks if there is not
	 * one true in the parents of this node up to the root.
	 *
	 * @return true if the indicator is true otherwise false
	 *
	 */
	public boolean isDeleted() {
		if (deleted)
			return true;
		else {
			Node pom = parent;
			while (pom != null) {
				if (pom.isDeleted())
					return true;
				pom = pom.parent;
			}
		}
		return false;
	}

	/**
	 * Sets the new indicator whether the node has been deleted.
	 *
	 * @param deleted the new indicator whether the node has been deleted
	 *
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}