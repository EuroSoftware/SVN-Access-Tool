package com.gk_software.tools.svnaccess.data.mail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import com.gk_software.tools.svnaccess.bussiness.impl.Change;
import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.bussiness.impl.User;
import com.gk_software.tools.svnaccess.data.IAccessListReader;
import com.gk_software.tools.svnaccess.data.accessList.AccessListReader;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.Rights;
import com.gk_software.tools.svnaccess.utils.Constants;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalMailSettings;

/**
 * Represents an e-mail writer.
 */
public class MailWriter {
	
	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(MailWriter.class);

	/** The date format string. */
	private static final String DATE_FORMAT = "dd.MM.yyyy - HH:mm:ss";
	
	/** The line format string. */
	private static final String formatLine = "\n*****************************************";

	/** The the added string. */
	public static final String ADDED_LDAP = "ADD";
	
	/** The removed string. */
	public static final String REMOVED_LDAP = "REM";

	/**
	 * Creates a new instance.
	 */
	public MailWriter() {

	}

	/**
	 * Deletes the files of the e-mails that have been already sent.
	 *
	 * @param sentMailsAdreses the list of the names of the files of the e-mails 
	 * that have been already sent
	 *
	 */
	public void clearMessages(List<String> sentMailsAdreses) {
		String path = Constants.getProperties().getProperty("mail_folder");
		logger.info("Cleaning the sent messages");
		File f = new File(path);
		for (File file : f.listFiles()) {
			if (file.isFile() && !file.getName().equals("properties.properties")
					&& sentMailsAdreses.contains(file.getName())) {
				try {
					FileUtils.forceDelete(file);
					logger
							.info("File " + file.getAbsolutePath() + " succesfully deleted");
				} catch (Exception e) {
					logger.info("File " + file.getAbsolutePath()
							+ "unsuccesfully deleted");
				}
			}
		}
	}

	/**
	 * Creates a file representing and e-mail for the given change.
	 * 
	 * @param user the user who made the change
	 * @param change the changed data
	 * @param group the group to which the user belongs
	 * @param groupRights the rights of the group
	 */
	public void appendMessage(String user, String change, String group,
			String groupRights) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		String path = Constants.getProperties().getProperty("mail_folder");

		// If user has not in individual settings allowed info about this group,
		// dont't send anything.
		if (ModalMailSettings.isIgnoredGroup(user, group)) {
			//logger.info("User "+user+" doesnt't want to be told about changes in this group: "+group+".");
			return;
		}

