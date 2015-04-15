package com.gk_software.tools.svnaccess.bussiness.impl;

import gk.ee_common.i18n.I18NResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.IViewInformation;
import com.gk_software.tools.svnaccess.data.accessList.CleanBean;
import com.gk_software.tools.svnaccess.data.accessList.LockBean;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.Rights;
import com.gk_software.tools.svnaccess.data.accessList.ViewInformation;
import com.gk_software.tools.svnaccess.main.MainApplicationWindow;
import com.gk_software.tools.svnaccess.utils.Constants;
import com.gk_software.tools.svnaccess.view.components.ControlButtons;
import com.gk_software.tools.svnaccess.view.components.FavoriteBean;
import com.gk_software.tools.svnaccess.view.components.Repositories;
import com.gk_software.tools.svnaccess.view.components.GroupsTree.GroupBean;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalConfirmationDialog;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window.Notification;

/**
 * Implements the methods from the interface {@code IControl}.
 */
public class Control implements IControl {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(Control.class);

	/** The instance of the {@code Logger} class for ACL. */
	private static final Logger aclLogger = Logger.getLogger("ACL_changes");

	/** The header of the out of date column. */
	public static final I18NResource OUT_OF_DATE_HEADER = new I18NResource(
			ControlButtons.class, "OUT_OF_DATE_HEADER", "Out of date");

	/** The text of the out of date message. */
	public static final I18NResource DATA_ARE_OUT_OF_DATE_TEXT = new I18NResource(
			ControlButtons.class, "DATA_ARE_OUT_OF_DATE_TEXT",
			"Data are out of date. Do you want to reload them?");

	/** The main app window. */
	private MainApplicationWindow application;

	/** The information about SVNs. */
	private IViewInformation information;

	/** The logged user. */
	private User user;

	/** The map of all the changes. */
	private Map<String, Changes> changesList;

	/** The instance of this class. */
	private IControl thisControl = this;

	/** The cookies of the logged user. */
	private CookieFactory cookies;

	/** The indicator whether the changes have been saved. */
	private boolean saved;

