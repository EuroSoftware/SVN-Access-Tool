package com.gk_software.tools.svnaccess.data;

import java.util.List;
import java.util.Map;

import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.bussiness.impl.User;
import com.gk_software.tools.svnaccess.data.accessList.AccessTree;
import com.gk_software.tools.svnaccess.data.accessList.LockBean;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.Rights;

/**
 * Interface containing methods for providing the low level data to the higher
 * application layer.
 */
public interface IViewInformation {

	/**
	 * Returns a list of the SVN repositories. It reads the SVNs from SVN and for every
	 * SVN searches rights in the access tree. If the SVN does not have any rights in
	 * the access tree a new tree is created and added to the tree list.
	 *
	 * @return a list of the SVN repositories
	 */
	public List<Node> getRepositories();

	/**
	 * Returns all access trees.
	 *
	 * @return a map with SVN name as key and its access tree as value
	 */
	public Map<String, AccessTree> getTrees();

	/**
	 * Returns a list of all the nodes on the given path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return a list of all the nodes on the given path and SVN
	 *
	 * @throws Exception node retrieval failed
	 *
	 */
	public List<Node> getChildren(String repoPath, String fullpath);

	/**
	 * Checks whether the node on the given path and SVN exists.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return true if the node exists otherwise false
	 *
	 */
	public boolean isPathExist(String repoPath, String fullpath);

	/**
	 * Returns a map with users as key and their rights as value for the node on the
	 * given path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return a map with users as key and their rights as value
	 *
	 */
	public Map<String, Rights> getUsersWithRights(String repoPath, String fullpath);

	/**
	 * Returns a map with inherited users/groups as key and their rights as value
	 * for the node on the given path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return a map with inherited users/groups as key and their rights as value
	 *
	 */
	public Map<String, Rights> getInehritedRights(String repoPath, String fullpath);

	/**
	 * Returns a map with groups as key and their rights as value for the node on the
	 * given path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return a map with groups as key and their rights as value
	 *
	 */
	public Map<String, Rights> getGroupsWithRights(String repoPath,
			String fullpath);

	/**
	 * Checks whether the inheritance flag is set or not for the node on the given
	 * path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return true if the flag is set otherwise false
	 *
	 */
	public boolean isInheritance(String repoPath, String fullpath);

	/**
	 * Returns a list of all the users from LDAP.
	 *
	 * @return a list of all the users from LDAP
	 */
	public List<String> getUsers();

	/**
	 * Returns a list of all the groups from LDAP.
	 *
	 * @return a list of all the groups from LDAP
	 */
	public List<String> getGroups();

	/**
	 * Saves the access list to a file
	 */
	public String saveAccessList();

	/**
	 * Checks if the node on the given path and SVN has children.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return true if the node has children otherwise false
	 *
	 */
	public boolean hasSVNChildren(String repoPath, String fullpath);

	/**
	 * Reloads the SVN. This is used when the path in settings is changed.
	 */
	public void reloadSVN();

	/**
	 * Reloads the ACL.
	 *
	 * @throws Exception reloading failed
	 */
	public void reloadACL() throws Exception;

	/**
	 * Updates rights for the node on the given path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param changes the changes in the rights
	 */
	public void updateRights(String repoPath, String fullpath, Changes changes);

	/**
	 * Checks if the logged user can change rights the node on the given path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param user the user whose rights are checked
	 *
	 * @return true if the user can change rights otherwise false
	 *
	 */
	public boolean canModifyCheckBoxes(String repoPath, String fullpath, User user);

	/**
	 * Checks if the logged user can change rights the node on the given path and SVN
	 * from the LockedNode.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param user the user whose rights are checked
	 *
	 * @return true if the user can change rights otherwise false
	 *
	 */
	public boolean canModifyCheckBoxesFromRoot(String repoPath, String fullpath,
			User user);

	/**
	 * Reloads data from LDAP.
	 */
	public void reloadLdap();

	/**
	 * Returns a list of users for the given group from LDAP.
	 *
	 * @param group group whose users are searched
	 * @return a list of users for the given group
	 */
	public List<String> getUsersForGroup(String group);

	/**
	 * Checks if the logged user has write flag for the node on the given path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param user the user whose rights are checked
	 *
	 * @return true if the user has write flag otherwise false
	 *
	 */
	public boolean hasWriteRights(String repoPath, String fullpath, User user);

	/**
	 * Locks the node on the given path and SVN and all its subnodes.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param user the user who locked the node
	 *
	 * @return true if the node has been locked otherwise false
	 *
	 * @throws Exception locking failed
	 *
	 */
	public boolean lockNode(String repoPath, String fullPath, User user)
			throws Exception;

	/**
	 * Unlocks the node on the given path and SVN and all its subnodes.
	 *
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param user the user who unlocked the node
	 *
	 * @return true if the node has been unlocked otherwise false
	 *
	 */
	public boolean unlockNode(String svn, String fullPath, User user);

	/**
	 * Returns the list of all the locked nodes.
	 *
	 * @return the list of all the locked nodes
	 */
	public List<LockBean> getLockedPaths();

	/**
	 * Returns the modification rights inherited from the upper directories for the node
	 * on the given path and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return the modification rights
	 *
	 */
	public Map<String, Rights> getModificationInheritedRights(String repoPath,
			String fullpath);

	/**
	 * Sets the given responsible person to the given SVN
	 *
	 * @param svn the SVN to which is the responsible person set
	 * @param responsiblePerson the responsible person to be set to the SVN
	 *
	 */
	public void setResponsiblePerson(String svn, String responsiblePerson);

	/**
	 * Cleans the access list according to the given list and depth. If checkOnSvn is
	 * true it also checks for duplications on the SVNs.
	 *
	 * @param list the list of SVNs to be cleaned
	 * @param depth the depth to which the cleaning goes
	 * @param checkOnSvn indicator whether to check the SVN
	 *
	 */
	public void cleanAccessList(String svn, int depth, boolean checkOnSvn);

	/**
	 * Adds a new mail to the mails to be send with the error message composed from
	 * the given parameters.
	 *
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param changes the changes made which caused the erorr
	 * @param user the user who made the changes
	 * @param reason the reason of the erorr
	 *
	 * @throws Exception adding failed
	 *
	 */
	public void addMail(String svn, String fullpath, Changes changes, User user,
			String reason) throws Exception;

	/**
	 * Returns the rights, free of inheritance, for the given group, svn and node.
	 *
	 * @param group the group whose rights are searched
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param node the instance of the node
	 *
	 * @return the rights, free of inheritance, for the given group, svn and node
	 *
	 */
	public String getRightsInheritanceFree(String group, String svn, String fullpath,
			Node node);

	/**
	 * Stops the mail checker thread.
	 *
	 * @throws Exception stoping failed
	 *
	 */
	public void stopMailChecker() throws Exception;

	/**
	 * Starts the mail checker thread.
	 *
	 * @throws Exception starting failed
	 *
	 */
	public void startMailChecker() throws Exception;
}
