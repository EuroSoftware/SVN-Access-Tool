package com.gk_software.tools.svnaccess.view.components.DirectoriesTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.TreeTable;

/**
 * Represents the node tree in the GUI.
 */
public class DirectoriesTreeTable extends TreeTable {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(DirectoriesTreeTable.class);


	/** The name of the name column. */
	public static final String NAME = "name";

	/** The name of the locked column. */
	public static final String LOCKED = "personLocked";

	/** The container for this class. */
	private DirectoriesTreeTableContainer nodeContainer;

	/** The list of the nodes on the path to the root node from the selected node. */
	private List<Node> nodePath;

	/** The instance of this class. */
	private DirectoriesTreeTable thisDirectories = this;

	/** The control class. */
	private IControl control;

	/** The root node. */
	private Node root = null;

	/** The indicator if the node was clicked on. */
	private boolean clickOnItem = false;

	/** The indicator if the node was expanded. */
	private boolean expandListener = false;


	/** The last chosen node. */
	private static Node lastChosenNode;

	/** The last expanded node. */
	private Node lastExpandedNode;

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public DirectoriesTreeTable(final IControl control) {
		logger.info("Parameters: control=" + control);
		this.control = control;
		this.setSelectable(true);
		this.setNullSelectionAllowed(false);
		this.setImmediate(true);
		this.setCacheRate(3);

		this.addListener(new Tree.ExpandListener() {

			public void nodeExpand(ExpandEvent event) {
				// System.out.println("expand listener");
				Node node = (Node) event.getItemId();
				logger.info("Node expanded node=" + node);

				// root node needs special treatment
				if (node == root) {
					control.repositorySelected(root.getSvn());
					return;
				}

				if (lastExpandedNode != null && node != null) {

					if (!node.getParent().equals(lastExpandedNode)) {
						ArrayList<Node> expandedNodeParents = new ArrayList<Node>();
						ArrayList<Node> nodeParents = new ArrayList<Node>();
						Node tmpNode = lastExpandedNode;

						// get all nodes from current selected node to root
						// they are all expanded
						while (tmpNode != null) {
							expandedNodeParents.add(tmpNode);
							tmpNode = tmpNode.getParent();
						}

						// get all node from selected node to root
						tmpNode = node;
						while (tmpNode != null) {
							nodeParents.add(tmpNode);
							tmpNode = tmpNode.getParent();
						}

						Collections.reverse(expandedNodeParents);
						Collections.reverse(nodeParents);

						// now I find common set of node which will be expanded
						int lastCommonNodeIndex = 0;
						for (int i = 1; i < expandedNodeParents.size(); i++) {
							if (nodeParents.size() <= i)
								break; // to avoid null point exception
							if (!nodeParents.get(i).equals(
									expandedNodeParents.get(i))) {
								break;
							}
							lastCommonNodeIndex = i;
						}
						// I collapse all nodes that are under common level of
						// both nodes
						if (lastCommonNodeIndex == expandedNodeParents.size() - 1)
							collapseNode(expandedNodeParents
									.get(lastCommonNodeIndex));
						else
							collapseNode(expandedNodeParents
									.get(lastCommonNodeIndex + 1));
					}
				}

				lastExpandedNode = node;
				// get selected repoPath
				String repoPath = control.getApplication().getRepositories()
						.getChosenRepo();
				// in control new children will be loaded
				if (repoPath != null && node != null) {
					control.directoryExpanded(repoPath, node);
				}

				// lastChosenNode.getFullPath());
				//
			}
		});

		this.addListener(new Tree.CollapseListener() {

			public void nodeCollapse(CollapseEvent event) {
				Node node = (Node) event.getItemId();
				logger.info("Node collapsed node=" + node);

				if (lastExpandedNode != null && node != null)
					lastExpandedNode = node.getParent();
			}
		});
		this.addListener(new ItemClickListener() {

			public void itemClick(ItemClickEvent event) {
				// System.out.println("item click");
				clickOnItem = true;
				Node node = (Node) event.getItemId();
				String repoPath = control.getApplication().getRepositories()
						.getChosenRepo();

				if (!control.pathExist(repoPath, node.getFullPath())) {
					Node pom = node;
					while (pom != null) {
						if (control.pathExist(repoPath, pom.getFullPath()))
							break;
						pom = pom.getParent();
					}

					setLastChosenItem(pom);
					control.getChildren(repoPath, pom.getFullPath());
					expandPath(pom.getFullPath());
					node = pom;
				} else {
					setLastChosenItem(node);
				}

				logger.info("Node was clicked node=" + node);
				logger.info("Last chosen node: " + lastChosenNode);
				logger.info("Last chosen node timestamp: "
						+ lastChosenNode.getTimestamp());

				control.directorySelected(repoPath,
						lastChosenNode.getFullPath());

			}
		});

		this.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {
				Node node = (Node) event.getProperty().getValue();
				if (node != null && !clickOnItem && !expandListener) {
					setLastChosenItem(node);
					// System.out.println("value change listener");
					logger.info("Node was key navigated node=" + node);
					logger.info("Last chosen node: " + lastChosenNode);
					logger.info("Last chosen node timestamp: "
							+ lastChosenNode.getTimestamp());
					String repoPath = control.getApplication()
							.getRepositories().getChosenRepo();

					control.directorySelected(repoPath,
							lastChosenNode.getFullPath());
				}
				if (clickOnItem)
					clickOnItem = false;

			}
		});
		this.setSizeFull();
		this.setColumnHeaderMode(TreeTable.COLUMN_HEADER_MODE_HIDDEN);

		logger.info("Creating new container");
		nodeContainer = new DirectoriesTreeTableContainer(control);

		this.setContainerDataSource(nodeContainer);
		this.setVisibleColumns(new String[] { NAME, LOCKED });
		this.setColumnWidth(LOCKED, 100);
	}

	/**
	 * Sets the new list of the nodes on the path to the root node from the selected
	 * node.
	 *
	 * @param nodePath the new list of the nodes on the path to the root node from
	 * the selected node
	 *
	 */
	public void setNodePath(List<Node> nodePath) {
		this.nodePath = nodePath;
	}

	/**
	 * Returns the last chosen node.
	 *
	 * @return the last chosen node
	 *
	 */
	public static Node getLastChosenNode() {
		return lastChosenNode;
	}

	/**
	 * Returns the indicator if the node was expanded.
	 *
	 * @return the indicator if the node was expanded
	 *
	 */
	public boolean isExpandListener() {
		return expandListener;
	}

	/**
	 * Returns the instance of this class.
	 *
	 * @return the instance of this class
	 *
	 */
	public DirectoriesTreeTable getThisDirectories() {
		return thisDirectories;
	}

	/**
	 * Sets the new instance of this class.
	 *
	 * @param thisDirectories the new instance of this class
	 *
	 */
	public void setThisDirectories(DirectoriesTreeTable thisDirectories) {
		this.thisDirectories = thisDirectories;
	}

	/**
	 * Sets the new indicator if the node was expanded.
	 *
	 * @param expandListener the new indicator if the node was expanded
	 *
	 */
	public void setExpandListener(boolean expandListener) {
		this.expandListener = expandListener;
	}

	/**
	 * Sets the given repository as selected and expands its children nodes list.
	 *
	 * @param nodes the children nodes of the given repo
	 * @param rootNode the selected repo root node
	 *
	 */
	public void repositorySelected(List<Node> nodes, Node rootNode) {
		// System.out.println("repository selected");
		logger.info("Parameters: nodes=" + nodes + " rootNode=" + rootNode);
		// name of chosen repo
		String repoName = control.getApplication().getRepositories()
				.getChosenRepo();
		logger.info("Removing all from container");
		removeAllItems();
		logger.info("Setting root node");
		root = rootNode;
		root.setSvn(repoName);
		root.setChildren(new ArrayList<Node>());
		nodeContainer.addBean(root);

		logger.info("Adding children");
		for (int i = 0; i < nodes.size(); i++) {
			nodeContainer.addBean(nodes.get(i));
			root.addChild(nodes.get(i));
			nodes.get(i).setParent(root);
		}
		this.setCollapsed(root, false);
		this.select(root);
		// this.validate();
		String repoPath = control.getApplication().getRepositories()
				.getChosenRepo();
		setLastChosenItem(root);

		control.directorySelected(repoPath, root.getFullPath());

	}

	/**
	 * Adds the given children to the given parent in the node tree.
	 *
	 * @param nodes the list of the added children
	 * @param parent the parent to which are the children added
	 *
	 */
	public void addChildren(List<Node> nodes, Node parent) {
		logger.info("Parameters: nodes=" + nodes + " parent=" + parent);
		// removing nodes
		logger.info("Removing children from container");
		for (int i = 0; i < parent.getChildren().size(); i++) {
			nodeContainer.removeItem(parent.getChildren().get(i));
		}
		parent.setChildren(new ArrayList<Node>());

		// adding nodes
		logger.info("Adding children to container");
		for (int i = 0; i < nodes.size(); i++) {
			parent.addChild(nodes.get(i));
			nodes.get(i).setParent(parent);
			nodeContainer.addBean(nodes.get(i));
		}
	}

	/**
	 * Colapses the expanded node.
	 *
	 * @param node the node to be colapsed
	 *
	 */
	private void collapseNode(Node node) {
		logger.info("Parameters: node=" + node);
		for (int i = 0; i < node.getChildren().size(); i++) {
			// recursively called method
			_collapseNode(node.getChildren().get(i));
			nodeContainer.removeItem(node.getChildren().get(i));
		}
		// in the end collapse node
		thisDirectories.setCollapsed(node, true);
		logger.info("End method");
	}

	/**
	 * Colapses the expanded node recursively.
	 *
	 * @param node the node to be colapsed
	 *
	 */
	private void _collapseNode(Node node) {
		logger.info("Parameters: node=" + node);
		for (int i = 0; i < node.getChildren().size(); i++) {
			_collapseNode(node.getChildren().get(i));
			nodeContainer.removeItem(node.getChildren().get(i));
		}
		thisDirectories.setCollapsed(node, true);
		logger.info("End method");
	}

	/**
	 * Returns the last expanded node.
	 *
	 * @return the last expanded node
	 *
	 */
	public Node getLastExpandedNode() {
		return lastExpandedNode;
	}

	/**
	 * Returns the last chosen node.
	 *
	 * @return the last chosen node
	 *
	 */
	public Node getLastChosenItemId() {
		return lastChosenNode;
	}

	/**
	 * Expands the given path in the tree if it exists and selects the node.
	 *
	 * @param path the path to be expanded
	 *
	 */
	public String expandPath(String path) {
		// System.out.println("expand path");
		try {
			logger.info("Parameters: path=" + path);
			// first I have to check if path exists
			boolean pathExists = false;
			// boolean pathExists = true;

			pathExists = control.pathExist(control.getApplication()
					.getRepositories().getChosenRepo(), path); // path must be

			// without in the beginning if not return false;
			if (!pathExists) {
				logger.info("Path not exist");
				return "Wrong path";
			} else {
				logger.info("Path \"" + path + "\"exists");

				// first collapse all nodes
				logger.info("Collapsing all nodes");
				List<Node> children = root.getChildren();
				for (int i = 0; i < children.size(); i++) {
					collapseNode(children.get(i));
				}

				logger.info("Setting new root");
				Node node = root;

				this.removeAllItems();
				this.nodeContainer.addBean(node);
				this.setCollapsed(node, false);

				// begin proccesing given path
				String[] pathFields = path.split("/");
				if (pathFields.length != 0 && pathFields[0].equals("")) {
					// children = root.getChildren();
					for (int i = 1; i < pathFields.length; i++) {
						// here I have to find node with appropriate name and
						// fire
						// expand event
						children = control.getChildren(control.getApplication()
								.getRepositories().getChosenRepo(),
								node.getFullPath());
						node.setChildren(children);
						int indexFound = -1;
						for (int j = 0; j < children.size(); j++) {
							children.get(j).setParent(node);
							nodeContainer.addBean(children.get(j));
							if (children.get(j).getName().equals(pathFields[i])) {
								indexFound = j;
							}
						}

						if (indexFound > -1) {
							logger.info("Extracting :"
									+ children.get(indexFound).getName());
							if (i < pathFields.length - 1) {
								this.setCollapsed(children.get(indexFound),
										false);
							}
							node = children.get(indexFound);
						}
					}

					logger.info("Choosing node " + node.getName());
					setLastChosenItem(node);

				} else {
					// for root actualization
					children = control.getChildren(control.getApplication()
							.getRepositories().getChosenRepo(),
							node.getFullPath());
					node.setChildren(children);
					for (int j = 0; j < children.size(); j++) {
						children.get(j).setParent(node);
						nodeContainer.addBean(children.get(j));
					}

				}
				this.select(node);
				this.setValue(node);
				this.setCurrentPageFirstItemId(node);
				control.directorySelected(control.getApplication()
						.getRepositories().getChosenRepo(), node.getFullPath());
				expandListener = false;
			}
		} catch (Exception e) {
			logger.info("Path not exist");
			return "Wrong path";
		}
		// everything was without errors
		return null;
	}

	/**
	 * Sets the new last chosen node.
	 *
	 * @param lastChosenNode the new last chosen node
	 *
	 */
	public void setLastChosenNode(Node lastChosenNode) {
		this.lastChosenNode = lastChosenNode;
	}

	/**
	 * Returns the root node.
	 *
	 * @return the root node
	 *
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * Sets the new root node.
	 *
	 * @param root the new root node
	 *
	 */
	public void setRoot(Node root) {
		this.root = root;
	}

	/**
	 * Sets the new last chosen node along with the nodePath.
	 *
	 * @param lastNode the new last chosen node
	 *
	 */
	private void setLastChosenItem(Node lastNode) {
		// scrollToItem(lastNode);
		logger.info("Parameters: lastNode=" + lastNode);
		this.nodePath = new ArrayList<Node>();
		nodePath.add(lastNode);
		Node tmpNode = lastNode;
		while (tmpNode.getParent() != null) {
			tmpNode = tmpNode.getParent();
			nodePath.add(tmpNode);
		}
		lastChosenNode = (Node) lastNode;
		logger.info("New node path=" + nodePath);
	}

	/**
	 * Scrolls trough the tree to the given node.
	 *
	 * @param itemId the node to which we scroll
	 *
	 */
	public void scrollToItem(Node itemId) {
		int targetIndex = this.indexOfId(itemId);
		int firstIndexShown = this.getCurrentPageFirstItemIndex();
		int lastIndexShown = firstIndexShown + this.getPageLength() - 1;

		boolean targetIsShowing = (targetIndex >= firstIndexShown)
				&& ((targetIndex < lastIndexShown));
		if (!targetIsShowing) {
			super.setCurrentPageFirstItemId(itemId);
		}
	}

	/**
	 * Returns the list of the nodes on the path to the root node from the selected node.
	 *
	 * @return the list of the nodes on the path to the root node from the selected node
	 *
	 */
	public List<Node> getNodePath() {
		return nodePath;
	}

	/**
	 * Updates the tree with the given path in the given root node.
	 *
	 * @param rootAct the root node
	 * @param path the path in the tree
	 *
	 */
	public void update(Node rootAct, String path) {
		logger.info("Setting new root");
		this.root.setTimestamp(rootAct.getTimestamp());
		List<Node> children = root.getChildren();
		String[] pathFields = path.split("/");

		Node fromContainer = root;
		if (pathFields.length != 0 && pathFields[0].equals("")) {
			for (int i = 1; i < pathFields.length; i++) {
				for (int j = 0; j < children.size(); j++) {
					if (children.get(j).getName().equals(pathFields[i])) {
						BeanItem<Node> g = nodeContainer.getItem(children
								.get(j));
						if (g == null) {
							// System.out.println("null pointer");

							return;
						}
						fromContainer = g.getBean();

						Node withNewTimestamp = control.getNode(root.getSvn(),
								fromContainer.getFullPath());
						fromContainer.setTimestamp(withNewTimestamp
								.getTimestamp());
						fromContainer.setInheritance(withNewTimestamp
								.isInheritance());
						// fromContainer.setGroups(withNewTimestamp.getGroups());
						// fromContainer.setUsers(withNewTimestamp.getUsers());
					}
				}
				children = fromContainer.getChildren();
			}
		}
	}
}
