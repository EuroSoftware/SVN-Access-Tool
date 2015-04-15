package com.gk_software.tools.svnaccess.data.ldap;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.data.ILdapReader;
import com.gk_software.tools.svnaccess.data.accessList.ViewInformation;
import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Periodically reads the users and groups from LDAP.
 */
public class LDAPAccess extends Thread implements ILdapReader {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(LDAPAccess.class);

	/** The instance of the {@code Logger} class. */
	private int exception = 0;

	/** The provider of LDAP. */
	private LdapProvider ldapProvider;

	/** The set of users. */
	private Set<String> users;

	/** The set of groups. */
	private Set<String> groups;

	/** The map with parent as key and its child groups as value. */
	private HashMap<String, Set<String>> parentGroups;

	/** The map with group as key and its users as value. */
	private HashMap<String, Set<String>> groupsUsers;

	/** The indicator whether the thread has been interupted. */
	public boolean interupt = false;

	// public static final String SEARCH_BY_SAM_ACCOUNT_NAME =
	// "(SAMAccountName={0})";

	/*
	 * public static void main(String[] args) {
	 * Constants.setPROP_FILE("X://SVNAccessTool//properties.properties");
	 * Constants.loadConstants(); LDAPAccess lr = new LDAPAccess(null); lr.run();
	 * }
	 */

