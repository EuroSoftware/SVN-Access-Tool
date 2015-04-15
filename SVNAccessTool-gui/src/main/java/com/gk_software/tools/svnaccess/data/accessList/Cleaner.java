package com.gk_software.tools.svnaccess.data.accessList;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.data.ISVNStructureReader;
import com.gk_software.tools.svnaccess.data.ldap.LdapProvider;

/**
 * Cleans the access tree of the nodes which do not exist in the SVN anymore.
 */
public class Cleaner {

	/** The instance of the {@code Logger} class. */
	private static final Logger logCleaner = Logger.getLogger("ACL_cleaner");

	/** The indicator whether to clean all. */
	private boolean clean = true;

	/** The reader of the SVN structure. */
	private ISVNStructureReader ssr;

	/**
	 * Creates a new instance and initializes the variable {@code ssr} according to the
	 * given value.
	 *
	 * @param ssr the reader of the SVN structure
	 *
	 */
	public Cleaner(ISVNStructureReader ssr) {
		this.ssr = ssr;
	}

	/**
	 * Adds the user as removed into changes. If the user was added in this "session"
	 * it will be deleted from changes.
	 *
	 * @param tree the access tree to be cleaned
	 * @param depth the depth to which the cleaning goes
	 * @param checkOnSvn indicator whether to check the SVN
	 *
	 */
	public void clean(AccessTree tree, int depth, boolean checkOnSvn) {
		logCleaner.info("Start of cleaning access list for svn: "
				+ tree.getSVN());
		boolean depthCheck = true;
		if (depth < 0)
			depthCheck = false;

		Stack<Node> stack = new Stack<Node>();
		stack.add(tree.getRoot());
		while (!stack.isEmpty()) {
			Node n = stack.pop();
			int depthNode = -1;
			if (n.getSvn() == null) {
				String[] path = n.getFullPath().substring(0).split("/");
				depthNode = path.length;
			} else {
				depthNode = 1;
			}

			if (!n.isLocked()) {
				if (depthCheck && depthNode > depth) {
					tree.removeNode(n.getFullPath());
					logCleaner
							.info("Directory: "
									+ n.getFullPath()
									+ " in SVN: "
									+ tree.getSVN()
									+ ", will be removed because of depth bigger than: "
									+ depth);
				} else {
					if (checkOnSvn) {
						// checks if the node exists
						if (ssr.isSvnExist(n.getSvn(), n.getFullPath())) {
							cleanDuplicities(n, tree.getSVN());
							stack.addAll(n.getChildren());
						} else {
							// nonExist
							tree.removeNode(n.getFullPath());
							logCleaner.info("Directory: " + n.getFullPath()
									+ " does not exist in SVN: "
									+ tree.getSVN() + ", will be removed");
						}
					} else {
						// nodes are not controlled
						cleanDuplicities(n, tree.getSVN());
						stack.addAll(n.getChildren());
					}
				}
			}
		}
	}

	/**
	 * Removes the duplicate nodes to the given node in the given SVN.
	 *
	 * @param n the node whose duplicates are cleaned
	 * @param svn the SVN to which the node belongs to
	 *
	 */
	public void cleanDuplicities(Node n, String svn) {
		Map<String, Rights> users = n.getUsers();
		Map<String, Rights> groups = n.getGroups();

		int removedGroupItems = cleanNonExistingGroups(groups);
		int removedUserItems = cleanNonWorkingUsers(users);

		int removeDuplicitiesGroupGroup = cleanDuplicitiesGroupGroup(n, groups,
				svn);
		boolean wh = false;
		while (removeDuplicitiesGroupGroup != 0) {
			// System.out.println(svn + " " + n.getFullPath());
			removeDuplicitiesGroupGroup = cleanDuplicitiesGroupGroup(n, groups,
					svn);
			wh = true;
		}

		int removeDuplicitiesUserGroup = cleanDuplicitiesUserGroup(n, users,
				groups, svn);

		if (removedUserItems != 0 || removedGroupItems != 0
				|| removeDuplicitiesUserGroup != 0
				|| removeDuplicitiesGroupGroup != 0 || wh)
			logCleaner.info("Directory " + n.getFullPath() + " in SVN: " + svn
					+ " was cleaned");

		// for (String s : userToRemove)
		// System.out.println("remove user " + s);
	}

