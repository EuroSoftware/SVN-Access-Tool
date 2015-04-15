package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Represent the window for setting the responsible person.
 */
public class ModalResponsiblePerson extends VerticalLayout {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger
			.getLogger(ModalResponsiblePerson.class);

	/** The subwindow for setting the responsible person. */
	Window subwindow;

	/** The ok button text. */
	public static final I18NResource OK_BUTTON_TEXT = new I18NResource(
			ModalResponsiblePerson.class, "OK_BUTTON_TEXT", "Ok");

	/** The title of the window for setting the responsible person. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalResponsiblePerson.class, "WINDOW_CAPTION_TEXT",
			"Change responsible persons");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalResponsiblePerson.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/** The header text of the SVN table. */
	public static final I18NResource TABLE_SVN_HEADER_TEXT = new I18NResource(
			ModalResponsiblePerson.class, "TABLE_SVN_HEADER_TEXT", "SVN");

	/** The header text of the responsible person column. */
	public static final I18NResource TABLE_RESPONSIBLE_PERSON_HEADER_TEXT = new I18NResource(
			ModalResponsiblePerson.class,
			"TABLE_RESPONSIBLE_PERSON_HEADER_TEXT", "RESPONSIBLE PERSON");

	/** The control class. */
	private static IControl control;

	/** The SVN table. */
	private Table table;


	/**
	 * Returns the subwindow for setting the responsible person.
	 *
	 * @return the subwindow for setting the responsible person
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
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public ModalResponsiblePerson(final IControl control) {
		this.control = control;
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
		table.addStyleName("myTable");
		HorizontalLayout hl = new HorizontalLayout();

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
						Container container = table.getContainerDataSource();
						Map<String, String> hm = new HashMap<String, String>();
						for (Object contItem : container.getItemIds()) {
							Item item = container.getItem(contItem);
							TextField ch = (TextField) item
									.getItemProperty(
											I18NSupport
													.getMessage(TABLE_RESPONSIBLE_PERSON_HEADER_TEXT))
									.getValue();
							String svn = item.getItemProperty(
									I18NSupport
											.getMessage(TABLE_SVN_HEADER_TEXT))
									.toString();
							hm.put(svn, ch.getValue().toString());
						}

						control.setResponsiblePersons(hm);
						(subwindow.getParent()).removeWindow(subwindow);
					}

				});
		ok.setClickShortcut(KeyCode.ENTER);
		ok.focus();

		layout.addComponent(table);

		hl.setHeight(30, UNITS_PIXELS);
		ok.setStyleName("link");
		ok.addStyleName("Button2");
		hl.addComponent(ok);
		cancel.setStyleName("link");
		cancel.addStyleName("Button2");
		hl.addComponent(cancel);
		hl.setComponentAlignment(ok, Alignment.MIDDLE_CENTER);
		hl.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);

		layout.addComponent(hl);
		layout.setComponentAlignment(hl, Alignment.BOTTOM_CENTER);
		layout.setExpandRatio(table, 1);
		layout.setExpandRatio(hl, 0);
	}

	/**
	 * Fills the container with data.
	 *
	 * @return the filled container
	 *
	 */
	public IndexedContainer fillData() {
		logger.info("Filling data in to modal responsible person.");
		IndexedContainer ic = new IndexedContainer();
		ic.addContainerProperty(I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT),
				String.class, null);
		ic.addContainerProperty(
				I18NSupport.getMessage(TABLE_RESPONSIBLE_PERSON_HEADER_TEXT),
				TextField.class, null);

		for (Node lock : control.getRepositories()) {

			Item item = ic.addItem(lock.getSvn() + ":" + lock.getName());
			item.getItemProperty(I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT))
					.setValue(lock.getSvn());
			TextField tf = new TextField();
			tf.setWidth(100, UNITS_PERCENTAGE);
			if (lock.getResponsiblePerson() == null)
				tf.setValue("");
			else
				tf.setValue(lock.getResponsiblePerson());

			item.getItemProperty(
					I18NSupport
							.getMessage(TABLE_RESPONSIBLE_PERSON_HEADER_TEXT))
					.setValue(tf);

		}
		return ic;
	}

	/**
	 * Initializes the components.
	 *
	 * @throws NullPointerException initialization failed
	 *
	 */
	public void init() throws NullPointerException {
		logger.info("Modal window for Editing responsible person initialization");
		subwindow.setWidth(500, UNITS_PIXELS);
		subwindow.setHeight(500, UNITS_PIXELS);
		table.setContainerDataSource(fillData());
		table.setColumnHeaders(new String[] {
				I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_RESPONSIBLE_PERSON_HEADER_TEXT) });
	}
}