	/**
	 * Creates a new instance and initializes the variable {@code ldapProvider}
	 * according to the given value.
	 *
	 * @param ldapProvider the provider of LDAP
	 *
	 */
	public LDAPAccess(LdapProvider ldapProvider) {
		this.ldapProvider = ldapProvider;
		this.setName("ldap");
		cleanThreads();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#run()
	 */
	public synchronized void run() {

		while (!interupt) {
			exception = 0;
			logger.info("Ldap reloading thread with id: " + this.getId());

			users = new HashSet<String>();
			groups = new HashSet<String>();
			groupsUsers = new HashMap<String, Set<String>>();
			parentGroups = new HashMap<String, Set<String>>();


				LDAPSettings servers = null;

				try {
					servers = LDAPSettings.loadSettings();
				} catch (JAXBException e) {
					logger.info("Failed to load servers settings from XML");
					e.printStackTrace();
				}
				try {
				for (Server server : servers.getServer()) {
					LdapContext myContex = null;
					Hashtable<String, String> srchEnv = new Hashtable<String, String>(11);

					srchEnv.put(Context.INITIAL_CONTEXT_FACTORY,
							"com.sun.jndi.ldap.LdapCtxFactory");
					srchEnv.put(Context.PROVIDER_URL,
							"ldap://" + server.getDomainServer() + ":389");
					srchEnv.put(Context.SECURITY_AUTHENTICATION, "simple");

					// TODO change to checkbox control
					if (server.getLogin().length() > 1) {
						srchEnv.put(Context.SECURITY_PRINCIPAL, server.getLogin());
						srchEnv.put(Context.SECURITY_CREDENTIALS, server.getPassword());
					}

					Control[] connCtls = new Control[] {};
					myContex = new InitialLdapContext(srchEnv, connCtls);

					// sAMAccounName a memberOf will be pulled out
					SearchControls searchCtrls = new SearchControls();
					searchCtrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
					searchCtrls.setReturningAttributes(new String[]{ server.getUserNameAttribute(), "memberOf" });
					NamingEnumeration<SearchResult> values = myContex.search(
							server.getBaseGroupsNamespace(), server.getGroupSearchFilter(),
							searchCtrls);

					printGrupsAndHisUsers(values);

					searchCtrls.setReturningAttributes(new String[]{ server.getUserNameAttribute(), "memberOf", "member",
					"userAccountControl" });
					NamingEnumeration<SearchResult> values2 = myContex.search(
							server.getBaseUserNamespace(), server.getUserSearchFilter(),
							searchCtrls);

					createStructure(values2, server);
				}

			} catch (CommunicationException ce) {
				exception = 1;
				//server = s;
			} catch (AuthenticationException authEx) {
				exception = 1;

			} catch (NamingException e) {
				exception = 1;
				logger.error("Exception during LDAP reloading. ");
				e.printStackTrace();
			}

			actualization();
			long elapsed = System.currentTimeMillis();
			DateFormat df = new SimpleDateFormat("HH:mm:ss");

			logger.info("Time of ldap reloading: " + df.format(new Date(elapsed)));

			synchronized (this) {
				try {
					logger
							.info("Reader is waiting until reload time interval is elapsed");
					this.wait(Integer.parseInt(Constants.getProperties().getProperty(
							"checkInterval")) * 60000);
				} catch (InterruptedException e1) {
					logger.error("Interrupted Exception.");
				}
			}
		}
	}

	/**
	 * Adds the groups and their users from the given enumeration to the appropriate
	 * sets.
	 *
	 * @param values the set from which are the values taken
	 *
	 */
	public void printGrupsAndHisUsers(NamingEnumeration<SearchResult> values)
			throws NamingException {

		while (values.hasMoreElements()) {
			SearchResult result = (SearchResult) values.next();
			Attributes attribs = result.getAttributes();
			String groupa = "";
			if (null != attribs) {
				for (NamingEnumeration<? extends Attribute> ae = attribs.getAll(); ae.hasMoreElements();) {
					Attribute atr = (Attribute) ae.next();
					String attributeID = atr.getID();

					for (Enumeration vals = atr.getAll(); vals.hasMoreElements();) {
						if (attributeID.equals("memberOf")) {
							String parent = vals.nextElement().toString().trim();
							parent = parent.split(",")[0].substring(3);
							addItemToGroup(parent, "@" + groupa);
							groups.add(parent);
							addItemToParentGroup(groupa, parent);
						}
						if (attributeID.equals("sAMAccountName")) {
							groupa = vals.nextElement().toString().trim();
							groups.add(groupa);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#interrupt()
	 */
	public void interrupt() {
		this.interupt = true;
	}

	/**
	 * Interrupts all LDAP threads.
	 */
	private void cleanThreads() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		long id = this.getId();
		for (Thread t : threadSet) {
			if (t.getName().equals("ldap") && t.getId() != id) {
				t.interrupt();
				logger.info("Interrupting thread with id: " + t.getId());
			}
		}
	}

	/**
	 * Updates the data from LDAP.
	 */
	private void actualization() {
		if (exception == 1) {
			logger.error("Exception was throwed, access list will not be saved");
			return;
		}
		if (isDataChanged()) {
			logger.info("Actualization LDAP data.");
			ldapProvider.setGroups(groups);
			ldapProvider.setGroupsUsers(groupsUsers);
			ldapProvider.setUsers(users);
			ldapProvider.setParentGroups(parentGroups);
			// save access list
			logger.info("New data from LDAP are different, saving the access list");
			try {
				ViewInformation.getInstance().saveAccessList();
			} catch (Exception e) {
				logger.error("Error during saving Access List");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			logger.info("Actualization LDAP data.");
			ldapProvider.setGroups(groups);
			ldapProvider.setGroupsUsers(groupsUsers);
			ldapProvider.setUsers(users);
			ldapProvider.setParentGroups(parentGroups);
		}
	}

	/**
	 * Checks if the data have been changed.
	 *
	 * @return true data have changed otherwise false
	 */
	private boolean isDataChanged() {
		// if the size of collections are different, there are some changes
		if (users.size() != 0 || groups.size() != 0 || groupsUsers.size() != 0) {
			if (ldapProvider.isSleeping()) {
				synchronized (ldapProvider) {
					ldapProvider.notify();
					ldapProvider.setSleeping(false);
				}
			}
		}
		if (ldapProvider.getUsersForLdap().size() == 0
				|| ldapProvider.getGroupsForLdap().size() == 0
				|| ldapProvider.getGroupsUsersForLdap().size() == 0)
			return false;

		if (ldapProvider.getUsersForLdap().size() != users.size()
				|| ldapProvider.getGroupsForLdap().size() != groups.size()
				|| ldapProvider.getGroupsUsersForLdap().size() != groupsUsers.size())
			return true;

		// if there are missing users, then there are changes
		for (String user : users) {
			if (!ldapProvider.getUsersForLdap().contains(user)) {
				return true;
			}
		}

		// if there are missing groups, then there are changes
		for (String group : groups) {
			if (!ldapProvider.getGroupsForLdap().contains(group)) {
				return true;
			}
		}
		// if there are different assignment users to group, then there are some
		// changes
		for (String group : groupsUsers.keySet()) {
			Set<String> newOne = groupsUsers.get(group);
			Set<String> original = ldapProvider.getGroupsUsersForLdap().get(group);
			if (original.size() != newOne.size()) {// different size
				return true;
			}

			for (String user : original) {
				if (!newOne.contains(user)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Adds the given user to the given group.
	 *
	 * @param group the group to which is the user added
	 * @param user the added user
	 *
	 */
	private void addItemToGroup(String group, String user) {
		if (!groupsUsers.containsKey(group)) {
			groupsUsers.put(group, new HashSet<String>());
		}
		if (!groups.contains(group)) {
			groups.add(group);
		}
		Set<String> userForGroup = groupsUsers.get(group);
		userForGroup.add(user);
	}

	/**
	 * Adds the given group to the given parent.
	 *
	 * @param group the added group
	 * @param parent the parent to which is the group added
	 *
	 */
	private void addItemToParentGroup(String group, String parent) {
		if (!parentGroups.containsKey(group)) {
			parentGroups.put(group, new HashSet<String>());
		}
		Set<String> groupForGroup = parentGroups.get(group);
		groupForGroup.add(parent);
	}

	/**
	 * Creates the list of user and groups and a hashMap with group as key
	 * and its users as values.
	 *
	 * @param values the set from which are the structures created
	 * @param server the LDAP server
	 *
	 * @throws NamingException creation failed
	 *
	 */
	public void createStructure(NamingEnumeration<SearchResult> values, Server server)
			throws NamingException {

		while (values.hasMoreElements()) {
			SearchResult result = (SearchResult) values.next();
			Attributes attribs = result.getAttributes();
			if (null != attribs) {
				String user = "";
				boolean disableUser = false;
				for (NamingEnumeration<?> ae = attribs.getAll(); ae.hasMoreElements();) {
					Attribute atr = (Attribute) ae.next();
					String attributeID = atr.getID();
					if (disableUser == true) {
						// System.out.println("disable user ");
						continue;
					}
					String group = "";
					for (Enumeration<?> vals = atr.getAll(); vals.hasMoreElements();) {
						if (disableUser == true) {
							// System.out.println("disable user ");
							continue;
						}
						String f = (String) vals.nextElement();
						if (attributeID.equals("userAccountControl")) {

							// System.out.println("my:" + f);
							String a = Integer.toHexString(Integer.parseInt(f));
							if (a.endsWith("2")) {
								disableUser = true;
								continue;
							}
						}
						if (attributeID.equals("memberOf")) {
							group = f.split(",")[0].substring(3);
						}
						if (attributeID.equals(server.getUserNameAttribute())) {
							// if (attributeID.equals("sAMAccountName")) {
							user = f;
							users.add(user + "@" + server.getDomainName().toUpperCase());
						}
						// addUserToGroup(group, user)
						if (group.length() != 0) {
							groups.add(group);
							addItemToGroup(group, user + "@" + server.getDomainName().toUpperCase());
						}
					}
				}
			}
		}

	}
}