	/**
	 * Removes the duplicate groups for the given node in the given SVN.
	 *
	 * @param n the node whose duplicate groups are cleaned
	 * @param groups the map with group name as key and rights as value
	 * @param svn the SVN to which the node belongs to
	 *
	 * @return number of the groups remaining to be removed
	 *
	 */
	private int cleanDuplicitiesGroupGroup(Node n, Map<String, Rights> groups,
			String svn) {
		ArrayList<String> groupsToRemove = new ArrayList<String>();

		for (String group : groups.keySet()) {
			String rightsForGroup = groups.get(group).toString();
			if (group.equals("*")) {
				continue;
			}
			group = group.substring(1);
			Map<String, Rights> allParentGroups = LdapProvider.getInstance()
					.getParentsGroupsForGroupFiltered(group, groups);

			if (allParentGroups.size() != 0) {
				for (String g : allParentGroups.keySet()) {
					String groupRights = groups.get("@" + g).toString();
					if (rightsForGroup.equals(groupRights)) {
						groupsToRemove.add("@" + group);
						logCleaner.info(" removing group: " + group
								+ ", duplicite with: " + g + ", rights: "
								+ groupRights + " in svn: " + svn + ", path: "
								+ n.getFullPath());
						// System.out.println(" removing group: " + group
						// + ", duplicite with: " + g + " in svn: " + svn
						// + ", path: " + n.getFullPath());

					}
				}
			} else {
				// checks rights when inheritance is disabled due to no parent group
				if (groups.containsKey("*")) {
					if (rightsForGroup.equals(groups.get("*").toString())) {
						groupsToRemove.add("@" + group);
						logCleaner.info(" removing group: " + group
								+ ", duplicite with: *, rights: "
								+ rightsForGroup + " in svn: " + svn
								+ ", path: " + n.getFullPath());
						// System.out.println(" removing group: " + group
						// + ", duplicite with: *, rights: "
						// + rightsForGroup + " in svn: " + svn
						// + ", path: " + n.getFullPath());
					}
				}
				if (n.isInheritanceAbsolute()) {
					// System.out
					// .println(" inheritance set in " + n.getFullPath());
				} else {
					checkParentGroup(n, group, rightsForGroup, groupsToRemove,
							svn);
				}
			}

			// System.out.println(group + " all parents group "
			// + allParentGroups.keySet());
		}

		if (clean)
			for (String group : groupsToRemove) {
				groups.remove(group);
			}

		return groupsToRemove.size();
	}

