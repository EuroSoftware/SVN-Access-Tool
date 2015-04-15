package com.gk_software.tools.svnaccess.view.components;

import java.util.ArrayList;
import java.util.Collection;

import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;

/**
 * The container class for the current path.
 */
public class CurrentPathContainer extends BeanItemContainer<FavoriteBean>
		implements Hierarchical {

	/**
	 * Creates a new instance and sets the {@code control} variable according to the
	 * given value.
	 *
	 * @param control the control class
	 *
	 */
	public CurrentPathContainer(IControl control) {
		super(FavoriteBean.class);
	}

	/**
	 * Creates a new instance.
	 */
	public CurrentPathContainer() {
		super(FavoriteBean.class);
	}

	/**
	 * Adds the given favorite bean to the container and returns the added bean object.
	 *
	 * @param bean the bean to be added
	 *
	 * @return the added bean
	 *
	 */
	public BeanItem<FavoriteBean> addBean(FavoriteBean bean) {
		BeanItem<FavoriteBean> addBean = super.addBean(bean);
		return addBean;
	}

	/**
	 * Unimplemented
	 */
	@Override
	public Collection<?> getChildren(Object itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Unimplemented
	 */
	@Override
	public Object getParent(Object itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the list of the root item ids.
	 *
	 * @return the list of the root item ids
	 *
	 */
	@Override
	public Collection<?> rootItemIds() {
		ArrayList<FavoriteBean> arrayList = new ArrayList<FavoriteBean>();
		for (FavoriteBean workItem : getItemIds()) {
			if (isRoot(workItem)) {
				arrayList.add(workItem);
			}
		}
		return arrayList;
	}

	/**
	 * Unimplemented
	 */
	@Override
	public boolean setParent(Object itemId, Object newParentId)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Unimplemented
	 */
	@Override
	public boolean areChildrenAllowed(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Unimplemented
	 */
	@Override
	public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Unimplemented
	 */
	@Override
	public boolean isRoot(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Unimplemented
	 */
	@Override
	public boolean hasChildren(Object itemId) {
		// TODO Auto-generated method stub
		return false;
	}

}
