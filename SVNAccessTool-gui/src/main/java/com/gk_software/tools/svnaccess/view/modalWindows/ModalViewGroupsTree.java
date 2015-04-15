package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.view.components.GroupsTree.GroupTreeTable;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Represent the window for group tree viewing.
 */
public class ModalViewGroupsTree extends VerticalLayout {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ModalViewGroupsTree.class);

	/** The subwindow for group tree viewing. */
	Window subwindow;

	/** The title of the window for group tree viewing. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalViewGroupsTree.class, "WINDOW_CAPTION_TEXT", "Members of ");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalViewGroupsTree.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/** The group tree table. */
	private GroupTreeTable gtt;

	/** The filter textfield. */
	private TextField filterField;

	/**
	 * Returns the subwindow for group tree viewing for the given group.
	 *
	 * @param group the group whose tree window is returned
	 *
	 * @return the subwindow for group tree viewing
	 *
	 */
	public Window getSubwindow(String group) {

		subwindow.setCaption(I18NSupport.getMessage(WINDOW_CAPTION_TEXT)
				+ group);
		init(group);

		return subwindow;
	}

	/**
	 * Creates a new instance, sets the {@code control} variable according to the
	 * given value and initializes all the other variables.
	 *
	 * @param control the control class
	 *
	 */
	public ModalViewGroupsTree(final IControl control) {
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
				gtt.filter(event.getText());
			}
		});

		filterField.setWidth(100, UNITS_PERCENTAGE);
		layout.addComponent(filterField);

		gtt = new GroupTreeTable();
		layout.addComponent(gtt);

		HorizontalLayout hl12 = new HorizontalLayout();

		layout.addComponent(hl12);
		layout.setComponentAlignment(hl12, Alignment.TOP_CENTER);

		Button cancel = new Button(I18NSupport.getMessage(CANCEL_BUTTON_TEXT),
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						(subwindow.getParent()).removeWindow(subwindow);
					}
				});
		cancel.setClickShortcut(KeyCode.ESCAPE);
		HorizontalLayout hl11 = new HorizontalLayout();
		hl11.setHeight(50, UNITS_PIXELS);
		cancel.setStyleName("link");
		cancel.addStyleName("Button2");
		hl11.addComponent(cancel);
		hl11.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);

		layout.addComponent(hl11);
		layout.setComponentAlignment(hl11, Alignment.TOP_CENTER);
		layout.setExpandRatio(gtt, 1);
		layout.setExpandRatio(hl11, 0);
	}

	/**
	 * Initializes the components.
	 *
	 * @throws NullPointerException initialization failed
	 *
	 */
	public void init(String group) throws NullPointerException {
		logger.info("Modal window for viewing groups hierarchy initialization");
		logger.info("Selected group: " + group);
		filterField.setValue("");
		subwindow.setWidth(550, UNITS_PIXELS);
		subwindow.setHeight(550, UNITS_PIXELS);
		gtt.init("@" + group);
	}
}