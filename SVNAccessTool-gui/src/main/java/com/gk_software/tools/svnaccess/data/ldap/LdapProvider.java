package com.gk_software.tools.svnaccess.data.ldap;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.data.ILdapProvider;
import com.gk_software.tools.svnaccess.data.ILdapReader;
import com.gk_software.tools.svnaccess.data.accessList.Rights;

/**
 * Implements the methods from the interface {@code ILdapProvider}.
 */
public class LdapProvider implements ILdapProvider {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(LdapProvider.class);

	/** The list of users. */
	private Set<String> users;

	/** The list of groups. */
	private Set<String> groups;

	/** The map with group as key and its users as value. */
	private Map<String, Set<String>> groupsUsers;

	/** The instance of this class. */
	private static LdapProvider ldapProvider;

	/** The reader of LDAP. */
	private ILdapReader ldapReader;

	/** The indicator if this thread is sleeping. */
	private boolean isSleeping;

	/** The map with parent as key and its child groups as value. */
	private HashMap<String, Set<String>> parentGroups;

	/**
	 * Creates a new instance and initializes the variables.
	 */
	private LdapProvider() {
		users = new HashSet<String>();
		groups = new HashSet<String>();
		groupsUsers = new HashMap<String, Set<String>>();
		parentGroups = new HashMap<String, Set<String>>();
		this.ldapReader = new LDAPAccess(this);
		logger.info("Starting Ldap reader thread");
		this.ldapReader.start();

	}

	/**
	 * Singleton class. This method provides only one instance of this class.
	 *
	 * @return the instance of this class
	 *
	 * @throws Exception instance retrieval failed
	 *
	 */
	public static LdapProvider getInstance() {
		if (ldapProvider == null) {
			ldapProvider = new LdapProvider();
		}
		return ldapProvider;
	}

	/**
	 * Returns the indicator if this thread is sleeping.
	 *
	 * @return the indicator if this thread is sleeping
	 *
	 */
	public synchronized boolean isSleeping() {
		return isSleeping;
	}

	/**
	 * Sets the new list of users.
	 *
	 * @param users the new list of users
	 *
	 */
	public void setUsers(Set<String> users) {
		this.users = users;
	}

	/**
	 * Sets the new list of groups.
	 *
	 * @param groups the new list of groups
	 *
	 */
	public void setGroups(Set<String> groups) {
		this.groups = groups;
	}

	/**
	 * Sets the new map with group as key and its users as as value.
	 *
	 * @param groupsUsers the new map with group as key and its users as as value
	 *
	 */
	public void setGroupsUsers(Map<String, Set<String>> groupsUsers) {
		this.groupsUsers = groupsUsers;
	}

	/**
	 * Sets the new map with parent as key and its child groups as value.
	 *
	 * @param parentGroups the new map with parent as key and its child groups as value
	 *
	 */
	public void setParentGroups(HashMap<String, Set<String>> parentGroups) {
		this.parentGroups = parentGroups;
	}

	/**
	 * Sets the new reader of LDAP.
	 *
	 * @param ldapReader the new reader of LDAP
	 *
	 */
	public void setLdapReader(ILdapReader ldapReader) {
		this.ldapReader = ldapReader;
	}

	/**
	 * Sets the new indicator if this thread is sleeping.
	 *
	 * @param isSleeping the new indicator if this thread is sleeping
	 *
	 */
	public synchronized void setSleeping(boolean isSleeping) {
		this.isSleeping = isSleeping;
	}

	/**
	 * Returns the reader of LDAP.
	 *
	 * @return the reader of LDAP
	 *
	 */
	public ILdapReader getLdapReader() {
		return ldapReader;
	}

	/**
	 * Returns the list of users.
	 *
	 * @return the list of users
	 *
	 */
	public Set<String> getUsersForLdap() {
		return users;
	}

	/**
	 * Returns the list of groups.
	 *
	 * @return the list of groups
	 *
	 */
	public Set<String> getGroupsForLdap() {
		return groups;
	}

