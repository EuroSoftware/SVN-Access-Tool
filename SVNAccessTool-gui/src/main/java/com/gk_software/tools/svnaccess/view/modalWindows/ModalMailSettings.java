package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.ViewInformation;
import com.gk_software.tools.svnaccess.data.ldap.LdapProvider;
import com.gk_software.tools.svnaccess.data.mail.MailSettings;
import com.gk_software.tools.svnaccess.data.mail.Required;
import com.gk_software.tools.svnaccess.data.mail.User;
import com.gk_software.tools.svnaccess.utils.Constants;
import com.gk_software.tools.svnaccess.view.components.TwinColumnSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Represent the mail settings window.
 */
public class ModalMailSettings extends VerticalLayout {

	/** The mail settings window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalWindowSettings.class, "WINDOW_CAPTION_TEXT",
			"Personal mail settings");

	/** The save button text. */
	public static final I18NResource SAVE_BTN_TEXT = new I18NResource(
			ModalWindowSettings.class, "SAVE_BTN_TEXT", "Save");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BTN_TEXT = new I18NResource(
			ModalWindowSettings.class, "CANCEL_BTN_TEXT", "Cancel");

	/** The selection text for all groups. */
	public static final I18NResource SELECTION_GOUPS_ALL = new I18NResource(
			ModalWindowSettings.class, "SELECTION_GOUPS_ALL", "All groups");

	/** The selection text for selected groups. */
	public static final I18NResource SELECTION_GOUPS_SELECTED = new I18NResource(
			ModalWindowSettings.class, "SELECTION_GOUPS_SELECTED", "Selected groups");

	/** The selection text for all repositories. */
	public static final I18NResource SELECTION_REPOS_ALL = new I18NResource(
			ModalWindowSettings.class, "SELECTION_REPOS_ALL", "All repositories");

	/** The selection text for selected repositories. */
	public static final I18NResource SELECTION_REPOS_SELECTED = new I18NResource(
			ModalWindowSettings.class, "SELECTION_REPOS_SELECTED",
			"Selected repositories");

	/** The subwindow for mail settings. */
	private Window subwindow;

	/** The control class. */
	private IControl control;

	/** The name of the user whose mail settings are changed. */
	private String userName;

	/** The name of the file of the user. */
	private String userNameFile;

	/** The path to the file of the user. */
	private String path;

	/** The two column select for groups. */
	private TwinColumnSelect twiceColumnSelectGroups;

	/** The two column select for repositories. */
	private TwinColumnSelect twiceColumnSelectRepos;

