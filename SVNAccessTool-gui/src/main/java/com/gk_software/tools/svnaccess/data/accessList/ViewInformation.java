package com.gk_software.tools.svnaccess.data.accessList;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.bussiness.impl.Change;
import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.bussiness.impl.User;
import com.gk_software.tools.svnaccess.data.IAccessListReader;
import com.gk_software.tools.svnaccess.data.ILdapProvider;
import com.gk_software.tools.svnaccess.data.ISVNStructureReader;
import com.gk_software.tools.svnaccess.data.IViewInformation;
import com.gk_software.tools.svnaccess.data.ldap.LDAPChangeControl;
import com.gk_software.tools.svnaccess.data.ldap.LdapProvider;
import com.gk_software.tools.svnaccess.data.mail.MailChecker;
import com.gk_software.tools.svnaccess.data.mail.MailNotification;
import com.gk_software.tools.svnaccess.data.mail.MailProperties;
import com.gk_software.tools.svnaccess.data.svn.SVNReader;
import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Implements the methods from the interface {@code IViewInformation}.
 */
public class ViewInformation implements IViewInformation {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ViewInformation.class);

	/** The instance of the {@code Logger} class for ACL. */
	private static final Logger aclLogger = Logger.getLogger("ACL_changes");

	/** The reader of the access list. */
	private IAccessListReader acr;

	/** The provider of LDAP. */
	private ILdapProvider gur;

	/** The reader of the SVN structure. */
	private ISVNStructureReader ssr;

	/** The instance of this class. */
	private static ViewInformation viewInformation = null;

	/** The mail notification. */
	private MailNotification mailNotification = null;

	/** The mail checker. */
	private MailChecker mailChecker = null;

	/**
	 * Creates a new instance and initializes the variables.
	 */
	private ViewInformation() throws Exception {
		aclReader();
		ldapProvider();
		svnReader();
		mailChecker();
		LDAPChangeControl();
	}

	/**
	 * Starts the checker for the changes in LDAP.
	 */
	private void LDAPChangeControl() {
		LDAPChangeControl lcc = LDAPChangeControl.getInstance();
		lcc.start();
	}

	/**
	 * Singleton class. This method provides only one instance of this class.
	 *
	 * @return the instance of this class
	 *
	 * @throws Exception instance retrieval failed
	 *
	 */
	public static ViewInformation getInstance() throws Exception {

		if (viewInformation == null) {
			logger.info("Creating a new ViewInformation instance");
			viewInformation = new ViewInformation();
		}
		return viewInformation;
	}

	/**
	 * Initializes the reader of the access list.
	 *
	 * @throws Exception initialization failed
	 *
	 */
	private void aclReader() throws Exception {
		logger.info("Get a new accessList instance");
		this.acr = AccessListReader.getInstance();
	}

	/**
	 * Tries to start the mail checker. If the mail notification is disabled it
	 * interrupts the start.
	 *
	 * @throws Exception starting failed
	 *
	 */
	private void mailChecker() throws Exception {
		logger.info("Get a new mail checker instance");
		this.mailChecker = MailChecker.getInstance();
		if (!Boolean.parseBoolean(Constants.getProperties()
				.getProperty("mailNotification").toString())) {
			logger.info("Mail checker set to interupted");
			this.mailChecker.setInterupt(true);
		}
		this.mailChecker.start();

	}

	/**
	 * Stops the mail checker.
	 *
	 * @throws Exception stoping failed
	 *
	 */
	public void stopMailChecker() throws Exception {
		logger.info("Stop a mail checker");
		this.mailChecker = MailChecker.getInstance();
		this.mailChecker.setInterupt(true);
	}

	/**
	 * Starts the mail checker without checking the state of mail notify.
	 *
	 * @throws Exception starting failed
	 *
	 */
	public void startMailChecker() throws Exception {
		logger.info("Start a mail checker");
		this.mailChecker = MailChecker.getInstance();
		this.mailChecker.setInterupt(false);
		this.mailChecker.notifyMy();
	}

	/**
	 * Initializes the provider of LDAP.
	 */
	private void ldapProvider() {
		logger.info("Get a new LdapProvider instance");
		this.gur = LdapProvider.getInstance();
		// this.gur = LDAPReal.getInstance();
	}

	/**
	 * Initializes the reader of the SVN structure.
	 */
	private void svnReader() {
		logger.info("Get a new svnReader instance");
		ssr = new SVNReader(Constants.getProperties().getProperty("repoRoot"));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#reloadACL()
	 */
	public void reloadACL() throws Exception {
		logger.info("Reloading accessList instance.");
		AccessListReader.reload();
		this.acr = AccessListReader.getInstance();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#reloadSVN()
	 */
	public void reloadSVN() {
		logger.info("Reloading SVN reader.");
		ssr = new SVNReader(Constants.getProperties().getProperty("repoRoot"));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#reloadLdap()
	 */
	public void reloadLdap() {
		logger.info("Reloading LDAP instance.");
		this.gur.reloadData();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#getTrees()
	 */
	public Map<String, AccessTree> getTrees() {
		return acr.getTrees();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#getGroups()
	 */
	public List<String> getGroups() {
		ArrayList<String> g = (ArrayList<String>) gur.getGroups();
		return g;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#getUsersForGroup
	 * (java.lang.String)
	 */
	public List<String> getUsersForGroup(String group) {
		List<String> users = new ArrayList<String>(gur.getGroupsUsers().get(
				group));
		java.util.Collections.sort(users);
		return users;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#getUsers()
	 */
	public List<String> getUsers() {
		ArrayList<String> g = (ArrayList<String>) gur.getUsers();
		return g;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#setResponsiblePerson
	 * (java.lang.String, java.lang.String)
	 */
	public void setResponsiblePerson(String svn, String responsiblePerson) {
		Map<String, AccessTree> trees = acr.getTrees();
		AccessTree tree = trees.get(svn);
		tree.getRoot().setResponsiblePerson(responsiblePerson);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#getRepositories()
	 */
	public List<Node> getRepositories() {
		// list the SVNs from the repo
		List<String> svnRepo = ssr.getRepositories();
		List<Node> listNode = new ArrayList<Node>();
		Map<String, AccessTree> trees = acr.getTrees();

		Node pom;
		for (String keySvn : svnRepo) {
			if (!acr.isExistTree(keySvn)) { // access rights do not exist
				// create a new tree with the access rights
				AccessTree tree = new AccessTree(keySvn);
				trees.put(keySvn, tree);
			}
			pom = trees.get(keySvn).getRoot();
			listNode.add(copyNode(pom));
		}
		java.util.Collections.sort(listNode, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				Collator collator = Collator
						.getInstance(new Locale("cs", "CZ"));
				return collator.compare(o1.getSvn(), o2.getSvn());

			}
		});
		return listNode;
	}

	/**
	 * Creates a copy of the given node.
	 *
	 * @param source the node to be copied
	 *
	 * @return the created node
	 *
	 */
	private synchronized Node copyNode(Node source) {

		Node newNode = new Node(source.getName(), null);
		newNode.setTimestamp(source.getTimestamp());
		newNode.setSvn(source.getSvn());
		newNode.setChildren(source.getChildren());
		newNode.setInheritance(source.isInheritance());
		newNode.setResponsiblePerson(source.getResponsiblePerson());
		newNode.setLocked(source.isLocked());
		newNode.setPersonLocked(source.getPersonLocked());
		// newNode.setParent(source.getParent());
		return newNode;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#getChildren(java
	 * .lang.String, java.lang.String)
	 */
	public List<Node> getChildren(String repoPath, String fullpath) {

		List<String> svnChildren = null;
		try {
			svnChildren = ssr.getChildren(repoPath, fullpath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Node nodeWithChildren = acr.getTree(repoPath).getNode(fullpath);
		nodeWithChildren.setCheckOnSVN(true);
		for (String s : svnChildren) {
			if (!nodeWithChildren.isChildExist(s)) { // if child does not exist
				// add children in AL children
				Node newNode = new Node(s, nodeWithChildren);
				nodeWithChildren.addChild(newNode);
			} else {
				for (Node n : nodeWithChildren.getChildren()) {
					if (n.getName().equals(s)) {
						n.setDeleted(false);
					}
				}

			}
		}
		// delete nodes not existing on SVN
		ArrayList<String> doesNotExist = new ArrayList<String>();
		for (Node nn : nodeWithChildren.getChildren()) {
			if (!svnChildren.contains(nn.getName())) {
				doesNotExist.add(nn.getFullPath());
			}
		}
		for (String s : doesNotExist) {
			nodeWithChildren.markAsDeleted(s);
			logger.info("Path: " + s + " marked as deleted from SVN");
		}

		List<Node> aa = new ArrayList<Node>();
		for (Node a : nodeWithChildren.getChildren()) {
			if (!a.isDeleted())
				aa.add(copyNode(a));
		}
		// return nodeWithChildren.getChildren();
		java.util.Collections.sort(aa, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				return o1.getName().compareTo(o2.getName());

			}
		});

		return aa;
		// return svnChildren;
	}

	/**
	 * Goes through the access tree from the given path for the given SVN and searches
	 * for the nodes which do not exist on the SVN anymore.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 */
	public void synchronizeSvnAccessList(String repoPath, String fullpath) {

		List<String> svnChildren = null;
		try {
			svnChildren = ssr.getChildren(repoPath, fullpath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Node nodeWithChildren = acr.getTree(repoPath).getNode(fullpath);
		if (!nodeWithChildren.getFullPath().equals(fullpath))
			return;
		nodeWithChildren.setCheckOnSVN(true);
		for (String s : svnChildren) {
			if (!nodeWithChildren.isChildExist(s)) { // if child does not exist
				// add children in AL children
				Node newNode = new Node(s, nodeWithChildren);
				nodeWithChildren.addChild(newNode);
			} else {
				for (Node n : nodeWithChildren.getChildren()) {
					if (n.getName().equals(s)) {
						n.setDeleted(false);
					}
				}
			}
		}

		ArrayList<String> doesNotExist = new ArrayList<String>();
		for (Node nn : nodeWithChildren.getChildren()) {
			if (!svnChildren.contains(nn.getName())) {
				doesNotExist.add(nn.getFullPath());
			}
		}
		for (String s : doesNotExist) {
			// System.out.println("removing: " + s + " from "
			// + nodeWithChildren.getFullPath());
			nodeWithChildren.markAsDeleted(s);
			logger.info("Path: " + s + " marked as deleted from SVN");
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#isPathExist
	 * (java.lang.String, java.lang.String)
	 */
	public boolean isPathExist(String repoPath, String fullpath) {
		String ed[] = fullpath.split("/");
		String editPath = "/";
		synchronizeSvnAccessList(repoPath, editPath);
		editPath = "";
		for (int i = 1; i < ed.length - 1; i++) {
			editPath += "/" + ed[i];
			synchronizeSvnAccessList(repoPath, editPath);
		}
		return acr.getTree(repoPath).isPathExist(fullpath);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#getUsersWithRights
	 * (java.lang.String, java.lang.String)
	 */
	public Map<String, Rights> getUsersWithRights(String repoPath, String fullpath) {
		Node nodeWithChildren = acr.getTree(repoPath).getNode(fullpath);
		// nodeWithChildren.printUsers();

		return new TreeMap<String, Rights>(nodeWithChildren.getUsers());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#getGroupsWithRights
	 * (java.lang.String, java.lang.String)
	 */
	public Map<String, Rights> getGroupsWithRights(String repoPath, String fullpath) {
		Node nodeWithChildren = acr.getTree(repoPath).getNode(fullpath);
		// nodeWithChildren.printGroups();

		return new TreeMap<String, Rights>(nodeWithChildren.getGroups());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#saveAccessList()
	 */
	public synchronized String saveAccessList() {
		synchronized (mailChecker.getLock()) {
			mailChecker.setReadyToCommit(false);
			logger.info("Saving the access list on a disk.");
			String returnValue = this.acr.saveAccessList();
			mailChecker.setReadyToCommit(true);
			return returnValue;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#getInehritedRights
	 * (java.lang.String, java.lang.String)
	 */
	public Map<String, Rights> getInehritedRights(String repoPath, String fullpath) {
		Node nodeWithChildren = acr.getTree(repoPath).getNode(fullpath);
		Map<String, Rights> map = new HashMap<String, Rights>();
		if (nodeWithChildren.isInheritance())
			return map;

		Rights rr = nodeWithChildren.getGroups().get("*");

		if (rr != null)
			return map;

		Node parentNode = nodeWithChildren.getParent();

		while (parentNode != null) {

			for (String u : parentNode.getUsers().keySet()) {
				// if (!map.containsKey(u)){
				// map.put(u, parentNode.getUsers().get(u));
				boolean exist = false;
				for (String gg : gur.getGroupsForUser(u)) {
					if (map.containsKey(gg)) { // if group exist
						exist = true;
					}
					// map.put("+" + gg + ":" + u, new Rights("rwm"));
				}
				if (!exist && !map.containsKey(u))
					map.put(u, parentNode.getUsers().get(u));
				// }
			}

			for (String g : parentNode.getGroups().keySet()) {
				if (!map.containsKey(g))
					map.put(g, parentNode.getGroups().get(g));
			}

			if (parentNode.isInheritance()) {
				break;
			}

			Rights rrr = parentNode.getGroups().get("*");

			if (rrr != null)
				break;

			parentNode = parentNode.getParent();

		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#isInheritance
	 * (java.lang.String, java.lang.String)
	 */
	public boolean isInheritance(String repoPath, String fullpath) {
		return this.acr.getTree(repoPath).getNode(fullpath).isInheritance();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#hasSVNChildren
	 * (java.lang.String, java.lang.String)
	 */
	public boolean hasSVNChildren(String repoPath, String fullpath) {
		try {
			return this.ssr.hasChildren(repoPath, fullpath, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#canModifyCheckBoxes
	 * (java.lang.String, java.lang.String,
	 * com.gk_software.tools.svnaccess.bussiness.impl.User)
	 */
	public boolean canModifyCheckBoxes(String repoPath, String fullpath, User user) {
		// List<String> groups = gur.getGroupsForUser(user.getUserName());
		List<String> groups = new ArrayList<String>(
				gur.getGroupsForUserWithParentGroups(user.getUserName()));

		if (user.isAdmin()
				|| acr.getTree(repoPath).checkModifyRightsAllTree(fullpath,
						groups, user.getUserName()))
			return true;
		return false;
		// System.out.println("check mod: " + repoPath + " " + fullpath);
		// return acr.getTree(repoPath).canModifyRights(fullpath, groups,
		// user.getUserName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#canModifyCheckBoxesFromRoot
	 * (java.lang.String, java.lang.String,
	 * com.gk_software.tools.svnaccess.bussiness.impl.User)
	 */
	public boolean canModifyCheckBoxesFromRoot(String repoPath, String fullpath,
			User user) {
		// List<String> groups = gur.getGroupsForUser(user.getUserName());
		List<String> groups = new ArrayList<String>(
				gur.getGroupsForUserWithParentGroups(user.getUserName()));

		if (user.isAdmin()
				|| acr.getTree(repoPath).checkModifyRightsAllTree(
						acr.getTree(repoPath).getLockedRoot(fullpath)
								.getFullPath(), groups, user.getUserName()))
			return true;
		return false;
		// System.out.println("check mod: " + repoPath + " " + fullpath);
		// return acr.getTree(repoPath).canModifyRights(fullpath, groups,
		// user.getUserName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#hasWriteFlag
	 * (java.lang.String, java.lang.String,
	 * com.gk_software.tools.svnaccess.bussiness.impl.User)
	 */
	public boolean hasWriteRights(String repoPath, String fullpath, User user) {
		// List<String> groups = new ArrayList<String>(
		// gur.getGroupsForUserWithParentGroups(user.getUserName()));

		return acr.getTree(repoPath).hasWriteRights(fullpath,
				user.getUserName(), gur);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#updateRights
	 * (java.lang.String, java.lang.String,
	 * com.gk_software.tools.svnaccess.bussiness.impl.Changes)
	 */
	public synchronized void updateRights(String repoPath, String fullpath,
			Changes changes) {

		Node n = acr.getTree(repoPath).getNode(fullpath);
		if (changes.isInheritance()) {
			n.reverseInheritance();
			if (n.isInheritance())
				aclLogger.info("Inheritance set to enabled");
			else
				aclLogger.info("Inheritance set to disabled");

		}

		logger.info("Start of updating rights in ACL tree.");
		aclLogger.info("Start of updating rights in ACL tree.");
		// changes.printInfo();

		Node nn = n;
		while (nn.getParent() != null) {
			nn = nn.getParent();
		}
		aclLogger
				.info("  NODE: " + n.getFullPath() + " in SVN: " + nn.getSvn());

		long newTime = System.currentTimeMillis();
		n.setTimestamp(newTime);

		aclLogger.info("    Setting a new time to node: " + newTime);
		aclLogger.info("  GROUP CHANGES");
		if (changes.getGroupChanges().size() == 0)
			aclLogger.info("    no changes");
		for (String group : changes.getGroupChanges().keySet()) {
			Change groupCh = changes.getGroup(group);
			switch (groupCh.getAction()) {
			case ADDED:
				aclLogger.info("    ADDED: " + group + ", rights: "
						+ groupCh.getRights().toString());
				n.addGroup(group, groupCh.getRights().toString());
				break;
			case UPDATED:
				String oldRights = n.getGroups().get(group).toString();

				n.updateGroup(group, groupCh.getRights());
				aclLogger.info("    UPDATED: " + group + ", from rights: "
						+ oldRights + " to new: "
						+ n.getGroups().get(group).toString());
				break;
			case REMOVED:
				aclLogger.info("    REMOVED: " + group + ", rights: "
						+ n.getGroups().get(group).toString());
				n.removeGroup(group);
				break;
			}
		}
		aclLogger.info("  USER CHANGES:");
		if (changes.getUserChanges().size() == 0)
			aclLogger.info("    no changes");
		for (String user : changes.getUserChanges().keySet()) {
			Change userCh = changes.getUser(user);
			switch (userCh.getAction()) {
			case ADDED:
				aclLogger.info("    ADDED: " + user + ", rights: "
						+ userCh.getRights().toString());
				n.addUser(user, userCh.getRights().toString());
				break;
			case UPDATED:
				String oldRights = n.getUsers().get(user).toString();
				n.updateUser(user, userCh.getRights());
				aclLogger.info("    UPDATED: " + user + ", from rights: "
						+ oldRights + " to new: "
						+ n.getUsers().get(user).toString());

				break;
			case REMOVED:
				aclLogger.info("    REMOVED: " + user + ", rights: "
						+ n.getUsers().get(user).toString());
				n.removeUser(user);
				break;
			}
		}

		aclLogger.info("End of updating rights in ACL tree.");
		logger.info("End of updating rights in ACL tree.");
	}

	/**
	 * Reinitializes the instance of this class to null.
	 */
	public static void removeAll() {
		viewInformation = null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#lockNode
	 * (java.lang.String, java.lang.String, com.gk_softw
	 * are.tools.svnaccess.bussiness.impl.User)
	 */
	public boolean lockNode(String repoPath, String fullPath, User user)
			throws Exception {
		return acr.getTree(repoPath).lockNodes(fullPath, user,
				getInehritedRights(repoPath, fullPath));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#unlockNode
	 * (java.lang.String, java.lang.String,
	 * com.gk_software.tools.svnaccess.bussiness.impl.User)
	 */
	public boolean unlockNode(String repoPath, String fullPath, User user) {
		List<String> groups = gur.getGroupsForUser(user.getUserName());
		return acr.getTree(repoPath).unlockNodes(fullPath, groups, user);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#getLockedPaths()
	 */
	public List<LockBean> getLockedPaths() {
		ArrayList<LockBean> list = new ArrayList<LockBean>();

		List<String> svnRepo = ssr.getRepositories();

		for (String keySvn : svnRepo) {
			Node root = acr.getTree(keySvn).getNode("/");
			if (root.isLocked() && !root.isDeleted()) {
				list.add(new LockBean(keySvn, root.getFullPath(), root
						.getPersonLocked()));
				continue;
			}
			Stack<Node> stack = new Stack<Node>();
			stack.addAll(root.getChildren());
			while (!stack.isEmpty()) {
				Node currentNode = stack.pop();
				if (currentNode.isLocked() && !currentNode.isDeleted()) {
					list.add(new LockBean(keySvn, currentNode.getFullPath(),
							currentNode.getPersonLocked()));
				} else {
					stack.addAll(currentNode.getChildren());
				}
			}

		}

		// for (int i = 0; i < 50; i++) {
		// LockBean newLock = new LockBean("svn" + i, "fullpath" + i,
		// "testUser" + i);
		// list.add(newLock);
		// }
		return list;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IViewInformation#
	 * getModificationInheritedRights(java.lang.String, java.lang.String)
	 */
	public Map<String, Rights> getModificationInheritedRights(String repoPath,
			String fullpath) {
		Node nodeWithChildren = acr.getTree(repoPath).getNode(fullpath);
		Map<String, Rights> map = new HashMap<String, Rights>();

		Node parentNode = nodeWithChildren.getParent();

		while (parentNode != null) {

			for (String u : parentNode.getUsers().keySet()) {
				if (!map.containsKey(u)) {
					Rights r = parentNode.getUsers().get(u);
					if (r.isModify())
						map.put(u, parentNode.getUsers().get(u));
				}
			}

			for (String g : parentNode.getGroups().keySet()) {
				if (!map.containsKey(g)) {
					Rights r = parentNode.getGroups().get(g);
					if (r.isModify())
						map.put(g, parentNode.getGroups().get(g));
				}
			}
			parentNode = parentNode.getParent();

		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#cleanAccessList()
	 */
	public synchronized void cleanAccessList(String svn, int depth,
			boolean checkOnSvn) {
		AccessTree tree = acr.getTree(svn);
		Cleaner cl = new Cleaner(new SVNReader(Constants.getProperties()
				.getProperty("repoRoot")));
		cl.clean(tree, depth, checkOnSvn);
		saveAccessList();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#addMail(java.lang.String,
	 * java.lang.String, com.gk_software.tools.svnaccess.bussiness.impl.Changes,
	 * com.gk_software.tools.svnaccess.bussiness.impl.User)
	 */
	public void addMail(String svn, String fullpath, Changes changes,
			User user, String reason) throws Exception {
		// Map<String, Rights> receivers = getModificationInheritedRights(svn,
		// fullpath);
		if (Boolean.parseBoolean(Constants.getProperties()
				.getProperty("mailNotification").toString())) {

			Map<String, Rights> receivers = getReceivers(user, svn, changes);

			mailChecker.getMailWriter().appendMessage(svn, fullpath, receivers,
					changes, user, reason);
		}
	}

	/**
	 * Returns a map of receivers for the given parameters.
	 *
	 * @param user the user who made the change
	 * @param svn the svn to which the changes have been made
	 * @param changes the list of the made changes
	 *
	 * @return the map with username as key and rights as value
	 *
	 */
	private Map<String, Rights> getReceivers(User user, String svn, Changes changes) {
		Map<String, Rights> receivers = new HashMap<String, Rights>();

		String settings = MailProperties.getProperties().getProperty(svn);
		if(settings==null)
			return receivers;
		if (settings.charAt(0) == '1') {
			// M users from Up node
			Set<String> modificationRepos = new HashSet<String>();
			Node root = acr.getTree(svn).getNode("/");
			for (String us : root.getUsers().keySet()) {
				Rights r = root.getUsers().get(us);
				if (r.isModify()) {
					modificationRepos.add(us);
				}

			}
			for (String gr : root.getGroups().keySet()) {
				Rights r = root.getGroups().get(gr);
				if (r.isModify()) {
					modificationRepos.addAll(gur.getUsersForGroup(gr));
				}
			}
			for (String rec : modificationRepos) {
				receivers.put(rec, new Rights(""));
			}
		}
		if (settings.charAt(1) == '1') {
			// originator
			receivers.put(user.getUserName(), new Rights(""));
		}
		if (settings.charAt(2) == '1') {
			// attended

			for (String rec : changes.getUserChanges().keySet()) {
				receivers.put(rec, new Rights(""));
			}

			Map<String, Change> gg = changes.getGroupChanges();
			for (String g : gg.keySet()) {
				for (String rec : gur.getUsersForGroup(g)) {
					receivers.put(rec, new Rights(""));
				}
			}
		}
		if (settings.charAt(3) == '1') {
			// admins

			List<String> jj = Arrays.asList(((String) (Constants
					.getProperties().get("tcs"))).split(";"));
			for (String us : jj) {
				receivers.put(us, new Rights(""));
			}
		}
		return receivers;
	}

	/**
	 * Adds a mail to the mail checker mails.
	 *
	 * @param svn the svn to which the changes have been made
	 * @param fullpath the path to the node in the repository
	 * @param lock the node which has been changed
	 * @param user the user who made the change
	 * @param reason the reason of the mail
	 *
	 */
	public void addMail(String svn, String fullpath, Node lock, User user,
			String reason) {
		// Map<String, Rights> receivers = getModificationInheritedRights(svn,
		// fullpath);
		if (Boolean.parseBoolean(Constants.getProperties()
				.getProperty("mailNotification").toString())) {
			// Map<String, Rights> receivers = new HashMap<String, Rights>();
			Map<String, Rights> receivers = getReceivers(user, svn,
					lock.getChanges());

			mailChecker.getMailWriter().appendMessage(svn, fullpath, receivers,
					lock, user, reason);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IViewInformation#getRightsInheritanceFree
	 * (java.lang.String, java.lang.String, java.lang.String,
	 * com.gk_software.tools.svnaccess.data.accessList.Node)
	 */
	public String getRightsInheritanceFree(String group, String svn, String fullpath,
			Node node) {
		logger.info("Searching for rights in group:" + group
				+ " for Rights Inheritance Free");
		String rightsToReturn = null;
		if (group.startsWith("@"))
			group = group.substring(1);
		if (node == null) {
			node = acr.getTree(svn).getNode(fullpath);
		}
		// case of inheritance checkbox
		Map<String, Rights> allParentGroups = LdapProvider.getInstance()
				.getParentsGroupsForGroupFiltered(group, node.getGroups());

		if (allParentGroups.size() != 0) {
			for (String g : allParentGroups.keySet()) {
				rightsToReturn = allParentGroups.get(g).toString();
				logger.info("Found parent group: " + g
						+ " in selected directory for group: " + group
						+ ", rights: " + rightsToReturn
						+ " from it will be used ");
			}
		}
		if (rightsToReturn == null) {
			rightsToReturn = checkParentDirectories(node, group, rightsToReturn);
		}

		if (rightsToReturn == null) {
			logger.info("For group: " + group
					+ " no rights founded. Only read rights will be used.");
			rightsToReturn = "r";
		}
		return rightsToReturn;
	}

	/**
	 * Checks if the parent groups of the given group for the given node do not already
	 * contain the same rights as the given rights. If they do it returns the particular
	 * parent group.
	 *
	 * @param node the node for which we search the rights
	 * @param group the group whose parents we search
	 * @param rightsToReturn the returned rights
	 *
	 * @return the parent group rights or null
	 *
	 */
	private String checkParentDirectories(Node node, String group, String rightsToReturn) {
		logger.info("Checking groups in parent directory");

		Node parent = node.getParent();
		if (parent == null) {
			// System.out.println(" null parent - root");

		} else {
			while (parent != null) {

				// System.out.println(" checking rights for parent: "
				// + parent.getFullPath());
				if (!parent.getGroups().containsKey("@" + group)) {

					Map<String, Rights> allParentGroups = LdapProvider
							.getInstance().getParentsGroupsForGroupFiltered(
									group, parent.getGroups());

					for (String g : allParentGroups.keySet()) {
						rightsToReturn = allParentGroups.get(g).toString();
						logger.info("Found parent group: " + g
								+ " in parent directory "
								+ parent.getFullPath() + " for group: " + group
								+ ", rights: " + rightsToReturn
								+ " from it will be used ");

					}

					if (rightsToReturn != null) {
						return rightsToReturn;
					} else {
						parent = parent.getParent();
					}

				} else {
					rightsToReturn = parent.getGroups().get("@" + group)
							.toString();
					logger.info("For group: "
							+ group
							+ " were found the same group in parent directory: "
							+ parent.getFullPath()
							+ ".Rights from it will be used: " + rightsToReturn);

					return rightsToReturn;
				}
			}

		}
		return null;
	}

}