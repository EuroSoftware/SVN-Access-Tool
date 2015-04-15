package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.Rights;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

/**
 * Represent the window for clear function.
 */
public class ModalClearFunction extends VerticalLayout {

	/** The clean function window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalClearFunction.class, "WINDOW_CAPTION_TEXT", "Clear rights");

	/** The text of the R rights checkbox. */
	public static final I18NResource R_CHECKBOX_TEXT = new I18NResource(
			ModalViewGroupsTree.class, "R_CHECKBOX_TEXT", "R");

	/** The text of the W rights checkbox. */
	public static final I18NResource W_CHECKBOX_TEXT = new I18NResource(
			ModalViewGroupsTree.class, "W_CHECKBOX_TEXT", "W");

	/** The text of the M rights checkbox. */
	public static final I18NResource M_CHECKBOX_TEXT = new I18NResource(
			ModalViewGroupsTree.class, "M_CHECKBOX_TEXT", "M");

	/** The description of the R rights checkbox. */
	public static final I18NResource R_CHECKBOX_DESCRIPTION = new I18NResource(
			ModalViewGroupsTree.class, "R_CHECKBOX_DESCRIPTION", "Read rights");

	/** The description of the W rights checkbox. */
	public static final I18NResource W_CHECKBOX_DESCRIPTION = new I18NResource(
			ModalViewGroupsTree.class, "W_CHECKBOX_DESCRIPTION", "Write rights");

	/** The description of the M rights checkbox. */
	public static final I18NResource M_CHECKBOX_DESCRIPTION = new I18NResource(
			ModalViewGroupsTree.class, "M_CHECKBOX_DESCRIPTION", "Modify rights");

	/** The text of the select label. */
	public static final I18NResource LABEL_TEXT = new I18NResource(
			ModalClearFunction.class, "LABEL_TEXT", "Select rights for removal.");

	/** The warning message text for bad selection. */
	public static final I18NResource WARNING_TEXT_C = new I18NResource(
			ModalClearFunction.class, "WARNING_TEXT_C", "Bad selection");

	/** The warning message text for wrong deselect. */
	public static final I18NResource WARNING_TEXT_T = new I18NResource(
			ModalClearFunction.class, "WARNING_TEXT_T",
			"You can not deselect \"W\" when \"R\" is selected.");

	/** The warning message text for no rights selected. */
	public static final I18NResource WARNING_TEXT_NO_RIGHTS_SELECTED = new I18NResource(
			ModalClearFunction.class, "WARNING_TEXT_NO_RIGHTS_SELECTED",
			"No rights for removal selected!");

	/** The confirm dialog title. */
	public static final I18NResource CONFIRM_DIALOG_TITLE = new I18NResource(
			ModalClearFunction.class, "CONFIRM_DIALOG_TITLE",
			"Clean Rights Confirmation");

	/** The confirm dialog message text. */
	public static final I18NResource CONFIRM_DIALOG_TEXT = new I18NResource(
			ModalClearFunction.class, "CONFIRM_DIALOG_TEXT",
			"Are you sure you want to DELETE ALL selected rights in selected subtree?");

	/** The R rights checkbox. */
	private CheckBox cbR;

	/** The M rights checkbox. */
	private CheckBox cbM;

	/** The W rights checkbox. */
	private CheckBox cbW;

	/** The subwindow for clear function. */
	private Window subwindow;

