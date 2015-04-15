package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.xbill.DNS.TextParseException;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.bussiness.impl.Authentization;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.ViewInformation;
import com.gk_software.tools.svnaccess.data.ldap.LDAPSettings;
import com.gk_software.tools.svnaccess.data.ldap.Server;
import com.gk_software.tools.svnaccess.utils.Constants;
import com.gk_software.tools.svnaccess.view.components.FavoriteBean;
import com.gk_software.tools.svnaccess.view.components.TwinColumnSelect;
import com.vaadin.data.Container.ItemSetChangeEvent;
import com.vaadin.data.Container.ItemSetChangeListener;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

/**
 * Represent the window for application settings.
 */
public class ModalWindowSettings extends VerticalLayout {

	/** The serial id. */
	private static final long serialVersionUID = -3928418669441812937L;

	/** The subwindow for application settings. */
	private Window subwindow;

	/** The repository root textfield. */
	private TextField repoRoot;

	/** The access list file location textfield. */
	private TextField aclLocation;

	/** The domain name textfield. */
	private TextField domainName;

	/** The port textfield. */
	private TextField port;

	/** The user namespace textfield. */
	private TextField userNamespace;

	/** The user search filter textfield. */
	private TextField userSearchFilter;

	/** The user name attribute textfield. */
	private TextField userNameAttribute;

	/** The group namespace textfield. */
	private TextField groupNamespace;

	/** The group search filter textfield. */
	private TextField groupSearchFilter;

	/** The check interval textfield. */
	private TextField checkInterval;

	/** The timeout textfield. */
	private TextField timeout;

	/** The backup folder textfield. */
	private TextField backup;

	/** The replace string textfield. */
	private TextField labelReplace;

	/** The cookies timeout textfield. */
	private TextField cookieField;

	/** The mail folder textfield. */
	private TextField mailField;

	/** The login textfield. */
	private TextField loginName;

	/** The mail suffix textfield. */
	private TextField mailSuffix;

	/** The password textfield. */
	private PasswordField password;

	/** The mail notification checkbox. */
	private CheckBox mailNotif;

	/** The combobox for server selection. */
	private ComboBox serverSelect;

	/** The combobox for LDAP selection. */
	private ComboBox activDirSelect;

	/** The control class. */
	private IControl control;

	/** The responsible person settings button. */
	private Button buttonResponsible;

	/** The add server button. */
	private Button addServer;

	/** The two column select for mail settings. */
	private TwinColumnSelect tcs;

	/** The two column select for inheritance free settings. */
	private TwinColumnSelect inheritance_free;

	/** The LDAP settings class. */
	private LDAPSettings ls;

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger
			.getLogger(ModalWindowSettings.class);

	/** The application settings window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalWindowSettings.class, "WINDOW_CAPTION_TEXT", "Settings");

	/** The path label text. */
	public static final I18NResource LABEL_PATH_SETTINGS = new I18NResource(
			ModalWindowSettings.class, "LABEL_PATH_SETTINGS", "Path Settings");

	/** The repository root label text. */
	public static final I18NResource LABEL_SVN_REPO_ROOT = new I18NResource(
			ModalWindowSettings.class, "LABEL_SVN_REPO_ROOT", "SVN repository root");

	/** The access file location label text. */
	public static final I18NResource LABEL_ACL_FILE_LOCATION = new I18NResource(
			ModalWindowSettings.class, "LABEL_ACL_FILE_LOCATION", "ACL file location");

	/** The mail folder label text. */
	public static final I18NResource MAIL_FOLDER_LOCATION = new I18NResource(
			ModalWindowSettings.class, "MAIL_FOLDER_LOCATION", "Mail folder location");

	/** The LDAP settings label text. */
	public static final I18NResource LABEL_AD_SETTINGS = new I18NResource(
			ModalWindowSettings.class, "LABEL_AD_SETTINGS",
			"Settings of Access to Active Directory");

	/** The other settings label text. */
	public static final I18NResource LABEL_OTHER_SETTINGS = new I18NResource(
			ModalWindowSettings.class, "LABEL_OTHER_SETTINGS", "Other settings");

	/** The domain name label text. */
	public static final I18NResource LABEL_DOMAIN_NAME = new I18NResource(
			ModalWindowSettings.class, "LABEL_DOMAIN_NAME", "Domain name");

	/** The text of the select prompt. */
	public static final I18NResource SERVER_SELECT_PROMPT = new I18NResource(
			ModalWindowSettings.class, "SERVER_SELECT_PROMPT",
			"Please select a server");

	/** The port label text. */
	public static final I18NResource LABEL_PORT = new I18NResource(
			ModalWindowSettings.class, "LABEL_PORT", "Port");

	/** The base user namespace label text. */
	public static final I18NResource LABEL_BASE_USER_NAMESPACE = new I18NResource(
			ModalWindowSettings.class, "LABEL_BASE_USER_NAMESPACE",
			"Base User Namespace");

	/** The user search filter label text. */
	public static final I18NResource LABEL_USER_SEARCH_FILTER = new I18NResource(
			ModalWindowSettings.class, "LABEL_USER_SEARCH_FILTER",
			"User search filter");

	/** The user name attribute label text. */
	public static final I18NResource LABEL_USER_NAME_ATTRIBUTE = new I18NResource(
			ModalWindowSettings.class, "LABEL_USER_NAME_ATTRIBUTE",
			"User name attribute");

	/** The base group namespace label text. */
	public static final I18NResource LABEL_BASE_GROUPS_NAMESPACE = new I18NResource(
			ModalWindowSettings.class, "LABEL_BASE_GROUPS_NAMESPACE",
			"Base Groups Namespace");

	/** The group search filter label text. */
	public static final I18NResource LABEL_GROUP_SEARCH_FILTER = new I18NResource(
			ModalWindowSettings.class, "LABEL_GROUP_SEARCH_FILTER",
			"Group Search filter");

	/** The check interval label text. */
	public static final I18NResource LABEL_CHECK_TIME_INTERVAL = new I18NResource(
			ModalWindowSettings.class, "LABEL_CHECK_TIME_INTERVAL",
			"Check time interval [min]");

