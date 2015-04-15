package com.gk_software.tools.svnaccess.data.accessList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.bussiness.impl.Changes;
import com.gk_software.tools.svnaccess.data.IAccessListReader;
import com.gk_software.tools.svnaccess.data.ldap.LdapProvider;
import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Implements the methods from the interface {@code IAccessListReader}.
 */
public class AccessListReader implements IAccessListReader {

	/** The path to the access.list file. */
	private String fileACL;

	/** The path to the modifyACL.list file. */
	private String fileModifyACL;

	/** The instance of this class. */
	private static AccessListReader accessListReader = null;

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(AccessListReader.class);

	/** The instance of the {@code Logger} class for ACL. */
	private static final Logger aclLogger = Logger.getLogger("ACL_changes");

	/** The map with svn name as key and its access tree as value. */
	private Map<String, AccessTree> trees;

	/**
	 * Creates a new instance and initializes the variables according to the given
	 * values.
	 *
	 * @param fileACL the path to the access.list file
	 * @param fileModifyACL the path to the modifyACL.list file
	 *
	 */
	private AccessListReader(String fileACL, String fileModifyACL) {
		this.fileACL = fileACL;
		this.fileModifyACL = fileModifyACL;
		trees = new HashMap<String, AccessTree>();
	}

	/**
	 * Singleton class. This method provides only one instance of this class.
	 *
	 * @return the instance of this class
	 *
	 * @throws Exception instance retrieval failed
	 */
	public static AccessListReader getInstance() throws Exception {

		if (accessListReader == null) {
			accessListReader = new AccessListReader(Constants.getProperties()
					.getProperty("aclLocation"), Constants.getACCESS_LIST_MODIFY_PATH());

			accessListReader.buildAccessRightsTrees();
			logger.info("Creating a new instance of accessList reader.");
		}
		return accessListReader;
	}

