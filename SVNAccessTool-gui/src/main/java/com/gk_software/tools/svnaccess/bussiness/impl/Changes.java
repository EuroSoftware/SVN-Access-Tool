package com.gk_software.tools.svnaccess.bussiness.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.bussiness.impl.ActionEnum.Actions;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.Rights;

/**
 * Contains all the changes made by the user for the selected directory.
 */
public class Changes {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(Changes.class);

	/** The map with user as key and change as value. */
	private Map<String, Change> userChanges;

	/** The map with group as key and change as value. */
	private Map<String, Change> groupChanges;

	/** The path to the node in the svn. */
	private List<Node> nodePath;

	/** The indicator whether the inheritance is allowed. */
	private boolean inheritance = false;

	/**
	 * Returns the indicator whether the inheritance is allowed.
	 *
	 * @return the indicator whether the inheritance is allowed
	 *
	 */
	public boolean isInheritance() {
		return inheritance;
	}

	/**
	 * Reverses the indicator whether the inheritance is allowed.
	 */
	public void reverseInheritance() {
		this.inheritance = !inheritance;
	}

	/**
	 * Creates a new instance and initializes the variables.
	 */
	public Changes() {
		this.userChanges = new HashMap<String, Change>();
		this.groupChanges = new HashMap<String, Change>();
		this.nodePath = new ArrayList<Node>();
	}

	/**
	 * Returns the path to the node in the svn.
	 *
	 * @return the path to the node in the svn
	 *
	 */
	public List<Node> getNodePath() {
		return nodePath;
	}

	/**
	 * Sets the new path to the node in the svn.
	 *
	 * @param nodePath the new path
	 *
	 */
	public void setNodePath(List<Node> nodePath) {
		this.nodePath = new ArrayList<Node>(nodePath.size());
		for (Node n : nodePath)
			this.nodePath.add(n);
	}

	/**
	 * Adds the user as added into changes.
	 *
	 * @param user the added user
	 * @param rights the rights of the added user
	 *
	 */
	public void addUser(String user, String rights) {
		logger.info("user=" + user + " rights=" + rights);
		this.userChanges.put(user, new Change(Actions.ADDED, rights));
	}

	/**
	 * Adds the group as added into changes.
	 *
	 * @param group the added group
	 * @param rights the rights of the added group
	 *
	 */
	public void addGroup(String group, String rights) {
		logger.info("group=" + group + " rights=" + rights);
		this.groupChanges.put(group, new Change(Actions.ADDED, rights));
	}

	/**
	 * Adds the user as removed into changes. If the user was added in this "session"
	 * it will be deleted from changes.
	 *
	 * @param user the removed user
	 * @param rights the rights of the removed user
	 *
	 */
	public void removeUser(String user, String rights) {
		logger.info("user=" + user + " rights=" + rights);
		// when user is added and then removed it will be deleted from changes
		if (this.userChanges.containsKey(user)) {
			logger.info("user is in changes");
			if (this.userChanges.get(user).getAction() == Actions.ADDED) {
				logger.info("user was added => remove from changes");
				this.userChanges.remove(user);
			} else {
				logger.info("user was added to changes as REMOVED");
				this.userChanges.get(user).setAction(Actions.REMOVED);
			}
		} else {
			logger.info("user was added to changes as REMOVED");
			this.userChanges.put(user, new Change(Actions.REMOVED, rights));
		}
	}

	/**
	 * Adds the group as removed into changes. If the group was added in this "session"
	 * it will be deleted from changes.
	 *
	 * @param group the removed group
	 * @param rights the rights of the removed group
	 *
	 */
	public void removeGroup(String group, String rights) {
		logger.info("group=" + group + " rights=" + rights);
		// when group is added and then removed it will be deleted from changes
		if (this.groupChanges.containsKey(group)) {
			logger.info("group is in changes");
			if (this.groupChanges.get(group).getAction() == Actions.ADDED) {
				logger.info("group was added => remove from changes");
				this.groupChanges.remove(group);
			} else {
				logger.info("group was added to changes as REMOVED");
				this.groupChanges.get(group).setAction(Actions.REMOVED);
			}
		} else {
			logger.info("group was added to changes as REMOVED");
			this.groupChanges.put(group, new Change(Actions.REMOVED, rights));
		}
	}