	/**
	 * Checks if the given group is defined in its parent group and if it is deletes it.
	 *
	 * @param n the node whose groups are cleaned
	 * @param group the group whose parent group is checked
	 * @param groupRightsChild the rights of the child group
	 * @param groupsToRemove the list of the groups to be removed
	 * @param svn the SVN to which the node belongs to
	 *
	 */
	private void checkParentGroup(Node node, String group,
			String groupRightsChild, ArrayList<String> groupsToRemove,
			String svn) {
		boolean inh = false;
		Node parent = node.getParent();
		if (parent == null) {
			// System.out.println(" null parent - root");

		} else {
			while (parent != null && !inh) {

				if (parent.isInheritanceAbsolute()) {
					inh = true;
				}

				// System.out.println(" checking rights for parent: "
				// + parent.getFullPath());
				if (!parent.getGroups().containsKey("@" + group)) {

					Map<String, Rights> allParentGroups = LdapProvider
							.getInstance().getParentsGroupsForGroupFiltered(
									group, parent.getGroups());

					for (String g : allParentGroups.keySet()) {
						String groupRights = parent.getGroups().get("@" + g)
								.toString();
						if (groupRightsChild.equals(groupRights)) {
							groupsToRemove.add("@" + group);
							logCleaner.info(" removing group: " + group
									+ ", duplicite with: " + g + ", rights: "
									+ groupRights + " in parent svn: " + svn
									+ ", path: " + parent.getFullPath());
							// System.out.println(" removing group: " + group
							// + ", duplicite with: " + g + ", rights: "
							// + groupRights + " in parent svn: " + svn
							// + ", path: " + parent.getFullPath());
						}
					}

					if (allParentGroups.size() != 0)
						return;
					else {
						if (parent.getGroups().containsKey("*")) {
							if (groupRightsChild.equals(parent.getGroups()
									.get("*").toString())) {
								groupsToRemove.add("@" + group);
								logCleaner.info(" removing group: " + group
										+ ", duplicite with: *, rights: "
										+ groupRightsChild + " in parent svn: "
										+ svn + ", path: "
										+ parent.getFullPath());
								// System.out.println(" removing group: " +
								// group
								// + ", duplicite with: *, rights: "
								// + groupRightsChild + " in parent svn: "
								// + svn + ", path: "
								// + parent.getFullPath());
							}
						}
					}

					parent = parent.getParent();

				} else {
					if (parent.getGroups().get("@" + group).toString()
							.equals(groupRightsChild)) {
						groupsToRemove.add("@" + group);
						logCleaner.info(" removing group: " + group
								+ ", duplicite with: " + group + ", rights: "
								+ groupRightsChild + " in parent svn: " + svn
								+ ", path: " + parent.getFullPath());
						// System.out.println(" removing group: " + group
						// + ", duplicite with: " + group + ", rights: "
						// + groupRightsChild + " in parent svn: " + svn
						// + ", path: " + parent.getFullPath());

					}
					return;
				}
			}

		}

	}

	/**
	 * Removes the users assigned to the given node which have the same rights as the
	 * group to which they belong which is also assigned to the given node.
	 *
	 * @param n the node whose duplicate users are cleaned
	 * @param users the map with username as key and rights as value
	 * @param groups the map with group name as key and rights as value
	 * @param svn the SVN to which the node belongs to
	 *
	 * @return number of the users remaining to be removed
	 *
	 */
	private int cleanDuplicitiesUserGroup(Node n, Map<String, Rights> users,
			Map<String, Rights> groups, String svn) {
		ArrayList<String> usersToRemove = new ArrayList<String>();

		for (String user : users.keySet()) {

			Set<String> allGroupsContains = LdapProvider.getInstance()
					.getGroupsForUserWithParentGroupsFiltered(user, groups);

			// keep only groups that corresponds to an user and directory
			allGroupsContains.retainAll(groups.keySet());
			// System.out.println(user + " " + allGroupsContains);

			for (String g : allGroupsContains) {
				if (users.get(user).toString().equals(groups.get(g).toString())) {
					usersToRemove.add(user);
					logCleaner.info(" removing: " + user + ", duplicite with: "
							+ g + ", rights: " + users.get(user).toString());
				}
			}

			// search the parent nodes if the group is not found in the current node
			if (allGroupsContains.size() == 0) {
				// If there is a * group not containing the user
				if (groups.containsKey("*")) {
					if (users.get(user).toString()
							.equals(groups.get("*").toString())) {
						usersToRemove.add(user);
						logCleaner.info(" removing: " + user
								+ ", duplicite with: *, rights: "
								+ users.get(user).toString());
					}
				}
				if (n.isInheritanceAbsolute()) {
					// System.out
					// .println(" inheritance set in " + n.getFullPath());
				} else {
					checkParentUser(n, user, users.get(user).toString(),
							usersToRemove, svn);
				}
			}

		}
		if (clean)
			for (String user : usersToRemove) {
				users.remove(user);
			}

		return usersToRemove.size();
	}

