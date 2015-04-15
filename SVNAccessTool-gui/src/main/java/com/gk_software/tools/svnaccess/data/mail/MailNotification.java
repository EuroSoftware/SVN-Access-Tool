package com.gk_software.tools.svnaccess.data.mail;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.data.ldap.LDAPSettings;
import com.gk_software.tools.svnaccess.data.ldap.Server;
import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Provides methods for mail sending.
 */
public class MailNotification {

	/** The default e-mail subject. */
	private static final String emailSubject = "Report from SVNAccessTool - do not reply";

	/** The default source e-mail. */
	private static final String sourceEmail = "SVNAccessTool@gk-software.com";

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(MailNotification.class);

	/**
	 * Send an e-mail to the given recepient.
	 *
	 * @param recepient the recepient of the mail
	 *
	 */
	public boolean sendMail(String recepient) throws Exception {
		String path = Constants.getProperties().getProperty("mail_folder");

		File f = new File(path + "/" + recepient);
		FileReader reader = new FileReader(f);

		String emailContent = "Hello,";
		emailContent += "\n\nThis email is automatically generated, do not reply.\n\n";
		emailContent += IOUtils.toString(reader);

		reader.close();
		emailContent += "Best regards\n";
		emailContent += "Your SVNAccessTool\n";

		List<String> emailAddress = new ArrayList<String>();
		emailAddress.add(recepient);
		return postMail(emailAddress, emailSubject, emailContent, sourceEmail);

	}

	/**
	 * Send an e-mail with the given atributes to the given list of recepients.
	 *
	 * @param recipients the list of the mail recepients
	 * @param subject the subject of the mail
	 * @param message the message of the mail
	 * @param from the recepient of the mail
	 *
	 * @return true if mail successfuly send otherwise false
	 *
	 */
	private boolean postMail(List<String> recipients, String subject,
			String message, String from) {
		boolean debug = false;

		if (recipients.size() == 0) {
			return false;
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.es.gk-software.com");

		// create some properties and get the default Session
		Session session = Session.getDefaultInstance(props, null);
		session.setDebug(debug);

		// create an email message
		Message msg = new MimeMessage(session);

		try {
			// set the from and to address
			InternetAddress addressFrom = new InternetAddress(from);
			msg.setFrom(addressFrom);

			Map<String, String> suffixes = new HashMap<String, String>();

			InternetAddress[] addressTo = new InternetAddress[recipients.size()];

			String[] a = recipients.get(0).split("@");
			String samaccountname = a[0];
			String domain = a[1];

			Server ldap = null;
			boolean containServer = false;
			try {
				List<Server> servery = LDAPSettings.loadSettings().getServer();
				for (Server server : servery) {
					suffixes.put(server.getName(), server.getMailSuffix());
					if (server.getName().equals(domain)) {
						containServer = true;
						ldap = server;
						break;
					}
				}
			} catch (JAXBException e1) {
				e1.printStackTrace();
			}

			//If no server is set - can not get mail adress from LDAP
			if (!containServer){
				logger.info("For domain "+domain+" is no server set.");
				return false;
			}

			String mailAtributeLDAP = getMailAdress(ldap, samaccountname);
			if(mailAtributeLDAP != null){
				addressTo[0] =  new InternetAddress(mailAtributeLDAP);
			} else {
				if(suffixes.containsKey(domain)){
					addressTo[0] =  new InternetAddress(samaccountname+suffixes.get(domain));
				} else return false;
			}

			msg.setRecipients(Message.RecipientType.TO, addressTo);
			msg.setSubject(subject);
			msg.setContent(message, "text/plain");

			Transport.send(msg);

			return true;
		} catch (MessagingException me) {
			me.printStackTrace();
			return false;
		}
	}

	/**
	 * Returns the e-mail address of the user with the given name from the given server.
	 *
	 * @param server the server on which we search the user
	 * @param samaccountName the name of the searched user
	 *
	 */
	private String getMailAdress(Server server, String samaccountName) {
		Hashtable<String, String> srchEnv = new Hashtable<String, String>(11);
		srchEnv.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
		srchEnv.put(Context.PROVIDER_URL, "ldap://" + server.getDomainServer()+ ":" + server.getPort());
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
			String[] attributes = { "mail" };
			searchCtls.setReturningAttributes(attributes);

			NamingEnumeration<SearchResult> answer = myContex.search(server.getBaseUserNamespace(),
					"(&(objectClass=user)(samaccountname=" + samaccountName + "))", searchCtls);

			if (answer.hasMoreElements()) {
				return (((SearchResult) answer.next()).getAttributes()
						.get("mail")).toString().split(":")[1].trim();
			}

		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}
}