	/** The timeout label text. */
	public static final I18NResource LABEL_TIMEOUT = new I18NResource(
			ModalWindowSettings.class, "LABEL_TIMEOUT", "Timeout [min]");

	/** The managers label text. */
	public static final I18NResource LABEL_MANAGERS = new I18NResource(
			ModalWindowSettings.class, "LABEL_MANAGERS", "Managers");

	/** The cookies timeout label text. */
	public static final I18NResource LABEL_COOKIE = new I18NResource(
			ModalWindowSettings.class, "LABEL_COOKIE", "Cookies timeout [hour]");

	/** The responsible person label text. */
	public static final I18NResource LABEL_RESPONSIBLE_PERSON = new I18NResource(
			ModalWindowSettings.class, "LABEL_RESPONSIBLE_PERSON",
			"SVN responsible person");

	/** The mail notification label text. */
	public static final I18NResource LABEL_MAIL_NOTIFICATION = new I18NResource(
			ModalWindowSettings.class, "LABEL_MAIL_NOTIFICATION", "Mail notification");

	/** The inheritance free label text. */
	public static final I18NResource LABEL_INHERITANCE_FREE = new I18NResource(
			ModalWindowSettings.class, "LABEL_INHERITANCE_FREE",
			"Rights inheritance free");

	/** The responsible person button text. */
	public static final I18NResource BUTTON_LABEL_RESPONSIBLE_PERSON = new I18NResource(
			ModalWindowSettings.class, "BUTTON_LABEL_RESPONSIBLE_PERSON",
			"Edit responsible persons");

	/** The label text for the left column of the admin rights select. */
	public static final I18NResource ADMIN_RIGHTS_LABEL_LEFT_TEXT = new I18NResource(
			ModalWindowSettings.class, "ADMIN_RIGHTS_LABEL_LEFT_TEXT",
			"Users without Admin Rights");

	/** The label text for the right column of the admin rights select. */
	public static final I18NResource ADMIN_RIGHTS_LABEL_RIGHT_TEXT = new I18NResource(
			ModalWindowSettings.class, "ADMIN_RIGHTS_LABEL_RIGHT_TEXT",
			"Users with Admin Rights");

	/** The label text for the left column of the inheritance free select. */
	public static final I18NResource INHERITANCE_FREE_LABEL_LEFT_TEXT = new I18NResource(
			ModalWindowSettings.class, "INHERITANCE_FREE_LABEL_LEFT_TEXT",
			"List of groups");

	/** The label text for the right column of the inheritance free select. */
	public static final I18NResource INHERITANCE_FREE_LABEL_RIGHT_TEXT = new I18NResource(
			ModalWindowSettings.class, "INHERITANCE_FREE_LABEL_RIGHT_TEXT",
			"Rights inheritance free");

	/** The backup folder label text. */
	public static final I18NResource LABEL_BACKUP = new I18NResource(
			ModalWindowSettings.class, "LABEL_BACKUP", "Backup folder");

	/** The replace string label text. */
	public static final I18NResource LABEL_REPLACE = new I18NResource(
			ModalWindowSettings.class, "LABEL_REPLACE", "Replace * = r with");

	/** The ok button text. */
	public static final I18NResource OK_BUTTON_TEXT = new I18NResource(
			ModalWindowSettings.class, "OK_BUTTON_TEXT", "OK");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalWindowSettings.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/** The add server button text. */
	public static final I18NResource BUTTON_LABEL_ADD_SERVER = new I18NResource(
			ModalWindowSettings.class, "BUTTON_LABEL_ADD_SERVER", "Add Server");

	/** The server beans container. */
	private BeanItemContainer<Server> serverContainer;

	/**
	 * Returns the subwindow for application settings.
	 *
	 * @return the subwindow for application settings
	 *
	 */
	public Window getSubwindow() {
		init();
		return subwindow;
	}

	/**
	 * Sets the new subwindow for application settings.
	 *
	 * @param subwindow the new subwindow for application settings
	 *
	 */
	public void setSubwindow(Window subwindow) {
		this.subwindow = subwindow;
	}

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public ModalWindowSettings(final IControl control) {
		this.control = control;

		setSpacing(false);
		subwindow = new Window(I18NSupport.getMessage(WINDOW_CAPTION_TEXT));
		subwindow.setModal(true);

		Panel panel = new Panel();
		panel.setSizeFull();

		panel.addComponent(createPathSettingsPanel());
		panel.addComponent(createHorizontalSeparator());
		panel.addComponent(createLDAPsettingsPanel(control));
		panel.addComponent(createHorizontalSeparator());
		panel.addComponent(createOtherSettingsPanel(control));
		panel.addComponent(createHorizontalSeparator());
		panel.addComponent(createInheritanceFreeSelection(control));
		panel.addComponent(createHorizontalSeparator());
		panel.addComponent(createAdminUsersSelection(control));
		panel.addComponent(createButtonsPanel(control));

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(false);
		layout.setSpacing(false);
		layout.addComponent(panel);
		subwindow.setScrollTop(0);
		panel.setScrollTop(0);
	}