	/**
	 * Adds the group as updated (changed) into changes.
	 *
	 * @param group the updated group
	 * @param rights the change of rights. E.g.: if group had rights r-- and now
	 *        has rw- the rights will be -w-.
	 *
	 */
	public void updateGroup(String group, String rights) {
		logger.info("group=" + group + " rights=" + rights);
		if (this.groupChanges.containsKey(group)) {
			logger.info("group is in changes");
			if (this.groupChanges.get(group).getAction() == Actions.ADDED) {
				logger.info("group was added => update changes");
				Change change = groupChanges.get(group);
				Rights oldRights = change.getRights();
				Rights changingRights = new Rights(rights);

				// every right means change so it will negate the current right
				if (changingRights.isRead()) {
					oldRights.setRead(!oldRights.isRead());
				}
				if (changingRights.isWrite()) {
					oldRights.setWrite(!oldRights.isWrite());
				}
				if (changingRights.isModify()) {
					oldRights.setModify(!oldRights.isModify());
				}

				// if (!oldRights.isRead() && !oldRights.isWrite()
				// && !oldRights.isModify()) {
				// groupChanges.remove(group);
				// }

			} else if (this.groupChanges.get(group).getAction() == Actions.REMOVED) {
				logger.info("group was removed => group will be now UPDATED");
				Change change = groupChanges.get(group);
				Rights oldRights = change.getRights();
				Rights changingRights = new Rights(rights);

				// every right means change so it will negate current right
				if (changingRights.isRead()) {
					oldRights.setRead(!oldRights.isRead());
				}
				if (changingRights.isWrite()) {
					oldRights.setWrite(!oldRights.isWrite());
				}
				if (changingRights.isModify()) {
					oldRights.setModify(!oldRights.isModify());
				}
				change.setAction(Actions.UPDATED);
			} else if (this.groupChanges.get(group).getAction() == Actions.UPDATED) {
				logger.info("group was updated => update changes");
				Change change = groupChanges.get(group);

				// every right means change so it will negate current right
				if (rights.contains("r")) {
					if (change.getRights().isRead())
						change.getRights().setRead(false);
					else
						change.getRights().setRead(true);

				} else if (rights.contains("w")) {

					if (change.getRights().isWrite())
						change.getRights().setWrite(false);
					else
						change.getRights().setWrite(true);

				} else if (rights.contains("m")) {

					if (change.getRights().isModify())
						change.getRights().setModify(false);
					else
						change.getRights().setModify(true);
				}

				// if (!change.getRights().isRead()
				// && !change.getRights().isWrite()
				// && !change.getRights().isModify()) {
				// groupChanges.remove(group);
				// }
			}
		} else {
			logger.info("group was added to changes as UPDATED");
			groupChanges.put(group, new Change(Actions.UPDATED, rights));
		}
	}

	/**
	 * Adds the user as updated (changed) into changes.
	 *
	 * @param user the updated user
	 * @param rights the change of rights. E.g.: if user had rights r-- and now
	 *        has rw- the rights will be -w-.
	 *
	 */
	public void updateUser(String user, String rights) {
		logger.info("group=" + user + " rights=" + rights);
		if (this.userChanges.containsKey(user)) {
			logger.info("user is in changes");
			if (this.userChanges.get(user).getAction() == Actions.ADDED) {
				logger.info("user was added => update changes");
				Change change = userChanges.get(user);
				Rights oldRights = change.getRights();
				Rights changingRights = new Rights(rights);

				// every right means change so it will negate current right
				if (changingRights.isRead()) {
					oldRights.setRead(!oldRights.isRead());
				}
				if (changingRights.isWrite()) {
					oldRights.setWrite(!oldRights.isWrite());
				}
				if (changingRights.isModify()) {
					oldRights.setModify(!oldRights.isModify());
				}

				// if (!oldRights.isRead() && !oldRights.isWrite()
				// && !oldRights.isModify()) {
				// userChanges.remove(user);
				// }

			} else if (this.userChanges.get(user).getAction() == Actions.REMOVED) {
				logger.info("user was removed => group will be now UPDATED");
				Change change = userChanges.get(user);
				Rights oldRights = change.getRights();
				Rights changingRights = new Rights(rights);

				// every right means change so it will negate current right
				if (changingRights.isRead()) {
					oldRights.setRead(!oldRights.isRead());
				}
				if (changingRights.isWrite()) {
					oldRights.setWrite(!oldRights.isWrite());
				}
				if (changingRights.isModify()) {
					oldRights.setModify(!oldRights.isModify());
				}
				change.setAction(Actions.UPDATED);
			} else if (this.userChanges.get(user).getAction() == Actions.UPDATED) {
				logger.info("user was updated => update changes");
				Change change = userChanges.get(user);

				// every right means change so it will negate current right
				if (rights.contains("r")) {
					if (change.getRights().isRead())
						change.getRights().setRead(false);
					else
						change.getRights().setRead(true);

				} else if (rights.contains("w")) {

					if (change.getRights().isWrite())
						change.getRights().setWrite(false);
					else
						change.getRights().setWrite(true);

				} else if (rights.contains("m")) {

					if (change.getRights().isModify())
						change.getRights().setModify(false);
					else
						change.getRights().setModify(true);
				}
				//
				// if (!change.getRights().isRead()
				// && !change.getRights().isWrite()
				// && !change.getRights().isModify()) {
				// userChanges.remove(user);
				// }
			}
		} else {
			logger.info("user was added to changes as UPDATED");
			userChanges.put(user, new Change(Actions.UPDATED, rights));
		}
	}