	/** The control class. */
	private static IControl control;

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ModalClearFunction.class);

	/** The text of the select label note. */
	private static final I18NResource LABEL_TEXT_NOTE = new I18NResource(
			ModalClearFunction.class,
			"LABEL_TEXT",
			"You can't select just \"R\". The rights you select will be DELETED" +
			" and it's not posible to have user with \"W\" rights who hasn't \"R\" rights.");

	/**
	 * Checks if there will be some rights change by comparing the given rights.
	 *
	 * @param rightsToRemove the rights which should be removed
	 * @param rightsActual the actual rights
	 *
	 * @return true if some rights will be changed otherwise false
	 *
	 */
	private boolean isRightToChange(Rights rightsRequir, Rights rightsActual) {
		if (rightsRequir.isRead() && rightsActual.isRead()) {
			return true;
		}
		if (rightsRequir.isWrite() && rightsActual.isWrite()) {
			return true;
		}
		if (rightsRequir.isModify() && rightsActual.isModify()) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the mask with rights to be changed for the given combination of rights
	 *s
	 * Example: Actual(r-m), ToRemove(-wm) -> Mask(--m)
	 *
	 * @param rightsToRemove the rights which should be removed
	 * @param rightsActual the actual rights
	 *
	 * @return the mask with rights to be changed
	 *
	 */
	private RightsContainer changeMask(Rights rightsToRemove, Rights rightsActual) {
		Rights mask = new Rights("");
		Rights futureRights = new Rights(true, true, true);

		if (rightsActual.isRead() && rightsToRemove.isRead()) {
			mask.setRead(true);
			futureRights.setRead(false);
		} else if (!rightsActual.isRead()) {
			futureRights.setRead(false);
		}

		if (rightsActual.isWrite() && rightsToRemove.isWrite()) {
			mask.setWrite(true);
			futureRights.setWrite(false);
		} else if (!rightsActual.isWrite()) {
			futureRights.setWrite(false);
		}

		if (rightsActual.isModify() && rightsToRemove.isModify()) {
			mask.setModify(true);
			futureRights.setModify(false);
		} else if (!rightsActual.isModify()) {
			futureRights.setModify(false);
		}

		return new RightsContainer(mask, !(futureRights.isRead()
				|| futureRights.isWrite() || futureRights.isModify()));
	}

	/**
	 * Cleans all selected rights from the tree of directories. Only user who calls
	 * this function is unaffected. Method is recursively going through the tree.
	 *
	 * @param repoPath the path to the repository
	 * @param fullpath the path to the node in the repository
	 * @param rights the rights which should be changed
	 *
	 */
	private void cleanRights(String repoPath, String fullpath, Rights rights) {

		Map<String, Rights> users = control.getUsersWithRights(repoPath,
				fullpath);
		Map<String, Rights> groups = control.getGroupsWithRights(repoPath,
				fullpath);

		if (users.containsKey(control.getUser().getUserName())) {
			users.remove(control.getUser().getUserName());
		}

		Node temp = control.getApplication().getDirectories()
				.getLastChosenItemId();
		control.getApplication().getDirectories()
				.setLastChosenNode(control.getNode(repoPath, fullpath));

		ArrayList<Node> nodePathOriginal = (ArrayList<Node>) control
				.getApplication().getDirectories().getNodePath();
		ArrayList<Node> nodePathTemp = new ArrayList<Node>();
		nodePathTemp.add(control.getNode(repoPath, fullpath));
		Node tmpNode = control.getNode(repoPath, fullpath);
		while (tmpNode.getParent() != null) {
			tmpNode = tmpNode.getParent();
			nodePathTemp.add(tmpNode);
		}

		control.getApplication().getDirectories().setNodePath(nodePathTemp);

		for (Map.Entry<String, Rights> entry : users.entrySet()) {
			RightsContainer rc = changeMask(rights, entry.getValue());
			System.out.println(rc);

			if (rc.isAllRightsRemoved()) {
				Set<String> usersToDelete = new HashSet<String>();
				usersToDelete.add(entry.getKey());
				Set<String> groupsToDelete = new HashSet<String>();
				control.removeSelected(groupsToDelete, usersToDelete);
				continue;
			}
			if (isRightToChange(rights, entry.getValue())) {
				control.updateUser(entry.getKey(), rc.getRights().toString());
			}
		}

		for (Map.Entry<String, Rights> entry : groups.entrySet()) {
			RightsContainer rc = changeMask(rights, entry.getValue());
			System.out.println(rc);

			if (rc.isAllRightsRemoved()) {
				Set<String> usersToDelete = new HashSet<String>();
				Set<String> groupsToDelete = new HashSet<String>();
				groupsToDelete.add(entry.getKey());
				control.removeSelected(groupsToDelete, usersToDelete);
				continue;
			}
			if (isRightToChange(rights, entry.getValue())) {
				control.updateGroup(entry.getKey(), rc.getRights().toString());
			}
		}

		control.getApplication().getDirectories().setLastChosenNode(temp);
		control.getApplication().getDirectories().setNodePath(nodePathOriginal);

		users = null;

		// Going through the tree - Recursively.
		if (control.getChildren(repoPath, fullpath) != null
				&& control.getChildren(repoPath, fullpath).size() > 0) {
			List<Node> a = control.getChildren(repoPath, fullpath);
			for (Node node : a) {
				String _fullpath = fullpath + "/" + node.getFullPath() + "/";
				cleanRights(repoPath, _fullpath, rights);
			}
		}
	}

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	@SuppressWarnings({ "serial", "static-access" })
	public ModalClearFunction(final IControl control) {
		this.control = control;

		subwindow = new Window(I18NSupport.getMessage(WINDOW_CAPTION_TEXT));
		subwindow.setModal(true);

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();

		Label plainText = new Label(I18NSupport.getMessage(LABEL_TEXT));
		plainText.setContentMode(Label.CONTENT_TEXT);

		cbR = new CheckBox(I18NSupport.getMessage(R_CHECKBOX_TEXT));
		cbR.setImmediate(true);

		cbR.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (cbR.booleanValue()) {
					cbW.setValue(cbR.getValue());
				}
			}
		});

		cbW = new CheckBox(I18NSupport.getMessage(W_CHECKBOX_TEXT));
		cbW.setImmediate(true);
		cbW.addListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (!cbW.booleanValue()) {
					cbR.setValue(cbW.getValue());
				}
			}
		});

		cbM = new CheckBox(I18NSupport.getMessage(M_CHECKBOX_TEXT));

		cbR.setSizeFull();
		cbW.setSizeFull();
		cbM.setSizeFull();

		cbR.setDescription(I18NSupport.getMessage(R_CHECKBOX_DESCRIPTION));
		cbW.setDescription(I18NSupport.getMessage(W_CHECKBOX_DESCRIPTION));
		cbM.setDescription(I18NSupport.getMessage(M_CHECKBOX_DESCRIPTION));

		HorizontalLayout hl12 = new HorizontalLayout();
		hl12.setSpacing(true);
		hl12.addComponent(cbR);
		hl12.addComponent(cbW);
		hl12.addComponent(cbM);
		hl12.setExpandRatio(cbR, 1);
		hl12.setExpandRatio(cbW, 1);
		hl12.setExpandRatio(cbM, 1);

		hl12.setComponentAlignment(cbR, Alignment.MIDDLE_CENTER);
		hl12.setComponentAlignment(cbW, Alignment.MIDDLE_CENTER);
		hl12.setComponentAlignment(cbM, Alignment.MIDDLE_CENTER);

		Button clearBtn = new Button("Clear rights");
		clearBtn.setClickShortcut(KeyCode.ENTER);
		clearBtn.setStyleName("link");
		clearBtn.addStyleName("Button2");

		clearBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				if (!(cbR.booleanValue() || cbW.booleanValue() || cbM.booleanValue())) {
					subwindow.showNotification(I18NSupport
							.getMessage(WARNING_TEXT_NO_RIGHTS_SELECTED),
							Notification.TYPE_WARNING_MESSAGE);
					return;
				}
				final ModalConfirmationDialog mcd = new ModalConfirmationDialog(
						I18NSupport.getMessage(CONFIRM_DIALOG_TITLE),
						I18NSupport.getMessage(CONFIRM_DIALOG_TEXT));

				final Window win2 = mcd.getSubwindow();
				(subwindow.getParent()).addWindow(win2);
				mcd.getOk().focus();

				mcd.getOk().addListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						logger.info("CleanRights permited");
						String chosenRepo = control.getApplication().getRepositories()
								.getChosenRepo();
						String fullPath = control.getApplication().getDirectories()
								.getLastChosenItemId().getFullPath();

						cleanRights(chosenRepo, fullPath, new Rights(cbR.booleanValue(),
								cbW.booleanValue(), cbM.booleanValue()));

						try {
							control.saveChanges();
						} catch (Exception e) {
							e.printStackTrace();
						}

						win2.getParent().removeWindow(win2);
						subwindow.getParent().removeWindow(subwindow);

					}
				});

				mcd.getCancel().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("CleanRights canceled");
						(win2.getParent()).removeWindow(win2);
					}
				});
			}
		});

		Button cancelBtn = new Button("Cancel");
		cancelBtn.setClickShortcut(KeyCode.ESCAPE);
		cancelBtn.setStyleName("link");
		cancelBtn.addStyleName("Button2");

		cancelBtn.addListener(new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				logger.info("Button cancel was clicked on. ");
				subwindow.getParent().removeWindow(subwindow);
			}
		});

		HorizontalLayout buttonsContainer = new HorizontalLayout();
		buttonsContainer.addComponent(clearBtn);
		buttonsContainer.addComponent(cancelBtn);
		buttonsContainer.setHeight(30, UNITS_PIXELS);

		buttonsContainer.setComponentAlignment(clearBtn, Alignment.MIDDLE_CENTER);
		buttonsContainer.setComponentAlignment(cancelBtn, Alignment.MIDDLE_CENTER);

		layout.addComponent(plainText);
		layout.addComponent(hl12);
		layout.addComponent(buttonsContainer);

		layout.setComponentAlignment(plainText, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(hl12, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(buttonsContainer, Alignment.MIDDLE_CENTER);

		Label info = new Label(I18NSupport.getMessage(LABEL_TEXT_NOTE));
		//info.setContentMode(Label.CONTENT_TEXT);

		info.addStyleName(Reindeer.LABEL_SMALL);
		//info.addStyleName(ChameleonTheme.LABEL_WARNING);

		layout.addComponent(info);

	}

	/**
	 * Returns the subwindow for clear function.
	 *
	 * @return the subwindow for clear function
	 *
	 */
	public Window getSubwindow() {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return subwindow;
	}

	/**
	 * Initializes the components.
	 *
	 * @throws NullPointerException initialization failed
	 *
	 */
	private void init() throws NullPointerException {
		logger.info("Modal window for clear rights function initialization");

		subwindow.setWidth(280, UNITS_PIXELS);
		subwindow.setHeight(230, UNITS_PIXELS);

		cbR.setValue(false);
		cbW.setValue(false);
		cbM.setValue(false);
	}

	/**
	 * The container class for rights.
	 */
	class RightsContainer {

		/**
		 * Returns the string representation of this class.
		 *
		 * @return the string representation of this class
		 *
		 */
		@Override
		public String toString() {
			return "RightsContainer [rights=" + rights + ", allRightsRemoved="
					+ allRightsRemoved + "]";
		}

		/** The contained rights. */
		private Rights rights;

		/** The indicator whether all rights should be removed. */
		private boolean allRightsRemoved;

		/**
		 * Creates a new instance and initializes the variables according to the
		 * given values.
		 *
		 * @param rights the contained rights
		 * @param allRightsRemoved the indicator whether all rights should be removed
		 *
		 */
		public RightsContainer(Rights rights, boolean allRightsRemoved) {
			this.rights = rights;
			this.allRightsRemoved = allRightsRemoved;
		}

		/**
		 * Returns the contained rights.
		 *
		 * @return the contained rights
		 *
		 */
		public Rights getRights() {
			return rights;
		}

		/**
		 * Sets the new contained rights.
		 *
		 * @param rights the new contained rights
		 *
		 */
		public void setRights(Rights rights) {
			this.rights = rights;
		}

		/**
		 * Returns the indicator whether all rights should be removed.
		 *
		 * @return the indicator whether all rights should be removed
		 *
		 */
		public boolean isAllRightsRemoved() {
			return allRightsRemoved;
		}

		/**
		 * Sets the new indicator whether all rights should be removed.
		 *
		 * @param allRightsRemoved the new indicator whether all rights should be
		 * removed
		 *
		 */
		public void setAllRightsRemoved(boolean allRightsRemoved) {
			this.allRightsRemoved = allRightsRemoved;
		}
	}
}
