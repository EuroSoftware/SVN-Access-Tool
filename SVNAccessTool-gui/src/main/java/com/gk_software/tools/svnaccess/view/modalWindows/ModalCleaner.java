package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.CleanBean;
import com.gk_software.tools.svnaccess.data.accessList.Node;
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
 * Represent the window for cleaning SVNs.
 */
public class ModalCleaner extends VerticalLayout {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ModalCleaner.class);

	/** The subwindow for cleaning SVNs. */
	Window subwindow;

	/** The ok button text. */
	public static final I18NResource OK_BUTTON_TEXT = new I18NResource(
			ModalCleaner.class, "OK_BUTTON_TEXT", "Clean");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalCleaner.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/** The clean window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalCleaner.class, "WINDOW_CAPTION_TEXT", "Cleaner");

	/** The header text of the SVN table. */
	public static final I18NResource TABLE_SVN_HEADER_TEXT = new I18NResource(
			ModalCleaner.class, "TABLE_SVN_HEADER_TEXT", "SVN");

	/** The header text of the CLEAN table. */
	public static final I18NResource TABLE_CLEAN_HEADER_TEXT = new I18NResource(
			ModalCleaner.class, "TABLE_CLEAN_HEADER_TEXT", "CLEAN");

	/** The confirm message text for cleaning SVNs. */
	public static final I18NResource CLEAN_CONFIRM_TEXT = new I18NResource(
			ModalCleaner.class, "CLEAN_CONFIRM_TEXT",
			"Are you sure you want clean up the selected SVNs?");

	/** The header text of the confirm message. */
	public static final I18NResource CLEANER_HEADER = new I18NResource(
			ModalCleaner.class, "CLEANER_HEADER", "Cleaner confirmation");

	/** The control class. */
	private static IControl control;

	/** The SVN table. */
	private Table table;

	/** The depth field. */
	private final TextField tf;

	/**
	 * Returns the subwindow for cleaning SVNs.
	 *
	 * @return the subwindow for cleaning SVNs
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
	public ModalCleaner(final IControl control) {
		ModalCleaner.control = control;
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
		// table.addStyleName("myTable");
		HorizontalLayout hll3 = new HorizontalLayout();
		Label lab2 = new Label("Check directories on SVN");
		hll3.addComponent(lab2);
		final CheckBox chb = new CheckBox();
		chb.setWidth(15, UNITS_PIXELS);
		chb.setValue(true);
		HorizontalLayout hll22 = new HorizontalLayout();
		hll22.setWidth(70, UNITS_PIXELS);
		hll22.addComponent(chb);
		hll22.setComponentAlignment(chb, Alignment.MIDDLE_LEFT);
		hll3.addComponent(hll22);

		HorizontalLayout hll = new HorizontalLayout();
		Label lab = new Label("Depth of directories in ACL");
		hll.addComponent(lab);
		tf = new TextField();
		tf.setColumns(2);
		tf.setValue(-1);

		HorizontalLayout hll2 = new HorizontalLayout();
		hll2.setWidth(70, UNITS_PIXELS);
		hll2.addComponent(tf);
		hll2.setComponentAlignment(tf, Alignment.MIDDLE_LEFT);
		hll.addComponent(hll2);

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

						final List<CleanBean> list = createListOfSelectedItems();
						if (!checkValues(list))
							return;

						final ModalConfirmationDialog mcd = new ModalConfirmationDialog(
								I18NSupport.getMessage(CLEANER_HEADER),
								I18NSupport.getMessage(CLEAN_CONFIRM_TEXT));
						final Window win2 = mcd.getSubwindow();
						(subwindow.getParent()).addWindow(win2);
						mcd.getOk().focus();
						mcd.getOk().addListener(new Button.ClickListener() {

							public void buttonClick(ClickEvent event) {
								logger.info("Unlock confirmed");
								control.cleanAccessList(list, Integer
										.parseInt(tf.getValue().toString()),
										Boolean.parseBoolean(chb.getValue()
												.toString()));
								(subwindow.getParent()).removeWindow(win2);
								(subwindow.getParent()).removeWindow(subwindow);
							}
						});
						mcd.getCancel().addListener(new Button.ClickListener() {

							public void buttonClick(ClickEvent event) {
								logger.info("Unlock canceled");
								(subwindow.getParent()).removeWindow(win2);
							}
						});

						// (subwindow.getParent()).removeWindow(subwindow);
					}

				});
		ok.setClickShortcut(KeyCode.ENTER);
		ok.focus();

		// HorizontalLayout hl2 = new HorizontalLayout();
		// hl2.setSizeFull();
		// hl2.addComponent(table);

		layout.addComponent(table);
		hll3.setWidth(100, UNITS_PERCENTAGE);
		hll3.setComponentAlignment(lab2, Alignment.MIDDLE_LEFT);
		hll3.setComponentAlignment(hll22, Alignment.MIDDLE_RIGHT);

		hll.setWidth(100, UNITS_PERCENTAGE);
		hll.setComponentAlignment(lab, Alignment.MIDDLE_LEFT);
		hll.setComponentAlignment(hll2, Alignment.MIDDLE_RIGHT);

		layout.addComponent(hll3);
		layout.addComponent(hll);

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
		// layout.setExpandRatio(hll, 0);
	}

	/**
	 * Checks if all the values are correctly filled.
	 *
	 * @param list the checked list
	 *
	 * @return true if correct otherwise false
	 *
	 */
	private boolean checkValues(List<CleanBean> list) {
		if (list.size() == 0) {
			control.getApplication().showNotification(
					"There are no selected SVNs.");
			logger.info("There are no selected SVNs.");
			return false;
		}

		try {
			int a = Integer.parseInt(tf.getValue().toString());
		} catch (Exception e) {
			control.getApplication().showNotification(
					"Depth field does not contain an integer value.");
			logger.info("Depth field does not contain an integer value.");
			return false;
		}

		return true;
	}

	/**
	 * Creates the list of the currently selected items.
	 *
	 * @return the list of the currently selected items
	 *
	 */
	private List<CleanBean> createListOfSelectedItems() {
		Container container = table.getContainerDataSource();
		List<CleanBean> list = new ArrayList<CleanBean>();
		for (Object contItem : container.getItemIds()) {
			Item item = container.getItem(contItem);
			CheckBox ch = (CheckBox) item.getItemProperty(
					I18NSupport.getMessage(TABLE_CLEAN_HEADER_TEXT)).getValue();
			if (ch.getValue().equals(true)) {
				String svn = item.getItemProperty(
						I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT))
						.toString();

				list.add(new CleanBean(svn));
			}
		}
		return list;
	}

	/**
	 * Fills the container with data.
	 *
	 * @return the filled container
	 *
	 */
	public IndexedContainer fillData() {
		logger.info("Filling data in to modal Clean.");
		IndexedContainer ic = new IndexedContainer();
		ic.addContainerProperty(I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT),
				String.class, null);
		ic.addContainerProperty(
				I18NSupport.getMessage(TABLE_CLEAN_HEADER_TEXT),
				CheckBox.class, false);
		for (Node cleanN : control.getRepositories()) {

			Item item = ic.addItem(cleanN.getSvn());
			item.getItemProperty(I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT))
					.setValue(cleanN.getSvn());
			CheckBox c = new CheckBox();
			c.setImmediate(true);
			c.setWidth(15, UNITS_PIXELS);
			c.setEnabled(true);
			item.getItemProperty(
					I18NSupport.getMessage(TABLE_CLEAN_HEADER_TEXT))
					.setValue(c);
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
		logger.info("Modal window for CLEAN initialization");
		subwindow.setWidth(500, UNITS_PIXELS);
		subwindow.setHeight(500, UNITS_PIXELS);
		table.setContainerDataSource(fillData());
		table.setColumnHeaders(new String[] {
				I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_CLEAN_HEADER_TEXT) });

		table.setColumnAlignment(
				I18NSupport.getMessage(TABLE_CLEAN_HEADER_TEXT),
				Table.ALIGN_CENTER);
		table.setColumnWidth(I18NSupport.getMessage(TABLE_CLEAN_HEADER_TEXT),
				70);

	}
}