	/**
	 * Returns the subwindow for mail settings.
	 *
	 * @return the subwindow for mail settings
	 *
	 */
	public Window getSubwindow() {
		return subwindow;
	}

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public ModalMailSettings(final IControl control) {

		this.control = control;
		this.userName = control.getUser().getUserName();
		// when new domain is addded the user files should be somehow checked so the
		// users won't overwrite each others files
		// this.userNameFile = userName.split("@")[0]+".xml";
		this.userNameFile = userName + ".xml";
		this.path = Constants.getProperties().getProperty("mail_folder") + "/users";


		subwindow = new Window(I18NSupport.getMessage(WINDOW_CAPTION_TEXT));
		subwindow.setWidth(60, UNITS_PERCENTAGE);
		subwindow.setHeight(450, UNITS_PIXELS);
		subwindow.setModal(true);

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();

		List<String> groupsWhereUserIs = LdapProvider.getInstance()
				.getGroupsForUser(userName);

		twiceColumnSelectGroups = new TwinColumnSelect(groupsWhereUserIs,
				I18NSupport.getMessage(SELECTION_GOUPS_ALL),
				I18NSupport.getMessage(SELECTION_GOUPS_SELECTED), false);
		twiceColumnSelectRepos = new TwinColumnSelect(getRepositories(),
				I18NSupport.getMessage(SELECTION_REPOS_ALL),
				I18NSupport.getMessage(SELECTION_REPOS_SELECTED), false);

		VerticalLayout vlSelections = new VerticalLayout();

		getActualSettings();

		vlSelections.addComponent(twiceColumnSelectGroups);
		vlSelections.addComponent(twiceColumnSelectRepos);
		vlSelections.setSizeFull();

		Button saveBtn = new Button(I18NSupport.getMessage(SAVE_BTN_TEXT),
				new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						String[] ignoredRepos = twiceColumnSelectRepos.getSelected().split(
								";");
						String[] ignoredGroups = twiceColumnSelectGroups.getSelected()
								.split(";");

						generateSettingsXMLFile(ignoredRepos, ignoredGroups);

						subwindow.getParent().removeWindow(subwindow);
					}

				});

		Button cancelBtn = new Button(I18NSupport.getMessage(CANCEL_BTN_TEXT), new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				subwindow.getParent().removeWindow(subwindow);
			}
		});

		HorizontalLayout hl = new HorizontalLayout();

		saveBtn.setStyleName("link");
		saveBtn.addStyleName("Button2");

		cancelBtn.setStyleName("link");
		cancelBtn.addStyleName("Button2");

		hl.addComponent(saveBtn);
		hl.addComponent(cancelBtn);

		hl.setComponentAlignment(saveBtn, Alignment.MIDDLE_CENTER);
		hl.setComponentAlignment(cancelBtn, Alignment.MIDDLE_CENTER);
		hl.setHeight(30, HorizontalLayout.UNITS_PIXELS);

		layout.addComponent(vlSelections);
		layout.addComponent(hl);

		layout.setComponentAlignment(hl, Alignment.BOTTOM_CENTER);
		layout.setExpandRatio(vlSelections, 1);
		layout.setExpandRatio(hl, 0);

	}

	/**
	 * Initializes the select columns with the actual mail settings.
	 */
	private void getActualSettings() {
		File pathToUserSettingsDir = new File(path);
		if (pathToUserSettingsDir.listFiles() != null
				&& Arrays.asList(pathToUserSettingsDir.listFiles()).contains(
						new File(path, userNameFile))) {
			JAXBContext jc;
			try {
				jc = JAXBContext.newInstance(MailSettings.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();

				File userSettingsFile = new File(path, userNameFile);
				MailSettings ms = (MailSettings) unmarshaller
						.unmarshal(userSettingsFile);

				List<String> ignoratedGroups = ms.getUser().getRequired().getGroup();
				List<String> ignoratedRepos = ms.getUser().getRequired().getRepo();

				twiceColumnSelectGroups.setDefaultValues(ignoratedGroups);
				twiceColumnSelectRepos.setDefaultValues(ignoratedRepos);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Generates the new mail settings file according to the given data
	 * from the twin selects.
	 *
	 * @param ignoredRepos the array of the ignored repositories
	 * @param ignoredGroups the array of the ignored groups
	 *
	 */
	private void generateSettingsXMLFile(String[] ignoredRepos,
			String[] ignoredGroups) {
		MailSettings root = new MailSettings();

		User user = new User();
		// whole nick with domain should be used when domain is used
		// user.setName(control.getUser().getUserName().split("@")[0]);
		user.setName(control.getUser().getUserName());

		Required notr = new Required();

		for (String repos : ignoredRepos) {
			notr.getRepo().add(repos);
		}

		for (String group : ignoredGroups) {
			notr.getGroup().add(group);
		}

		user.setRequired(notr);
		root.setUser(user);

		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(MailSettings.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			File f = new File(path, userNameFile);
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(f);

			marshaller.marshal(root, fos);
			fos.flush();
			fos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the list of currently available repositories.
	 *
	 * @return the list of currently available repositories
	 *
	 */
	private List<String> getRepositories() {
		List<String> repositories = new ArrayList<String>();
		try {
			for (Node node : ViewInformation.getInstance().getRepositories()) {
				repositories.add(node.getSvn());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return repositories;
	}

	/**
	 * Checks if the given user has the given group set as ignored.
	 *
	 * @param user the user whose settings we check
	 * @param group the group for which we check the settings
	 *
	 * @return true if ignored otherwise false.
	 *
	 */
	public static boolean isIgnoredGroup(String user, String group) {
		String path = Constants.getProperties().getProperty("mail_folder")
				+ "/users";
		String userNameFile = user + ".xml";
		File pathToUserSettingsDir = new File(path);
		if (pathToUserSettingsDir.listFiles() != null
				&& Arrays.asList(pathToUserSettingsDir.listFiles()).contains(
						new File(path, userNameFile))) {
			JAXBContext jc;
			try {
				jc = JAXBContext.newInstance(MailSettings.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();

				File userSettingsFile = new File(path, userNameFile);
				MailSettings ms = (MailSettings) unmarshaller
						.unmarshal(userSettingsFile);

				if (ms.getUser().getRequired().getGroup().contains(group)) {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		}
		return true;
	}

	/**
	 * Checks if the given user has the given repository set as ignored.
	 *
	 * @param user the user whose settings we check
	 * @param repo the repository for which we check the settings
	 *
	 * @return true if ignored otherwise false.
	 *
	 */
	public static boolean isIgnoredRepo(String user, String repo) {
		String path = Constants.getProperties().getProperty("mail_folder")
				+ "/users";
		String userNameFile = user + ".xml";
		File pathToUserSettingsDir = new File(path);

		if (pathToUserSettingsDir.listFiles() != null
				&& Arrays.asList(pathToUserSettingsDir.listFiles()).contains(
						new File(path, userNameFile))) {
			JAXBContext jc;
			try {
				jc = JAXBContext.newInstance(MailSettings.class);
				Unmarshaller unmarshaller = jc.createUnmarshaller();

				File userSettingsFile = new File(path, userNameFile);
				MailSettings ms = (MailSettings) unmarshaller
						.unmarshal(userSettingsFile);

				if (ms.getUser().getRequired().getRepo().contains(repo)) {
					return false;
				}

			} catch (Exception e) {
				e.printStackTrace();
				return true;
			}
		}
		return true;
	}
}
