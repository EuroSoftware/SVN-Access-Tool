package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * Represent the window for adding users.
 */
public class ModalAddUsers extends VerticalLayout {

	/** The subwindow for user adding. */
	Window subwindow;

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ModalAddUsers.class);

	/** The text of the R rights checkbox. */
	public static final I18NResource R_CHECKBOX_TEXT = new I18NResource(
			ModalAddUsers.class, "R_CHECKBOX_TEXT", "R");

	/** The text of the W rights checkbox. */
	public static final I18NResource W_CHECKBOX_TEXT = new I18NResource(
			ModalAddUsers.class, "W_CHECKBOX_TEXT", "W");

	/** The text of the M rights checkbox. */
	public static final I18NResource M_CHECKBOX_TEXT = new I18NResource(
			ModalAddUsers.class, "M_CHECKBOX_TEXT", "M");

	/** The description of the R rights checkbox. */
	public static final I18NResource R_CHECKBOX_DESCRIPTION = new I18NResource(
			ModalAddUsers.class, "R_CHECKBOX_DESCRIPTION", "Read rights");

	/** The description of the W rights checkbox. */
	public static final I18NResource W_CHECKBOX_DESCRIPTION = new I18NResource(
			ModalAddUsers.class, "W_CHECKBOX_DESCRIPTION", "Write rights");

	/** The description of the M rights checkbox. */
	public static final I18NResource M_CHECKBOX_DESCRIPTION = new I18NResource(
			ModalAddUsers.class, "M_CHECKBOX_DESCRIPTION", "Modify rights");

	/** The add user window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalAddUsers.class, "WINDOW_CAPTION_TEXT", "Add user");

	/** The ok button text. */
	public static final I18NResource OK_BUTTON_TEXT = new I18NResource(
			ModalAddUsers.class, "OK_BUTTON_TEXT", "OK");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalAddUsers.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/** The header text of the warning message. */
	public static final I18NResource WARNING_MESSAGE_HEADER_TEXT = new I18NResource(
			ModalAddUsers.class, "WARNING_MESSAGE_HEADER_TEXT",
			"Warning message");

	/** The warning message text for no users selected. */
	public static final I18NResource WARNING_MESSAGE_NOUSER_TEXT = new I18NResource(
			ModalAddUsers.class, "WARNING_MESSAGE_NOUSER_TEXT",
			"No user selected! Please, select at least one user.");

	/** The warning message text for no rights selected. */
	public static final I18NResource WARNING_MESSAGE_NORIGHTS_TEXT = new I18NResource(
			ModalAddUsers.class, "WARNING_MESSAGE_NORIGHTS_TEXT",
			"Are you sure you want to disable inherited rights for selected users?");

	/** The header text of the warning message for no rights selected. */
	public static final I18NResource WARNING_MESSAGE_NORIGHTS_HEADER = new I18NResource(
			ModalAddUsers.class, "WARNING_MESSAGE_NORIGHTS_HEADER",
			"No rights selected");

	/** The rights of the added user. */
	private String rights = "";

	/** The list for selecting the users. */
	private ListSelect l;

	/** The control class. */
	private static IControl control;

	/** The R rights checkbox. */
	private CheckBox cbR;

	/** The M rights checkbox. */
	private CheckBox cbM;

	/** The W rights checkbox. */
	private CheckBox cbW;

	/** The textfield for filtering. */
	private TextField filterField;

	/** The name container. */
	private IndexedContainer nameContainer;

	/** The ok button. */
	private Button ok;

	/**
	 * Returns the subwindow for user adding.
	 *
	 * @return the subwindow for user adding
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
	 * Returns the name container.
	 *
	 * @return the name container
	 *
	 */
	public static IndexedContainer getNameContainer() {
		IndexedContainer contactContainer = new IndexedContainer();
		contactContainer.addContainerProperty("User", String.class, "");
		Item item;

		for (String s : control.getUsersFiltered(control.getApplication()
				.getRepositories().getChosenRepo(), control.getApplication()
				.getDirectories().getLastChosenItemId().getFullPath())) {

			item = contactContainer.addItem(s);
			if (item != null) {
				item.getItemProperty("User").setValue(s);
			}
		}
		return contactContainer;
	}

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public ModalAddUsers(final IControl control) {
		this.control = control;
		subwindow = new Window(I18NSupport.getMessage(WINDOW_CAPTION_TEXT));
		subwindow.setModal(true);

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();

		setSpacing(true);

		filterField = new TextField();
		filterField.setTextChangeEventMode(TextChangeEventMode.LAZY);
		filterField.setTextChangeTimeout(200);

		filterField.addListener(new TextChangeListener() {
			@Override
			public void textChange(TextChangeEvent event) {
				nameContainer.removeAllContainerFilters();
				nameContainer.addContainerFilter("User", event.getText(), true,
						false);
				if (nameContainer.getItemIds().size() == 1) {
					for (Object a : nameContainer.getItemIds()) {
						l.select(a);
					}
				}
			}

		});
		filterField.setWidth(100, UNITS_PERCENTAGE);
		layout.addComponent(filterField);

		l = new ListSelect("");
		l.setSizeFull();
		l.setNullSelectionAllowed(true);
		l.setMultiSelect(true);
		l.setImmediate(true);


		layout.addComponent(l);

		cbR = new CheckBox(I18NSupport.getMessage(R_CHECKBOX_TEXT));
		cbW = new CheckBox(I18NSupport.getMessage(W_CHECKBOX_TEXT));
		cbM = new CheckBox(I18NSupport.getMessage(M_CHECKBOX_TEXT));

		cbR.setSizeFull();
		cbW.setSizeFull();
		cbM.setSizeFull();

		cbR.setDescription(I18NSupport.getMessage(R_CHECKBOX_DESCRIPTION));
		cbW.setDescription(I18NSupport.getMessage(W_CHECKBOX_DESCRIPTION));
		cbM.setDescription(I18NSupport.getMessage(M_CHECKBOX_DESCRIPTION));

		HorizontalLayout hl12 = new HorizontalLayout();
		hl12.addComponent(cbR);
		hl12.addComponent(cbW);
		hl12.addComponent(cbM);
		hl12.setExpandRatio(cbR, 1);
		hl12.setExpandRatio(cbW, 1);
		hl12.setExpandRatio(cbM, 1);

		hl12.setComponentAlignment(cbR, Alignment.MIDDLE_CENTER);
		hl12.setComponentAlignment(cbW, Alignment.MIDDLE_CENTER);
		hl12.setComponentAlignment(cbM, Alignment.MIDDLE_CENTER);

		hl12.setHeight(30, UNITS_PIXELS);
		hl12.setWidth(100, UNITS_PERCENTAGE);

		layout.addComponent(hl12);
		layout.setComponentAlignment(hl12, Alignment.MIDDLE_CENTER);

		Button cancel = new Button(I18NSupport.getMessage(CANCEL_BUTTON_TEXT),
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						(subwindow.getParent()).removeWindow(subwindow);
					}
				});
		cancel.setClickShortcut(KeyCode.ESCAPE);
		ok = new Button(I18NSupport.getMessage(OK_BUTTON_TEXT),
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						rights = "";
						if ((Boolean) cbR.getValue()) {
							rights = "r";
						}
						if ((Boolean) cbW.getValue()) {
							rights += "w";
						}
						if ((Boolean) cbM.getValue()) {
							rights += "m";
						}

						if (((Set<String>) (l.getValue())).size() == 0) {
							control.getApplication()
									.getWindow()
									.showNotification(
											I18NSupport
													.getMessage(WARNING_MESSAGE_HEADER_TEXT),
											I18NSupport
													.getMessage(WARNING_MESSAGE_NOUSER_TEXT),
											Notification.TYPE_WARNING_MESSAGE);

						} else { // nejsou vybrany prava
							if (rights.length() == 0) {

								final ModalConfirmationDialog mcd = new ModalConfirmationDialog(
										I18NSupport
												.getMessage(WARNING_MESSAGE_NORIGHTS_HEADER),
										I18NSupport
												.getMessage(WARNING_MESSAGE_NORIGHTS_TEXT));

								control.getApplication().getWindow()
										.addWindow(mcd.getSubwindow());
								mcd.getOk().focus();
								mcd.getOk().addListener(
										new Button.ClickListener() {

											public void buttonClick(
													ClickEvent event) {
												control.addUsers(
														(Set<String>) l
																.getValue(),
														rights);
												(control.getApplication()
														.getWindow()).removeWindow(mcd
														.getSubwindow());

												subwindow
														.getParent()
														.removeWindow(subwindow);
											}
										});
								mcd.getCancel().addListener(
										new Button.ClickListener() {

											public void buttonClick(
													ClickEvent event) {
												(control.getApplication()
														.getWindow()).removeWindow(mcd
														.getSubwindow());
												ok.focus();
											}
										});

							} else {
								control.addUsers((Set<String>) l.getValue(),
										rights);
								subwindow.getParent().removeWindow(subwindow);
							}
						}
					}
				});
		ok.setClickShortcut(KeyCode.ENTER);
		ok.focus();
		HorizontalLayout hl11 = new HorizontalLayout();
		hl11.setHeight(20, UNITS_PIXELS);
		ok.setStyleName("link");
		ok.addStyleName("Button2");
		hl11.addComponent(ok);
		cancel.setStyleName("link");
		cancel.addStyleName("Button2");
		hl11.addComponent(cancel);
		hl11.setComponentAlignment(ok, Alignment.MIDDLE_CENTER);
		hl11.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);

		layout.addComponent(hl11);
		layout.setComponentAlignment(hl11, Alignment.TOP_CENTER);
		layout.setExpandRatio(l, 1);
		layout.setExpandRatio(hl12, 0);
		layout.setExpandRatio(hl11, 0);

	}

	/**
	 * Initializes the components.
	 *
	 * @throws NullPointerException initialization failed
	 *
	 */
	private void init() throws NullPointerException {
		logger.info("Modal window for add users initialization");
		subwindow.setWidth(280, UNITS_PIXELS);
		subwindow.setHeight(430, UNITS_PIXELS);
		l.removeAllItems();
		ok.focus();
		filterField.setValue("");

		cbR.setValue(false);
		cbW.setValue(false);
		cbM.setValue(false);

		nameContainer = getNameContainer();
		l.setContainerDataSource(nameContainer);
	}
}
