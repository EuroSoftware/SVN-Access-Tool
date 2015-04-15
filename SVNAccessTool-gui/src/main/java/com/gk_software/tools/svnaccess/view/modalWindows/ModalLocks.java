package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.LockBean;
import com.gk_software.tools.svnaccess.data.accessList.ViewInformation;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Represent the window for locking SVNs.
 */
public class ModalLocks extends VerticalLayout {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ModalLocks.class);

	/** The subwindow for locking SVNs. */
	Window subwindow;

	/** The ok button text. */
	public static final I18NResource OK_BUTTON_TEXT = new I18NResource(
			ModalLocks.class, "OK_BUTTON_TEXT", "Unlock");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalLocks.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/** The lock SVN window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalLocks.class, "WINDOW_CAPTION_TEXT", "Locks");

	/** The header text of the SVN table. */
	public static final I18NResource TABLE_SVN_HEADER_TEXT = new I18NResource(
			ModalLocks.class, "TABLE_SVN_HEADER_TEXT", "SVN");

	/** The header text of the path column. */
	public static final I18NResource TABLE_PATH_HEADER_TEXT = new I18NResource(
			ModalLocks.class, "TABLE_PATH_HEADER_TEXT", "PATH");

	/** The header text of the locked by column. */
	public static final I18NResource TABLE_LOCKEDBY_HEADER_TEXT = new I18NResource(
			ModalLocks.class, "TABLE_LOCKEDBY_HEADER_TEXT", "LOCKED BY");

	/** The header text of the unlock column. */
	public static final I18NResource TABLE_UNLOCK_HEADER_TEXT = new I18NResource(
			ModalLocks.class, "TABLE_UNLOCK_HEADER_TEXT", "UNLOCK");

	/** The confirm message text for locking SVNs. */
	public static final I18NResource UNLOCK_CONFIRM_TEXT = new I18NResource(
			ModalLocks.class, "UNLOCK_CONFIRM_TEXT",
			"Are you sure you want to unlock selected directories?");

	/** The header text of the confirm message. */
	public static final I18NResource UNLOCK_HEADER = new I18NResource(
			ModalLocks.class, "UNLOCK_HEADER", "Unlock Confirmation");

	/** The control class. */
	private static IControl control;

	/** The SVN table. */
	private Table table;

	/**
	 * Returns the subwindow for locking SVNs.
	 *
	 * @return the subwindow for locking SVNs
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
	public ModalLocks(final IControl control) {
		ModalLocks.control = control;
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
						final List<LockBean> list = new ArrayList<LockBean>();
						for (Object contItem : container.getItemIds()) {
							Item item = container.getItem(contItem);
							CheckBox ch = (CheckBox) item
									.getItemProperty(
											I18NSupport
													.getMessage(TABLE_UNLOCK_HEADER_TEXT))
									.getValue();
							if (ch.getValue().equals(true)) {
								String svn = item
										.getItemProperty(
												I18NSupport
														.getMessage(TABLE_SVN_HEADER_TEXT))
										.toString();
								String fullPath = item
										.getItemProperty(
												I18NSupport
														.getMessage(TABLE_PATH_HEADER_TEXT))
										.toString();
								String lockedBy = item
										.getItemProperty(
												I18NSupport
														.getMessage(TABLE_LOCKEDBY_HEADER_TEXT))
										.toString();

								list.add(new LockBean(svn, fullPath, lockedBy));
							}
						}

						if (list.size() == 0) {
							control.getApplication().showNotification(
									"There are no selected directories.");
							logger.info("There are no selected directories.");
							return;
						}

						final ModalConfirmationDialog mcd = new ModalConfirmationDialog(
								I18NSupport.getMessage(UNLOCK_HEADER),
								I18NSupport.getMessage(UNLOCK_CONFIRM_TEXT));
						final Window win2 = mcd.getSubwindow();
						(subwindow.getParent()).addWindow(win2);
						mcd.getOk().focus();
						mcd.getOk().addListener(new Button.ClickListener() {

							public void buttonClick(ClickEvent event) {
								logger.info("Unlock confirmed");
								control.unlockNodes(list);
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
		logger.info("Filling data in to modal Locks.");
		IndexedContainer ic = new IndexedContainer();
		ic.addContainerProperty(I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT),
				String.class, null);
		ic.addContainerProperty(I18NSupport.getMessage(TABLE_PATH_HEADER_TEXT),
				String.class, null);
		ic.addContainerProperty(
				I18NSupport.getMessage(TABLE_LOCKEDBY_HEADER_TEXT),
				String.class, null);
		ic.addContainerProperty(
				I18NSupport.getMessage(TABLE_UNLOCK_HEADER_TEXT),
				CheckBox.class, false);
		for (LockBean lock : control.getLockedPaths()) {
			boolean canModifyCheckboxesFromNode = false;
			boolean hasWriteFlag = false;
			try {
				canModifyCheckboxesFromNode = ViewInformation.getInstance()
						.canModifyCheckBoxesFromRoot(lock.getSvn(),
								lock.getFullpath(), control.getUser());
				hasWriteFlag = ViewInformation.getInstance().hasWriteRights(
						lock.getSvn(), lock.getFullpath(), control.getUser());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (canModifyCheckboxesFromNode || hasWriteFlag) {
				Item item = ic
						.addItem(lock.getSvn() + ":" + lock.getFullpath());
				item.getItemProperty(
						I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT))
						.setValue(lock.getSvn());
				item.getItemProperty(
						I18NSupport.getMessage(TABLE_PATH_HEADER_TEXT))
						.setValue(lock.getFullpath());
				item.getItemProperty(
						I18NSupport.getMessage(TABLE_LOCKEDBY_HEADER_TEXT))
						.setValue(lock.getLockedBy());

				CheckBox c = new CheckBox();
				c.setImmediate(true);

				c.setWidth(15, UNITS_PIXELS);
				c.setEnabled(true);
				// c.addListener(new ValueChangeListener() {
				//
				// public void valueChange(ValueChangeEvent event) {
				// System.out.println("click "
				// + event.getProperty().getValue());
				//
				// }
				// });
				item.getItemProperty(
						I18NSupport.getMessage(TABLE_UNLOCK_HEADER_TEXT))
						.setValue(c);
			}
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
		logger.info("Modal window for LOCKS initialization");
		subwindow.setWidth(500, UNITS_PIXELS);
		subwindow.setHeight(500, UNITS_PIXELS);
		table.setContainerDataSource(fillData());
		table.setColumnHeaders(new String[] {
				I18NSupport.getMessage(TABLE_SVN_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_PATH_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_LOCKEDBY_HEADER_TEXT),
				I18NSupport.getMessage(TABLE_UNLOCK_HEADER_TEXT) });

		table.setColumnAlignment(
				I18NSupport.getMessage(TABLE_UNLOCK_HEADER_TEXT),
				Table.ALIGN_CENTER);
		 table.setColumnWidth(I18NSupport.getMessage(TABLE_UNLOCK_HEADER_TEXT), 70);

	}
}