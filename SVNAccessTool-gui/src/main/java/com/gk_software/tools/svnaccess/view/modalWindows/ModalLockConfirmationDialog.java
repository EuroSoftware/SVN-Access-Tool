package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Represent the lock confirmation dialog.
 */
public class ModalLockConfirmationDialog extends VerticalLayout {

	/** The subwindow for the lock confirmation dialog. */
	private Window subwindow;

	/** The ok button. */
	private Button ok;

	/** The cancel button. */
	private Button cancel;

	/** The text of the lock confirmation dialog. */
	private TextField confirmText;

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger
			.getLogger(ModalLockConfirmationDialog.class);

	/** The ok button text. */
	public static final I18NResource OK_BUTTON_TEXT = new I18NResource(
			ModalLockConfirmationDialog.class, "OK_BUTTON_TEXT", "OK");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalLockConfirmationDialog.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/**
	 * Creates a new instance and initializes the variables according to the given
	 * values.
	 *
	 * @param headerText the header text of the dialog
	 * @param message the message of the dialog
	 *
	 */
	public ModalLockConfirmationDialog(String headerText, String message) {
		subwindow = new Window(headerText);
		subwindow.setModal(true);
		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);
		layout.setSizeUndefined();
		Label lab = new Label(message + "\n");
		confirmText = new TextField();
		confirmText.setWidth(50, UNITS_PIXELS);

		this.ok = new Button(I18NSupport.getMessage(OK_BUTTON_TEXT));
		this.ok.setClickShortcut(KeyCode.ENTER);
		this.cancel = new Button(I18NSupport.getMessage(CANCEL_BUTTON_TEXT));
		this.cancel.setClickShortcut(KeyCode.ESCAPE);
		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.addComponent(lab);
		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.addComponent(confirmText);
		hl2.setSizeFull();
		hl2.setComponentAlignment(confirmText, Alignment.MIDDLE_CENTER);

		HorizontalLayout hl11 = new HorizontalLayout();
		hl11.setHeight(50, UNITS_PIXELS);
		ok.setStyleName("link");
		ok.addStyleName("Button2");
		hl11.addComponent(ok);
		cancel.setStyleName("link");
		cancel.addStyleName("Button2");
		hl11.addComponent(cancel);
		hl11.setComponentAlignment(ok, Alignment.MIDDLE_CENTER);
		hl11.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);

		layout.addComponent(hl1);
		layout.addComponent(hl2);
		layout.addComponent(hl11);
		layout.setComponentAlignment(hl1, Alignment.TOP_CENTER);
		layout.setComponentAlignment(hl11, Alignment.TOP_CENTER);
	}

	/**
	 * Returns the ok button.
	 *
	 * @return the ok button
	 *
	 */
	public Button getOk() {
		return ok;
	}

	/**
	 * Returns the cancel button.
	 *
	 * @return the cancel button
	 *
	 */
	public Button getCancel() {
		return cancel;
	}

	/**
	 * Returns the text of the lock confirmation dialog.
	 *
	 * @return the text of the lock confirmation dialog
	 *
	 */
	public TextField getConfirmText() {
		return confirmText;
	}

	/**
	 * Sets the new text of the lock confirmation dialog.
	 *
	 * @param confirmText the new text of the lock confirmation dialog
	 *
	 */
	public void setConfirmText(TextField confirmText) {
		this.confirmText = confirmText;
	}

	/**
	 * Returns the subwindow for the lock confirmation dialog.
	 *
	 * @return the subwindow for the lock confirmation dialog
	 *
	 */
	public Window getSubwindow() {
		try {
			init();
		} catch (NullPointerException e) {
			return null;
		}
		return subwindow;
	}

	/**
	 * Initializes the components.
	 */
	public void init() {
		logger.info("Modal lock confirmation dialog initialization.");
	}
}