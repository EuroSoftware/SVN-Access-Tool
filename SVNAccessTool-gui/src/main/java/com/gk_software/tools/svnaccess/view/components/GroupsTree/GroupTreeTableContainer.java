package com.gk_software.tools.svnaccess.view.components.GroupsTree;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

/**
 * Represents a container for the {@code GroupTreeTable} class.
 */
public class GroupTreeTableContainer extends BeanItemContainer<GroupBean>
		implements Hierarchical {

	/**
	 * Creates a new instance.
	 */
	public GroupTreeTableContainer() {
		super(GroupBean.class);
	}

	/**
	 * Adds the given node to the tree.
	 *
	 * @param bean the added node
	 *
	 * @return the created node
	 */
	public BeanItem<GroupBean> addBean(GroupBean bean) {
		BeanItem<GroupBean> addBean = super.addBean(bean);
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
		GroupBean g = (GroupBean) itemId;
		if (g == null || g.getChildren().size() == 0)
			return false;
		return true;
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
		return ((GroupBean) itemId).getChildren();
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
		GroupBean g = (GroupBean) itemId;
		if (g == null || g.getChildren().size() == 0)
			return false;
		return true;
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
		GroupBean g = (GroupBean) itemId;

		if (g.getParent() == null || g.getParent().getName().length() == 0)
			return true;
		return false;
	}

	/**
	 * Returns the list of the root nodes ids.
	 *
	 * @return the list of the root nodes ids
	 *
	 */
	public Collection<GroupBean> rootItemIds() {
		ArrayList<GroupBean> arrayList = new ArrayList<GroupBean>();
		for (GroupBean workItem : getItemIds()) {
			if (workItem.getParent() == null) {
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

	/**
	 * Returns the parent of the given node.
	 *
	 * @param itemId the node whose parent is returned
	 *
	 * @return the parent of the given node.
	 *
	 */
	@Override
	public Object getParent(Object itemId) {
		GroupBean g = (GroupBean) itemId;
		return g.getParent();
	}
}
