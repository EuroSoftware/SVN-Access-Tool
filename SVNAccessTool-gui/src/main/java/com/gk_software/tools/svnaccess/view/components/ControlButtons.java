package com.gk_software.tools.svnaccess.view.components;

import gk.ee_common.i18n.I18NResource;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalAddGroups;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalAddUsers;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalConfirmationDialog;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalLockConfirmationDialog;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalWindowSettings;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutAction.ModifierKey;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Window;

/**
 * Container class which contains control buttons.
 */
public class ControlButtons extends GridLayout {

	/** The count of the vertical buttons. */
	private static final int VERTICAL_BUTTON_COUNT = 4;

	/** The count of the horizontal buttons. */
	private static final int HORIZONTAL_BUTTON_COUNT = 2;

	/** The lock button text. */
	public static final I18NResource LOCK_TEXT = new I18NResource(
			ControlButtons.class, "LOCK_TEXT", "LOCK");

	/** The unlock button text. */
	public static final I18NResource UNLOCK_TEXT = new I18NResource(
			ControlButtons.class, "UNLOCK_TEXT", "UNLOCK");

	/** The add users button text. */
	public static final I18NResource ADD_USERS_TEXT = new I18NResource(
			ControlButtons.class, "ADD_USERS_TEXT", "ADD USERS");

	/** The add groups button text. */
	public static final I18NResource ADD_GROUPS_TEXT = new I18NResource(
			ControlButtons.class, "ADD_GROUPS_TEXT", "ADD GROUPS");

	/** The settings button text. */
	public static final I18NResource SETTINGS_TEXT = new I18NResource(
			ControlButtons.class, "SETTINGS_TEXT", "SETTINGS");

	/** The remove selected button text. */
	public static final I18NResource REMOVE_SELECTED_TEXT = new I18NResource(
			ControlButtons.class, "REMOVE_SELECTED", "REMOVE SELECTED");

	/** The remove all button text. */
	public static final I18NResource REMOVE_ALL_TEXT = new I18NResource(
			ControlButtons.class, "REMOVE_ALL_TEXT", "REMOVE ALL");

	/** The save changes button text. */
	public static final I18NResource SAVE_CHANGES_TEXT = new I18NResource(
			ControlButtons.class, "SAVE_CHANGES_TEXT", "SAVE CHANGES");

	/** The remove all confirmation text. */
	public static final I18NResource REMOVE_ALL_CONFIRM_TEXT = new I18NResource(
			ControlButtons.class, "REMOVE_ALL_CONFIRM_TEXT",
			"Are you sure you want to remove all users and groups?");

	/** The lock confirmation text. */
	public static final I18NResource LOCK_CONFIRM_TEXT = new I18NResource(
			ControlButtons.class,
			"LOCK_CONFIRM_TEXT",
			"Do you really want to apply lock for selected path? If you are sure, write \"YES\" to the field below.");

	/** The unlock confirmation text. */
	public static final I18NResource UNLOCK_CONFIRM_TEXT = new I18NResource(
			ControlButtons.class, "UNLOCK_CONFIRM_TEXT",
			"Are you sure you want to unlock the directory?");

	/** The unlock confirmation header. */
	public static final I18NResource UNLOCK_HEADER = new I18NResource(
			ControlButtons.class, "UNLOCK_HEADER", "Unlock Confirmation");

	/** The lock confirmation header. */
	public static final I18NResource LOCK_HEADER = new I18NResource(
			ControlButtons.class, "LOCK_HEADER", "Lock Confirmation");

	/** The remove selected confirmation header. */
	public static final I18NResource REMOVE_SELECTED_CONFIRM_TEXT = new I18NResource(
			ControlButtons.class, "REMOVE_SELECTED_CONFIRM_TEXT",
			"Are you sure you want to remove selected users/groups?");

