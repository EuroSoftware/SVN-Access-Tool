package beans;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent the log item.
 */
public class Item {

	/** The backup timestamp. */
	private long backupTimestamp;

	/** The time of the message. */
	private String timeOfMessage;

	/** The date of the message. */
	private String dateOfMessage;

	/** The type of the message. */
	private String typeOfMessage;

	/** The logged user. */
	private String loggedUser;

	/** The node name. */
	private String node;

	/** The list of the group changes. */
	private List<String> groupChanges;

	/** The list of the user changes. */
	private List<String> userChanges;

	/** The indicator if the item is valid. */
	private boolean valid;

	/** The name of the backup file for access list. */
	private String backupFileAcl;

	/** The name of the backup file for the changes in the access list. */
	private String backupFileModAcl;

	/** The inheritance string. */
	private String inheritance;

	/** The start tag. */
	private final String START_TAG = "<b style=background-color:yellow>";

	/** The end tag. */
	private final String END_TAG = "</b>";

	/** The indicator whether the searched text has been found. */
	private boolean findText = false;

	/** The path to the repository. */
	private String svn;

	/**
	 * Creates a new instance and initializes all the other variables.
	 */
	public Item() {
		this.groupChanges = new ArrayList<String>();
		this.userChanges = new ArrayList<String>();
	}

	/**
	 * Returns the path to the repository.
	 *
	 * @return the path to the repository
	 *
	 */
	public String getSvn() {
		return svn;
	}

	/**
	 * Sets the new path to the repository.
	 *
	 * @param svn the new path to the repository
	 *
	 */
	public void setSvn(String svn) {
		this.svn = svn;
	}

	/**
	 * Returns the indicator whether the searched text has been found.
	 *
	 * @return the indicator whether the searched text has been found
	 *
	 */
	public boolean isFindText() {
		return findText;
	}

	/**
	 * Sets the new indicator whether the searched text has been found.
	 *
	 * @param findText the indicator whether the searched text has been found
	 *
	 */
	public void setFindText(boolean findText) {
		this.findText = findText;
	}

	/**
	 * Returns the inheritance string.
	 *
	 * @return the inheritance string
	 *
	 */
	public String getInheritance() {
		return inheritance;
	}

	/**
	 * Sets the new inheritance string.
	 *
	 * @param inheritance the new inheritance string
	 *
	 */
	public void setInheritance(String inheritance) {
		this.inheritance = inheritance;
	}

	/**
	 * Returns the indicator if the item is valid.
	 *
	 * @return the indicator if the item is valid
	 *
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Sets the new indicator if the item is valid.
	 *
	 * @param valid the new indicator if the item is valid
	 *
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * Returns the backup timestamp.
	 *
	 * @return the backup timestamp
	 *
	 */
	public long getBackupTimestamp() {
		return backupTimestamp;
	}

	/**
	 * Returns the time of the message.
	 *
	 * @return the time of the message
	 *
	 */
	public String getTimeOfMessage() {
		return timeOfMessage;
	}

	/**
	 * Sets the new time of the message.
	 *
	 * @param timeOfMessage the new time of the message
	 *
	 */
	public void setTimeOfMessage(String timeOfMessage) {
		this.timeOfMessage = timeOfMessage;
	}

	/**
	 * Returns the date of the message.
	 *
	 * @return the date of the message
	 *
	 */
	public String getDateOfMessage() {
		return dateOfMessage;
	}

	/**
	 * Sets the new date of the message.
	 *
	 * @param dateOfMessage the new date of the message
	 *
	 */
	public void setDateOfMessage(String dateOfMessage) {
		this.dateOfMessage = dateOfMessage;
	}

	/**
	 * Returns the type of the message.
	 *
	 * @return the type of the message
	 *
	 */
	public String getTypeOfMessage() {
		return typeOfMessage;
	}

	/**
	 * Sets the new type of the message.
	 *
	 * @param selected the new type of the message
	 *
	 */
	public void setTypeOfMessage(String typeOfMessage) {
		this.typeOfMessage = typeOfMessage;
	}

	/**
	 * Sets the new backup timestamp.
	 *
	 * @param backupTimestamp the new backup timestamp
	 *
	 */
	public void setBackupTimestamp(long backupTimestamp) {
		this.backupTimestamp = backupTimestamp;
	}

	/**
	 * Returns the logged user.
	 *
	 * @return the logged user
	 *
	 */
	public String getLoggedUser() {
		return loggedUser;
	}