	/**
	 * Creates the buttons panel.
	 *
	 * @param control the control class
	 *
	 * @return the created panel
	 *
	 */
	private HorizontalLayout createButtonsPanel(final IControl control) {
		Button cancel = new Button(I18NSupport.getMessage(CANCEL_BUTTON_TEXT),
				new Button.ClickListener() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						(subwindow.getParent()).removeWindow(subwindow);
					}
				});
		cancel.setClickShortcut(KeyCode.ESCAPE);
		cancel.setStyleName("link");
		cancel.addStyleName("Button2");

		Button ok = new Button(I18NSupport.getMessage(OK_BUTTON_TEXT),
				new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						if (checkFields()) {
							updateConstants();
							try {
								LDAPSettings.saveLDAPSettings(ls);
							} catch (JAXBException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
							(subwindow.getParent()).removeWindow(subwindow);
						} else {
							control
									.getApplication()
									.getWindow()
									.showNotification(
											"Warning message",
											"Some of a fields are not filled or are filled wrong! Changes will not be saved.",
											Notification.TYPE_WARNING_MESSAGE);
						}
					}

				});
		ok.setClickShortcut(KeyCode.ENTER);
		ok.focus();
		ok.setStyleName("link");
		ok.addStyleName("Button2");

		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setWidth(150, UNITS_PIXELS);
		buttons.setHeight(50, UNITS_PIXELS);
		buttons.addComponent(ok);
		buttons.addComponent(cancel);
		buttons.setComponentAlignment(ok, Alignment.MIDDLE_CENTER);
		buttons.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);

		HorizontalLayout buttonsWraper = new HorizontalLayout();
		buttonsWraper.setWidth(100, UNITS_PERCENTAGE);
		buttonsWraper.addComponent(buttons);
		buttonsWraper.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
		return buttonsWraper;
	}

	/**
	 * Creates the admin users selection section.
	 *
	 * @param control the control class
	 *
	 * @return the admin users selection section
	 *
	 */
	private HorizontalLayout createAdminUsersSelection(final IControl control) {
		HorizontalLayout adminUsersSelection = new HorizontalLayout();
		adminUsersSelection.setHeight(120, UNITS_PIXELS);
		adminUsersSelection.setWidth(100, UNITS_PERCENTAGE);
		tcs = new TwinColumnSelect(control.getUsersAll(),
				I18NSupport.getMessage(ADMIN_RIGHTS_LABEL_LEFT_TEXT),
				I18NSupport.getMessage(ADMIN_RIGHTS_LABEL_RIGHT_TEXT), false);
		adminUsersSelection.addComponent(tcs);
		return adminUsersSelection;
	}

	/**
	 * Creates the inheritance free selection section.
	 *
	 * @param control the control class
	 *
	 * @return the inheritance free selection section
	 *
	 */
	private HorizontalLayout createInheritanceFreeSelection(final IControl control) {
		HorizontalLayout inheritanceFreeSelection = new HorizontalLayout();
		inheritanceFreeSelection.setHeight(120, UNITS_PIXELS);
		inheritanceFreeSelection.setWidth(100, UNITS_PERCENTAGE);
		inheritance_free = new TwinColumnSelect(control.getGroupsAll(),
				I18NSupport.getMessage(INHERITANCE_FREE_LABEL_LEFT_TEXT),
				I18NSupport.getMessage(INHERITANCE_FREE_LABEL_RIGHT_TEXT), true);
		inheritanceFreeSelection.addComponent(inheritance_free);
		return inheritanceFreeSelection;
	}

	/**
	 * Creates the other settings panel.
	 *
	 * @param control the control class
	 *
	 * @return the other settings panel
	 *
	 */
	private Panel createOtherSettingsPanel(final IControl control) {
		HorizontalLayout replace = new HorizontalLayout();
		replace.setHeight(30, UNITS_PIXELS);
		replace.setWidth(100, UNITS_PERCENTAGE);
		Label label999 = new Label(I18NSupport.getMessage(LABEL_REPLACE));
		labelReplace = new TextField();
		backup.setWidth(100, UNITS_PERCENTAGE);
		label999.setWidth(150, UNITS_PIXELS);
		replace.addComponent(label999);
		replace.addComponent(labelReplace);
		replace.setExpandRatio(label999, 0);
		replace.setExpandRatio(labelReplace, 1);

		HorizontalLayout checkTimeInterval = new HorizontalLayout();
		checkTimeInterval.setHeight(30, UNITS_PIXELS);
		checkTimeInterval.setWidth(100, UNITS_PERCENTAGE);
		Label label10 = new Label(I18NSupport.getMessage(LABEL_CHECK_TIME_INTERVAL));
		checkInterval = new TextField();
		checkInterval.setWidth(100, UNITS_PERCENTAGE);
		label10.setWidth(150, UNITS_PIXELS);
		checkTimeInterval.addComponent(label10);
		checkTimeInterval.addComponent(checkInterval);
		checkTimeInterval.setExpandRatio(label10, 0);
		checkTimeInterval.setExpandRatio(checkInterval, 1);

		HorizontalLayout timeoutLine = new HorizontalLayout();
		timeoutLine.setHeight(30, UNITS_PIXELS);
		timeoutLine.setWidth(100, UNITS_PERCENTAGE);
		Label label12 = new Label(I18NSupport.getMessage(LABEL_TIMEOUT));
		timeout = new TextField();
		timeout.setWidth(100, UNITS_PERCENTAGE);
		label12.setWidth(150, UNITS_PIXELS);
		timeoutLine.addComponent(label12);
		timeoutLine.addComponent(timeout);
		timeoutLine.setExpandRatio(label12, 0);
		timeoutLine.setExpandRatio(timeout, 1);

		HorizontalLayout cookiesTimeout = new HorizontalLayout();
		cookiesTimeout.setHeight(30, UNITS_PIXELS);
		cookiesTimeout.setWidth(100, UNITS_PERCENTAGE);
		Label label14 = new Label(I18NSupport.getMessage(LABEL_COOKIE));
		cookieField = new TextField();
		cookieField.setWidth(100, UNITS_PERCENTAGE);
		label14.setWidth(150, UNITS_PIXELS);
		cookiesTimeout.addComponent(label14);
		cookiesTimeout.addComponent(cookieField);
		cookiesTimeout.setExpandRatio(label14, 0);
		cookiesTimeout.setExpandRatio(cookieField, 1);

		HorizontalLayout mailNotification = new HorizontalLayout();
		mailNotification.setHeight(30, UNITS_PIXELS);
		mailNotification.setWidth(100, UNITS_PERCENTAGE);
		Label label12r = new Label(I18NSupport.getMessage(LABEL_MAIL_NOTIFICATION));
		mailNotif = new CheckBox();
		label12r.setWidth(150, UNITS_PIXELS);
		mailNotification.addComponent(label12r);
		mailNotification.addComponent(mailNotif);
		mailNotification.setExpandRatio(label12r, 0);
		mailNotification.setExpandRatio(mailNotif, 1);

		HorizontalLayout reponsiblePerson = new HorizontalLayout();
		reponsiblePerson.setHeight(30, UNITS_PIXELS);
		Label lableResponsiblePersons = new Label(
				I18NSupport.getMessage(LABEL_RESPONSIBLE_PERSON));
		buttonResponsible = new Button(
				I18NSupport.getMessage(BUTTON_LABEL_RESPONSIBLE_PERSON),
				new Button.ClickListener() {
					/**
					 *
					 */
					private static final long serialVersionUID = 1L;

					public void buttonClick(ClickEvent event) {
						ModalResponsiblePerson mrp = new ModalResponsiblePerson(control);
						Window win = mrp.getSubwindow();

						if (win.getParent() != null) {

							control.getApplication().getWindow()
									.showNotification("Window is already open");
						} else {
							control.getApplication().getWindow().addWindow(win);
						}
					}
				});
		lableResponsiblePersons.setWidth("150px");
		reponsiblePerson.addComponent(lableResponsiblePersons);
		reponsiblePerson.addComponent(buttonResponsible);

		Panel otherSettingsPanel = new Panel(
				I18NSupport.getMessage(LABEL_OTHER_SETTINGS));
		otherSettingsPanel.addComponent(replace);
		otherSettingsPanel.addComponent(checkTimeInterval);
		otherSettingsPanel.addComponent(timeoutLine);
		otherSettingsPanel.addComponent(cookiesTimeout);
		otherSettingsPanel.addComponent(mailNotification);
		otherSettingsPanel.addComponent(reponsiblePerson);
		return otherSettingsPanel;
	}

	/**
	 * Creates the LDAP settings panel.
	 *
	 * @param control the control class
	 *
	 * @return the LDAP settings panel
	 *
	 */
	private Panel createLDAPsettingsPanel(final IControl control) {
		HorizontalLayout hl3 = new HorizontalLayout();
		hl3.setHeight(30, UNITS_PIXELS);
		hl3.setWidth(100, UNITS_PERCENTAGE);

		Label label3 = new Label(I18NSupport.getMessage(LABEL_DOMAIN_NAME));
		label3.setWidth(150, UNITS_PIXELS);

		domainName = new TextField();
		domainName.setWidth(100, UNITS_PERCENTAGE);
		// domainName.addListener(listenerDomain);
		domainName.addListener(listenerDomainFocus);
		domainName.addListener(listenerDomainName);
		domainName.addListener(listenerDomain);
		domainName.setImmediate(true);

		serverSelect = new ComboBox();
		serverSelect.addListener(listenerServerSelectFocus);
		serverSelect.addListener(listenerDomainServer);
		serverSelect.setNullSelectionAllowed(false);
		serverSelect.setInputPrompt(I18NSupport.getMessage(SERVER_SELECT_PROMPT));
		serverSelect.setImmediate(true);
		serverSelect.setWidth(100, UNITS_PERCENTAGE);

		hl3.addComponent(label3);
		hl3.addComponent(domainName);
		hl3.addComponent(serverSelect);
		hl3.setExpandRatio(label3, 0);
		hl3.setExpandRatio(domainName, 1);
		hl3.setExpandRatio(serverSelect, 1);

		HorizontalLayout hl4HorizontalLayout = new HorizontalLayout();
		hl4HorizontalLayout.setHeight(30, UNITS_PIXELS);
		hl4HorizontalLayout.setWidth(100, UNITS_PERCENTAGE);

		Label label5l = new Label("Active Directory servers");
		label5l.setWidth(150, UNITS_PIXELS);

		activDirSelect = new ComboBox();
		activDirSelect.addListener(listenerLDAPSelect);
		activDirSelect.setWidth(100, UNITS_PERCENTAGE);
		activDirSelect.setNullSelectionAllowed(false);
		activDirSelect.setInputPrompt(I18NSupport.getMessage(SERVER_SELECT_PROMPT));
		activDirSelect.setImmediate(true);

		addServer = new Button(I18NSupport.getMessage(BUTTON_LABEL_ADD_SERVER),
				new Button.ClickListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void buttonClick(ClickEvent event) {
						ModalNewServerSettings mnss = new ModalNewServerSettings(control,
								ModalWindowSettings.this);
						subwindow.getParent().addWindow(mnss.getSubwindow());
					}
				});

		hl4HorizontalLayout.addComponent(label5l);
		hl4HorizontalLayout.addComponent(activDirSelect);
		hl4HorizontalLayout.addComponent(addServer);

		hl4HorizontalLayout.setExpandRatio(label5l, 0);
		hl4HorizontalLayout.setExpandRatio(activDirSelect, 1);
		hl4HorizontalLayout.setExpandRatio(addServer, 0);

		HorizontalLayout hl4 = new HorizontalLayout();
		hl4.setHeight(30, UNITS_PIXELS);
		hl4.setWidth(100, UNITS_PERCENTAGE);
		Label label4 = new Label(I18NSupport.getMessage(LABEL_PORT));
		port = new TextField();
		port.setWidth(100, UNITS_PERCENTAGE);
		port.addListener(listenerPort);
		label4.setWidth(150, UNITS_PIXELS);
		hl4.addComponent(label4);
		hl4.addComponent(port);
		hl4.setExpandRatio(label4, 0);
		hl4.setExpandRatio(port, 1);

		HorizontalLayout hl5 = new HorizontalLayout();
		hl5.setHeight(30, UNITS_PIXELS);
		hl5.setWidth(100, UNITS_PERCENTAGE);
		Label label5 = new Label(I18NSupport.getMessage(LABEL_BASE_USER_NAMESPACE));
		userNamespace = new TextField();
		userNamespace.setWidth(100, UNITS_PERCENTAGE);
		userNamespace.addListener(listenerUserNamespace);
		label5.setWidth(150, UNITS_PIXELS);
		hl5.addComponent(label5);
		hl5.addComponent(userNamespace);
		hl5.setExpandRatio(label5, 0);
		hl5.setExpandRatio(userNamespace, 1);

		HorizontalLayout hl6 = new HorizontalLayout();
		hl6.setHeight(30, UNITS_PIXELS);
		hl6.setWidth(100, UNITS_PERCENTAGE);
		Label label6 = new Label(I18NSupport.getMessage(LABEL_USER_SEARCH_FILTER));
		userSearchFilter = new TextField();
		userSearchFilter.setWidth(100, UNITS_PERCENTAGE);
		userSearchFilter.addListener(listenerUserSearchFilter);
		label6.setWidth(150, UNITS_PIXELS);
		hl6.addComponent(label6);
		hl6.addComponent(userSearchFilter);
		hl6.setExpandRatio(label6, 0);
		hl6.setExpandRatio(userSearchFilter, 1);

		HorizontalLayout hl7 = new HorizontalLayout();
		hl7.setHeight(30, UNITS_PIXELS);
		hl7.setWidth(100, UNITS_PERCENTAGE);
		Label label7 = new Label(I18NSupport.getMessage(LABEL_USER_NAME_ATTRIBUTE));
		userNameAttribute = new TextField();
		userNameAttribute.setWidth(100, UNITS_PERCENTAGE);
		userNameAttribute.addListener(listenerUserNameAttribute);
		label7.setWidth(150, UNITS_PIXELS);
		hl7.addComponent(label7);
		hl7.addComponent(userNameAttribute);
		hl7.setExpandRatio(label7, 0);
		hl7.setExpandRatio(userNameAttribute, 1);

		HorizontalLayout hl8 = new HorizontalLayout();
		hl8.setHeight(30, UNITS_PIXELS);
		hl8.setWidth(100, UNITS_PERCENTAGE);
		Label label8 = new Label(
				I18NSupport.getMessage(LABEL_BASE_GROUPS_NAMESPACE));
		groupNamespace = new TextField();
		groupNamespace.setWidth(100, UNITS_PERCENTAGE);
		groupNamespace.addListener(listenerGroupNamespace);
		label8.setWidth(150, UNITS_PIXELS);
		hl8.addComponent(label8);
		hl8.addComponent(groupNamespace);
		hl8.setExpandRatio(label8, 0);
		hl8.setExpandRatio(groupNamespace, 1);

		HorizontalLayout hl9 = new HorizontalLayout();

		hl9.setHeight(30, UNITS_PIXELS);
		hl9.setWidth(100, UNITS_PERCENTAGE);
		Label label9 = new Label(I18NSupport.getMessage(LABEL_GROUP_SEARCH_FILTER));
		groupSearchFilter = new TextField();
		groupSearchFilter.setWidth(100, UNITS_PERCENTAGE);
		groupSearchFilter.addListener(listenerGroupSearchFilter);
		label9.setWidth(150, UNITS_PIXELS);
		hl9.addComponent(label9);
		hl9.addComponent(groupSearchFilter);
		hl9.setExpandRatio(label9, 0);
		hl9.setExpandRatio(groupSearchFilter, 1);

		loginName = new TextField();
		loginName.setWidth(100, UNITS_PERCENTAGE);
		loginName.setImmediate(true);
		loginName.addListener(listenerLogin);

		Label labelLoginName = new Label("Login");
		labelLoginName.setWidth(150, UNITS_PIXELS);

		password = new PasswordField();
		password.setWidth(100, UNITS_PERCENTAGE);
		password.setImmediate(true);
		password.addListener(listenerPassword);

		Label labelLoginPass = new Label("Password");
		labelLoginPass.setWidth(70, UNITS_PIXELS);

		VerticalLayout verticalSpace = new VerticalLayout();
		verticalSpace.setWidth(15, UNITS_PIXELS);

		HorizontalLayout lineLoginInfo = new HorizontalLayout();
		lineLoginInfo.setHeight(30, UNITS_PIXELS);
		lineLoginInfo.setWidth(100, UNITS_PERCENTAGE);

		lineLoginInfo.addComponent(labelLoginName);
		lineLoginInfo.addComponent(loginName);
		lineLoginInfo.setExpandRatio(labelLoginName, 0);
		lineLoginInfo.setExpandRatio(loginName, 1);
		lineLoginInfo.addComponent(verticalSpace);
		lineLoginInfo.addComponent(labelLoginPass);
		lineLoginInfo.addComponent(password);
		lineLoginInfo.setExpandRatio(labelLoginPass, 0);
		lineLoginInfo.setExpandRatio(password, 1);

		lineLoginInfo.setComponentAlignment(labelLoginPass, Alignment.MIDDLE_RIGHT);

		HorizontalLayout mailSuffixHL = new HorizontalLayout();

		mailSuffixHL.setHeight(30, UNITS_PIXELS);
		mailSuffixHL.setWidth(100, UNITS_PERCENTAGE);
		Label labelMailSuffix = new Label("Mail suffix");
		mailSuffix = new TextField();
		mailSuffix.setWidth(100, UNITS_PERCENTAGE);
		mailSuffix.addListener(listenerMailSuffix);
		labelMailSuffix.setWidth(150, UNITS_PIXELS);
		mailSuffixHL.addComponent(labelMailSuffix);
		mailSuffixHL.addComponent(mailSuffix);
		mailSuffixHL.setExpandRatio(labelMailSuffix, 0);
		mailSuffixHL.setExpandRatio(mailSuffix, 1);

		Panel LDAPsettingsPanel = new Panel(
				"Settings of Access to Active Directory");

		LDAPsettingsPanel.addComponent(hl4HorizontalLayout);
		LDAPsettingsPanel.addComponent(hl3);
		LDAPsettingsPanel.addComponent(hl4);
		LDAPsettingsPanel.addComponent(lineLoginInfo);
		LDAPsettingsPanel.addComponent(hl5);
		LDAPsettingsPanel.addComponent(hl6);
		LDAPsettingsPanel.addComponent(hl7);
		LDAPsettingsPanel.addComponent(hl8);
		LDAPsettingsPanel.addComponent(hl9);
		LDAPsettingsPanel.addComponent(mailSuffixHL);
		return LDAPsettingsPanel;
	}

	/**
	 * Creates the horizontal separator.
	 *
	 * @return the horizontal separator
	 *
	 */
	private HorizontalLayout createHorizontalSeparator() {
		HorizontalLayout horizontal_separator = new HorizontalLayout();
		horizontal_separator.setWidth("100%");
		horizontal_separator.setHeight(10, UNITS_PIXELS);
		return horizontal_separator;
	}

	/**
	 * Creates the path settings panel.
	 *
	 * @return the path settings panel
	 *
	 */
	private Panel createPathSettingsPanel() {
		HorizontalLayout hl = new HorizontalLayout();
		hl.setHeight(30, UNITS_PIXELS);
		hl.setWidth(100, UNITS_PERCENTAGE);
		Label label = new Label(I18NSupport.getMessage(LABEL_SVN_REPO_ROOT));
		repoRoot = new TextField();
		repoRoot.setWidth(100, UNITS_PERCENTAGE);
		label.setWidth(150, UNITS_PIXELS);
		hl.addComponent(label);
		hl.addComponent(repoRoot);
		hl.setExpandRatio(label, 0);
		hl.setExpandRatio(repoRoot, 1);

		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.setHeight(30, UNITS_PIXELS);
		hl2.setWidth(100, UNITS_PERCENTAGE);
		Label label2 = new Label(I18NSupport.getMessage(LABEL_ACL_FILE_LOCATION));
		aclLocation = new TextField();
		aclLocation.setWidth(100, UNITS_PERCENTAGE);
		label2.setWidth(150, UNITS_PIXELS);
		hl2.addComponent(label2);
		hl2.addComponent(aclLocation);
		hl2.setExpandRatio(label2, 0);
		hl2.setExpandRatio(aclLocation, 1);

		HorizontalLayout hla = new HorizontalLayout();
		hla.setHeight(30, UNITS_PIXELS);
		hla.setWidth(100, UNITS_PERCENTAGE);
		Label labelMail = new Label(I18NSupport.getMessage(MAIL_FOLDER_LOCATION));
		mailField = new TextField();
		mailField.setWidth(100, UNITS_PERCENTAGE);
		labelMail.setWidth(150, UNITS_PIXELS);
		hla.addComponent(labelMail);
		hla.addComponent(mailField);
		hla.setExpandRatio(labelMail, 0);
		hla.setExpandRatio(mailField, 1);

		HorizontalLayout hl99 = new HorizontalLayout();
		hl99.setHeight(30, UNITS_PIXELS);
		hl99.setWidth(100, UNITS_PERCENTAGE);
		Label label99 = new Label(I18NSupport.getMessage(LABEL_BACKUP));
		backup = new TextField();
		backup.setWidth(100, UNITS_PERCENTAGE);
		label99.setWidth(150, UNITS_PIXELS);
		hl99.addComponent(label99);
		hl99.addComponent(backup);
		hl99.setExpandRatio(label99, 0);
		hl99.setExpandRatio(backup, 1);

		Panel panelPathSettings = new Panel(
				I18NSupport.getMessage(LABEL_PATH_SETTINGS));
		panelPathSettings.addComponent(hl);
		panelPathSettings.addComponent(hl2);
		panelPathSettings.addComponent(hla);
		panelPathSettings.addComponent(hl99);
		return panelPathSettings;
	}

	/**
	 * Checks if all the required fields have some value.
	 *
	 * @return true if they do otherwise false
	 *
	 */
	private boolean checkFields() {
		boolean isOk = true;

		if (repoRoot.getValue().toString().length() == 0)
			isOk = false;
		if (aclLocation.getValue().toString().length() == 0)
			isOk = false;
		if (mailField.getValue().toString().length() == 0)
			isOk = false;

		for (Server server : ls.getServer()) {
			isOk = server.isFilled();
		}

		if (checkInterval.getValue().toString().length() == 0)
			isOk = false;
		try {
			Integer.parseInt(checkInterval.getValue().toString());
		} catch (Exception e) {
			isOk = false;
		}

		if (backup.getValue().toString().length() == 0)
			isOk = false;

		if (labelReplace.getValue().toString().length() == 0)
			isOk = false;

		if (timeout.getValue().toString().length() == 0)
			isOk = false;
		try {
			Integer.parseInt(timeout.getValue().toString());
		} catch (Exception e) {
			isOk = false;
		}

		if (cookieField.getValue().toString().length() == 0)
			isOk = false;
		try {
			Integer.parseInt(cookieField.getValue().toString());
		} catch (Exception e) {
			isOk = false;
		}

		if (tcs.getSelected().length() == 0)
			isOk = false;

		return isOk;
	}

	/**
	 * Updates the constant variables with the values from the textfields.
	 */
	public void updateConstants() {

		String repoLast = (String) Constants.getProperties().setProperty(
				"repoRoot", repoRoot.getValue().toString());

		String aclLast = (String) Constants.getProperties().setProperty(
				"aclLocation", aclLocation.getValue().toString());

		String timeoutLast = (String) Constants.getProperties().setProperty(
				"timeout", timeout.getValue().toString());

		Constants.getProperties().setProperty(
				"checkInterval", checkInterval.getValue().toString());

		Constants.getProperties().setProperty(
				"cookieExp", cookieField.getValue().toString());

		Constants.getProperties().setProperty("mail_folder",
				mailField.getValue().toString());

		String labelReplaceLast = (String) Constants.getProperties().setProperty(
				"labelReplace", labelReplace.getValue().toString());

		Constants.getProperties().setProperty("backupFolder",
				backup.getValue().toString());
		Constants.getProperties().setProperty("tcs", tcs.getSelected());
		Constants.getProperties().setProperty("inheritance_free",
				inheritance_free.getSelected());

		String mailNotifLast = (String) Constants.getProperties().setProperty(
				"mailNotification", mailNotif.getValue().toString());

		if (!mailNotifLast.equals((String) Constants.getProperties().getProperty(
				"mailNotification"))) {
			try {
				if (Boolean.parseBoolean(Constants.getProperties()
						.getProperty("mailNotification").toString())) {
					ViewInformation.getInstance().startMailChecker();
				} else {
					ViewInformation.getInstance().stopMailChecker();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!labelReplaceLast.equals((String) Constants.getProperties()
				.getProperty("labelReplace"))) {
			logger.info("Access List realoaded, replace *=r changed.");
			try {
				control.reloadACL();

			} catch (Exception e) {
				logger.error("Error during reloading Access List");
				logger.error(e.getMessage());
				e.printStackTrace();
			}

			if (control.getApplication().getRepositories().getChosenRepo().trim()
					.length() != 0) {
			}
		}

		control.reloadLdap();

		if (!timeoutLast.equals((String) Constants.getProperties().getProperty(
				"timeout"))) {
			int t = Integer
					.parseInt(Constants.getProperties().getProperty("timeout")) * 60;
			((WebApplicationContext) control.getApplication().getApplication()
					.getContext()).getHttpSession().setMaxInactiveInterval(t);
			logger
					.info("Interval for Timeout was changed. Setting inactive interval to: "
							+ t + " s.");
		}

		Constants.saveConstants();
		control.checkAdmin();

		if (!repoLast.equals((String) Constants.getProperties().getProperty(
				"repoRoot"))) {
			logger.info("Repository path is changed, reloading repositories.");
			control.reloadSVN();
			control.getApplication().getRepositories()
					.updateRepositories(control.getRepositories());

			control.getApplication().getDirectories().removeAllItems();
			control.getApplication().getDirectories()
					.setLastChosenNode(new Node("", null));
			control.getApplication().getRepositories().setChosenRepo(null);
			// clean current path edit
			control.getApplication().getCurrentPath().setPathField("", "");
			control.getApplication().getCurrentPath().removeBools();
			// clean effective rigths
			control.getApplication().getEffectiveRightsTable().removeAllItems();
			// clean user rights table
			control.getApplication().getUsersTable().removeAllItems();
			// clean group rights table
			control.getApplication().getGroupsTable().removeAllItems();

			// disable all buttons
			control.getApplication().getButtons().disableAll();
			// settings will be enabled if logged user is
			// admin
			if (control.getUser().isAdmin()) {
				control.getApplication().getButtons().getSettings().setEnabled(true);
			}

			// uncheck disable inheritance checkbox
			control.getApplication().getInheritance().setValue(false);

		}

		if (!aclLast.equals((String) Constants.getProperties().getProperty(
				"aclLocation"))) {
			logger.info("Access List path is changed, reloading access list tree");
			try {
				control.reloadACL();
				control.reloadSVN();

			} catch (Exception e) {
				logger.error("Error during reloading Access List");
				logger.error(e.getMessage());
			}
			if (control.getApplication().getRepositories().getChosenRepo().trim()
					.length() != 0) {

				String path = ((FavoriteBean) control.getApplication().getCurrentPath()
						.getValue()).getFullpath();

				control.getApplication().getDirectories().removeAllItems();
				control.getApplication().getDirectories().expandPath(path);
				control.removeAllChanges();

				control.updateReposAndTree(control.getRepositories(), path, control
						.getApplication().getRepositories());
			}
		}

	}

	/**
	 * Initializes the components.
	 */
	public void init() {
		logger.info("Modal window for Settings initialization.");

		subwindow.setWidth(50, UNITS_PERCENTAGE);
		subwindow.setHeight(820, UNITS_PIXELS);

		try {

			ls = LDAPSettings.loadSettings();

			serverContainer = new BeanItemContainer<Server>(Server.class);

			for (Server server : ls.getServer()) {
				serverContainer.addItem(server);
			}

			activDirSelect.setContainerDataSource(serverContainer);
			activDirSelect.setItemCaptionMode(Select.ITEM_CAPTION_MODE_PROPERTY);
			activDirSelect.setItemCaptionPropertyId("name");

			if (ls.getServer() != null && ls.getServer().size() > 0) {
				activDirSelect.setValue(ls.getServer().get(0));
			}

			int selectedIndex = ls.getServer().indexOf(activDirSelect.getValue());

			reloadServerInfo(selectedIndex);

		} catch (Exception e) {
			e.printStackTrace();
		}

		repoRoot.setValue(Constants.getProperties().get("repoRoot"));
		aclLocation.setValue(Constants.getProperties().get("aclLocation"));
		mailField.setValue(Constants.getProperties().get("mail_folder"));
		// domainName.setValue(Constants.getProperties().get("domainName"));
		// serverSelect.setValue(Constants.getProperties().get("serverName"));
		// port.setValue(Constants.getProperties().get("port"));
		// userNamespace.setValue(Constants.getProperties().get("userNamespace"));
		// userSearchFilter.setValue(Constants.getProperties().get("userSearchFilter"));
		// userNameAttribute.setValue(Constants.getProperties().get("userNameAttribute"));
		// groupNamespace.setValue(Constants.getProperties().get("groupNamespace"));
		// groupSearchFilter.setValue(Constants.getProperties().get("groupSearchFilter"));
		backup.setValue(Constants.getProperties().get("backupFolder"));
		labelReplace.setValue(Constants.getProperties().get("labelReplace"));
		checkInterval.setValue(Constants.getProperties().get("checkInterval"));
		timeout.setValue(Constants.getProperties().get("timeout"));
		tcs.setDefaultValues(Arrays.asList(((String) (Constants.getProperties()
				.get("tcs"))).split(";")));

		inheritance_free.setDefaultValues(Arrays.asList(((String) (Constants
				.getProperties().get("inheritance_free"))).split(";")));

		cookieField.setValue(Constants.getProperties().get("cookieExp"));
		mailNotif.setValue(Boolean.parseBoolean(Constants.getProperties()
				.get("mailNotification").toString()));
	}

	/**
	 * Reloads the information for the server with the given index.
	 *
	 * @param selectedIndex the index of the server for which is the info reloaded
	 *
	 */
	private void reloadServerInfo(int selectedIndex) {
		domainName.setValue(ls.getServer().get(selectedIndex).getDomainName());
		port.setValue(ls.getServer().get(selectedIndex).getPort());
		userNamespace.setValue(ls.getServer().get(selectedIndex)
				.getBaseUserNamespace());
		userSearchFilter.setValue(ls.getServer().get(selectedIndex)
				.getUserSearchFilter());
		userNameAttribute.setValue(ls.getServer().get(selectedIndex)
				.getUserNameAttribute());
		groupNamespace.setValue(ls.getServer().get(selectedIndex)
				.getBaseGroupsNamespace());
		groupSearchFilter.setValue(ls.getServer().get(selectedIndex)
				.getGroupSearchFilter());
		serverSelect.setValue(ls.getServer().get(selectedIndex).getDomainServer());
		loginName.setValue(ls.getServer().get(selectedIndex).getLogin());
		password.setValue(ls.getServer().get(selectedIndex).getPassword());
		mailSuffix.setValue(ls.getServer().get(selectedIndex).getMailSuffix());
	}

	/**
	 * Reloads the list of the LDAP servers.
	 */
	protected void reloadServers() {

		try {
			ls = LDAPSettings.loadSettings();
			if (ls != null && ls.getServer().size() > 0) {
				serverContainer.removeAllItems();

				for (Server server : ls.getServer()) {
					serverContainer.addItem(server);
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/** The listener for the change of the selected LDAP server. */
	ValueChangeListener listenerLDAPSelect = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {

			int selectedIndex = ls.getServer().indexOf(activDirSelect.getValue());

			reloadServerInfo(selectedIndex);

			domainName.requestRepaint();
			port.requestRepaint();
			userNamespace.requestRepaint();
			userSearchFilter.requestRepaint();
			userNameAttribute.requestRepaint();
			groupNamespace.requestRepaint();
			groupSearchFilter.requestRepaint();
			serverSelect.requestRepaint();
			loginName.requestRepaint();
			password.requestRepaint();
			mailSuffix.requestRepaint();
		}
	};

	/** The listener for the change in the server list. */
	ItemSetChangeListener listenerServerSelectFocus = new ItemSetChangeListener() {

		private static final long serialVersionUID = 1L;

		public void containerItemSetChange(ItemSetChangeEvent event) {
			serverSelect.setValue("");
			serverSelect.validate();
			serverSelect.commit();
			serverSelect.requestRepaint();
			serverSelect.requestRepaintRequests();
		}
	};

	/** The listener for the change of the domain name. */
	ValueChangeListener listenerDomainName = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			List<String> list = null;
			try {
				list = Authentization.getNS(domainName.getValue().toString());
			} catch (TextParseException e) {
				e.printStackTrace();
			}
			if (list == null || list.size() == 0) {
				((Server) activDirSelect.getValue()).setDomainServer("");
				serverSelect.removeAllItems();
				serverSelect.setValue("");
			} else {
				for (String ns : list) {
					serverSelect.addItem(ns);
				}
			}
			// Uloží si do serveru v bean containeru hodnotu nastavenou do pole domain
			// name.
			((Server) activDirSelect.getValue()).setDomainName(domainName.getValue()
					.toString());
			// pokud je tam zadaná doména, která už byla v xml nastavená, nastaví se
			// tam původní hodnota serveru
			serverSelect.setValue(((Server) activDirSelect.getValue())
					.getDomainServer());

		}
	};

	/** The listener for the change of the domain server. */
	ValueChangeListener listenerDomainServer = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			if (serverSelect.getValue() != null) {
				((Server) activDirSelect.getValue()).setDomainServer(serverSelect
						.getValue().toString());
			}

		}
	};

	/** The listener for the change of the port. */
	ValueChangeListener listenerPort = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue()).setPort(port.getValue().toString());
		}
	};

	/** The listener for the change of the user name attribute. */
	ValueChangeListener listenerUserNameAttribute = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue())
					.setUserNameAttribute(userNameAttribute.getValue().toString());
		}
	};

	/** The listener for the change of the user namespace. */
	ValueChangeListener listenerUserNamespace = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue()).setBaseUserNamespace(userNamespace
					.getValue().toString());
		}
	};

	/** The listener for the change of the group namespace. */
	ValueChangeListener listenerGroupNamespace = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue())
					.setBaseGroupsNamespace(groupNamespace.getValue().toString());
		}
	};

	/** The listener for the change of the user search filter. */
	ValueChangeListener listenerUserSearchFilter = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue()).setUserSearchFilter(userSearchFilter
					.getValue().toString());
		}
	};

	/** The listener for the change of the group search filter. */
	ValueChangeListener listenerGroupSearchFilter = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue())
					.setGroupSearchFilter(groupSearchFilter.getValue().toString());
		}
	};

	/** The listener for the change of the login. */
	ValueChangeListener listenerLogin = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue()).setLogin(loginName.getValue()
					.toString());
		}
	};

	/** The listener for the change of the password. */
	ValueChangeListener listenerPassword = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue()).setPassword(password.getValue()
					.toString());
		}
	};

	/** The listener for the change of the mail suffix. */
	ValueChangeListener listenerMailSuffix = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			((Server) activDirSelect.getValue()).setMailSuffix(mailSuffix.getValue().toString());
		}
	};

	/** The listener for the change of the domain. */
	ValueChangeListener listenerDomain = new ValueChangeListener() {
		private static final long serialVersionUID = 1L;

		public void valueChange(ValueChangeEvent event) {
			List<String> list = null;
			try {
				list = Authentization.getNS(domainName.getValue().toString());
			} catch (TextParseException e) {
				e.printStackTrace();
			}
			if (list == null || list.size() == 0) {
				serverSelect.removeAllItems();
				serverSelect.setValue("");
			} else {
				serverSelect.removeAllItems();
				for (String ns : list) {
					serverSelect.addItem(ns);
				}
			}
		}
	};

	/** The listener for the change of the focus on the domain textfield. */
	FocusListener listenerDomainFocus = new FocusListener() {

		private static final long serialVersionUID = 1L;

		public void focus(FocusEvent event) {
			List<String> list = null;
			try {
				list = Authentization.getNS(domainName.getValue().toString());
			} catch (TextParseException e) {
				e.printStackTrace();
			}
			if (list == null || list.size() == 0) {
				serverSelect.removeAllItems();
				serverSelect.setValue("");
			} else {
				serverSelect.removeAllItems();
				for (String ns : list) {
					serverSelect.addItem(ns);
				}
			}
		}
	};
}