		try {
			String emailContent = "";

			emailContent = prepareContentLDAPChange(change, group, groupRights);

			fw = new FileWriter(path + "/" + user, true);
			bw = new BufferedWriter(fw);
			bw.write(emailContent);
			bw.newLine();
			bw.flush();

		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
			logger.error(stacktrace);
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String stacktrace = sw.toString();
					logger.error(stacktrace);
					e.printStackTrace();
				}
			}
			if (bw != null)
				try {
					fw.close();
					bw.close();
					fw = null;
					bw = null;
				} catch (IOException ioe2) {
					StringWriter sw = new StringWriter();
					ioe2.printStackTrace(new PrintWriter(sw));
					String stacktrace = sw.toString();
					logger.error(stacktrace);
					ioe2.printStackTrace();
				}
		}
	}

	/**
	 * Creates a file representing and e-mail for the given changes and receivers.
	 * 
	 * @param svn the name of the SVN
	 * @param fullpath the path to the node in the SVN
	 * @param receivers the list of the mail receivers
	 * @param changes the made changes
	 * @param originatorUser the user who made the changes
	 * @param reason the reason of the changes
	 */
	public void appendMessage(String svn, String fullpath,
			Map<String, Rights> receivers, Changes changes, User originatorUser,
			String reason) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		String path = Constants.getProperties().getProperty("mail_folder");

		try {
			String emailContent = prepareContent(svn, fullpath, changes,
					originatorUser, reason);
			if (emailContent == null)
				return;
			for (String user : receivers.keySet()) {

				// Control personal settings
				if (ModalMailSettings.isIgnoredRepo(user, svn)) {
					continue;
				}

				fw = new FileWriter(path + "/" + user, true);
				bw = new BufferedWriter(fw);

				bw.write(emailContent);
				bw.newLine();
				bw.flush();

			}
		} catch (IOException ioe) {
			StringWriter sw = new StringWriter();
			ioe.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
			logger.error(stacktrace);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
			logger.error(stacktrace);
		} finally { // always close the file
			if (bw != null)
				try {
					fw.close();
					bw.close();
					fw = null;
					bw = null;
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally
	}

	/**
	 * Creates a file representing and e-mail for the given change.
	 * 
	 * @param svn the name of the SVN
	 * @param fullpath the path to the node in the SVN
	 * @param receivers the list of the mail receivers
	 * @param lock the node which has been changed
	 * @param originatorUser the user who made the changes
	 * @param reason the reason of the changes
	 */
	public void appendMessage(String svn, String fullpath,
			Map<String, Rights> receivers, Node lock, User originatorUser,
			String reason) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		String path = Constants.getProperties().getProperty("mail_folder");

		try {
			String emailContent = "";
			if (reason.toLowerCase().equals("lock")) {
				emailContent = prepareContentLock(svn, fullpath, lock, originatorUser,
						reason);
			}
			if (reason.toLowerCase().equals("unlock"))
				emailContent = prepareContentUnlock(svn, fullpath, lock,
						originatorUser, reason);
			if (emailContent == null)
				return;
			for (String user : receivers.keySet()) {

				// Control personal settings
				if (ModalMailSettings.isIgnoredRepo(user, svn)) {
					continue;
				}

				fw = new FileWriter(path + "/" + user, true);
				bw = new BufferedWriter(fw);
				bw.write(emailContent);
				bw.newLine();
				bw.flush();
			}
		} catch (IOException ioe) {
			StringWriter sw = new StringWriter();
			ioe.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
			logger.error(stacktrace);
			ioe.printStackTrace();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
			logger.error(stacktrace);
			e.printStackTrace();

		} finally { // always close the file
			if (bw != null)
				try {
					fw.close();
					bw.close();
					fw = null;
					bw = null;
				} catch (IOException ioe2) {
					// just ignore it
				}
		} // end try/catch/finally

	}

	/**
	 * Prepares the content of the e-mail for the given change.
	 * 
	 * @param change the changed data
	 * @param group the group to which the user belongs
	 * @param groupRights the rights of the group
	 */
	private String prepareContentLDAPChange(String change, String group,
			String groupRights) throws IllegalArgumentException {
		String content = "";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String date = sdf.format(cal.getTime());
		content += "Time of change: " + date + " by LDAP admins. \n";

		if (change == ADDED_LDAP) {
			content += "You have been added to the group " + group + ".\n";
			content += "Now you have all rights that the group has:\n";
			content += groupRights;

		} else if (change == REMOVED_LDAP) {
			content += "You have been removed from the group " + group + ".\n";
			content += "You have lost all rights connected with the group:\n";
			content += groupRights;
		} else {
			throw new IllegalArgumentException();
		}
		content += formatLine;

		return content;
	}

	/**
	 * Prepares the content of the e-mail for the given changes.
	 * 
	 * @param svn the name of the SVN
	 * @param fullpath the path to the node in the SVN
	 * @param changes the made changes
	 * @param originatorUser the user who made the changes
	 * @param reason the reason of the changes
	 */
	private String prepareContent(String svn, String fullpath, Changes changes,
			User originatorUser, String reason) throws Exception {
		String content = "";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String date = sdf.format(cal.getTime());
		content += "Time of change: " + date + " by user: "
				+ originatorUser.getUserName() + "\n";
		content += "SVN: " + svn + "\n";
		content += "Path: " + fullpath + "\n";
		content += "Reason: " + reason + "\n";
		IAccessListReader acr = AccessListReader.getInstance();

		String content2 = "";
		Node n = acr.getTree(svn).getNode(fullpath);
		if (changes.isInheritance()) {
			if (n.isInheritance())
				content2 += "Inheritance set to enabled \n";
			else
				content2 += "Inheritance set to disabled \n";

		}

		if (changes.getGroupChanges().size() != 0)
			content2 += "GROUP CHANGES \n";

		for (String group : changes.getGroupChanges().keySet()) {
			Change groupCh = changes.getGroup(group);
			switch (groupCh.getAction()) {
			case ADDED:
				content2 += "    ADDED: " + group + ", rights: " + groupCh.getRights()
						+ "\n";
				break;
			case UPDATED:
				content2 += "    UPDATED: " + group + " to new rights: "
						+ n.getGroups().get(group) + "\n";
				break;
			case REMOVED:
				content2 += "    REMOVED: " + group + "\n";
				break;
			}
		}

		if (changes.getUserChanges().size() != 0)
			content2 += "\nUSER CHANGES \n";

		for (String user : changes.getUserChanges().keySet()) {
			Change userCh = changes.getUser(user);
			switch (userCh.getAction()) {
			case ADDED:
				content2 += "    ADDED: " + user + ", rights: " + userCh.getRights()
						+ "\n";
				break;
			case UPDATED:
				content2 += "    UPDATED: " + user + " to new rights: "
						+ n.getUsers().get(user) + "\n";
				break;
			case REMOVED:
				content2 += "    REMOVED: " + user + " \n";
				break;
			}
		}
		if (content2.length() == 0)
			return null;

		content2 += formatLine;
		content += content2;

		return content + "\n";
	}

	/**
	 * Prepares the content of the e-mail for node unlocking.
	 * 
	 * @param svn the name of the SVN
	 * @param fullpath the path to the node in the SVN
	 * @param lock the node which has been changed
	 * @param originatorUser the user who made the changes
	 * @param reason the reason of the changes
	 */
	private String prepareContentUnlock(String svn, String fullpath, Node lock,
			User originatorUser, String reason) throws Exception {
		String content = "";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String date = sdf.format(cal.getTime());
		content += "Time of change: " + date + " by user: "
				+ originatorUser.getUserName() + "\n";
		content += "SVN: " + svn + "\n";
		content += "Path: " + fullpath + "\n";
		content += "Reason: " + reason + "\n";
		IAccessListReader acr = AccessListReader.getInstance();
		String content2 = "";

		if (lock.getChanges().isInheritance()) {
			if (lock.isInheritance())
				content2 += "Inheritance set to disabled \n";
			else
				content2 += "Inheritance set to enabled \n";

		}

		if (lock.getChanges().getGroupChanges().size() != 0)
			content2 += "GROUP CHANGES \n";

		for (String group : lock.getChanges().getGroupChanges().keySet()) {
			Change groupCh = lock.getChanges().getGroup(group);
			Rights orig = lock.getGroups().get(group);
			switch (groupCh.getAction()) {
			case ADDED:
				content2 += "    REMOVED: " + group + "\n";
				// content += "    ADDED: " + group + ", rights: " + orig +
				// "\n";
				break;
			case UPDATED:
				Rights origCopy = new Rights(orig.toString());

				if (groupCh.getRights().isModify())
					origCopy.setModify(!orig.isModify());
				if (groupCh.getRights().isRead())
					origCopy.setRead(!orig.isRead());
				if (groupCh.getRights().isWrite())
					origCopy.setWrite(!orig.isWrite());

				content2 += "    UPDATED: " + group + " to new rights: " + origCopy
						+ "\n";
				break;

			}
		}

		if (lock.getChanges().getUserChanges().size() != 0)
			content2 += "\nUSER CHANGES \n";

		for (String user : lock.getChanges().getUserChanges().keySet()) {
			Change userCh = lock.getChanges().getUser(user);
			Rights orig = lock.getUsers().get(user);
			switch (userCh.getAction()) {
			case ADDED:
				content2 += "    REMOVED: " + user + " \n";
				// content += "    ADDED: " + user + ", rights: " + orig + "\n";
				break;
			case UPDATED:
				Rights origCopy = new Rights(orig.toString());
				if (userCh.getRights().isModify())
					origCopy.setModify(!orig.isModify());
				if (userCh.getRights().isRead())
					origCopy.setRead(!orig.isRead());
				if (userCh.getRights().isWrite())
					origCopy.setWrite(!orig.isWrite());
				content2 += "    UPDATED: " + user + " to new rights: " + origCopy
						+ "\n";
				break;
			}
		}
		if (content2.length() == 0)
			return null;

		content2 += formatLine;
		content += content2;
		return content + "\n";
	}

	/**
	 * Prepares the content of the e-mail for node locking.
	 * 
	 * @param svn the name of the SVN
	 * @param fullpath the path to the node in the SVN
	 * @param lock the node which has been changed
	 * @param originatorUser the user who made the changes
	 * @param reason the reason of the changes
	 */
	private String prepareContentLock(String svn, String fullpath, Node lock,
			User originatorUser, String reason) throws Exception {
		String content = "";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String date = sdf.format(cal.getTime());
		content += "Time of change: " + date + " by user: "
				+ originatorUser.getUserName() + "\n";
		content += "SVN: " + svn + "\n";
		content += "Path: " + fullpath + "\n";
		content += "Reason: " + reason + "\n";
		IAccessListReader acr = AccessListReader.getInstance();
		// System.out.println("bla");
		// opossite action
		String content2 = "";

		Node n = acr.getTree(svn).getNode(fullpath);
		if (lock.getChanges().isInheritance()) {
			if (lock.isInheritance())
				content2 += "Inheritance set to enabled \n";
			else
				content2 += "Inheritance set to disabled \n";

		}

		if (lock.getChanges().getGroupChanges().size() != 0)
			content2 += "GROUP CHANGES \n";

		for (String group : lock.getChanges().getGroupChanges().keySet()) {
			Change groupCh = lock.getChanges().getGroup(group);
			Rights orig = lock.getGroups().get(group);
			switch (groupCh.getAction()) {
			case ADDED:
				content2 += "    ADDED: " + group + ", rights: " + groupCh.getRights()
						+ "\n";
				break;
			case UPDATED:
				content2 += "    UPDATED: " + group + " to new rights: "
						+ n.getGroups().get(group) + "\n";
				break;
			case REMOVED:
				content2 += "    REMOVED: " + group + "\n";
				break;
			}
		}

		if (lock.getChanges().getUserChanges().size() != 0)
			content2 += "\nUSER CHANGES \n";

		for (String user : lock.getChanges().getUserChanges().keySet()) {
			Change userCh = lock.getChanges().getUser(user);
			Rights orig = lock.getUsers().get(user);
			switch (userCh.getAction()) {
			case ADDED:
				content2 += "    ADDED: " + user + ", rights: " + userCh.getRights()
						+ "\n";
				break;
			case UPDATED:
				content2 += "    UPDATED: " + user + " to new rights: "
						+ n.getUsers().get(user) + "\n";
				break;
			case REMOVED:
				content2 += "    REMOVED: " + user + " \n";
				break;
			}
		}
		if (content2.length() == 0)
			return null;

		content2 += formatLine;
		content += content2;

		return content + "\n";
	}
}