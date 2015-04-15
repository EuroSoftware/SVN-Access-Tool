package com.gk_software.tools.svnaccess.view.components.DirectoriesTree;

import java.util.ArrayList;
import java.util.Collection;

import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

/**
 * Represents a container for the {@code DirectoriesTreeTable} class.
 */
public class DirectoriesTreeTableContainer extends BeanItemContainer<Node>
		implements Hierarchical {

	/** The control class. */
	private IControl control;

	/**
	 * Creates a new instance and sets the {@code control} variable according to the
	 * given value.
	 *
	 * @param control the control class
	 *
	 */
	public DirectoriesTreeTableContainer(IControl control) {
		super(Node.class);
		this.control = control;
	}

	/**
	 * Creates a new instance.
	 */
	public DirectoriesTreeTableContainer() {
		super(Node.class);
	}

	/**
	 * Adds the given node to the tree.
	 *
	 * @param bean the added node
	 *
	 * @return the created node
	 */
	public BeanItem<Node> addBean(Node bean) {
		BeanItem<Node> addBean = super.addBean(bean);
		return addBean;
	}

	/**
	 * Checks if the given node can have children.
	 *
	 * @param itemId the checked node
	 *
	 * @return true if it can otherwise false
	 *
	 */
	public boolean areChildrenAllowed(Object itemId) {
		Node node = (Node) itemId;
		if (node.getParent() == null) {
			if (node.getChildren().size() > 0)
				return true;
			else
				return false;
		} else
			return hasChildren(itemId);
		// return true;
	}

	/**
	 * Returns the list of the given node's children.
	 *
	 * @param itemId the node whose children are returned
	 *
	 * @return the list of the given node's children
	 *
	 */
	public Collection<?> getChildren(Object itemId) {
		// Node lastNode = application.getDirectories().getLastExpandedNode();
		// System.out.println("last node "+lastNode);
		Node node = (Node) itemId;
		// List<Node> children = new ArrayList<Node>();
		// if (lastNode != null && node.equals(lastNode)) {
		// List<Node> children2 = ((Node) itemId).getChildren();
		//
		// children = control.getChildren(application.getRepositories()
		// .getChosenRepo(), node.getFullPath());
		// // System.out.println();
		// }

		// return children;
		return node.getChildren();
	}

	/**
	 * Returns the parent of the given node.
	 *
	 * @param itemId the node whose parent is returned
	 *
	 * @return the parent of the given node.
	 *
	 */
	public Object getParent(Object itemId) {
		return ((Node) itemId).getParent();
	}

	/**
	 * Checks if the given node has children.
	 *
	 * @param itemId the checked node
	 *
	 * @return true if it has otherwise false
	 *
	 */
	public boolean hasChildren(Object itemId) {
		Node node = (Node) itemId;

		// test solution
		// System.out.println(" : " + node.getFullPath());
		// lastExpanded = application.getDirectories().getLastExpandedNode();
		// return true;

		// if lesser than lastExpanded, return hasSVNchildren, else return
		// getChildren.size of this node.
		// return true;

			return control.hasSVNChildren(control.getApplication()
					.getRepositories().getChosenRepo(), node.getFullPath());

		// return node.getChildren().size()

		// previous solution
		// if (node.has == 0)
		// if
		// (control.getChildren(control.getApplication().getRepositories().getChosenRepo(),
		// node.getFullPath()).size() == 0)
		// // if (node.getChildren().size() == 0)
		// return false;
		// else
		// return true;

		// return true;
	}

	/**
	 * Checks if the given node is root.
	 *
	 * @param itemId the checked node
	 *
	 * @return true if it is otherwise false
	 *
	 */
	public boolean isRoot(Object itemId) {
		return ((Node) itemId).getParent() == null;
	}

	/**
	 * Returns the list of the root nodes ids.
	 *
	 * @return the list of the root nodes ids
	 *
	 */
	public Collection<?> rootItemIds() {
		ArrayList<Node> arrayList = new ArrayList<Node>();
		for (Node workItem : getItemIds()) {
			if (isRoot(workItem)) {
				arrayList.add(workItem);
			}
		}
		return arrayList;
	}

	/**
	 * Not implemented.
	 */
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Not implemented.
	 */
	public boolean setParent(Object itemId, Object newParentId)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException();
	}
}