	/**
	 * Reloads the access list tree.
	 *
	 * @throws Exception reloading failed
	 */
	public static void reload() throws Exception {
		accessListReader = new AccessListReader(Constants.getProperties()
				.getProperty("aclLocation"), Constants.getACCESS_LIST_MODIFY_PATH());
		accessListReader.buildAccessRightsTrees();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IAccessListReader#buildAccessRightsTrees()
	 */
	public void buildAccessRightsTrees() throws Exception {
		addModifyRights();

		// Map<String, String> responsiblePersons = new HashMap<String,
		// String>();
		logger.info("Start of building a accessList tree.");
		File f = null;
		String responsiblePerson = "";

		BufferedReader br = null;
		try {
			f = new File(fileACL);
			br = new BufferedReader(new FileReader(f));

			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {
					if (line.startsWith("#viewSvn"))
						break;
					try {
						int p = line.toLowerCase().indexOf("responsible:");
						if (p != -1) {
							int pp = line.length();
							responsiblePerson = line.substring(p + 12, pp).trim();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (line.startsWith("[") && line.endsWith("]")
						&& !line.equals("[groups]")) {
					String[] parts = line.substring(1, line.length() - 1).split(":");

					addNode(parts[0], parts[1]);

					Node node = getTree(parts[0]).getNode(parts[1]);
					// System.out.println("pats: " + parts[0] + " " + parts[1]);
					// System.out.println("node: " + node.getFullPath() + " "
					// + node.getName() + " " + node.getSvn());

					// System.out.println(node.getFullPath() + " "
					// + node.getUsers());
					// for (String u : node.getUsers().keySet()) {
					// System.out.println(node.getUsers().get(u).toString());
					// }

					if (responsiblePerson.length() != 0) {
						Node nR = getTree(node.getSvn()).getNode(node.getFullPath());
						nR.setResponsiblePerson(responsiblePerson);

						responsiblePerson = "";
					}

					while (true) {
						line = br.readLine().trim();
						if (line.startsWith("#")) // commentary
							break;

						if (line.length() == 0)
							break;

						String[] parts2 = line.split("=");
						String first = parts2[0].trim();
						String second = "";

						if (parts2.length == 2) {
							second = parts2[1].trim();
						} else {
							if (first.equals("*") && second.length() == 0) {
								node.setInheritance(true);
							}
						}

						if (first.startsWith("@")) { // group
							if (!node.getGroups().containsKey(first)) {
								node.addGroup(first, second);
							}
						} else { // single user
							if (first.equals("*")) {
								String[] splitConstant = Constants.getProperties()
										.getProperty("labelReplace").split("=");
								if (second.trim().length() != 0) {
									// node.addGroup("@es", second);
									try {

										if (splitConstant[0].trim().startsWith("@")
												|| splitConstant[0].trim().startsWith("*"))
											node.addGroup(splitConstant[0].trim(), second.trim());
										else {
											node.addUser(splitConstant[0].trim(), second.trim());
										}
									} catch (Exception e) {
										logger
												.error("There are mising property \"labelReplace\" in properties file.");
									}
								}
							} else {
								if (!node.getUsers().containsKey(first)) {
									node.addUser(first, second);
								}
							}
						}
					}
				}

			}

		} catch (IOException e) {
			logger.error("File: " + fileACL + " does not exist. ");
			e.printStackTrace();
			throw e;
		} finally {
			try {
				logger.info("End of reading ACL file, closing file.");
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Node node = getTree("Copy (3) of bla3").getNode("/");
		// System.out.println("pats: " + parts[0] + " " + parts[1]);
		// System.out.println("node: " + node.getFullPath() + " "
		// + node.getName() + " " + node.getSvn());

		// System.out.println(node.getFullPath() + " " + node.getUsers());
		// for (String u : node.getUsers().keySet()) {
		// System.out.println(node.getUsers().get(u).toString());
		// }

		logger.info("End of building a accessList tree.");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IAccessListReader#addModifyRights()
	 */
	public void addModifyRights() throws IOException {
		logger.info("Start of updating ACL tree with modify rights.");
		File f = null;
		BufferedReader br = null;
		Changes changes = null;
		try {
			f = new File(fileModifyACL);
			br = new BufferedReader(new FileReader(f));

			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) {

					continue;
				}

				if (line.startsWith("[") && line.endsWith("]")) {
					String[] parts = line.substring(1, line.length() - 1).split(":");
					changes = new Changes();
					// AccessTree at = trees.get(parts[0]);

					// check whether or not directory exist in existing ACL
					// tree, if not, the modify rights does not read.

					// if (at == null) {
					// continue;
					// // addNode(parts[0], parts[1]);
					// // at = trees.get(parts[0]);
					// }
					//
					addNode(parts[0], parts[1]);

					// Node n = at.getNode(parts[1]);

					Node n = getTree(parts[0]).getNode(parts[1]);

					while (true) {
						line = br.readLine();
						if (line == null)
							break;
						line = line.trim();

						if (line.startsWith("#")) { // commentary
							line = line.substring(2);
							if (line.startsWith("lockedBy")) {
								n.setPersonLocked(line.split("=")[1].trim());
								n.setLocked(true);
							} else {
								addChange(changes, line);
							}
							continue;
						}

						if (line.length() == 0)
							break;

						String[] parts2 = line.split("=");
						String first = parts2[0].trim();
						String second = parts2[1].trim();

						if (first.startsWith("@")) { // group
							n.changeRightsGroup(first, second);
						} else { // single user
							n.changeRightsUser(first, second);
						}
					}
					n.setChanges(changes);
				}
			}
		} catch (IOException e) {
			logger.error("File: " + fileModifyACL + " does not exist. ");
			e.printStackTrace();
			throw e;
		} finally {
			try {
				logger.info("End of reading ACL modify file, closing modify file.");
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.info("End of updating ACL tree with modify rights.");
	}


	/**
	 * Adds the change parsed from the given line to the given changes instance.
	 *
	 * @param changes the instance of the changes class
	 * @param line line from which the change is parsed
	 *
	 */
	private void addChange(Changes changes, String line) {
		if (line.equals("inh")) {
			changes.reverseInheritance();
			return;
		}
		String[] split = line.split(",");
		if (line.startsWith("@")) {

			if (split[2].equals("ADDED")) {
				changes.addGroup(split[0], split[1]);
			} else if (split[2].equals("UPDATED")) {
				changes.updateGroup(split[0], split[1]);
			}
		} else {
			if (split[2].equals("ADDED")) {
				changes.addUser(split[0], split[1]);
			} else if (split[2].equals("UPDATED")) {
				changes.updateUser(split[0], split[1]);
			}
		}

	}

	/**
	 * Returns the set of all the groups from the access list.
	 *
	 * @return the set of all the groups from the access list
	 *
	 */
	private Set<String> collectAllGroups() {
		// groups that are in the access list
		Set<String> listGroup = new HashSet<String>();
		for (String s : Arrays.asList(((String) (Constants.getProperties()
				.get("inheritance_free"))).split(";"))) {
			listGroup.add(s);
		}

		for (String key : trees.keySet()) {
			AccessTree tree = trees.get(key);
			recursion2(tree.getRoot(), listGroup);
		}
		return listGroup;
	}

	/**
	 * Auxiliary method for traversing the group list.
	 *
	 * @param pom the traversed node
	 * @param listGroup the set of already found groups
	 *
	 */
	private void recursion2(Node pom, Set<String> listGroup) {
		Map<String, Rights> groups = pom.getGroups();
		for (String group : groups.keySet()) {
			listGroup.add(group);
		}
		if (!pom.getChildren().isEmpty()) {
			for (Node a : pom.getChildren()) {
				recursion2(a, listGroup);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IAccessListReader#saveAccessList()
	 */
	public String saveAccessList() {
		try {
			logger.info("Start of saving the access list.");
			File f = new File(Constants.getProperties().getProperty("aclLocation"));
			File f2 = new File(Constants.getACCESS_LIST_MODIFY_PATH());

			File backupFolder = new File(Constants.getProperties().getProperty(
					"backupFolder"));

			logger.info("Backup folder: " + backupFolder);

			if (!backupFolder.exists()) {
				backupFolder.mkdirs();
			}
			long currentMillis = System.currentTimeMillis();

			File backup = new File(backupFolder, f.getName() + "-" + currentMillis);

			File backup2 = new File(backupFolder, f2.getName() + "-" + currentMillis);

			FileUtils.copyFile(f, backup);
			logger.info("Copy acl " + f.getAbsolutePath() + "  to " + backup);
			aclLogger.info("  Copy acl " + f.getAbsolutePath() + "  to " + backup);

			FileUtils.copyFile(f2, backup2);
			logger.info("Copy modify acl " + f2.getAbsolutePath() + " to " + backup2);
			aclLogger.info("  Copy modify acl " + f2.getAbsolutePath() + " to "
					+ backup2);

			f = new File(Constants.getProperties().getProperty("aclLocation")
					+ ".temp");
			f2 = new File(Constants.getACCESS_LIST_MODIFY_PATH());

			BufferedWriter bw = null;
			BufferedWriter bw2 = null;
			try {
				String s = LdapProvider.getInstance().saveToACL(collectAllGroups());
				if (s.length() > 0) {
					bw = new BufferedWriter(new FileWriter(f));
					bw2 = new BufferedWriter(new FileWriter(f2));
					bw.write(s);
				} else {
					logger
							.error("Error during reading data from LDAP, Access List will not be save.");
					return "Error during reading data from LDAP";
				}
				for (String key : getTrees().keySet()) {
					AccessTree at = getTree(key);
					at.print(bw2, true, false);
					at.print(bw, false, false);
				}
				bw.write("#viewSvn Access list \n\n");

				bw.write("[viewsvn:/]\n");
				bw.write("* = r \n\n");
				// printing access rights in viewSvn format
				for (String key : getTrees().keySet()) {
					AccessTree at = getTree(key);
					at.print(bw, false, true);
				}

			} catch (IOException e) {
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String stacktrace = sw.toString();
				logger.error(stacktrace);
				e.printStackTrace();
				if (e.getMessage() == null)
					return "";
				else
					return e.getMessage();
			} finally {
				try {
					// System.out.println("uzaviram soubor vystupni");
					bw.flush();
					bw.close();
					bw2.flush();
					bw2.close();
					File f_original_acl = new File(Constants.getProperties().getProperty(
							"aclLocation"));
					f_original_acl.delete();
					f.renameTo(f_original_acl);

				} catch (Exception e) {
					e.printStackTrace();
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String stacktrace = sw.toString();
					logger.error(stacktrace);
				}
			}
			logger.info("End of saving the access list.");
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String stacktrace = sw.toString();
			logger.error(stacktrace);
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.gk_software.tools.svnaccess.data.IAccessListReader#getTrees()
	 */
	public Map<String, AccessTree> getTrees() {
		return trees;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IAccessListReader#getTree
	 * (java.lang.String)
	 */
	public AccessTree getTree(String SVN) {
		if (!isExistTree(SVN))
			addNode(SVN, "/");

		return trees.get(SVN);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IAccessListReader#addNode
	 * (java.lang.String, java.lang.String)
	 */
	public void addNode(String SVN, String path) {
		// System.out.println("add node: " + SVN + " " + path);
		AccessTree tree;
		if (!isExistTree(SVN)) {
			tree = new AccessTree(SVN);
			// System.out.println("creating svn");
		} else {
			tree = trees.get(SVN);
		}
		if (!tree.isPathExist(path)) {
			tree.addNode(path);
		}

		trees.put(SVN, tree);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.gk_software.tools.svnaccess.data.IAccessListReader#isExistTree
	 * (java.lang.String)
	 */
	public boolean isExistTree(String SVN) {
		if (trees.get(SVN) == null)
			return false;

		return true;
	}

	/**
	 * Reinitializes the access list.
	 */
	public static void removeAll() {
		accessListReader = null;
	}
}