	/**
	 * Creates a new instance and initializes the variables according to the
	 * given values.
	 *
	 * @param application the main app window
	 * @param user the logged user
	 * @param cookies the cookies of the logged user
	 *
	 * @throws Exception instance creation failed
	 */
	public Control(MainApplicationWindow application, String user,
			CookieFactory cookies2) throws Exception {
		logger.info("Parameters: user=" + user + " application=" + application);
		this.application = application;
		this.cookies = cookies2;

		this.information = ViewInformation.getInstance();
		this.user = new User(user);
		this.changesList = new HashMap<String, Changes>();
		checkAdmin();
		logger.debug("End constructor");

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#setPathFromCookies()
	 */
	public void setPathFromCookies() {
		String svn = cookies.getLastViewSvn();
		String fullpath = cookies.getlastViewPath();
		if (svn != null && fullpath != null) {
			logger.info("Expandig path from cookies: svn: " + svn + ", path: "
					+ fullpath);
			String returnValue = application.getRepositories().setChosenRepo(
					getRepositories(), svn);
			if (returnValue == null) {
				application.getDirectories().setRoot(
						application.getRepositories().getChosenRepoNode());

				returnValue = pathEdited(fullpath);
			} else {
				application.getCurrentPath().setCookies();
			}
		} else {
			application.getCurrentPath().setCookies();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getUser()
	 */
	public User getUser() {
		return user;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getNode
	 * (java.lang.String, java.lang.String)
	 */
	public Node getNode(String svn, String fullpath) {
		return information.getTrees().get(svn).getNode(fullpath);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getChanges()
	 */
	public Map<String, Changes> getChanges() {
		return changesList;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getUsersForGroup
	 * (java.lang.String)
	 */
	public List<String> getUsersForGroup(String group) {
		return information.getUsersForGroup(group);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#repositorySelected
	 * (java.lang.String)
	 */
	public void repositorySelected(String repoPath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repopath=" + repoPath);
		// we need "/" to express that it is root
		// changesList = new HashMap<String, Changes>();

		List<Node> nodes = information.getChildren(repoPath, "/");

		application.getDirectories().repositorySelected(nodes,
				application.getRepositories().getChosenRepoNode());

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#directorySelected
	 * (java.lang.String, java.lang.String)
	 */
	public void directorySelected(String repoPath, String fullpath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repopath=" + repoPath + " fullpath=" + fullpath);
		logger.info("Logged user : " + this.user.getUserName()
				+ " Can modify: "
				+ information.canModifyCheckBoxes(repoPath, fullpath, user));

		// check if the item was selected before
		information.getChildren(repoPath, fullpath);
		Map<String, Rights> usersWithRights = information.getUsersWithRights(
				repoPath, fullpath);
		Map<String, Rights> groupsWithRights = information.getGroupsWithRights(
				repoPath, fullpath);

		// if the selected node is root it is necessary to put / to the current path
		if (application.getDirectories().getLastChosenItemId() == application
				.getDirectories().getRoot()) {
			application.getCurrentPath().setPathField(repoPath, "/");
		} else {
			application.getCurrentPath().setPathField(repoPath, fullpath);
		}
		Node node = information.getTrees().get(repoPath).getNode(fullpath);

		boolean canModifyCheckboxes = information.canModifyCheckBoxes(repoPath,
				fullpath, user);

		application.getEffectiveRightsTable().setRights(
				information.getInehritedRights(repoPath, fullpath),
				information.getModificationInheritedRights(repoPath, fullpath));

		// setting the visibility of controls for lock/unlock
		if (node.isLocked()) {
			application.getGroupsTable().setCanModifyCheckboxes(false);
			application.getUsersTable().setCanModifyCheckboxes(false);
			application.getInheritance().setEnabled(false);
			application.getButtons().allowUnlock();
		} else {
			application.getGroupsTable().setCanModifyCheckboxes(
					canModifyCheckboxes);
			application.getUsersTable().setCanModifyCheckboxes(
					canModifyCheckboxes);
			application.getInheritance().setEnabled(canModifyCheckboxes);
			application.getButtons().allowLock();
		}

		application.getGroupsTable().setGroups(groupsWithRights);
		application.getUsersTable().setUsers(usersWithRights);
		application.getInheritance().setValue(
				application.getDirectories().getLastChosenItemId()
						.isInheritance());
		refreshDataPanel();
		// setting the enabled state of the buttons for M rights
		if (canModifyCheckboxes) {
			// check of the M right - enabling the Clear rights function
			application.getClearRights().setEnabled(true);
			if (node.isLocked()) {
				application.getButtons().disableAll();
			} else {
				application.getButtons().enableAll();
			}
			if (user.isAdmin()) {
				application.getButtons().getSettings().setEnabled(true);
			} else {
				application.getButtons().getSettings().setEnabled(false);
			}
		} else {
			application.getButtons().disableAll();
			// if user does not have M rights the Clear rights function is disabled
			application.getClearRights().setEnabled(false);
		}

		boolean hasWriteFlag = information.hasWriteRights(repoPath, fullpath,
				user);
		if (node.isLocked()) {
			boolean canModifyCheckboxesFromRoot = information
					.canModifyCheckBoxesFromRoot(repoPath, fullpath, user);

			if (canModifyCheckboxesFromRoot
					|| node.getPersonLockedWithoutCheck()
							.toLowerCase()
							.equals(user.getUserName().toLowerCase().split("@")[0])) {
				application.getButtons().allowUnlock();
			} else {
				application.getButtons().getLock().setEnabled(false);
				application.getButtons().getUnlock().setEnabled(false);
			}
		} else {
			if (!hasWriteFlag && !canModifyCheckboxes) {
				application.getButtons().getLock().setEnabled(false);
				application.getButtons().getUnlock().setEnabled(false);
			}
		}

		this.getApplication().getCookies().addLastViewPath("svn", repoPath);
		this.getApplication().getCookies()
				.addLastViewPath("fullpath", fullpath);
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#directoryExpanded
	 * (java.lang.String, java.lang.String,
	 * com.gk_software.tools.svnaccess.data.accessList.Node)
	 */
	public void directoryExpanded(String repoPath, Node parent) {
		String fullpath = parent.getFullPath();
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repopath=" + repoPath + " fullpath=" + fullpath
				+ " parent=" + parent);
		// Maybe children should be first removed, because there can be
		// directories that was removed load children
		// if (!control.pathExist(root.getSvn(),node.getFullPath())) {
		// System.out.println("removing "+node.getFullPath());
		// thisDirectories.removeItem(node);
		// return;
		// }

		// add them into the tree (parent is not used, because container take
		// care of hiearachy)
		// System.out.println("children: " + children.size());
		// System.out.println("patrh exist: " + pathExist(repoPath, fullpath));
		// System.out.println("children: " + children);
		// System.out.println("fullpath " + fullpath);
		if (!pathExist(repoPath, fullpath)) {
			Node pom = parent;
			while (pom != null) {

				if (pathExist(repoPath, pom.getFullPath()))
					break;

				pom = pom.getParent();
			}
			// application.showNotification("")
			application.getDirectories().expandPath(pom.getFullPath());

		} else {
			List<Node> children = information.getChildren(repoPath, fullpath);

			application.getCurrentPath().setPathField(repoPath, fullpath);
			application.getDirectories().addChildren(children, parent);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getRepositories()
	 */
	public List<Node> getRepositories() {
		List<Node> aa = null;
		try {
			aa = information.getRepositories();
			logger.info("Logged user : " + this.user.getUserName() + " Start");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return aa;

	}

	/**
	 * Checks whether someone did not change the nodes in the tree and thus the current
	 * changes cannot be saved.
	 *
	 * @return true if the changes can be saved otherwise false
	 *
	 */
	private boolean checkChangesValidity() {
		boolean isValid = true;

		for (String s : changesList.keySet()) {
			Changes changes = changesList.get(s);

			final String[] s2 = s.split(":");

			if (!isDirectoryValid(s2[0], s2[1])) {
				isValid = false;
				logger.info("LOGGED USER : " + this.user.getUserName()
						+ " Timestamp is not valid - changes are not performed");
				aclLogger
						.info("LOGGED USER : "
								+ this.user.getUserName()
								+ " Timestamp is not valid - changes are not performed");

				logNonPerformedChanges(s2[0], s2[1], changes, information
						.getTrees().get(s2[0]).getNode(s2[1]));
				logger.info("LOGGED USER : " + this.user.getUserName()
						+ " End of saving changes");
				aclLogger.info("End of saving changes");

				timestampNotValidNotification(s2[0], s2[1]);

			}
		}
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#saveChanges()
	 */
	public synchronized void saveChanges() throws Exception {
		if (checkChangesValidity()) {
			for (String s : changesList.keySet()) {
				Changes changes = changesList.get(s);

				String[] s2 = s.split(":");

				logger.info("Logged user : " + this.user.getUserName()
						+ " Start of saving changes");

				final String fullpath = s2[1];
				String svn = s2[0];

				logger.info("LOGGED USER : " + this.user.getUserName()
						+ " fullpath=" + fullpath);
				logger.info("LOGGED USER :  " + this.user.getUserName() + " "
						+ "svn=" + svn);

				// if
				// (isDirectoryValid(application.getDirectories().getNodePath()))
				// {
				logger.info("LOGGED USER : " + this.user.getUserName()
						+ " Timestamp is valid - changes performed");
				aclLogger.info("LOGGED USER : " + this.user.getUserName()
						+ " Timestamp is valid - changes performed");

				Node nodeP = information.getTrees().get(svn).getNode(fullpath);
				logger.info("LOGGED USER : " + this.user.getUserName()
						+ " Old time: " + nodeP.getTimestamp());

				information.updateRights(svn, fullpath, changes);

				aclLogger.info("Start of saving changes on a disk");
				String returnString = information.saveAccessList();

				this.getApplication().getCookies().addSaveCookie(svn, fullpath);
				if (returnString != null) {
					aclLogger.error("Error during saving ACL file");
					application
							.showNotification("Error during saving ACL file");
				} else {
					information.addMail(svn, fullpath, changes, user,
							"Save access list");
				}

				application.getRepositories().updateRepositoryTimestamps(
						getRepositories());
				application.getDirectories().update(
						application.getRepositories().getChosenRepoNode(),
						fullpath);

				// after saving all the changes will be deleted and replaced by
				// permanent changes
				logger.info("LOGGED USER : "
						+ this.user.getUserName()
						+ " new time: "
						+ information.getTrees().get(svn).getNode(fullpath)
								.getTimestamp());

				aclLogger.info("End of saving changes");

			}
			if (changesList.size() == 0) {
				application.getWindow().showNotification("SAVE CHANGES",
						"There are no changes to be performed",
						Notification.TYPE_WARNING_MESSAGE);
			} else {

				removeAllChanges();
				directorySelected(
						application.getRepositories().getChosenRepo(),
						((FavoriteBean) application.getCurrentPath().getValue())
								.getFullpath());
				saved = true;
				refreshDataPanel();
				application.getWindow().showNotification("SAVE CHANGES",
						"Changes were successfuly performed.",
						Notification.TYPE_WARNING_MESSAGE);

			}
		} else {

			application.getWindow().showNotification("SAVE CHANGES",
					"Changes are not performed",
					Notification.TYPE_WARNING_MESSAGE);

		}
	}

	/**
	 * Logs the non performed changes. It is called after the user tries to save
	 * the file but the timestamp is not valid.
	 *
	 * @param svn the repository to which the changes have been made
	 * @param fullpath the path to the node which has been changed
	 * @param changes2 the made changes
	 * @param node the node which hase been changed
	 *
	 */
	private void logNonPerformedChanges(String svn, String fullpath,
			Changes changes2, Node node) {
		boolean in = changes2.isInheritance();

		if (node.isInheritance() != in) {
			if (in)
				aclLogger.info("Inheritance was enabled");
			else
				aclLogger.info("Inheritance was disabled");
		}

		aclLogger.info("  NODE: " + fullpath + " in SVN: " + svn);

		aclLogger.info("  GROUP CHANGES");
		if (changes2.getGroupChanges().size() == 0)
			aclLogger.info("    no changes");
		for (String group : changes2.getGroupChanges().keySet()) {
			Change groupCh = changes2.getGroup(group);
			switch (groupCh.getAction()) {
			case ADDED:
				aclLogger.info("    ADDED: " + group + ", rights: "
						+ groupCh.getRights().toString());
				break;
			case UPDATED:
				String oldRights = node.getGroups().get(group).toString();
				aclLogger.info("    UPDATED: " + group + ", from rights: "
						+ oldRights + " to new: "
						+ node.getGroups().get(group).toString());
				break;
			case REMOVED:
				aclLogger.info("    REMOVED: " + group + ", rights: "
						+ node.getGroups().get(group).toString());
				break;
			}
		}
		aclLogger.info("  USER CHANGES:");
		if (changes2.getUserChanges().size() == 0)
			aclLogger.info("    no changes");
		for (String user : changes2.getUserChanges().keySet()) {
			Change userCh = changes2.getUser(user);
			switch (userCh.getAction()) {
			case ADDED:
				aclLogger.info("    ADDED: " + user + ", rights: "
						+ userCh.getRights().toString());
				break;
			case UPDATED:
				String oldRights = node.getUsers().get(user).toString();
				aclLogger.info("    UPDATED: " + user + ", from rights: "
						+ oldRights + " to new: "
						+ node.getUsers().get(user).toString());

				break;
			case REMOVED:
				aclLogger.info("    REMOVED: " + user + ", rights: "
						+ node.getUsers().get(user).toString());
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#getUsersFiltered
	 * (java.lang.String, java.lang.String)
	 */
	public List<String> getUsersFiltered(String repoPath, String fullpath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repoPath=" + repoPath + " fullpath=" + fullpath);
		List<String> users = information.getUsers();

		List<String> temp = new ArrayList<String>(users.size());
		for (String item : users)
			temp.add(new String(item));

		// admin filtration
		// temp.removeAll(Arrays.asList(((String) (Constants.getProperties()
		// .get("tcs"))).split(";")));

		// System.out.println("users " + repoPath + " " + fullpath);
		temp.removeAll(information.getUsersWithRights(repoPath, fullpath)
				.keySet());
		logger.info("Logged user : " + this.user.getUserName()
				+ " actual dir: "
				+ information.getUsersWithRights(repoPath, fullpath).keySet());

		Changes changes = changesList.get(repoPath + ":" + fullpath);
		if (changes == null)
			changes = new Changes();

		temp.removeAll(changes.getUserChanges().keySet());
		logger.info("Logged user : " + this.user.getUserName() + " changes: "
				+ changes.getUserChanges().keySet());
		//TODO filter with actual node
		return temp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#getGroupsFiltered
	 * (java.lang.String, java.lang.String)
	 */
	public List<String> getGroupsFiltered(String repoPath, String fullpath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repoPath=" + repoPath + " fullpath=" + fullpath);
		List<String> groups = information.getGroups();
		List<String> temp = new ArrayList<String>(groups.size());
		for (String item : groups)
			temp.add("@" + new String(item));
		// System.out.println("groups " + repoPath + " " + fullpath);
		temp.removeAll(information.getGroupsWithRights(repoPath, fullpath)
				.keySet());

		Changes changes = changesList.get(repoPath + ":" + fullpath);
		if (changes == null)
			changes = new Changes();

		temp.removeAll(changes.getGroupChanges().keySet());
		return temp;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getUsersAll()
	 */
	public List<String> getUsersAll() {
		return information.getUsers();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getGroupsAll()
	 */
	public List<String> getGroupsAll() {
		return information.getGroups();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#hasSVNChildren
	 * (java.lang.String, java.lang.String)
	 */
	public boolean hasSVNChildren(String repoPath, String fullpath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repoPath=" + repoPath + " fullpath=" + fullpath);
		// if(selected)
		// return !getChildren(repoPath, fullpath).isEmpty();
		Node n = getNode(repoPath, fullpath);
		if (n.isCheckOnSVN())
			return !n.getChildren().isEmpty();
		else
			return !getChildren(repoPath, fullpath).isEmpty();
		// for(Node n:list){
		// getChildren(repoPath, n.getFullPath());
		// }
		// return true;
		// return information.hasSVNChildren(repoPath, fullpath);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#reloadSVN()
	 */
	public void reloadSVN() {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Reloading SVN");
		information.reloadSVN();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#reloadACL()
	 */
	public void reloadACL() throws Exception {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Reloading ACL");
		information.reloadACL();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#reloadLdap()
	 */
	public void reloadLdap() {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Reloading ACL");
		information.reloadLdap();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#getChildren
	 * (java.lang.String, java.lang.String)
	 */
	public List<Node> getChildren(String repoPath, String fullpath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repoPath=" + repoPath + " fullpath=" + fullpath);

		return information.getChildren(repoPath, fullpath);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getApplication()
	 */
	public MainApplicationWindow getApplication() {
		return application;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#addGroups
	 * (java.util.Set, java.lang.String)
	 */
	public void addGroups(Set<GroupBean> groups, String rights) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: groups=" + groups + " rights=" + rights);
		// Map<String, Rights> groupWithRigths = application.getGroupsTable()
		// .getGroupsWithRights();
		// Rights tmpRights = new Rights(rights);
		Node lastChosenNode = application.getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = application.getRepositories().getChosenRepo();
		Changes changes = changesList.get(svn + ":" + fullpath);
		if (changes == null) {
			changes = createChange(svn, fullpath);
		}

		for (GroupBean u : groups) {
			String name = u.getName();
			if (name.startsWith("@"))
				changes.addGroup(name, rights);
			else
				changes.addUser(name, rights);
		}
		application.getUsersTable().update();
		application.getGroupsTable().update();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#addGroup
	 * (java.lang.String, java.lang.String)
	 */
	public void addGroup(String group, String rights) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: groups=" + group + " rights=" + rights);
		// Map<String, Rights> groupWithRigths = application.getGroupsTable()
		// .getGroupsWithRights();
		// Rights tmpRights = new Rights(rights);
		Node lastChosenNode = application.getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = application.getRepositories().getChosenRepo();
		Changes changes = changesList.get(svn + ":" + fullpath);
		if (changes == null) {
			changes = createChange(svn, fullpath);
		}

		if (group.startsWith("@"))
			changes.addGroup(group, rights);

		application.getUsersTable().update();
		application.getGroupsTable().update();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#addUsers
	 * (java.util.Set, java.lang.String)
	 */
	public void addUsers(Set<String> users, String rights) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: groups=" + users + " rights=" + rights);
		// Map<String, Rights> userWithRigths = application.getUsersTable()
		// .getUsersWithRights();
		// Rights tmpRights = new Rights(rights);
		Node lastChosenNode = application.getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = application.getRepositories().getChosenRepo();
		Changes changes = changesList.get(svn + ":" + fullpath);
		if (changes == null) {
			changes = createChange(svn, fullpath);
		}

		for (String u : users) {
			changes.addUser(u, rights);
		}
		application.getUsersTable().update();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#removeSelected
	 * (java.util.Set, java.util.Set)
	 */
	public void removeSelected(Set<String> groups, Set<String> users) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: groups=" + users + " users=" + users);
		Node lastChosenNode = application.getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = application.getRepositories().getChosenRepo();
		Changes changes = changesList.get(svn + ":" + fullpath);
		if (changes == null) {
			changes = createChange(svn, fullpath);

		}
		for (String g : groups) {
			changes.removeGroup(g, "");
		}
		if (groups.size() > 0)
			application.getGroupsTable().update();
		for (String u : users) {
			changes.removeUser(u, "");
		}
		if (users.size() > 0)
			application.getUsersTable().update();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#removeAll
	 * (java.util.List, java.util.List)
	 */
	public void removeAll(List<String> groups, List<String> users) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: groups=" + users + " rights=" + users);

		Node lastChosenNode = application.getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = application.getRepositories().getChosenRepo();
		Changes changes = changesList.get(svn + ":" + fullpath);
		if (changes == null) {
			changes = createChange(svn, fullpath);

		}
		application.getInheritance().setValue(false);
		for (String g : groups) {
			changes.removeGroup(g, "");
		}
		application.getGroupsTable().update();
		for (String u : users) {
			changes.removeUser(u, "");
		}
		application.getUsersTable().update();

		if (lastChosenNode.isInheritance())
			changes.reverseInheritance();

		application.getInheritance().update();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#updateGroup
	 * (java.lang.String, java.lang.String)
	 */
	public void updateGroup(String group, String changedRights) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: group=" + group + " changedRights");

		Node lastChosenNode = application.getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = application.getRepositories().getChosenRepo();
		Changes changes = changesList.get(svn + ":" + fullpath);

		if (changes == null) {
			changes = createChange(svn, fullpath);
		}

		changes.updateGroup(group, changedRights);

		Rights groupRights = application.getGroupsTable().getGroupsWithRights()
				.get(group);
		if (groupRights != null) {
			Rights newRights = new Rights("");
			newRights.setRead(groupRights.isRead());
			newRights.setWrite(groupRights.isWrite());
			newRights.setModify(groupRights.isModify());
			Change change = changes.getGroup(group);

			// every right means change so it will negate the current right
			if (change != null) {
				logger.info("Logged user : " + this.user.getUserName()
						+ " Changed group is in changes");
				if (change.getRights().isRead()) {
					newRights.setRead(!newRights.isRead());
				}
				if (change.getRights().isWrite()) {
					newRights.setWrite(!newRights.isWrite());
				}
				if (change.getRights().isModify()) {
					newRights.setModify(!newRights.isModify());
				}
			}
			logger.info("Logged user : " + this.user.getUserName()
					+ " Changed group is not in changes");
			// check if changes lead to group has no rights, so it will be
			// removed
			// if (!newRights.isRead() && !newRights.isWrite()
			// && !newRights.isModify()) {
			// changes.removeGroup(group, "");
			// }
		}
		application.getGroupsTable().update();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#updateUser
	 * (java.lang.String, java.lang.String)
	 */
	public void updateUser(String user, String changedRights) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: user=" + user + " changedRights");

		Node lastChosenNode = application.getDirectories()
				.getLastChosenItemId();
		final String fullpath = lastChosenNode.getFullPath();
		String svn = application.getRepositories().getChosenRepo();

		Changes changes = changesList.get(svn + ":" + fullpath);

		if (changes == null) {
			changes = createChange(svn, fullpath);
		}

		changes.updateUser(user, changedRights);

		Rights userRights = application.getUsersTable().getUsersWithRights()
				.get(user);

		if (userRights != null) {
			Rights newRights = new Rights("");
			newRights.setRead(userRights.isRead());
			newRights.setWrite(userRights.isWrite());
			newRights.setModify(userRights.isModify());
			Change change = changes.getUser(user);
			// every right means change so it will negate the current right
			if (change != null) {
				logger.info("Logged user : " + this.user.getUserName()
						+ " Changed user is in changes");
				if (change.getRights().isRead()) {
					newRights.setRead(!newRights.isRead());
				}
				if (change.getRights().isWrite()) {
					newRights.setWrite(!newRights.isWrite());
				}
				if (change.getRights().isModify()) {
					newRights.setModify(!newRights.isModify());
				}
			}
			logger.info("Logged user : " + this.user.getUserName()
					+ " Changed user is not in changes");
			// check if changes lead to user has no rights, so it will be
			// removed
			// if (!newRights.isRead() && !newRights.isWrite()
			// && !newRights.isModify()) {
			// changes.removeUser(user, "");
			// }
		}

		application.getUsersTable().update();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#checkAdmin()
	 */
	public void checkAdmin() {
		logger.info("Logged user : " + this.user.getUserName() + " Check admin");
		user.setAdmin(isAdmin(user));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#getUsersWithRights
	 * (java.lang.String, java.lang.String)
	 */
	public Map<String, Rights> getUsersWithRights(String repoPath,
			String fullpath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repoPath=" + repoPath + " fullpath=" + fullpath);
		return information.getUsersWithRights(repoPath, fullpath);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#getGroupsWithRights
	 * (java.lang.String, java.lang.String)
	 */
	public Map<String, Rights> getGroupsWithRights(String repoPath,
			String fullpath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repoPath=" + repoPath + " fullpath=" + fullpath);
		return information.getGroupsWithRights(repoPath, fullpath);
	}

	/**
	 * Check whether the user is admin or not.
	 *
	 * @param user the checked user
	 *
	 * @return true if user is admin otherwise false
	 *
	 */
	private boolean isAdmin(User user) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: user=" + user);
		List<String> a = Arrays.asList(((String) (Constants.getProperties()
				.get("tcs"))).split(";"));
		return a.contains(this.user.getUserName());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#removeAllChanges()
	 */
	public void removeAllChanges() {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Remove all changes");
		// this.changes = new Changes();
		this.changesList = new HashMap<String, Changes>();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#pathEdited
	 * (java.lang.String)
	 */
	public String pathEdited(String path) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: path=" + path);

		return application.getDirectories().expandPath(path);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#pathExist
	 * (java.lang.String, java.lang.String)
	 */
	public boolean pathExist(String repoPath, String fullpath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: repoPath=" + repoPath + " fullpath=" + fullpath);
		return information.isPathExist(repoPath, fullpath);
	}

	/**
	 * Checks whether the dir on the given path is valid.
	 *
	 * @param svn the svn where the dir is located
	 * @param path the path to the checked dir
	 *
	 * @return true if the dir is valid otherwise false
	 *
	 */
	private boolean isDirectoryValid(String svn, String path) {
		boolean isValid = true;
		Changes changes = changesList.get(svn + ":" + path);
		List<Node> oldList;
		if (changes != null) {
			oldList = changes.getNodePath();
		} else {
			oldList = this.getApplication().getDirectories().getNodePath();
		}
		Node n = information.getTrees().get(svn).getNode(path);

		int i = 0;
		while (n != null) {
			if (!isValid)
				break;
			long loadTime = oldList.get(i).getTimestamp();
			long actualTime = n.getTimestamp();

			logger.info("node load time: " + oldList.get(i).getFullPath()
					+ ": " + loadTime);
			logger.info("node actual time: " + n.getFullPath() + ": "
					+ actualTime);
			if (actualTime != loadTime) {
				isValid = false;
			}
			n = n.getParent();
			i++;
		}
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#isDirectoryValid
	 * (java.util.List)
	 */
	public boolean isDirectoryValid(List<Node> nodePath) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: nodePath=" + nodePath);
		Node lastChosenNode = application.getDirectories()
				.getLastChosenItemId();
		String fullpath = lastChosenNode.getFullPath();
		String svn = application.getRepositories().getChosenRepo();
		boolean isValid = true;

		// from the access tree
		Node n = information.getTrees().get(svn).getNode(fullpath);

		int i = 0;
		while (n != null) {
			if (!isValid)
				break;
			logger.info(" - " + n.getName() + " " + n.getTimestamp());

			logger.info(" -- "
					+ application.getDirectories().getNodePath().get(i)
					+ " "
					+ application.getDirectories().getNodePath().get(i)
							.getTimestamp());
			if (n.getTimestamp() != application.getDirectories().getNodePath()
					.get(i).getTimestamp()) {
				isValid = false;
			}
			n = n.getParent();
			i++;
		}
		return isValid;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#updateReposAndTree
	 * (java.util.List, java.lang.String, java.util.List)
	 */
	public void updateReposAndTree(List<Node> repositories, String currentPath,
			Repositories repositories2) {
		logger.info("Logged user : " + this.user.getUserName()
				+ " Parameters: control=" + thisControl + " repositories="
				+ repositories + "currentPath=" + currentPath + " ");
		// removeAllChanges();
		application.getRepositories().updateRepositories(repositories);
		application.getDirectories().setRoot(repositories2.getChosenRepoNode());
		refreshDataPanel();

		application.getDirectories().expandPath(
				((FavoriteBean) application.getCurrentPath().getValue())
						.getFullpath());
		// directorySelected(repositories2.getChosenRepo(), application
		// .getDirectories().getLastChosenItemId().getFullPath());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#cleanChanges
	 * (java.lang.String)
	 */
	public void cleanChanges(String s) {
		Changes ch = changesList.get(s);
		if (ch != null && !ch.isChange()) {
			changesList.remove(s);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#createChange
	 * (java.lang.String, java.lang.String)
	 */
	public Changes createChange(String svn, String fullpath) {
		Changes ch = new Changes();
		ch.setNodePath(application.getDirectories().getNodePath());
		changesList.put(svn + ":" + fullpath, ch);
		return ch;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#lockNode
	 * (java.lang.String, java.lang.String)
	 */
	public synchronized void lockNode(final String svn, final String fullPath)
			throws Exception {
		logger.info("Locking node svn: " + svn + ", fullpath: " + fullPath
				+ ", user: " + user.getUserName());
		if (isDirectoryValid(svn, fullPath)) {

			if (information.lockNode(svn, fullPath, user)) {
				changesList.remove(svn + ":" + fullPath);
				String returnString = information.saveAccessList();

				if (returnString != null) {
					aclLogger.error("Error during saving ACL file");
					application
							.showNotification("Error during saving ACL file");
				}

				application.getRepositories().updateRepositories(
						getRepositories());

				application.getDirectories().setRoot(
						application.getRepositories().getChosenRepoNode());

				refreshDataPanel();
				application.getDirectories().expandPath(fullPath);
			} else {
				application
						.showNotification("Some of the subdirectories are already locked.");
			}
		} else {
			timestampNotValidNotification(svn, fullPath);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#setResponsiblePersons
	 * (java.util.Map)
	 */
	public void setResponsiblePersons(Map<String, String> data) {
		for (String svn : data.keySet()) {
			String person = data.get(svn);
			logger.info("set the responsible person: " + person + " to svn: "
					+ svn);
			information.setResponsiblePerson(svn, person);
		}
		this.getApplication().getRepositories()
				.updateRepositories(getRepositories());
		information.saveAccessList();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#
	 * timestampNotValidNotification(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("serial")
	public void timestampNotValidNotification(final String svn,
			final String fullPath) {
		logger.info("Timestamp is not valid");
		final ModalConfirmationDialog cDialog = new ModalConfirmationDialog(
				I18NSupport.getMessage(Control.OUT_OF_DATE_HEADER),
				I18NSupport.getMessage(Control.DATA_ARE_OUT_OF_DATE_TEXT));
		cDialog.getOk().focus();
		cDialog.getOk().addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				getChanges().remove(svn + ":" + fullPath);
				Repositories rep = thisControl.getApplication()
						.getRepositories();
				updateReposAndTree(getRepositories(), fullPath, rep);

				application.removeWindow(cDialog.getSubwindow());
			}
		});

		cDialog.getCancel().addListener(new ClickListener() {

			public void buttonClick(ClickEvent event) {
				application.removeWindow(cDialog.getSubwindow());
			}
		});

		application.getWindow().addWindow(cDialog.getSubwindow());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#unlockNode
	 * (java.lang.String, java.lang.String)
	 */
	public synchronized void unlockNode(String svn, String fullPath) {
		logger.info("Unlocking node svn: " + svn + ", fullpath: " + fullPath
				+ ", user: " + user.getUserName());
		if (isDirectoryValid(svn, fullPath)) {
			if (information.unlockNode(svn, fullPath, user)) {
				String returnString = information.saveAccessList();

				if (returnString != null) {
					aclLogger.error("Error during saving ACL file");
					application
							.showNotification("Error during saving ACL file");
				}

				application.getRepositories().updateRepositories(
						getRepositories());

				application.getDirectories().setRoot(
						application.getRepositories().getChosenRepoNode());

				refreshDataPanel();
				application.getDirectories().expandPath(fullPath);
			} else {
				application
						.showNotification("You have no permission to unlock the directory.");
			}
		} else {
			timestampNotValidNotification(svn, fullPath);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#unlockNodes
	 * (java.util.List)
	 */
	public synchronized void unlockNodes(List<LockBean> list) {

		for (LockBean bean : list) {
			logger.info("Unlocking node svn: " + bean.getSvn() + ", fullpath: "
					+ bean.getFullpath() + ", lockedBy: " + bean.getLockedBy());

			if (!information
					.unlockNode(bean.getSvn(), bean.getFullpath(), user)) {
				timestampNotValidNotification(bean.getSvn(), bean.getFullpath());
			}

		}

		String returnString = information.saveAccessList();

		if (returnString != null) {
			aclLogger.error("Error during saving ACL file");
			application.showNotification("Error during saving ACL file");
		}

		application.getRepositories().updateRepositories(getRepositories());

		application.getDirectories().setRoot(
				application.getRepositories().getChosenRepoNode());

		refreshDataPanel();
		application.getDirectories().expandPath(
				((FavoriteBean) this.getApplication().getCurrentPath()
						.getValue()).getFullpath());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getLockedPaths()
	 */
	public List<LockBean> getLockedPaths() {
		return information.getLockedPaths();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.bussiness.IControl#canModifyCheckBoxes
	 * (java.lang.String, java.lang.String,
	 * com.gk_software.tools.svnaccess.bussiness.impl.User)
	 */
	public boolean canModifyCheckBoxes(String repoPath, String fullpath, User user) {
		return information.canModifyCheckBoxes(repoPath, fullpath, user);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#cleanAccessList
	 * (java.util.List, int, boolean)
	 */
	public void cleanAccessList(List<CleanBean> list, int depth, boolean checkOnSvn) {
		logger.info("Start of cleaning the accessList");
		for (CleanBean c : list) {
			logger.info("Cleaning repository: " + c.getSVN());
			information.cleanAccessList(c.getSVN(), depth, checkOnSvn);
		}
		logger.info("End of cleaning the accessList");

		Repositories rep = thisControl.getApplication().getRepositories();
		String path = ((FavoriteBean) getApplication().getCurrentPath()
				.getValue()).getFullpath();

		updateReposAndTree(getRepositories(), path, rep);

	}

	/**
	 * Refreshes the data panel.
	 */
	public void refreshDataPanel() {
		application.getUsersTable().update();
		application.getGroupsTable().update();
		application.getInheritance().update();

		application.getDirectories().scrollToItem(
				(Node) application.getDirectories().getValue());
		if (changesList.size() != 0 || saved) {
			application.getDirectories().refreshRowCache();
			saved = false;
		}
		// System.out.println(application.getDirectories().getValue());

		// application.getDirectories().refreshRowCache();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.bussiness.IControl#getRightsInheritanceFree
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	public String getRightsInheritanceFree(String group, String svn, String fullpath) {
		return information.getRightsInheritanceFree(group, svn, fullpath, null);
	}

}
