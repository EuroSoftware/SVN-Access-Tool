package com.gk_software.tools.svnaccess.data.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;

import com.gk_software.tools.svnaccess.data.ISVNStructureReader;
import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Implements the methods from the interface {@code ISVNStructureReader}.
 */
public class SVNReader implements ISVNStructureReader {
	
	/** The path to the SVN. */
	private String path;
	
	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(SVNReader.class);

	/** The SVN repository. */
	private SVNRepository repos;

	/**
	 * Creates a new instance and initializes the variable {@code path} according to the given value.
	 * 
	 * @param path the path to the SVN
	 * 
	 */
	public SVNReader(String path) {
		this.path = path;
	}

	/**
	 * Connects to the given SVN.
	 * 
	 * @param svn the path to the SVN
	 * 
	 * @return SVNRepository or null if connection failed.
	 * 
	 * @throws Exception connecting failed
	 */
	private SVNRepository SVNconnection(String svn) throws Exception {
		SVNURL url;
		SVNRepository repos;
		try {
			// readLoginInfo();
			url = SVNURL.parseURIEncoded(path + "/" + svn);
			repos = SVNRepositoryFactory.create(url);
			BasicAuthenticationManager authManager = new BasicAuthenticationManager(
					Constants.getUSERNAME_SVN(), Constants.getPASSWORD_SVN());
			repos.setAuthenticationManager(authManager);
			logger.info("SVN url: " + url.toString());
			return repos;
		} catch (Exception e) {
			logger.error("Error during SVN repository connection.");
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Checks if the given path points to an SVN.
	 * 
	 * @param string the checked path
	 * 
	 * @return true if it points to SVN otherwise false
	 *
	 */
	private boolean isSVN(String string) {

		boolean isOk = true;
		/*
		 * try { url = SVNURL.parseURIEncoded(path + "/" + string); repos =
		 * SVNRepositoryFactory.create(url); BasicAuthenticationManager
		 * authManager = new BasicAuthenticationManager(
		 * Constants.getUSERNAME_SVN(), Constants.getPASSWORD_SVN());
		 * repos.setAuthenticationManager(authManager);
		 * 
		 * logger.info("SVN url: " + url.toString()); SVNURL root =
		 * repos.getRepositoryRoot(true); SVNURLUtil.getRelativeURL(root, url);
		 * // Collection<SVNDirEntry> entries = repos.getDir("", -1, null, //
		 * (Collection<SVNDirEntry>) null); } catch (Exception e) {
		 * logger.info("Exc "+string+" "+e.getMessage()); e.printStackTrace();
		 * logger.info(e.getStackTrace()); isOk = false; }
		 */

		File f = new File(path.substring(7) + "/" + string + "/hooks");
		if (!f.exists())
			isOk = false;

		return isOk;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gk_software.tools.svnaccess.data.ISVNStructureReader#getRepositories()
	 */
	public List<String> getRepositories() {

		List<String> repositories = new ArrayList<String>();
		String path2 = path.substring(7);
		logger.info("Read repositories from following path: " + path2);
		try {
			File f = new File(path2);
			File[] filesList = f.listFiles();
			for (int i = 0; i < filesList.length; i++) {
				// logger.info("Read from a disk: "+filesList[i].getName()+" "+filesList[i].isDirectory());
				if (filesList[i].isDirectory()) {
					if (isSVN(filesList[i].getName())) {
						repositories.add(filesList[i].getName());
					}
				}

			}
		} catch (Exception e) {
			logger.error("Error during reading repositories, please check the path: "
					+ path2);
			logger.error(e.getMessage());
		}
		return repositories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gk_software.tools.svnaccess.data.ISVNStructureReader#getChildren
	 * (java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<String> getChildren(String repoPath, String fullpath)
			throws Exception {
		List<String> children = new ArrayList<String>();
		long revision;

		this.repos = SVNconnection(repoPath);

		Collection<SVNDirEntry> entries;
		try {
			revision = repos.getLatestRevision();
			entries = repos.getDir(fullpath, revision, null,
					(Collection<SVNDirEntry>) null);

			Iterator<SVNDirEntry> iter = entries.iterator();
			while (iter.hasNext()) { // all files
				SVNDirEntry file = iter.next();
				SVNNodeKind kind = file.getKind();
				// System.out.println(file.getName());
				// children.add(file.getName());

				if (kind.equals(SVNNodeKind.DIR)) {
					children.add(file.getName());
				}
			}
			// long b = System.currentTimeMillis();
			// System.out.println("time for "+fullpath+" "+(b-a));
		} catch (Exception e) {
			logger.error("Error during getting SVN children.");
			logger.error(e.getMessage());
			// e.printStackTrace();
		}

		return children;
	}

	/**
	 * Checks if the given path exists in the given SVN.
	 * 
	 * @param repoPath svn where the path is searched
	 * @param fullpath the path to the node in the repository
	 * 
	 * @return true if it exists otherwise false
	 *
	 */
	public boolean isSvnExist(String repoPath, String fullpath) {
		long revision;
		
		if (this.repos == null) {
			try {
				this.repos = SVNconnection(repoPath);
			} catch (Exception e) {
				logger.error("Error during SVN connection.");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
		try {
			revision = repos.getLatestRevision();
			repos.getDir(fullpath, revision, null,
					(Collection<SVNDirEntry>) null);
		} catch (Exception e) {
			logger.error("Directory: " + fullpath + " does not exist in svn: "
					+ repoPath);
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.gk_software.tools.svnaccess.data.ISVNStructureReader#hasChildren
	 * (java.lang.String, java.lang.String, boolean)
	 */
	public boolean hasChildren(final String repoPath, String fullpath, 
			boolean fileView) throws Exception {
		long revision;

		this.repos = SVNconnection(repoPath);

		Collection<SVNDirEntry> entries;
		try {
			revision = repos.getLatestRevision();
			entries = repos.getDir(fullpath, revision, null,
					(Collection<SVNDirEntry>) null);

			Iterator<SVNDirEntry> iter = entries.iterator();
			while (iter.hasNext()) { // all files
				SVNDirEntry file = iter.next();
				SVNNodeKind kind = file.getKind();
				// System.out.println(file.getName());
				// children.add(file.getName());

				if (kind.equals(SVNNodeKind.DIR)) {
					return true;
				}
			}
			// long b = System.currentTimeMillis();
			// System.out.println("time for "+fullpath+" "+(b-a));
		} catch (Exception e) {
			logger.error("Error during getting SVN children.");
			logger.error(e.getMessage());
			// e.printStackTrace();
		}
		return false;

		// Map dirProps = null;
		// Map fileProps = null;
		// try {
		//
		// this.repos2 = SVNconnection(repoPath);
		//
		// rev = repos2.getLatestRevision();
		//
		// reporter = new ISVNReporterBaton() {
		// public void report(ISVNReporter reporter) throws SVNException {
		// reporter.setPath("", null, rev, SVNDepth.IMMEDIATES, true);
		// reporter.finishReport();
		// }
		// };
		// // our editor only stores properties of files and directories
		// PropFetchingEditor editor = new PropFetchingEditor();
		// editor.openDir(fullpath, rev);
		// // long b = System.currentTimeMillis();
		// // System.out.println(" " + (b - a));
		//
		// repos2.status(rev, fullpath, SVNDepth.IMMEDIATES, reporter,
		// editor);
		// // System.out.println(" " + (System.currentTimeMillis() - b));
		//
		// // now iterate over file and directory properties and print them out
		// // to
		// // the console
		// dirProps = editor.getDirsToProps();
		// fileProps = editor.getFilesToProps();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// if (fileView) {
		// if (dirProps.size() > 1 || fileProps.size() > 1)
		// return true;
		// else
		// return false;
		// } else {
		// if (dirProps.size() > 1)
		// return true;
		// else
		// return false;
		// }
	}
}