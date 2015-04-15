package com.gk_software.tools.svnaccess.view.modalWindows;

import gk.ee_common.i18n.I18NResource;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.view.components.FavoriteBean;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Represent the window for cookies removal.
 */
public class ModalCookiesSettings extends VerticalLayout {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger
			.getLogger(ModalCookiesSettings.class);

	/** The remove button. */
	Button remove;

	/** The subwindow for cookies removal. */
	Window subwindow;

	/** The ok button text. */
	public static final I18NResource OK_BUTTON_TEXT = new I18NResource(
			ModalCookiesSettings.class, "OK_BUTTON_TEXT", "Remove");

	/** The cancel button text. */
	public static final I18NResource CANCEL_BUTTON_TEXT = new I18NResource(
			ModalCookiesSettings.class, "CANCEL_BUTTON_TEXT", "Cancel");

	/** The cookies removal window title. */
	public static final I18NResource WINDOW_CAPTION_TEXT = new I18NResource(
			ModalCookiesSettings.class, "WINDOW_CAPTION_TEXT", "Remove cookies");

	/** The confirm message text for cookies removal. */
	public static final I18NResource REMOVE_ALL_CONFIRM_TEXT = new I18NResource(
			ModalCookiesSettings.class, "REMOVE_ALL_CONFIRM_TEXT",
			"Are you sure you want to remove selected cookies?");

	/** The header text of the confirm message. */
	public static final I18NResource REMOVE_ALL_HEADER = new I18NResource(
			ModalCookiesSettings.class, "REMOVE_ALL_HEADER", "Remove cookies");

	/** The list of the cookies. */
	private ListSelect l;

	/** The control class. */
	private static IControl control;

	/** The container for the cookies. */
	private IndexedContainer nameContainer;

	/**
	 * Returns the subwindow for cookies removal.
	 *
	 * @return the subwindow for cookies removal
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
	 * Returns the container for the cookies.
	 *
	 * @return the container for the cookies
	 *
	 */
	public static IndexedContainer getNameContainer() {
		IndexedContainer contactContainer = new IndexedContainer();

		Map<String, Long> map = control.getApplication().getCookies()
				.getUserCookies();
		for (String key : map.keySet()) {
			FavoriteBean f = new FavoriteBean(key, map.get(key));
			contactContainer.addItem(f);
		}

		Map<String, Integer> map2 = control.getApplication().getCookies()
				.getSaveCookies();

		for (String key : map2.keySet()) {
			FavoriteBean f = new FavoriteBean(key, map2.get(key));
			contactContainer.addItem(f);

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
	public ModalCookiesSettings(final IControl control) {
		this.control = control;
		subwindow = new Window(I18NSupport.getMessage(WINDOW_CAPTION_TEXT));
		subwindow.setModal(true);

		VerticalLayout layout = (VerticalLayout) subwindow.getContent();
		layout.setMargin(true);
		layout.setSpacing(true);

		setSpacing(true);

		l = new ListSelect("");
		l.setSizeFull();
		l.setNullSelectionAllowed(true);
		l.setMultiSelect(true);
		l.setImmediate(true);

		layout.addComponent(l);

		HorizontalLayout hl12 = new HorizontalLayout();

		hl12.setHeight(30, UNITS_PIXELS);
		hl12.setWidth(100, UNITS_PERCENTAGE);

		layout.addComponent(hl12);
		layout.setComponentAlignment(hl12, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
		layout.setHeight(100, UNITS_PERCENTAGE);
		Button cancel = new Button(I18NSupport.getMessage(CANCEL_BUTTON_TEXT),
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {
						(subwindow.getParent()).removeWindow(subwindow);
						logger.info("Removing cookies was canceled");
					}
				});

		cancel.setClickShortcut(KeyCode.ESCAPE);
		remove = new Button(I18NSupport.getMessage(OK_BUTTON_TEXT),
				new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {

						if (((Set<FavoriteBean>) l.getValue()).size() == 0) {
							control.getApplication().showNotification(
									"No cookie selected");
							return;
						}
						final ModalConfirmationDialog mc = new ModalConfirmationDialog(
								I18NSupport.getMessage(REMOVE_ALL_HEADER),
								I18NSupport.getMessage(REMOVE_ALL_CONFIRM_TEXT));

						(subwindow.getParent()).addWindow(mc.getSubwindow());
						mc.getOk().focus();
						mc.getOk().addListener(new Button.ClickListener() {
							public void buttonClick(ClickEvent event) {
								FavoriteBean fb = (FavoriteBean) control
										.getApplication().getCurrentPath()
										.getValue();

								for (FavoriteBean f : (Set<FavoriteBean>) l
										.getValue()) {

									control.getApplication().getCookies()
											.removeSaveCookie(f);
									control.getApplication().getCookies()
											.removeUserCookie(f);
									logger.info("Removing cookies for: svn: "
											+ f.getSvn() + ", path: "
											+ f.getFullpath());
									if (!f.equals(fb))
										control.getApplication()
												.getCurrentPath().removeItem(f);
								}
								control.getApplication().getCurrentPath()
										.removeBools();
								(subwindow.getParent()).removeWindow(mc
										.getSubwindow());
								(subwindow.getParent()).removeWindow(subwindow);
							}
						});
						mc.getCancel().addListener(new Button.ClickListener() {
							public void buttonClick(ClickEvent event) {
								(subwindow.getParent()).removeWindow(mc
										.getSubwindow());
								logger.info("Removing cookies was canceled");
								remove.focus();
							}
						});
					}
				});
		remove.setClickShortcut(KeyCode.ENTER);
		remove.focus();
		HorizontalLayout hl11 = new HorizontalLayout();
		hl11.setHeight(20, UNITS_PIXELS);
		remove.setStyleName("link");
		remove.addStyleName("Button2");
		hl11.addComponent(remove);
		cancel.setStyleName("link");
		cancel.addStyleName("Button2");
		hl11.addComponent(cancel);
		hl11.setComponentAlignment(remove, Alignment.MIDDLE_CENTER);
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
	public void init() throws NullPointerException {
		logger.info("Modal window for cookie removing initialization");

		l.removeAllItems();
		subwindow.setWidth(50,UNITS_PERCENTAGE);
		subwindow.setHeight(380, UNITS_PIXELS);
		remove.focus();
		nameContainer = getNameContainer();
		l.setContainerDataSource(nameContainer);
	}

}