package com.gk_software.tools.svnaccess.data;

import java.util.List;

/**
 * SVN reader interface. Contains all important method used in the application.
 */
public interface ISVNStructureReader {

	/**
	 * Returns the list of the SVN repositories.
	 *
	 * @return the list of the SVN repositories
	 */
	public List<String> getRepositories();

	/**
	 * Returns the list of the names of the children of the node on the given path
	 * and SVN.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return the list of the names of the children
	 *
	 * @throws Exception children retreival failed
	 *
	 */
	public List<String> getChildren(String repoPath, String fullpath)
			throws Exception;

	/**
	 * Checks whether the node on the given path and SVN has subdirs.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param fileView indicator if we are interested also in file
	 *
	 * @return true if the node contains subdirs otherwise false
	 *
	 * @throws Exception check failed
	 *
	 */
	public boolean hasChildren(String repoPath, String fullpath,
			boolean fileView) throws Exception;

	/**
	 * Checks whether the node on the given path and SVN exists.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 * @return true if the node exists otherwise false
	 *
	 */
	public boolean isSvnExist(String repoPath, String fullpath);
}
