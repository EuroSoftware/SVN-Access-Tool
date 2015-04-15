package com.gk_software.tools.svnaccess.data.accessList;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.bussiness.impl.User;
import com.gk_software.tools.svnaccess.data.ILdapProvider;
import com.gk_software.tools.svnaccess.data.ldap.LdapProvider;
import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Represents the SVN tree structure with rights read from the access.list file.
 * Every directory is represented by a Node instance and it is located in an
 * appropriate tree deep structure.
 */
public class AccessTree {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(AccessTree.class);

	/**
	 * The tree root. It contains only slash symbol.
	 */
	private Node root;

	/**
	 * The name of the selected SVN.
	 */
	private String SVN;

	/**
	 * Indicator whether the structure contains a locked subtree. It is used only in
	 * synchronized method "isLockedPathExists()".
	 */
	boolean locked = false;

	/**
	 * Creates a new instance and initializes the variable {@code root} according to the
	 * given parameter.
	 *
	 * @param SVN the name of the SVN to which will this tree belong
	 *
	 */
	public AccessTree(String SVN) {
		this.root = new Node("/", (Node) null);
		this.root.setSvn(SVN);
		this.SVN = SVN;
	}

	/**
	 * Returns the name of the SVN to which this access tree belongs.
	 *
	 * @return the name of the SVN to which this access tree belongs
	 *
	 */
	public String getSVN() {
		return SVN;
	}

	/**
	 * Adds a node to the given path in the tree.
	 *
	 * @param path the path where the node will be put
	 *
	 */
	public void addNode(String path) {
		String[] parts = path.split("/");
		Node pom = root;
		boolean found = false;
		for (int i = 1; i < parts.length; i++) {
			if (!pom.getChildren().isEmpty()) {

				for (Node a : pom.getChildren()) {
					if (a.getName().equals(parts[i])) {
						pom = a;
						found = true;
					}
				}
				if (!found) {
					Node newNode = new Node(parts[i], pom);
					pom.addChild(newNode);
					pom = newNode;
				}
			} else {
				Node newNode = new Node(parts[i], pom);
				pom.addChild(newNode);
				pom = newNode;
			}
			found = false;
		}

	}

