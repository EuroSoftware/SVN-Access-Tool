package com.gk_software.tools.svnaccess.view.modalWindows;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.xbill.DNS.TextParseException;

import gk.ee_common.i18n.I18NResource;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.bussiness.impl.Authentization;
import com.gk_software.tools.svnaccess.data.ldap.LDAPSettings;
import com.gk_software.tools.svnaccess.data.ldap.Server;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;

/**
 * Represent the window for new server settings.
 */
public class ModalNewServerSettings extends HorizontalLayout {

	/** The serial id. */
	private static final long serialVersionUID = 1L;

	/** The new server settings window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalWindowSettings.class, "WINDOW_CAPTION_TEXT",
			"New LDAP server settings");

	/** The save button text. */
	public static final I18NResource SAVE_BUTTON_TEXT = new I18NResource(
			ModalWindowSettings.class, "SAVE_BUTTON_TEXT", "Save");

	/** The domain name label text. */
	public static final I18NResource LABEL_DOMAIN_NAME = new I18NResource(
			ModalWindowSettings.class, "LABEL_DOMAIN_NAME", "Domain name");

	/** The port label text. */
	public static final I18NResource LABEL_PORT = new I18NResource(
			ModalWindowSettings.class, "LABEL_PORT", "Port");

	/** The name label text. */
	public static final I18NResource LABEL_NAME = new I18NResource(
			ModalWindowSettings.class, "LABEL_NAME", "Name");

	/** The user namespace label text. */
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

	/** The base groups namespace label text. */
	public static final I18NResource LABEL_BASE_GROUPS_NAMESPACE = new I18NResource(
			ModalWindowSettings.class, "LABEL_BASE_GROUPS_NAMESPACE",
			"Base Groups Namespace");

	/** The group search filter label text. */
	public static final I18NResource LABEL_GROUP_SEARCH_FILTER = new I18NResource(
			ModalWindowSettings.class, "LABEL_GROUP_SEARCH_FILTER",
			"Group Search filter");

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

	/** The name textfield. */
	private TextField name;

	/** The login textfield. */
	private TextField loginName;

	/** The password textfield. */
	private PasswordField password;

	/** The server selection combobox. */
	private ComboBox serverSelect;

	/** The subwindow for new server settings. */
	private Window subwindow;

	/** The control class. */
	private IControl control;

	/** The parent window. */
	private ModalWindowSettings parent;

