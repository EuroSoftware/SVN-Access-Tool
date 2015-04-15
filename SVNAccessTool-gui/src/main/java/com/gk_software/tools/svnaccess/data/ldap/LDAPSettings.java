package com.gk_software.tools.svnaccess.data.ldap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Stores info about LDAP servers. Contains JAXB annotations
 * for marshaling and unmarshaling XML docs. 
 */
@XmlRootElement
public class LDAPSettings {
	
	/** The lock object for thread synchronization. */
	private static Object lock = new Object();
	
	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(LDAPSettings.class);
	
	/** The list of servers. */
	private List<Server> server = new ArrayList<Server>();

	/*private LDAPSettings ldapSettings = null;
	
	private LDAPSettings() {}

	public LDAPSettings getInstance(){
		if(ldapSettings == null){
			return new LDAPSettings();
		}
		else return ldapSettings;
	}*/
	
	/**
	 * Returns the list of servers.
	 *
	 * @return the list of servers
	 *
	 */
	public List<Server> getServer() {
		return server;
	}

	/**
	 * Sets the new list of servers.
	 *
	 * @param server the new list of servers
	 *
	 */
	public void setServer(List<Server> server) {
		this.server = server;
	}

	/**
	 * Checks wheter all server in the list of server have all the fields filled.
	 */
	public boolean isAllFilled() {
		boolean isOk = true;
		try {
			for (Server server : this.server) {
				if (!server.isFilled()) {
					logger
							.info("Trying to save settings with uncorectly filled fields.");
					isOk = false;
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			isOk = false;
		}

		return isOk;

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
		builder.append("LDAPSettings [server=");
		builder.append(server);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Loads the settings from the settings file defined in the properties.
	 * 
	 * @throws JAXBException loading failed
	 * 
	 */
	public static LDAPSettings loadSettings() throws JAXBException {
		synchronized (lock) {
			JAXBContext jc;
			LDAPSettings ls = null;

			File LDAPSettingsFile = new File(Constants.getLDAP_SETTINGS_PATH());

			jc = JAXBContext.newInstance(LDAPSettings.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			ls = (LDAPSettings) unmarshaller.unmarshal(LDAPSettingsFile);
			return ls;
		}
	}

	/**
	 * Saves the given settings to the settings file defined in the properties.
	 * 
	 * @param ls the saved settings
	 * 
	 * @throws JAXBException, IOException saving failed
	 * 
	 */
	public static void saveLDAPSettings(LDAPSettings ls) throws JAXBException, IOException {
		synchronized (lock) {
			JAXBContext jc;
			jc = JAXBContext.newInstance(LDAPSettings.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			File f = new File(Constants.getLDAP_SETTINGS_PATH());

			FileOutputStream fos = new FileOutputStream(f);

			marshaller.marshal(ls, fos);
			fos.flush();
			fos.close();
		}
	}
}
