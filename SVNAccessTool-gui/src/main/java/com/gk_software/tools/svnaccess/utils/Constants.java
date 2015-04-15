package com.gk_software.tools.svnaccess.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Reads and saves the properties file.
 */
public class Constants {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(Constants.class);
	
	/** The properties. */
	public static Properties properties;
	
	/** The path to the properties file. */
	private static String PROP_FILE;
	
	/** The path to the modifyACL file. */
	private static String ACCESS_LIST_MODIFY_PATH;
	
	/** The path to the LDAP settings file. */
	private static String LDAP_SETTINGS_PATH;
	
	/** The username for logging to the SVN. */
	private static String USERNAME_SVN;
	
	/** The password for logging to the SVN. */
	private static String PASSWORD_SVN;
	
	/** The default repo root. */
	private static String DEFAULT_repoRoot = "file://";
	
	/** The default backup folder. */
	private static String DEFAULT_backupFolder = "tmp/backup";
	
	/** The default check interval. */
	private static String DEFAULT_checkInterval = "10";
	
	/** The default access list location. */
	private static String DEFAULT_aclLocation = "X//";
	
	/** The default replace string for "* = r". */
	private static String DEFAULT_labelReplace = "* = r";
	
	/** The default timeout. */
	private static String DEFAULT_timeout = "10";
	
	/** The default cookie expiration time. */
	private static String DEFAULT_cookieExp = "10000";
	
	/** The default mail receivers list. */
	private static String DEFAULT_tcs = "tklima@GK-DOMAIN;jdivis@GK-DOMAIN;";
	
	/** The default value for the mail notification indicator. */
	private static String DEFAULT_mailNotification = "false";
	
	/** The default value for the inheritance indicator. */
	private static String DEFAULT_inheritance_free = "";
	
	/** The default mail folder. */
	private static String DEFAULT_mail_folder = "";
	
	/** The default interval for checking the LDAP changes. */
	private static String DEFAULT_LDAPChangeControlTimer = "30000000";
	
	/**
	 * Returns the default interval for checking the LDAP changes.
	 *
	 * @return the default interval for checking the LDAP changes
	 *
	 */
	public static String getDEFAULT_LDAPChangeControlTimer() {
		return DEFAULT_LDAPChangeControlTimer;
	}

	/**
	 * Sets the new default interval for checking the LDAP changes.
	 *
	 * @param dEFAULT_LDAPChangeControlTimer the new default interval 
	 * for checking the LDAP changes
	 *
	 */
	public static void setDEFAULT_LDAPChangeControlTimer(
			String dEFAULT_LDAPChangeControlTimer) {
		DEFAULT_LDAPChangeControlTimer = dEFAULT_LDAPChangeControlTimer;
	}

	/**
	 * Returns the path to the LDAP settings file.
	 *
	 * @return the path to the LDAP settings file
	 *
	 */
	public static String getLDAP_SETTINGS_PATH() {
		return LDAP_SETTINGS_PATH;
	}

	/**
	 * Sets the new path to the LDAP settings file.
	 *
	 * @param lDAP_SETTINGS_PATH the new path to the 
	 * LDAP settings file
	 *
	 */
	public static void setLDAP_SETTINGS_PATH(String lDAP_SETTINGS_PATH) {
		LDAP_SETTINGS_PATH = lDAP_SETTINGS_PATH;
	}
	
	/**
	 * Returns the username for logging to the SVN.
	 *
	 * @return the username for logging to the SVN
	 *
	 */
	public static String getUSERNAME_SVN() {
		return USERNAME_SVN;
	}

	/**
	 * Sets the new username for logging to the SVN.
	 *
	 * @param uSERNAME_SVN the new username for logging to the SVN
	 *
	 */
	public static void setUSERNAME_SVN(String uSERNAME_SVN) {
		USERNAME_SVN = uSERNAME_SVN;
	}

	/**
	 * Returns the password for logging to the SVN.
	 *
	 * @return the password for logging to the SVN
	 *
	 */
	public static String getPASSWORD_SVN() {
		return PASSWORD_SVN;
	}

	/**
	 * Sets the new password for logging to the SVN.
	 *
	 * @param pASSWORD_SVN the new password for logging to the SVN
	 *
	 */
	public static void setPASSWORD_SVN(String pASSWORD_SVN) {
		PASSWORD_SVN = pASSWORD_SVN;
	}

	/**
	 * Returns the path to the modifyACL file.
	 *
	 * @return the path to the modifyACL file
	 *
	 */
	public static String getACCESS_LIST_MODIFY_PATH() {
		return ACCESS_LIST_MODIFY_PATH;
	}

	/**
	 * Sets the new path to the modifyACL file.
	 *
	 * @param aCCESS_LIST_MODIFY_PATH the path to 
	 * the modifyACL file
	 *
	 */
	public static void setACCESS_LIST_MODIFY_PATH(String aCCESS_LIST_MODIFY_PATH) {
		ACCESS_LIST_MODIFY_PATH = aCCESS_LIST_MODIFY_PATH;
	}

	/**
	 * Returns the path to the properties file.
	 *
	 * @return the path to the properties file
	 *
	 */
	public static String getPROP_FILE() {
		return PROP_FILE;
	}

	/**
	 * Sets the new path to the properties file.
	 *
	 * @param pROP_FILE the new path to the properties file
	 *
	 */
	public static void setPROP_FILE(String pROP_FILE) {
		PROP_FILE = pROP_FILE;
	}

	/**
	 * Load properties from the properties file.
	 */
	public static void loadConstants() {
		properties = new Properties();
		try {
			logger.info("Loading constants from file: " + PROP_FILE);
			properties.load(new FileInputStream(PROP_FILE));
			checkLoaded();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Sets the properties, which where not loaded, to the default values.
	 */
	private static void checkLoaded() {
		if (properties.get("repoRoot") == null)
			properties.setProperty("repoRoot", DEFAULT_repoRoot);



		if (properties.get("backupFolder") == null)
			properties.setProperty("backupFolder", DEFAULT_backupFolder);
		if (properties.get("checkInterval") == null)
			properties.setProperty("checkInterval", DEFAULT_checkInterval);
		if (properties.get("labelReplace") == null)
			properties.setProperty("labelReplace", DEFAULT_labelReplace);
		if (properties.get("timeout") == null)
			properties.setProperty("timeout", DEFAULT_timeout);
		if (properties.get("cookieExp") == null)
			properties.setProperty("cookieExp", DEFAULT_cookieExp);

		if (properties.get("tcs") == null)
			properties.setProperty("tcs", DEFAULT_tcs);
		if (properties.get("aclLocation") == null)
			properties.setProperty("aclLocation", DEFAULT_aclLocation);

		if(properties.get("mailNotification")==null)
			properties.setProperty("mailNotification", DEFAULT_mailNotification);
		if(properties.get("inheritance_free")==null)
			properties.setProperty("inheritance_free", DEFAULT_inheritance_free);
		if(properties.get("mail_folder")==null)
			properties.setProperty("mail_folder", DEFAULT_mail_folder);
		if(properties.get("LDAPChangeControlTimer")==null)
			properties.setProperty("LDAPChangeControlTimer", DEFAULT_LDAPChangeControlTimer);
	}

	/**
	 * Returns the properties.
	 *
	 * @return the properties
	 *
	 */
	public static Properties getProperties() {
		return properties;
	}

	/**
	 * Save the properties to the property file.
	 */
	public static void saveConstants() {
		try {
			logger.info("Saving constants to file: " + PROP_FILE);
			properties.store(new FileOutputStream(PROP_FILE), null);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}