	/**
	 * Checks if the given user is defined in its parent user and if it is deletes it.
	 *
	 * @param n the node whose users are cleaned
	 * @param user the user whose parent user is checked
	 * @param userRights the rights of the child user
	 * @param usersToRemove the list of the users to be removed
	 * @param svn the SVN to which the node belongs to
	 *
	 */
	private void checkParentUser(Node node, String user, String userRights,
			ArrayList<String> usersToRemove, String svn) {
		boolean inh = false;
		Node parent = node.getParent();
		if (parent == null) {
			// System.out.println(" null parent - root");

		} else {
			while (parent != null && !inh) {

				if (parent.isInheritanceAbsolute()) {
					// System.out.println(" inheritance set in parent: "
					// + parent.getFullPath());
					inh = true;
				}

				// System.out.println(" checking rights for parent: "
				// + parent.getFullPath());
				if (!parent.getUsers().containsKey(user)) {

					Set<String> allGroupsContains = LdapProvider.getInstance()
							.getGroupsForUserWithParentGroupsFiltered(user,
									parent.getGroups());
					allGroupsContains.retainAll(parent.getGroups().keySet());
					for (String g : allGroupsContains) {
						if (userRights.equals(parent.getGroups().get(g)
								.toString())) {
							usersToRemove.add(user);
							logCleaner.info(" removing: " + user
									+ ", duplicite with: " + g + ", rights: "
									+ userRights + " in svn: " + svn
									+ ", path: " + parent.getFullPath());
						}
					}
					if (allGroupsContains.size() != 0)
						return;
					else {
						if (parent.getGroups().containsKey("*")) {
							if (userRights.equals(parent.getGroups().get("*")
									.toString())) {
								usersToRemove.add(user);
								logCleaner.info(" removing: " + user
										+ ", duplicite with: *, rights: "
										+ userRights);
							}
						}
					}

					parent = parent.getParent();

				} else {
					if (parent.getUsers().get(user).toString()
							.equals(userRights)) {
						usersToRemove.add(user);
						logCleaner.info(" removing: " + user
								+ ", duplicite with: " + user + ", rights: "
								+ userRights + " in parent svn: " + svn
								+ ", path: " + parent.getFullPath());
					}
					return;
				}
			}

		}
	}

	/**
	 * Removes the users from the given map which do not work in the company anymore.
	 *
	 * @param users the map with username as key and rights as value
	 *
	 * @return number of the users remaining to be removed
	 *
	 */
	private int cleanNonWorkingUsers(Map<String, Rights> users) {
		ArrayList<String> usersToRemove = new ArrayList<String>();

		for (String user : users.keySet()) {
			if (!LdapProvider.getInstance().getUsers().contains(user)) {
				usersToRemove.add(user);
				logCleaner
						.info(" user: "
								+ user
								+ " does not work in company anymore, will be removed.");
			}
		}
		if (clean)
			for (String user : usersToRemove) {
				users.remove(user);

			}
		return usersToRemove.size();
	}

	/**
	 * Removes the groups from the given map which do not exist anymore.
	 *
	 * @param groups the map with group as key and rights as value
	 *
	 * @return number of the users remaining to be removed
	 *
	 */
	private int cleanNonExistingGroups(Map<String, Rights> groups) {
		ArrayList<String> groupToRemove = new ArrayList<String>();

		for (String group : groups.keySet()) {
			if (!group.equals("*")) {
				group = group.substring(1);
				if (!LdapProvider.getInstance().getGroups().contains(group)) {
					groupToRemove.add("@" + group);
					logCleaner.info(" group: " + group
							+ " does not exists anymore, will be removed");
				}
			}
		}
		if (clean)
			for (String group : groupToRemove) {
				groups.remove(group);
			}
		return groupToRemove.size();
	}

}