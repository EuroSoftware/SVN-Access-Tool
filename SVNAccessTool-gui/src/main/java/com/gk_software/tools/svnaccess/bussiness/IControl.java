package com.gk_software.tools.svnaccess.bussiness;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.bussiness.impl.User;
import com.gk_software.tools.svnaccess.data.accessList.CleanBean;
import com.gk_software.tools.svnaccess.data.accessList.LockBean;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.Rights;
import com.gk_software.tools.svnaccess.main.MainApplicationWindow;
import com.gk_software.tools.svnaccess.view.components.Repositories;
import com.gk_software.tools.svnaccess.view.components.GroupsTree.GroupBean;

/**
 * Main control class providing methods for svn manipulation and app settings.
 */
public interface IControl {

	/**
	 * Set the path from cookies.
	 */
	public void setPathFromCookies();

	/**
	 * Searches for the node in the tree on the given path.
	 *
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return found node
	 *
	 */
	public Node getNode(String svn, String fullpath);

	/**
	 * Provides all available repositories.
	 *
	 * @return list of repositories
	 *
	 */
	public List<Node> getRepositories();

	/**
	 * Get application context.
	 *
	 * @return instance of application window
	 *
	 */
	public MainApplicationWindow getApplication();

	/**
	 * Repository was selected.
	 *
	 * @param fullpath the path to repository
	 *
	 */
	public void repositorySelected(String fullpath);

	/**
	 * Directory from repository tree was selected (user clicked on directory).
	 * It is neccessary to load users and group rights.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the relative path in the repository
	 *
	 */
	public void directorySelected(String repoPath, String fullpath);

	/**
	 * Node in directory tree was expanded. It is necessary to load its children
	 * and add it to the tree.
	 *
	 * @param repoPath the path to the repository
	 * @param parent the node which is expanded
	 *
	 */
	public void directoryExpanded(String repoPath, Node parent);

	/**
	 * This method saves all changes (changed objects) for the selected directory to
	 * the ACL tree and generates an ACL list.
	 *
	 * @throws Exception saving failed
	 *
	 */
	public void saveChanges() throws Exception;

	/**
	 * Removes selected users and groups from the selected directory. Aka add
	 * selected groups and users to changed objects and sets the modification
	 * action to REMOVE.
	 *
	 * @param groups the list of groups
	 * @param users the list of users
	 *
	 */
	public void removeSelected(Set<String> groups, Set<String> users);

	/**
	 * Removes all users and groups from the selected directory. Aka add all groups
	 * and all users to changed objects and sets the modification action to REMOVE.
	 *
	 * @param groups the list of groups
	 * @param users the list of users
	 *
	 */
	public void removeAll(List<String> groups, List<String> users);

	/**
	 * Updates rights of the given group to the appropriate directory. It updates
	 * changed objects and correctly marks the group.
	 *
	 * @param group the name of the group
	 * @param changedRights 'r','w' or 'm' and their combination
	 *
	 */
	public void updateGroup(String group, String changedRights);

	/**
	 * Updates rights of the given user to the appropriate directory. It updates
	 * changed objects and correctly marks the user.
	 *
	 * @param user the name of user.
	 * @param changedRights 'r','w' or 'm' and their combination
	 *
	 */
	public void updateUser(String user, String changedRights);

	/**
	 * Updates practically the whole screen with new information - repositories,
	 * directories.
	 *
	 * @param repositories the list of repositories
	 * @param currentPath the current relative path in the repository
	 * @param repoPath the path to the repository
	 *
	 */
	public void updateReposAndTree(List<Node> repositories, String currentPath,
			Repositories repositories2);

	/**
	 * Adds group/s to the selected directory. Method adds groups and their rights
	 * to changed objects and sets the modification action to ADD.
	 *
	 * @param set the set of group names
	 * @param rights the rights for every item of the set
	 *
	 */
	public void addGroups(Set<GroupBean> set, String rights);

	/**
	 * Adds user/s to the selected directory. Method adds users and their rights
	 * to changed objects and sets the modification action to ADD.
	 *
	 * @param value the set of user names
	 * @param rights the rights for every item of the set
	 *
	 */
	public void addUsers(Set<String> value, String rights);

	/**
	 * Gets all users from LDAP, admins and users that already have rights are
	 * filtered from the list.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return the list of filtered users
	 *
	 */
	public List<String> getUsersFiltered(String repoPath, String fullpath);

	/**
	 * Gets all groups from LDAP, groups that already have rights are filtered
	 * from the list.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return the list of filtered groups
	 *
	 */
	public List<String> getGroupsFiltered(String repoPath, String fullpath);

	/**
	 * Gets all users from LDAP for admin settings in the window settings without
	 * filtering.
	 *
	 * @return the list of all users
	 *
	 */
	public List<String> getUsersAll();

	/**
	 * Gets all groups from LDAP for admin settings in the window settings without
	 * filtering.
	 *
	 * @return the list of all groups
	 *
	 */
	public List<String> getGroupsAll();

	/**
	 * Finds whether or not the directory has children (sub directories).
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return true/false depending on the result of the search
	 *
	 */
	public boolean hasSVNChildren(String repoPath, String fullpath);

	/**
	 * Reloads SVN repositories when the repo URL is changed (in the settings window).
	 */
	public void reloadSVN();