	/** The remove all header. */
	public static final I18NResource REMOVE_ALL_HEADER = new I18NResource(
			ControlButtons.class, "REMOVE_ALL_HEADER", "Remove all");

	/** The remove selected header. */
	public static final I18NResource REMOVE_SELECTED_HEADER = new I18NResource(
			ControlButtons.class, "REMOVE_SELECTED_HEADER", "Remove selected");

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ControlButtons.class);

	/** The lock button. */
	private Button lock;

	/** The unlock button. */
	private Button unlock;

	/** The add user button. */
	private Button addUser;

	/** The add group button. */
	private Button addGroups;

	/** The settings button. */
	private Button settings;

	/** The remove selected button. */
	private Button removeSelected;

	/** The remove all button. */
	private Button removeAll;

	/** The save changes button. */
	private Button saveChanges;

	/** The text of the confirm yes button. */
	private final String CONFIRM_TEXT_YES = "yes";

	/** The text of the confirm no button. */
	private final String CONFIRM_TEXT_NO = "no";

	/** The control class. */
	private IControl control;

	/** The settings window. */
	private ModalWindowSettings mvs;

	/** The add user window. */
	private ModalAddUsers mau;

	/** The add group window. */
	private ModalAddGroups mag;

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public ControlButtons(final IControl control) {
		super(HORIZONTAL_BUTTON_COUNT, VERTICAL_BUTTON_COUNT);
		logger.info("Parameters: control=" + control);
		this.control = control;

		// creating modal windows
		logger.info("Creating control buttons modal window");

		logger.info("Setting button captions");
		lock = new Button(I18NSupport.getMessage(LOCK_TEXT));
		unlock = new Button(I18NSupport.getMessage(UNLOCK_TEXT));
		addUser = new Button(I18NSupport.getMessage(ADD_USERS_TEXT));
		addGroups = new Button(I18NSupport.getMessage(ADD_GROUPS_TEXT));
		settings = new Button(I18NSupport.getMessage(SETTINGS_TEXT));

		removeSelected = new Button(
				I18NSupport.getMessage(REMOVE_SELECTED_TEXT));
		removeAll = new Button(I18NSupport.getMessage(REMOVE_ALL_TEXT));
		saveChanges = new Button(I18NSupport.getMessage(SAVE_CHANGES_TEXT));

		removeAll.setWidth(140, Sizeable.UNITS_PIXELS);
		removeSelected.setWidth(140, Sizeable.UNITS_PIXELS);
		saveChanges.setWidth(140, Sizeable.UNITS_PIXELS);
		settings.setWidth(140, Sizeable.UNITS_PIXELS);
		addGroups.setWidth(140, Sizeable.UNITS_PIXELS);
		addUser.setWidth(140, Sizeable.UNITS_PIXELS);

		lock.setWidth(140, Sizeable.UNITS_PIXELS);
		unlock.setWidth(140, Sizeable.UNITS_PIXELS);

		lock.setStyleName("link");
		lock.addStyleName("Button3");

		unlock.setStyleName("link");
		unlock.addStyleName("Button3");

		settings.setStyleName("link");
		settings.addStyleName("Button3");

		addUser.setStyleName("link");
		addUser.addStyleName("Button3");

		addGroups.setStyleName("link");
		addGroups.addStyleName("Button3");

		removeAll.setStyleName("link");
		removeAll.addStyleName("Button3");

		removeSelected.setStyleName("link");
		removeSelected.addStyleName("Button3");

		saveChanges.setStyleName("link");
		saveChanges.addStyleName("Button3");

		logger.info("Adding components");
		this.setHeight(120, UNITS_PIXELS);
		this.addComponent(lock, 0, 0);
		this.addComponent(unlock, 1, 0);

		this.addComponent(addUser, 0, 1);
		this.addComponent(removeSelected, 1, 1);

		this.addComponent(addGroups, 0, 2);
		this.addComponent(removeAll, 1, 2);

		this.addComponent(settings, 0, 3);
		this.addComponent(saveChanges, 1, 3);

		this.setComponentAlignment(lock, Alignment.MIDDLE_CENTER);
		this.setComponentAlignment(unlock, Alignment.MIDDLE_CENTER);
		this.setComponentAlignment(addUser, Alignment.MIDDLE_CENTER);
		this.setComponentAlignment(addGroups, Alignment.MIDDLE_CENTER);
		this.setComponentAlignment(removeSelected, Alignment.MIDDLE_CENTER);
		this.setComponentAlignment(removeAll, Alignment.MIDDLE_CENTER);
		this.setComponentAlignment(settings, Alignment.MIDDLE_CENTER);
		this.setComponentAlignment(saveChanges, Alignment.MIDDLE_CENTER);

		addListeners();
		addShortcuts();
	}

	/**
	 * Adds the keyboard shortcuts to the buttons.
	 */
	private void addShortcuts() {
		lock.setClickShortcut(KeyCode.L, ModifierKey.CTRL);
		lock.setDescription("Lock the directory(Ctrl+L)");
		unlock.setClickShortcut(KeyCode.U, ModifierKey.CTRL);
		unlock.setDescription("Unlock the directory(Ctrl+U)");
		addUser.setClickShortcut(KeyCode.U, ModifierKey.ALT);
		addUser.setDescription("Add user (Alt+U)");
		addGroups.setClickShortcut(KeyCode.G, ModifierKey.ALT);
		addGroups.setDescription("Add group (Alt+G)");
		saveChanges.setClickShortcut(KeyCode.S, ModifierKey.CTRL);
		saveChanges.setDescription("Save changes (Ctrl+S)");
		settings.setClickShortcut(KeyCode.S, ModifierKey.ALT);
		settings.setDescription("Settings (Alt+S)");
		removeSelected.setClickShortcut(KeyCode.R, ModifierKey.ALT);
		removeSelected.setDescription("Remove selected (Alt+R)");
		removeAll.setClickShortcut(KeyCode.A, ModifierKey.ALT);
		removeAll.setDescription("Remove all (Alt+A)");
	}

	/**
	 * Adds the event listeners to the buttons.
	 */
	public void addListeners() {
		logger.info("Adding listeners");

		lock.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				logger.info("Lock clicked");
				final ModalLockConfirmationDialog mc = new ModalLockConfirmationDialog(
						I18NSupport.getMessage(LOCK_HEADER), I18NSupport
								.getMessage(LOCK_CONFIRM_TEXT));
				final Window win = mc.getSubwindow();
				logger.info("Lock confirmation dialog number 1");
				getWindow().addWindow(win);
				mc.getOk().focus();
				mc.getOk().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("User confirm action by clicking on OK in dialog 1");
						if (mc.getConfirmText().getValue().toString()
								.toLowerCase().equals(CONFIRM_TEXT_YES)) {
							logger.info("User successfuly confirmed locking by typing YES");

							try {
								control.lockNode(control.getApplication()
										.getRepositories().getChosenRepo(), control
										.getApplication().getDirectories()
										.getLastChosenItemId().getFullPath());
							} catch (Exception e) {
								logger.error(e.getMessage());
							}
							(getWindow()).removeWindow(win);

						} else if (mc.getConfirmText().getValue().toString()
								.toLowerCase().equals(CONFIRM_TEXT_NO)) {
							logger.info("User unsuccessfuly confirmed locking by typing NO");
							(getWindow()).removeWindow(win);
						} else {
							logger.info("User unsuccessfuly confirmed locking by typing something else");
							getWindow().showNotification(
									"The confirmation text is not recognize.");
						}
					}
				});

				mc.getCancel().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("User canceled action by clicking on Cancel in dialog 1");
						(getWindow()).removeWindow(win);
					}
				});

			}
		});

		unlock.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				logger.info("Unlock clicked");
				final ModalConfirmationDialog mc = new ModalConfirmationDialog(
						I18NSupport.getMessage(UNLOCK_HEADER), I18NSupport
								.getMessage(UNLOCK_CONFIRM_TEXT));
				final Window win = mc.getSubwindow();
				getWindow().addWindow(win);
				mc.getOk().focus();
				mc.getOk().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("Unlock confirmed");
						control.unlockNode(control.getApplication()
								.getRepositories().getChosenRepo(), control
								.getApplication().getDirectories()
								.getLastChosenItemId().getFullPath());

						(getWindow()).removeWindow(win);
					}
				});
				mc.getCancel().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("Unlock canceled");
						(getWindow()).removeWindow(win);
					}
				});
			}
		});

		removeAll.addListener(new Button.ClickListener() {

			public void buttonClick(ClickEvent event) {
				logger.info("Remove all clicked");
				final ModalConfirmationDialog mc = new ModalConfirmationDialog(
						I18NSupport.getMessage(REMOVE_ALL_HEADER), I18NSupport
								.getMessage(REMOVE_ALL_CONFIRM_TEXT));

				getWindow().addWindow(mc.getSubwindow());
				mc.getOk().focus();
				mc.getOk().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("Remove all confirmed");
						control.removeAll((List<String>) control
								.getApplication().getGroupsTable()
								.getVisibleItemIds(), (List<String>) control
								.getApplication().getUsersTable()
								.getVisibleItemIds());
						(getWindow()).removeWindow(mc.getSubwindow());
					}
				});
				mc.getCancel().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("Remove all canceled");
						(getWindow()).removeWindow(mc.getSubwindow());
					}
				});

			}
		});
		removeSelected.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				logger.info("Remove selected clicked");
				final ModalConfirmationDialog mc = new ModalConfirmationDialog(
						I18NSupport.getMessage(REMOVE_SELECTED_HEADER),
						I18NSupport.getMessage(REMOVE_SELECTED_CONFIRM_TEXT));

				getWindow().addWindow(mc.getSubwindow());
				mc.getOk().focus();
				mc.getOk().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("Remove selected confirmed");
						control.removeSelected((Set<String>) control
								.getApplication().getGroupsTable().getValue(),
								(Set<String>) control.getApplication()
										.getUsersTable().getValue());
						(getWindow()).removeWindow(mc.getSubwindow());
					}
				});
				mc.getCancel().addListener(new Button.ClickListener() {

					public void buttonClick(ClickEvent event) {
						logger.info("Remove selected canceled");
						(getWindow()).removeWindow(mc.getSubwindow());
					}
				});
			}
		});

		saveChanges.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				logger.info("Save changes clicked");
				try {
					control.saveChanges();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		settings.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				logger.info("Settings clicked");
				mvs = new ModalWindowSettings(control);
				Window win = mvs.getSubwindow();
				if (win.getParent() != null) {
					// window is already showing
					getWindow().showNotification("Window is already open");
				} else {
					// Open the subwindow by adding it to the parent
					// window
					getWindow().addWindow(win);
				}
			}
		});

		addUser.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				logger.info("Add user clicked");
				mau = new ModalAddUsers(control);

				Window win = mau.getSubwindow();
				if (win != null)
					if (win.getParent() != null) {
						// window is already showing
						getWindow().showNotification("Window is already open");
					} else {
						// Open the subwindow by adding it to the parent
						// window
						// System.out.println("open window user");
						getWindow().addWindow(win);

					}
			}
		});

		addGroups.addListener(new Button.ClickListener() {
			public void buttonClick(ClickEvent event) {
				logger.info("Add group clicked");
				mag = new ModalAddGroups(control);
				Window win2 = mag.getSubwindow();
				if (win2 != null)
					if (win2.getParent() != null) {
						// window is already showing
						getWindow().showNotification("Window is already open");
					} else {
						// Open the subwindow by adding it to the parent
						// window
						// System.out.println("open window group");
						getWindow().addWindow(win2);
					}
			}
		});

	}

	/**
	 * Disables all the buttons.
	 */
	public void disableAll() {
		addUser.setEnabled(false);
		addGroups.setEnabled(false);
		settings.setEnabled(false);

		removeSelected.setEnabled(false);
		removeAll.setEnabled(false);
		saveChanges.setEnabled(false);
	}

	/**
	 * Enables all the buttons.
	 */
	public void enableAll() {
		addUser.setEnabled(true);
		addGroups.setEnabled(true);
		settings.setEnabled(true);

		removeSelected.setEnabled(true);
		removeAll.setEnabled(true);
		saveChanges.setEnabled(true);

	}

	/**
	 * Enables the lock and disables the unlock buttons.
	 */
	public void allowLock() {
		lock.setEnabled(true);
		unlock.setEnabled(false);
	}

	/**
	 * Enables the unlock and disables the lock buttons.
	 */
	public void allowUnlock() {
		lock.setEnabled(false);
		unlock.setEnabled(true);
	}

	/**
	 * Returns the add user button.
	 *
	 * @return the add user button
	 *
	 */
	public Button getAddUser() {
		return addUser;
	}

	/**
	 * Sets the new add user button.
	 *
	 * @param addUser the new add user button
	 *
	 */
	public void setAddUser(Button addUser) {
		this.addUser = addUser;
	}

	/**
	 * Returns the add groups button.
	 *
	 * @return the add groups button
	 *
	 */
	public Button getAddGroups() {
		return addGroups;
	}

	/**
	 * Returns the lock button.
	 *
	 * @return the lock button
	 *
	 */
	public Button getLock() {
		return lock;
	}

	/**
	 * Sets the new lock button.
	 *
	 * @param lock the new lock button
	 *
	 */
	public void setLock(Button lock) {
		this.lock = lock;
	}

	/**
	 * Returns the unlock button.
	 *
	 * @return the unlock button
	 *
	 */
	public Button getUnlock() {
		return unlock;
	}

	/**
	 * Sets the new unlock button.
	 *
	 * @param unlock the new unlock button
	 *
	 */
	public void setUnlock(Button unlock) {
		this.unlock = unlock;
	}

	/**
	 * Sets the new add groups button.
	 *
	 * @param addGroups the new add groups button
	 *
	 */
	public void setAddGroups(Button addGroups) {
		this.addGroups = addGroups;
	}

	/**
	 * Returns the settings button.
	 *
	 * @return the settings button
	 *
	 */
	public Button getSettings() {
		return settings;
	}

	/**
	 * Sets the new settings button.
	 *
	 * @param settings the new settings button
	 *
	 */
	public void setSettings(Button settings) {
		this.settings = settings;
	}

	/**
	 * Returns the remove selected button.
	 *
	 * @return the remove selected button
	 *
	 */
	public Button getRemoveSelected() {
		return removeSelected;
	}

	/**
	 * Sets the new remove selected button.
	 *
	 * @param removeSelected the new remove selected button
	 *
	 */
	public void setRemoveSelected(Button removeSelected) {
		this.removeSelected = removeSelected;
	}

	/**
	 * Returns the remove all button.
	 *
	 * @return the remove all button
	 *
	 */
	public Button getRemoveAll() {
		return removeAll;
	}

	/**
	 * Sets the new remove all button.
	 *
	 * @param removeAll the new remove all button
	 *
	 */
	public void setRemoveAll(Button removeAll) {
		this.removeAll = removeAll;
	}

	/**
	 * Returns the save changes button.
	 *
	 * @return the save changes button
	 *
	 */
	public Button getSaveChanges() {
		return saveChanges;
	}

	/**
	 * Sets the new save changes button.
	 *
	 * @param saveChanges the new save changes button
	 *
	 */
	public void setSaveChanges(Button saveChanges) {
		this.saveChanges = saveChanges;
	}
}
