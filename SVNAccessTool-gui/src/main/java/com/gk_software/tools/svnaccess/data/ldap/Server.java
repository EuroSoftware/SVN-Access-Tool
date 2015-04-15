package com.gk_software.tools.svnaccess.data.ldap;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * Represents an LDAP server. 
 */

@XmlType
public class Server implements Serializable {
	
	/** The serial id. */
	private static final long serialVersionUID = -8222679627802759564L;
	
	/** The name of the server. */
	private String name = ""; 
	
	/**
	 * Sets the new name of the server.
	 *
	 * @param name the new name of the server
	 *
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Returns the name of the server.
	 *
	 * @return the name of the server
	 *
	 */
	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}
	
	/** The server port. */
	private String port = "";
	
	/** The name of the domain to which the server belongs. */
	private String domainName = "";
	
	/** The name of the domain server. */
	private String domainServer = "";
	
	/** The name of the base namespace for users. */
	private String baseUserNamespace = "";
	
	/** The user search filter. */
	private String userSearchFilter = "";
	
	/** The name of the username attribute. */
	private String userNameAttribute = "";
	
	/** The name of the base namespace for groups. */
	private String baseGroupsNamespace = "";
	
	/** The group search filter. */
	private String groupSearchFilter = "";
	
	/** The login to the server. */
	private String login;
	
	/** The password to the server. */
	private String password;
	
	/** The suffix for mails from this server. */
	private String mailSuffix = "";
	
	/**
	 * Returns the suffix for mails from this server.
	 *
	 * @return the suffix for mails from this server
	 *
	 */
	public String getMailSuffix() {
		return mailSuffix;
	}
	
	/**
	 * Sets the new suffix for mails from this server.
	 *
	 * @param mailSuffix the suffix for mails from this server
	 *
	 */
	public void setMailSuffix(String mailSuffix) {
		this.mailSuffix = mailSuffix;
	}
	
	/**
	 * Returns the login to the server.
	 *
	 * @return the login to the server
	 *
	 */
	public String getLogin() {
		return login;
	}
	
	/**
	 * Sets the new login to the server.
	 *
	 * @param login the login to the server
	 *
	 */
	public void setLogin(String login) {
		this.login = login;
	}
	
	/**
	 * Returns the password to the server.
	 *
	 * @return the password to the server
	 *
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the new password to the server.
	 *
	 * @param password the password to the server
	 *
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Returns the server port.
	 *
	 * @return the server port
	 *
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Sets the new server port.
	 *
	 * @param port the server port
	 *
	 */
	public void setPort(String port) {
		this.port = port;
	}
	
	/**
	 * Returns the name of the domain to which the server belongs.
	 *
	 * @return the name of the domain to which the server belongs
	 *
	 */
	public String getDomainName() {
		return domainName;
	}
	
	/**
	 * Sets the new name of the domain to which the server belongs.
	 *
	 * @param domainName the new name of the domain to which the server belongs
	 *
	 */
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	/**
	 * Returns the name of the domain server.
	 *
	 * @return the name of the domain server
	 *
	 */
	public String getDomainServer() {
		return domainServer;
	}
	
	/**
	 * Sets the new name of the domain server.
	 *
	 * @param domainServer the new name of the domain server
	 *
	 */
	public void setDomainServer(String domainServer) {
		this.domainServer = domainServer;
	}
	
	/**
	 * Returns the name of the base namespace for users.
	 *
	 * @return the name of the base namespace for users
	 *
	 */
	public String getBaseUserNamespace() {
		return baseUserNamespace;
	}
	
	/**
	 * Sets the new name of the base namespace for users.
	 *
	 * @param baseUserNamespace the new name of the base namespace for users
	 *
	 */
	public void setBaseUserNamespace(String baseUserNamespace) {
		this.baseUserNamespace = baseUserNamespace;
	}
	
	/**
	 * Returns the user search filter.
	 *
	 * @return the user search filter
	 *
	 */
	public String getUserSearchFilter() {
		return userSearchFilter;
	}
	
	/**
	 * Sets the new user search filter.
	 *
	 * @param userSearchFilter the new user search filter
	 *
	 */
	public void setUserSearchFilter(String userSearchFilter) {
		this.userSearchFilter = userSearchFilter;
	}
	
	/**
	 * Returns the name of the username attribute.
	 *
	 * @return the name of the username attribute
	 *
	 */
	public String getUserNameAttribute() {
		return userNameAttribute;
	}
	
	/**
	 * Sets the new name of the username attribute.
	 *
	 * @param userNameAttribute the new name of the username attribute
	 *
	 */
	public void setUserNameAttribute(String userNameAttribute) {
		this.userNameAttribute = userNameAttribute;
	}
	
	/**
	 * Returns the name of the base namespace for groups.
	 *
	 * @return the name of the base namespace for groups
	 *
	 */
	public String getBaseGroupsNamespace() {
		return baseGroupsNamespace;
	}
	
	/**
	 * Sets the new name of the base namespace for groups.
	 *
	 * @param baseGroupsNamespace the new name of the base namespace for groups
	 *
	 */
	public void setBaseGroupsNamespace(String baseGroupsNamespace) {
		this.baseGroupsNamespace = baseGroupsNamespace;
	}
	
	/**
	 * Returns the group search filter.
	 *
	 * @return the group search filter
	 *
	 */
	public String getGroupSearchFilter() {
		return groupSearchFilter;
	}
	
	/**
	 * Sets the new group search filter.
	 *
	 * @param groupSearchFilter the new group search filter
	 *
	 */
	public void setGroupSearchFilter(String groupSearchFilter) {
		this.groupSearchFilter = groupSearchFilter;
	}
	
	/**
	 * Returns the string representation of this class.
	 *
	 * @return the string representation of this class
	 *
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Server [name=");
		builder.append(name);
		builder.append(", port=");
		builder.append(port);
		builder.append(", domainName=");
		builder.append(domainName);
		builder.append(", domainServer=");
		builder.append(domainServer);
		builder.append(", baseUserNamespace=");
		builder.append(baseUserNamespace);
		builder.append(", userSearchFilter=");
		builder.append(userSearchFilter);
		builder.append(", userNameAttribute=");
		builder.append(userNameAttribute);
		builder.append(", baseGroupsNamespace=");
		builder.append(baseGroupsNamespace);
		builder.append(", groupSearchFilter=");
		builder.append(groupSearchFilter);
		builder.append(", login=");
		builder.append(login);
		builder.append(", password=");
		builder.append(password);
		builder.append("]");
		return builder.toString();
	}
	
	/**
	 * Checks if all the variables are filled.
	 *
	 * @return true if all filled otherwise false
	 *
	 */
	public boolean isFilled(){
		boolean isOk = true;
		try{
				if(getName().toString().length() == 0)
					isOk = false;
				if (getDomainName().toString().length() == 0)
					isOk = false;
				if (getPort().toString().length() == 0)
					isOk = false;
				try {
					Integer.parseInt(getPort().toString());
				} catch (Exception e) {
					isOk = false;
				}
				if (getBaseUserNamespace().toString().length() == 0)
					isOk = false;
				if (getUserSearchFilter().toString().length() == 0)
					isOk = false;
				if (getUserNameAttribute().toString().length() == 0)
					isOk = false;
				if (getBaseGroupsNamespace().toString().length() == 0)
					isOk = false;
				if (getGroupSearchFilter().toString().length() == 0)
					isOk = false;
				if (getDomainServer()==null || getDomainServer().toString().length() == 0)
					isOk = false;
		} catch (Exception e){
			//TODO: logging
			e.printStackTrace();
			isOk = false;
		}
		
		return isOk;
		
	}
}
