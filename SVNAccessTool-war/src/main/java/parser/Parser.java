package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import beans.Item;

/**
 * Represents a file parser.
 */
public class Parser {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(Parser.class);

	/** The name of the access list file. */
	private String aclFileName;

	/** The list of logs. */
	private List<Item> logs;

	/** The time from which are the logs shown. */
	private String from;

	/** The time to which are the logs shown. */
	private String to;

	/** The searched text. */
	private String search;

	/** The amount of days ago from which are the logs displayed. */
	private int daysAgo;

	/** The value of the radio button. */
	private String radio;

	/** The date from which are the logs shown. */
	private Date fromD;

	/** The date to which are the logs shown. */
	private Date toD;

	/** The set of the repositories. */
	private Set<String> repos;

	/** The selected repository. */
	private String repo;

	/** The indicator if the user is admin. */
	private boolean isAdmin;

	/**
	 * Creates a new instance, sets the variables according to the given values and
	 * initializes all the other variables.
	 *
	 * @param aclFileName the name of the access list file
	 * @param from the time from which are the logs shown
	 * @param to the time to which are the logs shown
	 * @param search the searched text
	 *
	 */
	public Parser(String aclFileName, String from, String to, String search) {
		super();
		this.aclFileName = aclFileName;
		this.from = from;
		this.to = to;
		this.logs = new ArrayList<Item>();
		this.search = search;
		this.repos = new HashSet<String>();
	}

	/**
	 * Creates a new instance, sets the variables according to the given values and
	 * initializes all the other variables.
	 *
	 * @param aclFileName the name of the access list file
	 * @param from the time from which are the logs shown
	 * @param to the time to which are the logs shown
	 * @param search the searched text
	 * @param daysAgo the amount of days ago from which are the logs displayed
	 *
	 */
	public Parser(String aclFileName, String from, String to, String search,
			int daysAgo) {
		super();
		this.aclFileName = aclFileName;
		this.from = from;
		this.to = to;
		this.logs = new ArrayList<Item>();
		this.search = search;
		this.daysAgo = daysAgo;
		this.repos = new HashSet<String>();
	}

	/**
	 * Returns the indicator if the user is admin.
	 *
	 * @return the indicator if the user is admin
	 *
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * Sets the new indicator if the user is admin.
	 *
	 * @param isAdmin the new indicator if the user is admin
	 *
	 */
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * Returns the selected repository.
	 *
	 * @return the selected repository
	 *
	 */
	public String getRepo() {
		return repo;
	}

	/**
	 * Sets the new selected repository.
	 *
	 * @param repo the new selected repository
	 *
	 */
	public void setRepo(String repo) {
		this.repo = repo;
	}

	/**
	 * Returns the date from which are the logs shown.
	 *
	 * @return the date from which are the logs shown
	 *
	 */
	public Date getFromD() {
		return fromD;
	}

	/**
	 * Sets the new date from which are the logs shown.
	 *
	 * @param fromD the new date from which are the logs shown
	 *
	 */
	public void setFromD(Date fromD) {
		this.fromD = fromD;
	}

	/**
	 * Returns the date to which are the logs shown.
	 *
	 * @return the date to which are the logs shown
	 *
	 */
	public Date getToD() {
		return toD;
	}

	/**
	 * Sets the new date to which are the logs shown.
	 *
	 * @param toD the new date to which are the logs shown
	 *
	 */
	public void setToD(Date toD) {
		this.toD = toD;
	}

	/**
	 * Returns the value of the radio button.
	 *
	 * @return the value of the radio button
	 *
	 */
	public String getRadio() {
		return radio;
	}

	/**
	 * Sets the new value of the radio button.
	 *
	 * @param radio the new value of the radio button
	 *
	 */
	public void setRadio(String radio) {
		this.radio = radio;
	}

	/**
	 * Returns the set of the repositories.
	 *
	 * @return the set of the repositories
	 *
	 */
	public Set<String> getRepos() {
		return repos;
	}

	/**
	 * Sets the new set of the repositories.
	 *
	 * @param repos the new set of the repositories
	 *
	 */
	public void setRepos(Set<String> repos) {
		this.repos = repos;
	}

