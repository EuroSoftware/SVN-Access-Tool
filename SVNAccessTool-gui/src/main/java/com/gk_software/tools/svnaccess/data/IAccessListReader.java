package com.gk_software.tools.svnaccess.data;

import java.io.IOException;
import java.util.Map;

import com.gk_software.tools.svnaccess.data.accessList.AccessTree;

/**
 * Access list reader interface. Methods are important for building a tree of rights
 * for the svn structure.
 */
public interface IAccessListReader {

	/**
	 * Build an access list tree.
	 *
	 * @throws Exception building failed
	 *
	 */
	public void buildAccessRightsTrees() throws Exception;

	/**
	 * Reads the file with modification rights and updates the access list tree.
	 *
	 * @throws IOException adding failed
	 *
	 */
	public void addModifyRights() throws IOException;

	/**
	 * Saves the access tree to a file in the correct format.
	 */
	public String saveAccessList();

	/**
	 * Returns all the SVN trees.
	 *
	 * @return the map of the SVN trees
	 *
	 */
	public Map<String, AccessTree> getTrees();

	/**
	 * Returns the access tree for the given SVN.
	 *
	 * @param SVN the name of the SVN whose access tree is returned
	 *
	 * @return the access tree for the given SVN
	 *
	 */
	public AccessTree getTree(String SVN);

	/**
	 * Adds a node to the given SVN and path.
	 *
	 * @param SVN the name of the SVN to which the node is added
	 * @param path the path where the node is added
	 *
	 */
	public void addNode(String SVN, String path);

	/**
	 * Checks whether the given SVN has an existing access tree .
	 *
	 * @param SVN the name of the SVN whose access tree is searched
	 *
	 * @return true if the access tree exists otherwise false
	 *
	 */
	public boolean isExistTree(String SVN);
}