	/**
	 * Returns the change of the given user.
	 *
	 * @param user the user whose change is searched
	 * @return the change of the given user
	 *
	 */
	public Change getUser(String user) {
		return userChanges.get(user);
	}

	/**
	 * Returns the change of the given group.
	 *
	 * @param user the group whose change is searched
	 * @return the change of the given group
	 *
	 */
	public Change getGroup(String group) {
		return groupChanges.get(group);
	}

	/**
	 * Returns the deep copy of the changes for all users.
	 *
	 * @return the map of changes with user name as a key and change as value.
	 */
	public Map<String, Change> getUserChangesDeepCopy() {
		logger.info("start");
		Map<String, Change> userChangesCopy = new HashMap<String, Change>();
		for (String s : userChanges.keySet()) {
			userChangesCopy.put(s, userChanges.get(s));
		}
		return userChangesCopy;
	}

	/**
	 * Returns the deep copy of the changes for all groups.
	 *
	 * @return the map of changes with group name as a key and change as value.
	 */
	public Map<String, Change> getGroupsChangesDeepCopy() {
		logger.info("start");
		Map<String, Change> groupChangesCopy = new HashMap<String, Change>();
		for (String s : groupChanges.keySet()) {
			groupChangesCopy.put(s, groupChanges.get(s));
		}
		return groupChangesCopy;
	}

	/**
	 * Returns the change of the given user.
	 *
	 * @param user the group whose change is searched
	 * @return the change of the given group
	 *
	 */
	public Change getUserChange(String user) {
		logger.info("start");
		return userChanges.get(user);
	}

	/**
	 * Returns the map of all the user changes.
	 *
	 * @return the map of all the user changes
	 *
	 */
	public Map<String, Change> getUserChanges() {
		return userChanges;
	}

	/**
	 * Returns the map of all the group changes.
	 *
	 * @return the map of all the group changes
	 *
	 */
	public Map<String, Change> getGroupChanges() {
		return groupChanges;
	}

	/**
	 * Returns the change of the given group.
	 *
	 * @param user the group whose change is searched
	 * @return the change of the given group
	 *
	 */
	public Change getGroupChange(String group) {
		logger.info("start");
		return groupChanges.get(group);
	}

	/**
	 * Checks if there were already some changes made.
	 *
	 * @return true if there were already some changes made otherwise false
	 *
	 */
	public boolean isChange() {
//		Iterator it = userChanges.keySet().iterator();
//		while (it.hasNext()) {
//			String key = (String) it.next();
//			Change ch = userChanges.get(key);
//			if (ch.isEmpty())
//				it.remove();
//		}
//		Iterator it2 = groupChanges.keySet().iterator();
//
//		while (it2.hasNext()) {
//			String key = (String) it2.next();
//			Change ch = groupChanges.get(key);
//			if (ch.isEmpty())
//				it2.remove();
//		}
//
		if (isInheritance())
			return true;
		if (!userChanges.isEmpty())
			return true;
		if (!groupChanges.isEmpty())
			return true;
		return false;
	}

	// public void printInfo() {
	// System.out.println(userChanges.keySet());
	// System.out.println(userChanges.values());
	// System.out.println(groupChanges.keySet());
	// System.out.println(groupChanges.values());
	// }
}