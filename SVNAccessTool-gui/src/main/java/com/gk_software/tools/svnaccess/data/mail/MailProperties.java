package com.gk_software.tools.svnaccess.data.mail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Reads and saves the data from the mail properties file.
 */
public class MailProperties {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(MailProperties.class);
	
	/** The properties file. */
	public static Properties properties;
	
	/** The name of the properties file. */
	private static String PROP_FILE;
	
	/** The mail check interval. */
	private static String MAIL_CHECK="100";
	
	
	/**
	 * Returns the name of the properties file.
	 *
	 * @return the name of the properties file
	 *
	 */
	public static String getPROP_FILE() {
		return PROP_FILE;
	}

	/**
	 * Sets the new name of the properties file.
	 *
	 * @param pROP_FILE the new name of the properties file
	 *
	 */
	public static void setPROP_FILE(String pROP_FILE) {
		PROP_FILE = pROP_FILE + "/properties.properties";
	}

	/**
	 * Loads the properties from the property file.
	 */
	public static void loadConstants() {
		properties = new Properties();

		try {
			logger.info("Loading mail settings from file: " + PROP_FILE);
			properties.load(new FileInputStream(PROP_FILE));
			checkLoaded();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * Sets the mail check property to default value if it is not set.
	 */
	private static void checkLoaded() {
		 if(properties.get("mail_check")==null)
		 properties.setProperty("mail_check", MAIL_CHECK);
	}

	/**
	 * Returns the properties file.
	 *
	 * @return the properties file
	 *
	 */
	public static Properties getProperties() {
		return properties;
	}

	/**
	 * Saves the given properties to a property file.
	 * 
	 * @param map the map containing property name as key and its value as value
	 * 
	 */
	public static void saveConstants(Map<String, String> map) {
		for (String key : map.keySet()) {
			if(map.get(key).contains("1")){
				properties.put(key, map.get(key));
			} else {
				properties.remove(key);
			}
		}
		try {
			logger.info("Saving constants to file: " + PROP_FILE);
			properties.store(new FileOutputStream(PROP_FILE), null);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
	}
}