	/**
	 * Sets the new logged user.
	 *
	 * @param loggedUser the new logged user
	 *
	 */
	public void setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
	}

	/**
	 * Returns the node name.
	 *
	 * @return the node name
	 *
	 */
	public String getNode() {
		return node;
	}

	/**
	 * Sets the new node name.
	 *
	 * @param node the new node name
	 *
	 */
	public void setNode(String node) {
		this.node = node;
	}

	/**
	 * Returns the list of the group changes.
	 *
	 * @return the list of the group changes
	 *
	 */
	public List<String> getGroupChanges() {
		return groupChanges;
	}

	/**
	 * Sets the new list of the group changes.
	 *
	 * @param groupChanges the new list of the group changes
	 *
	 */
	public void setGroupChanges(List<String> groupChanges) {
		this.groupChanges = groupChanges;
	}

	/**
	 * Adds the given group change to the list.
	 *
	 * @param groupChange the added group change
	 *
	 */
	public void addGroupChange(String groupChange) {
		this.groupChanges.add(groupChange);
	}

	/**
	 * Returns the list of the user changes.
	 *
	 * @return the list of the user changes
	 *
	 */
	public List<String> getUserChanges() {
		return userChanges;
	}

	/**
	 * Sets the new list of the user changes.
	 *
	 * @param userChanges the new list of the user changes
	 *
	 */
	public void setUserChanges(List<String> userChanges) {
		this.userChanges = userChanges;
	}

	/**
	 * Adds the given user change to the list.
	 *
	 * @param userChange the added user change
	 *
	 */
	public void addUserChange(String userChange) {
		this.userChanges.add(userChange);
	}

	/**
	 * Returns the name of the backup file for access list.
	 *
	 * @return the name of the backup file for access list
	 *
	 */
	public String getBackupFileAcl() {
		return backupFileAcl;
	}

	/**
	 * Sets the new name of the backup file for access list.
	 *
	 * @param backupFileAcl the new name of the backup file for access list
	 *
	 */
	public void setBackupFileAcl(String backupFileAcl) {
		this.backupFileAcl = backupFileAcl;
	}

	/**
	 * Returns the name of the backup file for the changes in the access list.
	 *
	 * @return the name of the backup file for the changes in the access list
	 *
	 */
	public String getBackupFileModAcl() {
		return backupFileModAcl;
	}

	/**
	 * Sets the new name of the backup file for the changes in the access list.
	 *
	 * @param backupFileModAcl the new name of the backup file for the changes in
	 * the access list
	 *
	 */
	public void setBackupFileModAcl(String backupFileModAcl) {
		this.backupFileModAcl = backupFileModAcl;
	}

	/**
	 * Marks the given text with the start and end tags.
	 *
	 * @param search the searched text
	 *
	 */
	public synchronized void markText(String search) {
		Pattern pattern = Pattern.compile(search);
		Matcher matcher = pattern.matcher(loggedUser);

		if (matcher.find()) {
			String match = (String) loggedUser.subSequence(matcher.start(),
					matcher.end());
			loggedUser = loggedUser.replaceAll(match, START_TAG + match
					+ END_TAG);
			findText = true;
		}
		ArrayList<String> tempList = new ArrayList<String>();

		for (int i = 0; i < groupChanges.size(); i++) {
			String group = groupChanges.get(i);
			matcher = pattern.matcher(group);

			if (matcher.find()) {
				String match = (String) group.subSequence(matcher.start(),
						matcher.end());
				group = group.replaceAll(match, START_TAG + match + END_TAG);
				findText = true;
			}
			tempList.add(group);
		}
		groupChanges = tempList;

		tempList = new ArrayList<String>();

		for (int i = 0; i < userChanges.size(); i++) {
			String user1 = userChanges.get(i);
			Matcher matcher2 = pattern.matcher(user1);

			if (matcher2.find()) {
				String match = (String) user1.subSequence(matcher2.start(),
						matcher2.end());
				user1 = user1.replaceAll(match, START_TAG + match + END_TAG);
				findText = true;
			}
			tempList.add(user1);
		}
		userChanges = tempList;

		if (isValid()) {
			matcher = pattern.matcher(backupFileAcl);

			if (matcher.find()) {
				String match = (String) backupFileAcl.subSequence(
						matcher.start(), matcher.end());
				backupFileAcl = backupFileAcl.replaceAll(match, START_TAG
						+ match + END_TAG);
				findText = true;
			}

			matcher = pattern.matcher(backupFileModAcl);

			if (matcher.find()) {
				String match = (String) backupFileModAcl.subSequence(
						matcher.start(), matcher.end());
				backupFileModAcl = backupFileModAcl.replaceAll(match, START_TAG
						+ match + END_TAG);
				findText = true;
			}
		}
	}
}