	/**
	 * Checks whether the given path exists in the access tree.
	 *
	 * @param path the searched path
	 *
	 * @return true if the path exists otherwise false
	 *
	 */
	public boolean isPathExist(String path) {
		String[] parts = path.split("/");

		Node pom = root;
		boolean found = false;
		for (int i = 1; i < parts.length; i++) {
			found = false;
			for (Node a : pom.getChildren()) {
				if (a.getName().equals(parts[i]) && !a.isDeleted()) {
					pom = a;
					found = true;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	/**
	 * Returns the node found on the given path.
	 *
	 * @param path the path to the searched node
	 *
	 * @return the found node or the root node
	 *
	 */
	public Node getNode(String path) {
		String[] parts = path.split("/");
		Node pom = root;
		// boolean found = false;

		for (int i = 1; i < parts.length; i++) {
			for (Node a : pom.getChildren()) {
				if (a.getName().equals(parts[i])) {
					pom = a;
					// found = true;
				}
			}
		}
		return pom;
	}

	/**
	 * Returns the root node of the access tree.
	 *
	 * @return the root node of the access tree
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * Saves the access tree to the access.list file.
	 *
	 * @param bw the buffered file writer
	 * @param modify indicator whether to save the M rights
	 * @param viewSvnFormat indicator whether we save the tree in the viewSVN format
	 *
	 * @throws Exception saving failed
	 *
	 */
	public void print(BufferedWriter bw, boolean modify, boolean viewSvnFormat)
			throws Exception {
		if (viewSvnFormat)
			printViewSVN(root, bw);
		else
			printStandartSVN(root, bw, modify);
	}

	/**
	 * Saves the access tree in the standard format. It recursively passes all trees
	 * and saves every node that has user or group rights set.
	 *
	 * @param pom the saved node
	 * @param bw the buffered file writer
	 * @param modify indicator whether to save the M rights
	 *
	 * @throws Exception saving failed
	 *
	 */
	private void printStandartSVN(Node pom, BufferedWriter bw, boolean modify)
			throws Exception {
		String s = "";
		String res = "";
		if (pom.getResponsiblePerson() != null
				&& pom.getResponsiblePerson().length() != 0) {
			res = "### " + SVN + " Responsible: " + pom.getResponsiblePerson()
					+ "\n";
		}

		if (modify) {
			s = pom.saveModifyNode();
		} else {
			s = pom.saveNode();
		}
		if (s != null) {
			if (!modify) {
				bw.write(res);
			}
			bw.write("[" + SVN + ":" + pom.getFullPath() + "]\n");
			bw.write(s + "\n\n");
		} else {
			if (res.length() != 0 && !modify) {
				bw.write(res);
				bw.write("[" + SVN + ":" + pom.getFullPath() + "]\n\n\n");
			}
		}
		if (!pom.getChildren().isEmpty()) {
			for (Node a : pom.getChildren()) {
				printStandartSVN(a, bw, modify);
			}
		}
	}

	/**
	 * Saves the access tree in the viewSVN format. It recursively passes all trees
	 * and saves every node that has user or group rights set.
	 *
	 * @param pom the saved node
	 * @param bw the buffered file writer
	 *
	 * @throws Exception saving failed
	 *
	 */
	private void printViewSVN(Node pom, BufferedWriter bw) throws IOException {
		String s = "";

		s = pom.saveNodeViewSVN();

		if (s != null) {
			String fullP = pom.getFullPath();
			if (fullP.length() == 1)
				fullP = "";
			bw.write("[viewsvn:/" + SVN + "" + fullP + "]\n");
			bw.write(s + "\n\n");
		}
		if (!pom.getChildren().isEmpty()) {
			for (Node a : pom.getChildren()) {
				printViewSVN(a, bw);
			}
		}
	}// [viewsvn:/service/navision]

	// /**
	// * Recursively check from Node to root of a tree if logged user can change
	// * rights for a Node. If inheritance flag is set, the searching will be
	// * stopped.
	// *
	// * @param fullpath
	// * path we are checking
	// * @param groups
	// * list of groups where logged user is present
	// * @param userName
	// * username of logged user
	// * @return true if user can change rights for a node, otherwise false
	// */
	// public boolean canModifyRights(String fullpath, List<String> groups,
	// String userName) {
	//
	// Node lastNode = getNode(fullpath);
	// boolean canUserModify = false;
	// while (lastNode != null) {
	//
	// canUserModify = canModifyRightsNode(fullpath, groups, userName,
	// lastNode);
	// logger.info("Check if user: " + userName
	// + ", can modify rights for directory \"" + fullpath
	// + "\": result:" + canUserModify);
	//
	// // for right ineritance of modified rights
	// // if (lastNode.getUsers().containsKey(userName) && !canUserModify)
	// // {
	// // return false;
	// // } else {
	// // Map<String, Rights> groupsNode = lastNode.getGroups();
	// //
	// // for (String gr : groups) {
	// // if (groupsNode.containsKey(gr) && !canUserModify) {
	// // return false;
	// // }
	// // }
	// // }
	//
	// if (lastNode.isInheritance() || canUserModify)
	// lastNode = null;
	// else
	// lastNode = lastNode.getParent();
	// }
	//
	// return canUserModify;
	// }

	/**
	 * Checks if the logged user has modify rights for the node on the given path.
	 *
	 * @param fullpath the path to the node in the repository
	 * @param groups the list of the groups to which the user belongs to
	 * @param userName the name of the logged user
	 * @param lastNode the node we are currently checking
	 *
	 * @return true if the user has modify rights otherwise false
	 *
	 */
	private boolean canModifyRightsNode(String fullpath, List<String> groups,
			String userName, Node lastNode) {

		logger.info("Checking if user can modify rights. ");
		Map<String, Rights> usersNode = lastNode.getUsers();
		if (usersNode.containsKey(userName)) { // user exists
			Rights r = usersNode.get(userName);
			if (r.isModify()) // has M rights?
				return true;
		}
		logger.info("Checking if groups that contains user can modify rights. ");
		// has the group containing the user M rights?
		Map<String, Rights> groupsNode = lastNode.getGroups();

		for (String gr : groups) { // all groups containing the user
			if (groupsNode.containsKey(gr)) {
				Rights r = groupsNode.get(gr);
				// System.out.println(gr + " " + r.isModify());
				if (r.isModify())
					return true;
			}
		}

		return false;
	}

	/**
	 * Checks if the logged user has modify rights. It
	 * searches the whole tree.
	 *
	 * @param fullpath the path to the node in the repository
	 * @param groups the list of the groups to which the user belongs to
	 * @param userName the name of the logged user
	 *
	 * @return true if the user has modify rights for the given node otherwise false
	 *
	 */
	public boolean checkModifyRightsAllTree(String fullpath, List<String> groups,
			String userName) {

		Node lastNode = getNode(fullpath);
		boolean canUserModify = false;
		while (lastNode != null) {

			canUserModify = canModifyRightsNode(fullpath, groups, userName,
					lastNode);

			if (canUserModify)
				lastNode = null;
			else
				lastNode = lastNode.getParent();
		}

		return canUserModify;
	}

	/**
	 * Recursively checks from the given path to the root node if the logged user has W
	 * rights. If inheritance flag is set the searching is stopped.
	 *
	 * @param fullpath the path to the node in the repository
	 * @param groups the list of the groups to which the user belongs to
	 * @param userName the name of the logged user
	 *
	 * @return true if the user has write rights otherwise false
	 *
	 */
	public synchronized boolean hasWriteRights(String fullpath, String userName,
			ILdapProvider gur) {

		Node lastNode = getNode(fullpath);
		boolean hasWriteRights = false;
		while (lastNode != null) {
			found = false;
			hasWriteRights = hasWriteRightsNode(fullpath, userName, lastNode,
					gur);
			logger.info("Check if user: " + userName
					+ ", has write rights for directory \"" + fullpath
					+ "\": result:" + hasWriteRights);
			// stop when the inheritance is disabled or the rights are found
			if (lastNode.isInheritance() || hasWriteRights || found)
				lastNode = null;
			else
				lastNode = lastNode.getParent();
		}

		return hasWriteRights;
	}

	private boolean found;

	/**
	 * Checks if the logged user has W rights for the given path.
	 *
	 * @param fullpath the path to the node in the repository
	 * @param groups the list of the groups to which the user belongs to
	 * @param userName the name of the logged user
	 * @param lastNode the node we are currently checking
	 *
	 * @return true if the user has write rights for the given path otherwise false
	 *
	 */
	private boolean hasWriteRightsNode(String fullpath, String userName, Node lastNode,
			ILdapProvider gur) {
		logger.info("Checking if user has write rights. ");
		Map<String, Rights> usersNode = lastNode.getUsers();
		if (usersNode.containsKey(userName)) { // user exists
			Rights r = usersNode.get(userName);
			if (r.isWrite()) // has W rights?
				return true;
			else
				return false;
		}
		logger.info("Checking if groups that contains user has write rights. ");
		// has the group containing the user W rights?
		Map<String, Rights> groupsNode = lastNode.getGroups();

		List<String> groups = new ArrayList<String>(
				gur.getGroupsForUserWithParentGroupsFiltered(userName,
						groupsNode));

		boolean searchInGroup = false;
		for (String gr : groups) { // all groups containing the user
			if (groupsNode.containsKey(gr)) {
				found = true;
				Rights r = groupsNode.get(gr);
				if (r.isWrite())
					searchInGroup = true;
			}
		}
		if (found)
			return searchInGroup;

		return false;
	}

	/**
	 * Locks the node on the given path and all its children.
	 *
	 * @param fullpath the path to the node in the repository
	 * @param user the user who locked the node
	 * @param inheritedRights the rights inherited from the parent node
	 *
	 * @return true if locking was successful otherwise false
	 *
	 * @throws Exception locking failed
	 */
	public boolean lockNodes(String fullPath, User user,
			Map<String, Rights> inheritedRights) throws Exception {
		if (!isLockedPathExists(fullPath)) {
			int i = 0;
			Node lock = getNode(fullPath);

			setLocked(lock, user.getUserName().split("@")[0], user, i,
					inheritedRights);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Unlocks the node on the given path and all its children
	 *
	 * @param fullpath the path to the node in the repository
	 * @param groups the list of the groups to which the user belongs to
	 * @param user the user who locked the node
	 *
	 * @return true if unlocking was successful otherwise false
	 *
	 * @throws Exception unlocking failed
	 */
	public boolean unlockNodes(String fullPath, List<String> groups, User user) {
		Node unlock = getLockedRoot(fullPath);
		if (unlock.getPersonLocked().equals(user.getUserName().split("@")[0])
				|| checkModifyRightsAllTree(fullPath, groups,
						user.getUserName()) || user.isAdmin()) {
			try {
				setUnlocked(unlock, user);
			} catch (Exception e) {
				logger.error("error during unlocking");
				logger.error(e);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Saves the change object to the given node for later unlocking.
	 *
	 * @param lock the node we are locking
	 * @param groups the list of the groups to which the user belongs to
	 * @param userName the user who locked the node
	 * @param i the position of the node
	 * @param inheritedRights the rights inherited from the parent node
	 *
	 * @throws Exception getting failed
	 *
	 */
	private void getLockChange(Node lock, User user, int i,
			Map<String, Rights> inheritedRights) throws Exception {
		String userName = user.getUserName();
		Changes ch = new Changes();
		if (i == 0 && !lock.isInheritance()) {
			ch.reverseInheritance();

			for (String s : Arrays.asList(((String) (Constants.getProperties()
					.get("inheritance_free"))).split(";"))) {
				if (s.trim().length() != 0)
					if (!lock.getGroups().containsKey(s)) {
						Rights righ = new Rights(ViewInformation.getInstance()
								.getRightsInheritanceFree(s, "", "", lock));
						lock.getGroups().put(s, righ);
						ch.addGroup(s, righ.toString());
					}
			}

		}
		Rights forNewUserGroup = new Rights("");

		boolean userAdded = false;
		List<String> groups = new ArrayList<String>(LdapProvider.getInstance()
				.getGroupsForUserWithParentGroupsFiltered(userName,
						lock.getGroups()));
		List<String> excpGroup = Arrays.asList(((String) (Constants
				.getProperties().get("inheritance_free"))).split(";"));
		Set<String> setH = new HashSet<String>(excpGroup);

		for (String g : lock.getGroups().keySet()) {
			if (setH.contains(g))
				continue;

			Rights r = lock.getGroups().get(g);
			Rights rch = new Rights(""); // remove W rights from all groups
			if (r.isWrite())
				rch.setWrite(true);
			if (rch.toString().trim().length() != 0)
				ch.updateGroup(g, rch.toString());

			if (groups.contains(g)) {
				if (r.isWrite()) {
					userAdded = true;
					forNewUserGroup.setWrite(r.isWrite());
					forNewUserGroup.setRead(r.isRead());
				}
			}
		}
		boolean ex = false;
		for (String u : lock.getUsers().keySet()) {
			Rights r = lock.getUsers().get(u);
			Rights rch = new Rights("");

			if (u.equals(userName)) {
				ex = true;
			} else {// remove W rights from all users
				if (r.isWrite())
					rch.setWrite(true);
				if (rch.toString().trim().length() != 0)
					ch.updateUser(u, rch.toString());
			}
		}
		// adding the user with the rights of the group
		if (!ex && userAdded) {
			ch.addUser(userName, forNewUserGroup.toString());
		}
		// if the user has not been found in a group or by itself search in inherited
		if (i == 0) { // root node
			boolean userAddedInh = false;
			boolean exInh = false;

			forNewUserGroup = new Rights("");
			Map<String, Rights> hmG = new HashMap<String, Rights>();
			Map<String, Rights> hmU = new HashMap<String, Rights>();

			for (String inh : inheritedRights.keySet()) {
				if (inh.startsWith("@")) {
					if (!lock.getGroups().containsKey(inh))
						hmG.put(inh, inheritedRights.get(inh));
				} else {
					if (!lock.getUsers().containsKey(inh))
						hmU.put(inh, inheritedRights.get(inh));
				}
			}

			groups = new ArrayList<String>(LdapProvider.getInstance()
					.getGroupsForUserWithParentGroupsFiltered(userName, hmG));

			for (String g : hmG.keySet()) { // group
				Rights r = inheritedRights.get(g);
				Rights rch = new Rights(""); // remove W rights from all groups

				rch.setRead(r.isRead());
				ch.addGroup(g, rch.toString());

				if (groups.contains(g)) {
					if (!userAdded && !ex && r.isWrite()) {
						userAddedInh = true;
						forNewUserGroup.setWrite(r.isWrite());
						forNewUserGroup.setRead(r.isRead());
					}
				}
			}
			for (String u : hmU.keySet()) { // user
				Rights r = inheritedRights.get(u);
				Rights rch = new Rights("");

				if (u.equals(userName) && !userAdded && !ex) {
					exInh = true;
					ch.addUser(u, r.toString());
				} else {// remove W rights from all users
					rch.setRead(r.isRead());
					ch.addUser(u, rch.toString());
				}

			}

			if (!exInh && userAddedInh) {
				ch.addUser(userName, forNewUserGroup.toString());
			}
		}

		lock.setChanges(ch);
		lock.applyChangesLock();
		if (!lock.isDeleted())
			ViewInformation.getInstance().addMail(SVN, lock.getFullPath(),
					lock, user, "Lock");
	}

	/**
	 * Locks the given node.
	 *
	 * @param n the node we are locking
	 * @param user the name of the user who locked the node
	 * @param user2 the same user but with more information
	 * @param groups the list of the groups to which the user belongs to
	 * @param i the position of the node
	 * @param inheritedRights the rights inherited from the parent node
	 *
	 * @throws Exception locking failed
	 *
	 */
	private void setLocked(Node n, String user, User user2, int i,
			Map<String, Rights> inheritedRights) throws Exception {
		n.setPersonLocked(user);
		n.setLocked(true);

		getLockChange(n, user2, i, inheritedRights);
		i++;

		if (!n.getChildren().isEmpty()) {
			for (Node a : n.getChildren()) {
				setLocked(a, user, user2, i, inheritedRights);
			}
		}
	}

	/**
	 * Unlocks the given node.
	 *
	 * @param n the node we are unlocking
	 *
	 * @throws Exception unlocking failed
	 *
	 */
	private void setUnlocked(Node n, User user) throws Exception {
		if (!n.isDeleted()) {
			ViewInformation.getInstance().addMail(SVN, n.getFullPath(), n,
					user, "Unlock");
			n.removeChangesUnlock();
		} else {
			n.removeChangesUnlock();
		}
		if (!n.getChildren().isEmpty()) {
			for (Node a : n.getChildren()) {
				setUnlocked(a, user);
			}
		}
	}

	/**
	 * Returns the root of the locked subtree.
	 *
	 * @param fullPath the path to the starting node of the search in the repository.
	 *
	 * @return the root of locked subtree
	 *
	 */
	public Node getLockedRoot(String fullPath) {
		Node pom = getNode(fullPath);
		while (pom.getParent() != null) {
			if (!pom.getParent().isLocked())
				break;
			else
				pom = pom.getParent();
		}
		return pom;

	}

	/**
	 * Checks all the children of the node on the given path if there is already a
	 * locked node.
	 *
	 * @param fullPath the path to the starting node of the search in the repository
	 *
	 * @return true if there is already a locked node otherwise false
	 *
	 */
	private synchronized boolean isLockedPathExists(String fullPath) {
		locked = false;
		isDirectoryLocked(getNode(fullPath));
		return locked;
	}

	/**
	 * Checks all the children of the given node if there is already a locked node.
	 *
	 * @param n the node from which the search starts.
	 *
	 */
	private void isDirectoryLocked(Node n) {
		if (n.isLocked() && n.isDeleted()) {
			try {
				setUnlocked(n, null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (n.isLocked())
			locked = true;
		if (locked)
			return;
		if (!n.getChildren().isEmpty()) {
			for (Node a : n.getChildren()) {
				isDirectoryLocked(a);
			}
		}
	}

	/**
	 * Removes the node on the given path.
	 *
	 * @param n the path to the node in the repository.
	 *
	 */
	public void removeNode(String fullPath) {
		Node n = getNode(fullPath).getParent();
		n.removeChildren(fullPath);
	}
}