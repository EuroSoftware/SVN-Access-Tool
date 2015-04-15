package com.gk_software.tools.svnaccess.data;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gk_software.tools.svnaccess.data.accessList.Rights;

/**
 * LdapProvider interface. It contains methods for getting the information from the
 * LDAP.
 */
public interface ILdapProvider {

	/**
	 * Returns all the groups from LDAP
	 *
	 * @return the list of all the groups from LDAP
	 *
	 */
	public List<String> getGroups();

	/**
	 * Returns all the users from LDAP
	 *
	 * @return the list of all the users from LDAP
	 *
	 */
	public List<String> getUsers();

	/**
	 * Returns a map with group as a key and set of its users as value.
	 *
	 * @return a map with group as a key and set of its users as value
	 *
	 */
	public Map<String, Set<String>> getGroupsUsers();

	/**
	 * Returns a list of groups that the user belongs to.
	 *
	 * @param user the user whose groups are searched
	 *
	 * @return a list of groups that the user belongs to
	 *
	 */
	public List<String> getGroupsForUser(String user);

	/**
	 * Returns a list of groups that the user belongs to including the parent groups.
	 *
	 * @param user the user whose groups are searched
	 *
	 * @return a list of groups that the user belongs to
	 *
	 */
	public Set<String> getGroupsForUserWithParentGroups(String userName);

	/**
	 * Saves the groups and correspondent users to the ACL.
	 *
	 * @return the saved string
	 *
	 */
	public String saveToACL(Set<String> set);

	/**
	 * Wakes up a sleeping LDAP reader thread and reloads the data.
	 */
	public void reloadData();

	/**
	 * Returns a list of groups that the user belongs to including the filtered
	 * parent groups.
	 *
	 * @param user the user whose groups are searched
	 *
	 * @return a list of groups that the user belongs to
	 *
	 */
	public Set<String> getGroupsForUserWithParentGroupsFiltered(String userName,
			Map<String, Rights> groupsNode);

	/**
	 * Returns a list of users of the given group.
	 *
	 * @param gr the group whose users are searched
	 *
	 * @return a list of users of the given group
	 *
	 */
	public Set<String> getUsersForGroup(String gr);
}
