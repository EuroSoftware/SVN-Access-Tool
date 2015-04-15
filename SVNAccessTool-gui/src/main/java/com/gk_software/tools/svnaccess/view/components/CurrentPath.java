package com.gk_software.tools.svnaccess.view.components;

import java.util.Map;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;

/**
 * Represents the cookie panel and the actual path to repository.
 */
public class CurrentPath extends ComboBox implements
		Property.ValueChangeListener, AbstractSelect.NewItemHandler {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(CurrentPath.class);

	/** The control class. */
	private final IControl control;

	/** The instance of this class. */
	private CurrentPath thisCurrentPath;

	/** The indicator whether the current path is the last added. */
	private Boolean lastAdded = false;

	/** The indicator whether the current path was changed. */
	private boolean setChange = false;

	/** The indicator whether the current path was null. */
	private boolean wasNull = false;

	/** The caption of the added item. */
	private String newItemCaption = "";

	/**
	 * Returns the indicator whether the current path was null.
	 *
	 * @return the indicator whether the current path was null
	 *
	 */
	public boolean isWasNull() {
		return wasNull;
	}

	/**
	 * Sets the {@code wasNull} and {@code setChange} indicators to false.
	 */
	public void removeBools() {
		this.wasNull = false;
		this.setChange = false;
	}

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public CurrentPath(final IControl control) {
		this.control = control;
		thisCurrentPath = this;
		this.setWidth("100%");
		this.setImmediate(true);
		this.setValidationVisible(true);
		this.setNewItemsAllowed(true);
		this.setNewItemHandler(this);
		this.setNullSelectionAllowed(false);
		this.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
		this.pageLength = 15;

		this.addListener(new ItemSetChangeListener() {

			@Override
			public void containerItemSetChange(ItemSetChangeEvent event) {
				setChange = true;
			}
		});
		this.addListener(new ValueChangeListener() {
			@Override
			public void valueChange(
					com.vaadin.data.Property.ValueChangeEvent event) {

				if (!lastAdded) {
					newItemCaption = event.toString();
				}
				String returnValue = null;
				lastAdded = false;
				if (event.getProperty().toString() != null) {
					if (!setChange && !wasNull) {

						FavoriteBean f = (FavoriteBean) thisCurrentPath
								.getValue();

						String chosen = control.getApplication()
								.getRepositories().getChosenRepo();
						if (!chosen.equals(f.getSvn())) {

							logger.info("Changing SVN " + f.getSvn());
							// System.out.println("Changing SVN " + f.getSvn());
							returnValue = control
									.getApplication()
									.getRepositories()
									.setChosenRepo(control.getRepositories(),
											f.getSvn());
							if (returnValue == null) {
								control.getApplication()
										.getDirectories()
										.setRoot(
												control.getApplication()
														.getRepositories()
														.getChosenRepoNode());
							} else {
								logger.info(returnValue);
								control.getApplication().showNotification(
										returnValue);
							}
						}
						logger.info("Expandig path " + f.getFullpath());
						if (returnValue == null) {
							returnValue = control.pathEdited(f.getFullpath());
							if (returnValue != null) {
								control.getApplication().showNotification(
										returnValue);
								logger.info(returnValue);
								setPathField(chosen, control.getApplication()
										.getDirectories().getLastChosenItemId()
										.getFullPath());

							}
						} else {
							logger.info(returnValue);
							control.getApplication().showNotification(
									returnValue);
							Node lastCh = control.getApplication()
									.getDirectories().getLastChosenItemId();
							if (lastCh == null)
								setPathField("", "");
							else
								setPathField(chosen, lastCh.getFullPath());
						}

					}
					setChange = false;
					wasNull = false;
				} else {
					wasNull = true;
				}
				thisCurrentPath.commit();
			}

		});

	}

	/**
	 * Sets the given path as the first item to the path combobox.
	 *
	 * @param svn the path to the repository
	 * @param path the path to be set as the first item
	 */
	public void setPathField(String svn, String path) {
		this.removeAllItems();

		CurrentPathContainer cp = new CurrentPathContainer(control);
		FavoriteBean v = new FavoriteBean();
		v.setFullpath(path);
		v.setSvn(svn);
		v.setSelected(true);
		cp.addBean(v);
		Map<String, Long> map = control.getApplication().getCookies()
				.getUserCookies();
		for (String key : map.keySet()) {
			FavoriteBean f = new FavoriteBean(key, map.get(key));
			cp.addBean(f);

		}

		Map<String, Integer> map2 = control.getApplication().getCookies()
				.getSaveCookies();
		for (String key : map2.keySet()) {
			FavoriteBean f = new FavoriteBean(key, map2.get(key));
			cp.addBean(f);

		}
		this.setContainerDataSource(cp);

		this.select(v);
		thisCurrentPath.commit();
		thisCurrentPath.attach();

	}

	/**
	 * Updates the tree of nodes for the selected item in the combobox.
	 */
	protected void updateTree() {

		String returnValue;
		FavoriteBean f = null;

		if (lastAdded)
			returnValue = control.pathEdited(newItemCaption);
		else {
			f = (FavoriteBean) getValue();
			returnValue = control.pathEdited(f.getFullpath());

		}
		if (returnValue != null) {
			control.getApplication().showNotification(returnValue);
		} else {
			if (f != null)
				f.setSelected(true);
		}

	}

	/**
	 * Adds the given item to the tree.
	 *
	 * @param newItemCaption the caption of the added item
	 *
	 */
	@Override
	public void addNewItem(String newItemCaption) {
		String spl[] = newItemCaption.split("-");
		String fullpath;
		String svn;
		if (spl.length != 2) {
			fullpath = newItemCaption;
			svn = control.getApplication().getRepositories().getChosenRepo();

		} else {
			svn = spl[0].trim();
			fullpath = spl[1].trim();
		}
		FavoriteBean f = new FavoriteBean();
		f.setFullpath(fullpath);
		f.setSvn(svn);
		if (!thisCurrentPath.getContainerDataSource().getItemIds().contains(f)) {
			lastAdded = true;
			this.newItemCaption = newItemCaption;
			updateTree();
		} else {
			if (!this.getValue().equals(f)) {
				lastAdded = true;
				this.newItemCaption = newItemCaption;
				updateTree();
			}
		}
	}

	/**
	 * Saves the current path to the cookies.
	 */
	public void setCookies() {
		CurrentPathContainer cp = new CurrentPathContainer(control);

		Map<String, Long> map = control.getApplication().getCookies()
				.getUserCookies();
		for (String key : map.keySet()) {
			FavoriteBean f = new FavoriteBean(key, map.get(key));
			cp.addBean(f);

		}

		Map<String, Integer> map2 = control.getApplication().getCookies()
				.getSaveCookies();
		for (String key : map2.keySet()) {
			FavoriteBean f = new FavoriteBean(key, map2.get(key));
			cp.addBean(f);

		}
		this.setContainerDataSource(cp);
		thisCurrentPath.commit();
		thisCurrentPath.attach();

	}
}