	/**
	 * Reloads ACL tree when the URL to ACL list is changed (in the settings window).
	 *
	 * @throws Exception reloading failed
	 */
	public void reloadACL() throws Exception;

	/**
	 * Reloads data from LDAP
	 */
	public void reloadLdap();

	/**
	 * Returns a list of nodes(directories) for the directory on the given path.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return a list of nodes(directories) for the directory on the given path
	 *
	 */
	public List<Node> getChildren(String repoPath, String fullpath);

	/**
	 * Returns a map with user name as key and rights as value for the given directory.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return a map with user name as key and rights as value for the given directory
	 *
	 */
	public Map<String, Rights> getUsersWithRights(String repoPath, String fullpath);

	/**
	 * Returns a map with group name as key and rights as value for the given directory.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return a map with group name as key and rights as value for the given directory
	 *
	 */
	public Map<String, Rights> getGroupsWithRights(String repoPath, String fullpath);

	/**
	 * Checks if the user is admin or not.
	 *
	 * @return true for admin otherwise false
	 */
	public void checkAdmin();

	/**
	 * Returns the logged user.
	 *
	 * @return the logged user
	 *
	 */
	public User getUser();

	/**
	 * Returns the changes for the selected node.
	 *
	 * @return the changes for the selected node
	 */
	public Map<String, Changes> getChanges();

	/**
	 * Removes all changes from Change object. It is used when a new directory or
	 * repository is selected.
	 */
	public void removeAllChanges();

	/**
	 * Current path in textField is changed. So we have to expand the
	 * right path in the tree.
	 *
	 * @param path the new path
	 *
	 * @return null if everything is OK, errorMessage if there were some errors
	 */
	public String pathEdited(String path);

	/**
	 * Checks if the given path in the repository exists. It is used in pathEdited and
	 * Directories.expandPath.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return true if path exists otherwise false
	 *
	 */
	public boolean pathExist(String repoPath, String fullpath);

	/**
	 * Checks if the timestamp of the directory is valid
	 *
	 * @param nodePath the path to the node in the repository
	 *
	 * @return true if valid otherwise false
	 *
	 */
	public boolean isDirectoryValid(List<Node> nodePath);

	/**
	 * Returns a list of users for the given group from LDAP.
	 *
	 * @param group the group whose users are returned
	 *
	 * @return a list of users for the given group from LDAP
	 *
	 */
	public List<String> getUsersForGroup(String group);

	/**
	 * Creates a node containing the following changes: inheritance check
	 * box, user rights or group rights.
	 *
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return the created node
	 *
	 */
	public Changes createChange(String svn, String fullpath);

	/**
	 * Cleans all changes for the given directory.
	 *
	 * @param s path to directory given in format : "svn:path"
	 */
	public void cleanChanges(String s);

	/**
	 * Locks the selected node and all its children.
	 *
	 * @param chosenRepo the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @throws Exception locking failed
	 *
	 */
	public void lockNode(String chosenRepo, String fullPath) throws Exception;

	/**
	 * Unlocks the selected node and all its children.
	 *
	 * @param chosenRepo the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 */
	public void unlockNode(String chosenRepo, String fullPath);

	/**
	 * Unlocks all nodes from LOCKS.
	 *
	 * @param list the list of nodes to be unlocked
	 *
	 */
	public void unlockNodes(List<LockBean> list);

	/**
	 * Shows notification that timestamp is not valid and ask user if he wants
	 * to reload it. Also contains button listeners that call the reload methods.
	 *
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 */
	public void timestampNotValidNotification(final String svn,
			final String fullPath);

	/**
	 * Returns all the locked paths.
	 *
	 * @return the list the of locked paths
	 *
	 */
	public List<LockBean> getLockedPaths();

	/**
	 * Sets the responsible person for the svns given in the map.
	 *
	 * @param hm the map having svn as key and responsible person as value
	 *
	 */
	public void setResponsiblePersons(Map<String, String> hm);

	/**
	 * Checks whether the given user can modify the checkboxes for the given directory.
	 *
	 * @param svn the path to the repository
	 * @param string the path to the node in the repository
	 * @param user the user whose rights are checked
	 *
	 * @return true if the given user has modify rights otherwise false
	 *
	 */
	public boolean canModifyCheckBoxes(String svn, String string, User user);

	/**
	 * Cleans the access list according to the given list and depth. If checkOnSvn is
	 * true it also checks for duplications on the SVNs.
	 *
	 * @param list the list of SVNs to be cleaned
	 * @param depth the depth to which the cleaning goes
	 * @param checkOnSvn indicator whether to check the SVN
	 *
	 */
	public void cleanAccessList(List<CleanBean> list, int depth, boolean checkOnSvn);

	/**
	 * Returns the rights, free of inheritance, for the given group, svn and node.
	 *
	 * @param group the group whose rights are searched
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return the rights, free of inheritance, for the given group, svn and node
	 *
	 */
	public String getRightsInheritanceFree(String group, String svn, String fullpath);

	/**
	 * Adds a given group with the given rights.
	 *
	 * @param group the added group
	 * @param rightsInheritanceFree the rights of the added group
	 *
	 */
	public void addGroup(String group, String rightsInheritanceFree);
}