	/**
	 * Returns the name of the SVN the node belongs to.
	 *
	 * @return the name of the SVN the node belongs to
	 *
	 */
	public Map<String, Set<String>> getGroupsUsersForLdap() {
		return groupsUsers;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.ILdapProvider#getGroups()
	 */
	public List<String> getGroups() {
		if (groups.size() == 0) {
			synchronized (this) {
				try {
					isSleeping = true;
					// System.out.println("provider uspan");
					this.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		ArrayList<String> ar = new ArrayList<String>(groups);
		Collections.sort(ar);
		return ar;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.ILdapProvider#getUsers()
	 */
	public List<String> getUsers() {
		if (groups.size() == 0) {
			synchronized (this) {
				try {
					isSleeping = true;
					this.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		List<String> l = new ArrayList<String>(users);
		Collections.sort(l);
		return l;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.ILdapProvider#getGroupsUsers()
	 */
	public Map<String, Set<String>> getGroupsUsers() {
		if (groupsUsers.size() == 0) {
			synchronized (this) {
				try {
					isSleeping = true;
					this.wait();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		return groupsUsers;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.ILdapProvider#getGroupsForUser
	 * (java.lang.String)
	 */
	public List<String> getGroupsForUser(String user) {
		List<String> list = new ArrayList<String>();
		for (String group : groupsUsers.keySet()) {
			Set<String> s = groupsUsers.get(group);

			if (s.contains(user))
				list.add("@" + group);
		}
		logger.info("User: " + user + " is appearing in a following groups: "
				+ list);
		return list;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.ILdapProvider#
	 * getGroupsForUserWithParentGroups(java.lang.String)
	 */
	public Set<String> getGroupsForUserWithParentGroups(String user) {
		Set<String> list = new HashSet<String>();

		Set<String> parentGroupsP = new HashSet<String>();

		for (String group : groupsUsers.keySet()) {
			Set<String> s = groupsUsers.get(group);

			if (s.contains(user)) {
				list.add("@" + group);
				// System.out.println("base:"+group);
				Set<String> pom = parentGroups.get(group);
				if (pom != null) {
					parentGroupsP.addAll(pom);
					parentGroupsP.add(group);
					// System.out.println(group+" : "+pom);
				}
			}

		}

		while (true) {
			int sizeStart = parentGroupsP.size();
			int i = 0;
			parentGroupsP.addAll(findAllParentGroups(parentGroupsP));
			int sizeEnd = parentGroupsP.size();
			if (sizeStart == sizeEnd)
				break;

		}
		for (String g : parentGroupsP)
			list.add("@" + g);

		logger.info("User: " + user + " is appearing in a following groups: "
				+ list);
		// System.out.println("User: " + user +
		// " is appearing in a following groups: "
		// + list);

		return list;
	}

	/**
	 * Returns the map with parent as key and its child groups as value.
	 *
	 * @return the map with parent as key and its child groups as value
	 *
	 */
	public HashMap<String, Set<String>> getParentGroups() {
		return parentGroups;
	}

	/**
	 * Returns a set of all the child groups of the given set of parents.
	 *
	 * @param parentGroupsP the set of parents whose children are searched
	 *
	 * @return a set of all the child groups of the given set of parents
	 *
	 */
	private List<String> findAllParentGroups(Set<String> parentGroupsP) {
		List<String> p = new ArrayList<String>();

		for (String g : parentGroupsP) {
			Set<String> pom = parentGroups.get(g);
			if (pom != null) {
				p.addAll(pom);
				// System.out.println(g+" :: "+pom);
			}

		}
		return p;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.ILdapProvider#
	 * getGroupsForUserWithParentGroupsFiltered(java.lang.String, java.util.Map)
	 */
	public Set<String> getGroupsForUserWithParentGroupsFiltered(String user,
			Map<String, Rights> groupsNode) {
		Set<String> list = new HashSet<String>();

		Set<String> parentGroupsP = new HashSet<String>();

		for (String group : groupsUsers.keySet()) {
			Set<String> s = groupsUsers.get(group);

			if (s.contains(user)) {
				list.add("@" + group); // user is in the group

				if (!groupsNode.containsKey("@" + group)) {
					Set<String> pom = parentGroups.get(group);

					if (pom != null) {
						parentGroupsP.addAll(pom);
						parentGroupsP.add(group);
						// System.out.println(group+" : "+pom);
					}
				}
			}

		}
		while (true) {
			int sizeStart = parentGroupsP.size();
			int i = 0;
			parentGroupsP.addAll(findAllParentGroupsFiltered(parentGroupsP,
					groupsNode));
			int sizeEnd = parentGroupsP.size();
			if (sizeStart == sizeEnd)
				break;

		}
		for (String g : parentGroupsP)
			list.add("@" + g);

		logger.info("User: " + user + " is appearing in a following groups: "
				+ list);

		return list;
	}

	/**
	 * Returns a set of the child groups of the given set of parents for the given
	 * group map.
	 *
	 * @param parentGroupsP the set of parents whose children are searched
	 * @param groupsNode the map with group as key and its rights as value
	 *
	 * @return a set of the child groups
	 *
	 */
	private List<String> findAllParentGroupsFiltered(Set<String> parentGroupsP,
			Map<String, Rights> groupsNode) {
		List<String> p = new ArrayList<String>();

		for (String g : parentGroupsP) {
			if (!groupsNode.containsKey("@" + g)) {

				Set<String> pom = parentGroups.get(g);
				if (pom != null) {
					p.addAll(pom);
				}
			}

		}
		return p;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.ILdapProvider#saveToACL()
	 */
	public String saveToACL(Set<String> set) {
		if (groupsUsers.size() == 0) {
			return "";
		}

		List<String> keys = new ArrayList<String>(groupsUsers.keySet());

		while (true) {
			int sizeStart = set.size();
			int i = 0;
			findAllGroups(set, keys);
			int sizeEnd = set.size();

			if (sizeStart == sizeEnd)
				break;

		}
		List<String> keys2 = new ArrayList<String>(groups);

		Collections.sort(keys2);
		String content = "[groups]\n";

		// System.out.println(set);
		for (String k : keys2) {
			if (set.contains("@" + k)) {
				content += k + " = ";
				Set<String> values = groupsUsers.get(k);
				if (values == null) {
					content += "\n";
					set.remove("@" + k);
					continue;
				}
				List<String> l = new ArrayList<String>(values);
				Collections.sort(l);

				for (int i = 0; i < values.size() - 1; i++) {
					content += l.get(i) + ",";
				}
				if (values.size() == 0)
					content += "\n";
				else
					content += l.get(values.size() - 1) + "\n";
				set.remove("@" + k);
			} else {
				// System.out.println(k);
			}
		}
		// System.out.println(set);
		content += saveToACLNonExistingGroup(set);
		content += "\n";
		return content;
	}

	/**
	 * Returns the given base groups with the groups added from LDAP.
	 *
	 * @param parentGroupsP the set of the base groups
	 * @param groupsNode the set of the LDAP groups
	 *
	 * @return the given base groups with the groups added from LDAP
	 *
	 */
	private Set<String> findAllGroups(Set<String> allBaseGroups,
			List<String> allLdapGroups) {

		for (String k : allLdapGroups) {
			if (allBaseGroups.contains("@" + k)) {

				// System.out.println("@" + k.toLowerCase());

				Set<String> usersGro = groupsUsers.get(k);
				for (String us : usersGro) {
					if (us.startsWith("@")) {
						allBaseGroups.add(us);
						// System.out.print(us + ", ");
					}
				}
				// System.out.println("****");
			}
		}

		return allBaseGroups;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.ILdapProvider#reloadData()
	 */
	public void reloadData() {
		synchronized (ldapProvider.getLdapReader()) {
			ldapProvider.getLdapReader().notify();
		}
	}

	/**
	 * Returns all the parent groups for the given set of groups.
	 *
	 * @param groupMain the parent group to start the search from
	 * @param groups the groups whose parents are searched
	 *
	 * @return all the parent groups for the given set of groups
	 *
	 */
	public HashMap<String, Rights> getParentsGroupsForGroupFiltered
			(String groupMain, Map<String, Rights> groups) {
		HashMap<String, Rights> list = new HashMap<String, Rights>();

		Set<String> parentG = parentGroups.get(groupMain);
		if (parentG != null) {
			for (String groupN : groups.keySet()) {
				Rights r = groups.get(groupN);
				if (!groupN.equals("*")) {
					groupN = groupN.substring(1);
				}
				if (parentG.contains(groupN))
					list.put(groupN, new Rights(r.toString()));
			}
			if (list.size() != 0)
				return list;

			while (true) {
				parentG = findAllParentsGroupsForGroupFiltered(parentG, groups,
						list);
				if (list.size() != 0 || parentG != null)
					break;
			}
		}
		return list;

	}

	/**
	 * Returns the filtered parent groups for the given set of groups.
	 *
	 * @param parentG the parent group to start the search from
	 * @param groups the groups whose parents are searched
	 * @param list the list according to which we filter the parents
	 *
	 * @return the filtered parent groups for the given set of groups
	 *
	 */
	private Set<String> findAllParentsGroupsForGroupFiltered(
			Set<String> parentG, Map<String, Rights> groups,
			HashMap<String, Rights> list) {
		Set<String> listP = new HashSet<String>();
		for (String g : parentG) {
			Set<String> par = parentGroups.get(g);
			if (par != null)
				listP.addAll(par);
		}
		if (listP != null) {
			for (String groupN : groups.keySet()) {
				Rights r = groups.get(groupN);
				if (!groupN.equals("*")) {
					groupN = groupN.substring(1);
				}
				if (listP.contains(groupN))
					list.put(groupN, new Rights(r.toString()));
			}
		}
		return listP;
	}

	/**
	 * Returns the alphabetically sorted list of root nodes.
	 *
	 * @return the alphabetically sorted list of root nodes
	 *
	 */
	public List<String> findRoots() {
		List<String> roots = new ArrayList<String>(groups);

		for (String g : parentGroups.keySet()) {
			roots.remove(g);
		}
		Collator collator = Collator.getInstance(new Locale("cs", "CZ"));
		Collections.sort(roots, collator);
		return roots;
	}

	/**
	 * Returns the string representing the groups to be added to ACL.
	 *
	 * @param sset the set of groups to  be added to ACL
	 *
	 * @return the string representing the groups to be added to ACL
	 *
	 */
	private String saveToACLNonExistingGroup(Set<String> sset) {
		String content = "";
		Set<String> gr = groupsUsers.keySet();

		for (String group : sset) {
			if (group.length()>1) {
				if (!gr.contains(group.substring(1)) && !group.equals("*")) {
					content += group.substring(1) + " = \n";
				}
			}
		}
		return content;
	}

	/**
	 * Returns the set of the users of the given group.
	 *
	 * @param group the group whose users are searched
	 *
	 * @return the set of the users of the given group
	 *
	 */
	public Set<String> getUsersForGroup(String group) {
		Set<String> allUsersForGroup = new HashSet<String>();
		Set<String> groupsEmpty = new HashSet<String>();

		if (group.startsWith("@"))
			group = group.substring(1);

		Set<String> jj = groupsUsers.get(group);
		if (jj == null)
			return allUsersForGroup;

		for (String user : jj) {
			if (user.startsWith("@"))
				groupsEmpty.add(user);
			else {
				allUsersForGroup.add(user);
			}
		}
		while (groupsEmpty.size() != 0) {
			allUsersForGroup.addAll(findAllUsersForGroup(allUsersForGroup,
					groupsEmpty));
		}

		return allUsersForGroup;
	}

	/**
	 * Adds all users from the given set of groups to the given set of users.
	 *
	 * @param userAndGroups the set of users
	 * @param groupsEmpty the set of groups
	 *
	 * @return the given set of users with new users
	 *
	 */
	private Set<String> findAllUsersForGroup(Set<String> userAndGroups,
			Set<String> groupsEmpty) {
		Set<String> groupsEmptyTemp = new HashSet<String>();

		for (String g : groupsEmpty) {
			if (g.startsWith("@"))
				g = g.substring(1);

			Set<String> jj = groupsUsers.get(g);
			if (jj == null)
				continue;

			for (String user : jj) {
				if (user.startsWith("@"))
					groupsEmptyTemp.add(user);
				else
					userAndGroups.add(user);
			}
		}
		groupsEmpty.clear();
		groupsEmpty.addAll(groupsEmptyTemp);

		return userAndGroups;
	}
}