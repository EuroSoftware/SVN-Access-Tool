package com.gk_software.tools.svnaccess.bussiness.impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.CommunicationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.xbill.DNS.Lookup;
import org.xbill.DNS.NSRecord;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

import com.gk_software.tools.svnaccess.data.ldap.LDAPSettings;
import com.gk_software.tools.svnaccess.data.ldap.Server;
import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Authenticates the user via LDAP. Also provides all name servers
 * for the given domain.
 */
public class Authentization {

	/** The user to be authenticated. */
	private String user;

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(Authentization.class);

	/**
	 * Returns list of all the name servers for the given domain.
	 *
	 * @param domain the domain name
	 *
	 * @return the list of all the name servers for the given domain
	 *
	 * @throws TextParseException list retrieval failed
	 *
	 */
	public static List<String> getNS(String domain) throws TextParseException {
		Record[] records;
		List<String> list = new ArrayList<String>();
		records = new Lookup(domain + ".", Type.NS).run();
		if (records == null) {
			logger.info("There are no servers in domain: " + domain);
			return null;
		}

		for (int i = 0; i < records.length; i++) {
			NSRecord ns = (NSRecord) records[i];
			list.add(ns.getTarget().toString()
					.substring(0, ns.getTarget().toString().length() - 1));
		}
		return list;
	}

	/**
	 * Logs the user in via LDAP.
	 *
	 * @param username the username of the logged user
	 * @param password the password of the logged user
	 *
	 * @return true if the authentication is all right, otherwise false.
	 *
	 * @throws TextParseException login failed
	 *
	 */
	@Deprecated
	public boolean login(String username, String password)
			throws TextParseException {
		if (username.length() == 0 || password.length() == 0)
			return false;
		this.user = username;
		boolean authSuccessfull = false;
		String server = Constants.getProperties().getProperty("serverName");
		String port = Constants.getProperties().getProperty("port");
		List<String> servers = getNS(Constants.getProperties().getProperty(
				"domainName"));
		boolean failed = false;
		for (String s : servers) {

			Hashtable<String, String> srchEnv = new Hashtable<String, String>(11);
			srchEnv.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			srchEnv.put(Context.PROVIDER_URL, "ldap://" + server + ":" + port);
			logger.info("Url for login: " + srchEnv.get(Context.PROVIDER_URL));
			srchEnv.put(Context.SECURITY_AUTHENTICATION, "simple");

			srchEnv.put(Context.SECURITY_PRINCIPAL, username + "@gk-domain");
			srchEnv.put(Context.SECURITY_CREDENTIALS, password);

			try {
				new InitialDirContext(srchEnv);
				authSuccessfull = true;
				failed = false;
			} catch (CommunicationException ce) {
				server = s;
				failed = true;
			} catch (AuthenticationException authEx) {
				authSuccessfull = false;
				failed = false;
			} catch (NamingException namEx) {
				authSuccessfull = false;
				failed = false;
			}
			if (failed != true)
				break;
		}
		return authSuccessfull;
	}

	/**
	 * Logs the user in via LDAP and the given domain.
	 *
	 * @param username the username of the logged user
	 * @param password the password of the logged user
	 * @param domain the domain to which the user belongs
	 *
	 * @return true if the authentication is all right, otherwise false.
	 *
	 * @throws TextParseException login failed
	 *
	 */
	public boolean login(String username, String password, String domain)
			throws TextParseException {

		if (username.length() == 0 || password.length() == 0)
			return false;
		this.user = username;
		boolean authSuccessfull = false;

		Server ldap = null;
		boolean containServer = false;
		try {
			List<Server> servery = LDAPSettings.loadSettings().getServer();
			for (Server server : servery) {
				if (server.getName().equals(domain)) {
					containServer = true;
					ldap = server;
					break;
				}
			}
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}

		if (!containServer)
			return false;

		String server = ldap.getDomainServer();
		String port = ldap.getPort();
		List<String> servers = getNS(ldap.getDomainName());

		boolean failed = false;
		for (String s : servers) {

			Hashtable<String, String> srchEnv = new Hashtable<String, String>(11);
			srchEnv.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");
			srchEnv.put(Context.PROVIDER_URL, "ldap://" + server + ":" + port);
			logger.info("Url for login: " + srchEnv.get(Context.PROVIDER_URL));
			srchEnv.put(Context.SECURITY_AUTHENTICATION, "simple");

			srchEnv.put(Context.SECURITY_PRINCIPAL, getDN(ldap, username));
			srchEnv.put(Context.SECURITY_CREDENTIALS, password);

			try {
				new InitialDirContext(srchEnv);
				authSuccessfull = true;
				failed = false;
			} catch (CommunicationException ce) {
				server = s;
				failed = true;
			} catch (AuthenticationException authEx) {
				authSuccessfull = false;
				failed = false;
			} catch (NamingException namEx) {
				authSuccessfull = false;
				failed = false;
			}
			if (failed != true)
				break;
		}
		return authSuccessfull;
	}

	/**
	 * Returns the distinguished name for the given login from LDAP.
	 *
	 * @param server the LDAP server
	 * @param login the login for which is the DN searched.
	 *
	 * @return the distinguished name for the given login.
	 */
	@SuppressWarnings("rawtypes")
	private String getDN(Server server, String login) {
		Hashtable<String, String> srchEnv = new Hashtable<String, String>(11);
		srchEnv.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		srchEnv.put(Context.PROVIDER_URL, "ldap://" + server.getDomainServer()
				+ ":" + server.getPort());
		srchEnv.put(Context.SECURITY_AUTHENTICATION, "simple");

		if (server.getLogin().length() > 1) {
			srchEnv.put(Context.SECURITY_PRINCIPAL, server.getLogin());
			srchEnv.put(Context.SECURITY_CREDENTIALS, server.getPassword());
		}

		try {
			Control[] connCtls = new Control[] {};
			LdapContext myContex = new InitialLdapContext(srchEnv, connCtls);

			SearchControls searchCtls = new SearchControls();

			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String[] attributes = { "distinguishedname" };
			searchCtls.setReturningAttributes(attributes);

			NamingEnumeration answer = myContex.search(server.getBaseUserNamespace(),
					"(&(objectClass=user)(samaccountname=" + login + "))", searchCtls);

			if (answer.hasMoreElements()) {
				return (((SearchResult) answer.next()).getAttributes()
						.get("distinguishedname")).toString().substring(19);
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the logged user.
	 *
	 * @return the logged user.
	 *
	 */
	public String getUser() {
		return user;
	}
}