	/**
	 * Returns the name of the access list file.
	 *
	 * @return the name of the access list file
	 *
	 */
	public String getAclFileName() {
		return aclFileName;
	}

	/**
	 * Sets the new name of the access list file.
	 *
	 * @param aclFileName the new name of the access list file
	 *
	 */
	public void setAclFileName(String aclFileName) {
		this.aclFileName = aclFileName;
	}

	/**
	 * Returns the list of logs.
	 *
	 * @return the list of logs
	 *
	 */
	public List<Item> getLogs() {
		return logs;
	}

	/**
	 * Sets the new list of logs.
	 *
	 * @param logs the new list of logs
	 *
	 */
	public void setLogs(List<Item> logs) {
		this.logs = logs;
	}

	/**
	 * Parses the logs.
	 *
	 * @throws ParseException, IOException the reading failed
	 *
	 */
	public void reader() throws ParseException, IOException {
		BufferedReader br = null;
		br = new BufferedReader(new FileReader(aclFileName));
		String line;
		Item item = null;
		boolean insideGroup = false;
		boolean insideUser = false;
		boolean valid = true;
		boolean startTag = false;
		DateFormat dfm2 = new SimpleDateFormat("dd.MM.yyyy");

		if (radio.equals("daysAgo")) {
			fromD = dfm2.parse(to.trim());
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(fromD);
			gc.add(Calendar.DAY_OF_YEAR, -daysAgo);
			fromD = gc.getTime();

		} else {
			fromD = dfm2.parse(from.trim());
		}
		toD = dfm2.parse(to.trim());

		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.length() == 0)
				continue;

			String[] split = line.split(" ");

			DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
			// time from acl
			Date a = dfm.parse(split[1]);
			// time from servlet,

			// System.out.println(dfm.format(fromD));
			// System.out.println(dfm.format(toD));

			if (a.equals(fromD) || a.equals(toD)
					|| (a.after(fromD) && a.before(toD))) {
				// System.out.println(dfm.format(a) + " " +
				// dfm.format(fromD)
				// + " " + dfm.format(toD));

				String message = line.substring(42).trim();
				// System.out.println(message);

				if (message.startsWith("LOGGED USER")) {
					// System.out.println(message);
					if (message.endsWith("changes are not performed")) {
						valid = false;
					} else {
						valid = true;
					}
					startTag = true;
					// System.out.println(valid);
					item = new Item();
					item.setTimeOfMessage(split[2]);
					item.setDateOfMessage(split[1]);
					item.setTypeOfMessage(split[0]);
					item.setLoggedUser(message);
				}
				if (message.startsWith("Inheritance")) {
					item.setInheritance(message);
				}

				if (message.startsWith("NODE:")) {
					String svn = message.split("SVN:")[1].trim();
					item.setNode(message);
					item.setSvn(svn);
				}
				if (message.startsWith("GROUP CHANGES")) {
					insideGroup = true;
					continue;

				}
				if (message.startsWith("USER CHANGES")) {
					insideGroup = false;
					insideUser = true;
					continue;

				}
				if (message.startsWith("End of updating rights in ACL tree")) {
					insideUser = false;
				}
				if (message.startsWith("End of saving changes")) {
					insideUser = false;
					startTag = false;
					item.setValid(valid);
					// System.out.println(item.getLoggedUser());
					item.markText(search);
					if (item.isFindText()) {
						if (repo.equals("#allRepos")) {
							if (repos.contains(item.getSvn()) || isAdmin)
								logs.add(item);

						} else if (repo.equals(item.getSvn())) {
							logs.add(item);
						}
					}
				}

				if (insideGroup) {
					item.addGroupChange(message);
					continue;
				}
				if (insideUser) {
					item.addUserChange(message);
					continue;
				}
				if (message.startsWith("Copy acl") && startTag) {
					item.setBackupFileAcl(message);
				}
				if (message.startsWith("Copy modify acl") && startTag) {
					item.setBackupFileModAcl(message);
				}

			}
		}

		try {
			logger.info("End of reading log file, closing file.");
			if (br != null)
				br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