	/** The listener for domain name change. */
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
				serverSelect.removeAllItems();
				serverSelect.setValue("");
			} else {
				for (String ns : list) {
					serverSelect.addItem(ns);
				}
			}
		}
	};

	/**
	 * Returns the subwindow for new server settings.
	 *
	 * @return the subwindow for new server settings
	 *
	 */
	public Window getSubwindow() {
		return subwindow;
	}

	/**
	 * Creates a new instance, sets the variables according to the given values and
	 * initializes all the other variables.
	 *
	 * @param control the control class
	 * @param parent the parent window
	 *
	 */
	public ModalNewServerSettings(IControl control, ModalWindowSettings parent) {
		this.parent = parent;
		this.control = control;

		subwindow = new Window(I18NSupport.getMessage(WINDOW_CAPTION_TEXT));
		subwindow.setWidth(60, UNITS_PERCENTAGE);
		subwindow.setHeight(450, UNITS_PIXELS);
		subwindow.setModal(true);

		subwindow.getContent().addComponent(createLDAPsettingsPanel());

	}

	/**
	 * Creates and fills the LDAP settings panel and returns it.
	 *
	 * @return the created and filled LDAP settings panel
	 *
	 */
	private Panel createLDAPsettingsPanel() {
		Label labelDomainName = new Label(I18NSupport.getMessage(LABEL_DOMAIN_NAME));
		labelDomainName.setWidth(150, UNITS_PIXELS);

		domainName = new TextField();
		domainName.setWidth(100, UNITS_PERCENTAGE);
		domainName.setImmediate(true);
		domainName.addListener(listenerDomainName);

		serverSelect = new ComboBox();
		serverSelect.setWidth(100, UNITS_PERCENTAGE);
		serverSelect.setImmediate(true);
		serverSelect.setNullSelectionAllowed(false);

		/*server = new TextField();
		server.setWidth(100, UNITS_PERCENTAGE);
		server.setImmediate(true);*/

		HorizontalLayout lineServer = new HorizontalLayout();
		lineServer.setHeight(30, UNITS_PIXELS);
		lineServer.setWidth(100, UNITS_PERCENTAGE);

		lineServer.addComponent(labelDomainName);
		lineServer.addComponent(domainName);
		lineServer.addComponent(serverSelect);
		lineServer.setExpandRatio(labelDomainName, 0);
		lineServer.setExpandRatio(domainName, 1);
		lineServer.setExpandRatio(serverSelect, 1);

		// =====================================================

		port = new TextField();
		port.setWidth(100, UNITS_PERCENTAGE);
		port.setImmediate(true);

		Label labelPort = new Label(I18NSupport.getMessage(LABEL_PORT));
		labelPort.setWidth(150, UNITS_PIXELS);

		HorizontalLayout linePort = new HorizontalLayout();
		linePort.setHeight(30, UNITS_PIXELS);
		linePort.setWidth(100, UNITS_PERCENTAGE);

		linePort.addComponent(labelPort);
		linePort.addComponent(port);
		linePort.setExpandRatio(labelPort, 0);
		linePort.setExpandRatio(port, 1);

		// =====================================================

		loginName = new TextField();
		loginName.setWidth(100, UNITS_PERCENTAGE);
		loginName.setImmediate(true);

		Label labelLoginName = new Label("Login");
		labelLoginName.setWidth(150, UNITS_PIXELS);

		password = new PasswordField();
		password.setWidth(100, UNITS_PERCENTAGE);
		password.setImmediate(true);

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

		// =====================================================

		name = new TextField();
		name.setWidth(100, UNITS_PERCENTAGE);
		name.setImmediate(true);

		Label labelName = new Label(I18NSupport.getMessage(LABEL_NAME));
		labelName.setWidth(150, UNITS_PIXELS);

		HorizontalLayout lineName = new HorizontalLayout();
		lineName.setHeight(30, UNITS_PIXELS);
		lineName.setWidth(100, UNITS_PERCENTAGE);

		lineName.addComponent(labelName);
		lineName.addComponent(name);
		lineName.setExpandRatio(labelName, 0);
		lineName.setExpandRatio(name, 1);

		// =====================================================

		Label labelBaseUserNamespace = new Label(
				I18NSupport.getMessage(LABEL_BASE_USER_NAMESPACE));
		labelBaseUserNamespace.setWidth(150, UNITS_PIXELS);

		userNamespace = new TextField();
		userNamespace.setWidth(100, UNITS_PERCENTAGE);

		HorizontalLayout lineBaseUserNamespace = new HorizontalLayout();
		lineBaseUserNamespace.setHeight(30, UNITS_PIXELS);
		lineBaseUserNamespace.setWidth(100, UNITS_PERCENTAGE);

		lineBaseUserNamespace.addComponent(labelBaseUserNamespace);
		lineBaseUserNamespace.addComponent(userNamespace);
		lineBaseUserNamespace.setExpandRatio(labelBaseUserNamespace, 0);
		lineBaseUserNamespace.setExpandRatio(userNamespace, 1);

		// =====================================================

		Label labelUserSearchFilter = new Label(
				I18NSupport.getMessage(LABEL_USER_SEARCH_FILTER));
		labelUserSearchFilter.setWidth(150, UNITS_PIXELS);

		userSearchFilter = new TextField();
		userSearchFilter.setWidth(100, UNITS_PERCENTAGE);

		HorizontalLayout lineUserSearchFilter = new HorizontalLayout();
		lineUserSearchFilter.setHeight(30, UNITS_PIXELS);
		lineUserSearchFilter.setWidth(100, UNITS_PERCENTAGE);

		lineUserSearchFilter.addComponent(labelUserSearchFilter);
		lineUserSearchFilter.addComponent(userSearchFilter);
		lineUserSearchFilter.setExpandRatio(labelUserSearchFilter, 0);
		lineUserSearchFilter.setExpandRatio(userSearchFilter, 1);

		// =====================================================

		Label labelUserNameAttribute = new Label(
				I18NSupport.getMessage(LABEL_USER_NAME_ATTRIBUTE));
		labelUserNameAttribute.setWidth(150, UNITS_PIXELS);

		userNameAttribute = new TextField();
		userNameAttribute.setWidth(100, UNITS_PERCENTAGE);

		HorizontalLayout lineUserNameAttribute = new HorizontalLayout();
		lineUserNameAttribute.setHeight(30, UNITS_PIXELS);
		lineUserNameAttribute.setWidth(100, UNITS_PERCENTAGE);

		lineUserNameAttribute.addComponent(labelUserNameAttribute);
		lineUserNameAttribute.addComponent(userNameAttribute);
		lineUserNameAttribute.setExpandRatio(labelUserNameAttribute, 0);
		lineUserNameAttribute.setExpandRatio(userNameAttribute, 1);

		// =====================================================

		Label labelBaseGroupsNamespace = new Label(
				I18NSupport.getMessage(LABEL_BASE_GROUPS_NAMESPACE));
		labelBaseGroupsNamespace.setWidth(150, UNITS_PIXELS);

		groupNamespace = new TextField();
		groupNamespace.setWidth(100, UNITS_PERCENTAGE);

		HorizontalLayout lineBaseGroupsNamespace = new HorizontalLayout();
		lineBaseGroupsNamespace.setHeight(30, UNITS_PIXELS);
		lineBaseGroupsNamespace.setWidth(100, UNITS_PERCENTAGE);

		lineBaseGroupsNamespace.addComponent(labelBaseGroupsNamespace);
		lineBaseGroupsNamespace.addComponent(groupNamespace);
		lineBaseGroupsNamespace.setExpandRatio(labelBaseGroupsNamespace, 0);
		lineBaseGroupsNamespace.setExpandRatio(groupNamespace, 1);

		// =====================================================

		Label labelGroupSearchFilter = new Label(
				I18NSupport.getMessage(LABEL_GROUP_SEARCH_FILTER));
		labelGroupSearchFilter.setWidth(150, UNITS_PIXELS);

		groupSearchFilter = new TextField();
		groupSearchFilter.setWidth(100, UNITS_PERCENTAGE);

		HorizontalLayout lineGroupSearchFilter = new HorizontalLayout();
		lineGroupSearchFilter.setHeight(30, UNITS_PIXELS);
		lineGroupSearchFilter.setWidth(100, UNITS_PERCENTAGE);

		lineGroupSearchFilter.addComponent(labelGroupSearchFilter);
		lineGroupSearchFilter.addComponent(groupSearchFilter);
		lineGroupSearchFilter.setExpandRatio(labelGroupSearchFilter, 0);
		lineGroupSearchFilter.setExpandRatio(groupSearchFilter, 1);

		// =====================================================

		Button saveButton = new Button(I18NSupport.getMessage(SAVE_BUTTON_TEXT), new Button.ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {

				LDAPSettings ls = null;
				try {
					ls = LDAPSettings.loadSettings();
				} catch (JAXBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

					Server serverNew = new Server();
					serverNew.setBaseGroupsNamespace(groupNamespace.getValue().toString());
					serverNew.setBaseUserNamespace(userNamespace.getValue().toString());
					serverNew.setDomainName(domainName.getValue().toString());
					serverNew.setDomainServer(serverSelect.getValue().toString());
					serverNew.setGroupSearchFilter(groupSearchFilter.getValue().toString());
					serverNew.setName(name.getValue().toString());
					serverNew.setPort(port.getValue().toString());
					serverNew.setUserNameAttribute(userNameAttribute.getValue().toString());
					serverNew.setUserSearchFilter(userSearchFilter.getValue().toString());

					if (serverNew.isFilled()){
						try {
							ls.getServer().add(serverNew);
							LDAPSettings.saveLDAPSettings(ls);
						} catch (JAXBException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						subwindow.getParent().removeWindow(subwindow);
						parent.reloadServers();
					} else {
						control.getApplication().getWindow().showNotification(
								"Error message",
								"Some fields are filled incorrectly!",
								Notification.TYPE_ERROR_MESSAGE);
					}
			}
		});

		Button cancelButton = new Button("Cancel", new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				subwindow.getParent().removeWindow(getWindow());
			}
		});

		HorizontalLayout buttons = new HorizontalLayout();
		HorizontalLayout buttonsWrapper = new HorizontalLayout();
		buttons.addComponent(saveButton);
		buttons.addComponent(cancelButton);
		buttons.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
		buttons.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
		buttonsWrapper.addComponent(buttons);
		buttonsWrapper.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);

		// =====================================================

		Panel LDAPsettingsPanel = new Panel(
				"Settings of Access to Active Directory");

		LDAPsettingsPanel.addComponent(lineName);
		LDAPsettingsPanel.addComponent(lineServer);
		LDAPsettingsPanel.addComponent(linePort);
		LDAPsettingsPanel.addComponent(lineLoginInfo);
		LDAPsettingsPanel.addComponent(lineBaseUserNamespace);
		LDAPsettingsPanel.addComponent(lineUserSearchFilter);
		LDAPsettingsPanel.addComponent(lineUserNameAttribute);
		LDAPsettingsPanel.addComponent(lineBaseGroupsNamespace);
		LDAPsettingsPanel.addComponent(lineGroupSearchFilter);
		LDAPsettingsPanel.addComponent(buttonsWrapper);

		return LDAPsettingsPanel;
	}
}
