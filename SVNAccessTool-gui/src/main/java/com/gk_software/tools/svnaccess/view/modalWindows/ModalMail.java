package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.mail.MailProperties;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Represent the window for e-amil sending.
 */
public class ModalMail extends VerticalLayout {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ModalMail.class);

	/** The subwindow for e-mail sending. */
	Window subwindow;

	/** The ok button text. */
	public static final I18NResource OK_BUTTON_TEXT = new I18NResource(
			ModalMail.class, "OK_BUTTON_TEXT", "Save");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalMail.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/** The peersonal settings button text. */
	public static final I18NResource PERSONAL_BUTTON_TEXT = new I18NResource(
			ModalMail.class, "PERSONAL_BUTTON_TEXT", "Personal Settings");

	/** The send e-mail window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalMail.class, "WINDOW_CAPTION_TEXT", "Mail Settings");

	/** The header text of the SVN table. */
	public static final I18NResource TABLE_SVN_HEADER_TEXT = new I18NResource(
			ModalMail.class, "TABLE_SVN_HEADER_TEXT", "SVN");

	/** The header text of the m user column. */
	public static final I18NResource TABLE_M_USER_UP_NODE_HEADER_TEXT = new I18NResource(
			ModalMail.class, "TABLE_M_USER_UP_NODE_HEADER_TEXT",
			"M user from parent node");

	/** The header text of the originator column. */
	public static final I18NResource TABLE_ORIGINATOR_HEADER_TEXT = new I18NResource(
			ModalMail.class, "TABLE_ORIGINATOR_HEADER_TEXT", "Originator");

	/** The header text of the attended column. */
	public static final I18NResource TABLE_ATTENDED_HEADER_TEXT = new I18NResource(
			ModalMail.class, "TABLE_ATTENDED_HEADER_TEXT", "Attended");

	/** The header text of the admins column. */
	public static final I18NResource TABLE_ADMINS_HEADER_TEXT = new I18NResource(
			ModalMail.class, "TABLE_ADMINS_HEADER_TEXT", "Admins");

	/** The header text of the cleaner. */
	public static final I18NResource CLEANER_HEADER = new I18NResource(
			ModalMail.class, "CLEANER_HEADER", "Modal confirmation");

	/** The mail check label text. */
	public static final I18NResource LABEL_MAIL_CHECKER = new I18NResource(
			ModalMail.class, "LABEL_MAIL_CHECKER", "Mail check [min]");

	/** The control class. */
	private static IControl control;

	/** The SVN table. */
	private Table table;

	/** The mail checker. */
	private TextField mailChecker;

	/**
	 * Returns the subwindow for e-mail sending.
	 *
	 * @param modificationRepos the set of the repos the user can modify
	 *
	 * @return the subwindow for e-mail sending
	 *
	 */
	public Window getSubwindow(Set<String> modificationRepos) {
		try {
			init(modificationRepos);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return subwindow;
	}

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public ModalMail(final IControl control) {
		ModalMail.control = control;
		subwindow = new Window(I18NSupport.getMessage(WINDOW_CAPTION_TEXT));
		subwindow.setModal(true);

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeFull();

		table = new Table();
		table.setSizeFull();
		table.setSelectable(true);
		table.setMultiSelect(true);
		table.setImmediate(true);

		HorizontalLayout hll2 = new HorizontalLayout();
		hll2.setWidth(70, UNITS_PIXELS);

		HorizontalLayout hl = new HorizontalLayout();

		Button settings = new Button (I18NSupport.getMessage(PERSONAL_BUTTON_TEXT), new Button.ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ModalMailSettings mms = new ModalMailSettings(control);
				subwindow.getParent().addWindow(mms.getSubwindow());
			}
		});


		Button cancel = new Button(I18NSupport.getMessage(CANCEL_BUTTON_TEXT),
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						logger.info("Button cancel was clicked on. ");
						(subwindow.getParent()).removeWindow(subwindow);
					}
				});
		cancel.setClickShortcut(KeyCode.ESCAPE);

		Button ok = new Button(I18NSupport.getMessage(OK_BUTTON_TEXT),
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						logger.info("Button ok was clicked on");
						MailProperties.getProperties().put("mail_check",
								mailChecker.getValue().toString());
						MailProperties
								.saveConstants(createListOfSelectedItems());
						(subwindow.getParent()).removeWindow(subwindow);
					}
				});
		ok.setClickShortcut(KeyCode.ENTER);
		ok.focus();

		HorizontalLayout hlm = new HorizontalLayout();
		hlm.setHeight(30, UNITS_PIXELS);
		hlm.setWidth(100, UNITS_PERCENTAGE);

		Label label = new Label(I18NSupport.getMessage(LABEL_MAIL_CHECKER));
		mailChecker = new TextField();
		mailChecker.setColumns(4);
		mailChecker.setValue(MailProperties.getProperties().getProperty(
				"mail_check"));
		label.setWidth(150, UNITS_PIXELS);
		hlm.addComponent(label);
		hlm.addComponent(mailChecker);
		hlm.setExpandRatio(label, 0);
		hlm.setExpandRatio(mailChecker, 1);

		layout.addComponent(hlm);
		layout.addComponent(table);

		hl.setHeight(30, UNITS_PIXELS);

		ok.setStyleName("link");
		ok.addStyleName("Button2");
		hl.addComponent(ok);

		cancel.setStyleName("link");
		cancel.addStyleName("Button2");
		hl.addComponent(cancel);

		settings.setStyleName("link");
		settings.addStyleName("Button2");
		hl.addComponent(settings);

		hl.setComponentAlignment(ok, Alignment.MIDDLE_CENTER);
		hl.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
		hl.setComponentAlignment(settings, Alignment.MIDDLE_CENTER);


		layout.addComponent(hl);
		layout.setComponentAlignment(hl, Alignment.BOTTOM_CENTER);
		layout.setExpandRatio(hlm, 0);
		layout.setExpandRatio(table, 1);
		layout.setExpandRatio(hl, 0);

		if(!control.getUser().isAdmin()){
			mailChecker.setEnabled(false);
		}
		// layout.setExpandRatio(hll, 0);
	}

	/**
	 * Create the map of the currently selected items.
	 *
	 * @return the map of the currently selected items
	 *
	 */
	private Map<String, String> createListOfSelectedItems() {
		Container container = table.getContainerDataSource();
		Map<String, String> list = new HashMap<String, String>();
		for (Object contItem : container.getItemIds()) {
			String value = "";
			Item item = container.getItem(contItem);
			CheckBox ch = (CheckBox) item.getItemProperty(
					I18NSupport.getMessage(TABLE_M_USER_UP_NODE_HEADER_TEXT)).getValue();
			if (ch.getValue().equals(true))
				value += "1";
			else
				value += "0";

			ch = (CheckBox) item.getItemProperty(
					I18NSupport.getMessage(TABLE_ORIGINATOR_HEADER_TEXT)).getValue();
			if (ch.getValue().equals(true))
				value += "1";
			else
				value += "0";

			ch = (CheckBox) item.getItemProperty(
					I18NSupport.getMessage(TABLE_ATTENDED_HEADER_TEXT)).getValue();
			if (ch.getValue().equals(true))
				value += "1";
			else
				value += "0";

			ch = (CheckBox) item.getItemProperty(
					I18NSupport.getMessage(TABLE_ADMINS_HEADER_TEXT)).getValue();
			if (ch.getValue().equals(true))
				value += "1";
			else
				value += "0";

				list.put(
						item.getItemProperty(I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT))
								.getValue().toString(), value);
		}
		return list;
	}

	/**
	 * Fills the container with data.
	 *
	 * @param modificationRepos the set of the repos the user can modify
	 *
	 * @return the filled container
	 *
	 */
	public IndexedContainer fillData(Set<String> modificationRepos) {
		logger.info("Filling data in to modal Clean.");
		IndexedContainer ic = new IndexedContainer();
		ic.addContainerProperty(I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT),
				String.class, null);
		ic.addContainerProperty(
				I18NSupport.getMessage(TABLE_M_USER_UP_NODE_HEADER_TEXT),
				CheckBox.class, false);
		ic.addContainerProperty(
				I18NSupport.getMessage(TABLE_ORIGINATOR_HEADER_TEXT),
				CheckBox.class, false);
		ic.addContainerProperty(
				I18NSupport.getMessage(TABLE_ATTENDED_HEADER_TEXT),
				CheckBox.class, false);
		ic.addContainerProperty(
				I18NSupport.getMessage(TABLE_ADMINS_HEADER_TEXT),
				CheckBox.class, false);

		for (Node svn : control.getRepositories()) {
			boolean canedit = false;
			if (modificationRepos.contains(svn.getSvn()))
				canedit = true;

			String value = MailProperties.getProperties().getProperty(
					svn.getSvn());
			if (value == null)
				value = "0000";
			Item item = ic.addItem(svn.getSvn());
			item.getItemProperty(I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT))
					.setValue(svn.getSvn());
			CheckBox c = new CheckBox();
			c.setImmediate(true);
			c.setEnabled(canedit);
			c.setWidth(15, UNITS_PIXELS);
			if ((value.charAt(0)) == '0')
				c.setValue(false);
			else
				c.setValue(true);
			item.getItemProperty(
					I18NSupport.getMessage(TABLE_M_USER_UP_NODE_HEADER_TEXT))
					.setValue(c);

			c = new CheckBox();
			c.setEnabled(canedit);
			c.setImmediate(true);
			c.setWidth(15, UNITS_PIXELS);
			if (value.charAt(1) == '0')
				c.setValue(false);
			else
				c.setValue(true);
			item.getItemProperty(
					I18NSupport.getMessage(TABLE_ORIGINATOR_HEADER_TEXT))
					.setValue(c);

			c = new CheckBox();
			c.setEnabled(canedit);
			c.setImmediate(true);
			c.setWidth(15, UNITS_PIXELS);
			if (value.charAt(2) == '0')
				c.setValue(false);
			else
				c.setValue(true);
			item.getItemProperty(
					I18NSupport.getMessage(TABLE_ATTENDED_HEADER_TEXT))
					.setValue(c);

			c = new CheckBox();
			c.setEnabled(canedit);
			c.setImmediate(true);
			c.setWidth(15, UNITS_PIXELS);
			if (value.charAt(3) == '0')
				c.setValue(false);
			else
				c.setValue(true);
			item.getItemProperty(
					I18NSupport.getMessage(TABLE_ADMINS_HEADER_TEXT)).setValue(
					c);
		}
		return ic;
	}

	/**
	 * Initializes the components.
	 *
	 * @param modificationRepos the set of the modified repositories
	 *
	 * @throws NullPointerException initialization failed
	 *
	 */
	public void init(Set<String> modificationRepos) throws NullPointerException {
		logger.info("Modal window for CLEAN initialization");
		subwindow.setWidth(70, UNITS_PERCENTAGE);
		subwindow.setHeight(500, UNITS_PIXELS);
		table.setContainerDataSource(fillData(modificationRepos));
		table.setColumnHeaders(new String[] {
				I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_M_USER_UP_NODE_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_ORIGINATOR_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_ATTENDED_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_ADMINS_HEADER_TEXT) });

		table.setColumnAlignment(
				I18NSupport.getMessage(TABLE_M_USER_UP_NODE_HEADER_TEXT),
				Table.ALIGN_CENTER);
		table.setColumnAlignment(
				I18NSupport.getMessage(TABLE_ORIGINATOR_HEADER_TEXT),
				Table.ALIGN_CENTER);
		table.setColumnAlignment(
				I18NSupport.getMessage(TABLE_ATTENDED_HEADER_TEXT),
				Table.ALIGN_CENTER);
		table.setColumnAlignment(
				I18NSupport.getMessage(TABLE_ADMINS_HEADER_TEXT),
				Table.ALIGN_CENTER);

		table.setColumnWidth(
				I18NSupport.getMessage(TABLE_M_USER_UP_NODE_HEADER_TEXT), 120);
		table.setColumnWidth(
				I18NSupport.getMessage(TABLE_ORIGINATOR_HEADER_TEXT), 70);
		table.setColumnWidth(
				I18NSupport.getMessage(TABLE_ATTENDED_HEADER_TEXT), 70);
		table.setColumnWidth(I18NSupport.getMessage(TABLE_ADMINS_HEADER_TEXT),
				70);

	}
}