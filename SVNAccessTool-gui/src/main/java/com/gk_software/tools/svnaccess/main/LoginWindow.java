package com.gk_software.tools.svnaccess.main;

import gk.ee_common.i18n.I18NResource;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.impl.Authentization;
import com.gk_software.tools.svnaccess.bussiness.impl.CookieFactory;
import com.gk_software.tools.svnaccess.data.accessList.AccessListReader;
import com.gk_software.tools.svnaccess.data.accessList.ViewInformation;
import com.gk_software.tools.svnaccess.data.mail.MailProperties;
import com.gk_software.tools.svnaccess.utils.Constants;
import com.gk_software.tools.svnaccess.view.components.CustomLoginForm;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.LoginForm.LoginEvent;
import com.vaadin.ui.LoginForm.LoginListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * Represent the login window. It also services the login event.
 */
@SuppressWarnings("serial")
public class LoginWindow extends Window {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(LoginWindow.class);

	/** The login window title. */
	public static final I18NResource LOGIN_WINDOW_TITLE = new I18NResource(
			LoginWindow.class, "LOGIN_WINDOW_TITLE",
			"SVN Access Tool - Authetication required !!!");

	/** The login window name. */
	public static final I18NResource LOGIN_WINDOW_NAME = new I18NResource(
			LoginWindow.class, "LOGIN_WINDOW_NAME", "login");

	/** The username label text. */
	public static final I18NResource LOGIN_USERNAME_CAPTION = new I18NResource(
			LoginWindow.class, "LOGIN_WINDOW_NAME", "Username:");

	/** The password label text. */
	public static final I18NResource LOGIN_PASSWORD_CAPTION = new I18NResource(
			LoginWindow.class, "LOGIN_WINDOW_NAME", "Password:");

	/** The login button text. */
	public static final I18NResource LOGIN_SUBMIT_BUTTON_CAPTION = new I18NResource(
			LoginWindow.class, "LOGIN_WINDOW_NAME", "Log in");

	/** The footer text. */
	public static final I18NResource FOOTER_TEXT_ABOUT = new I18NResource(
			LoginWindow.class, "FOOTER_TEXT_ABOUT", "SVN_Access_Tool - v "
					+ LoginWindow.class.getPackage().getImplementationVersion()
					+ ", (c) 2014 Eurosoftware ");

	/** The cookie factory. */
	private CookieFactory cookies;

	/** The authentization class. */
	private Authentization auth = new Authentization();

	/** The main app window. */
	private MainApplicationWindow mainWindow;

	/** The instance of this class. */
	private LoginWindow thisWindow = this;

	/**
	 * Creates a new instance and initializes the variables.
	 */
	public LoginWindow() {
		super(I18NSupport.getMessage(LOGIN_WINDOW_TITLE));
		setName(I18NSupport.getMessage(LOGIN_WINDOW_NAME));
	}

	/**
	 * Authenticates the user with the given credentials over LDAP.
	 *
	 * @param login the login of the logging user
	 * @param password the password of the logging user
	 * @param domain the domain under which the user is logging in
	 *
	 * @throws Exception authentication failed
	 *
	 */
	public void authenticate(String login, String password, String domain) throws Exception {
		logger.info("Authentication started");
		mainWindow = new MainApplicationWindow();

		initConstants();

		int d = Integer.parseInt(Constants.getProperties().getProperty(
				"timeout")) * 60;
		((WebApplicationContext) this.getApplication().getContext())
				.getHttpSession().setMaxInactiveInterval(d);
		logger.info("Setting inactive interval to: " + d + " s.");

		if (auth.login(login, password, domain)) {
			logger.info("Authentication is successful.");
			String longLogin = login+"@"+domain;
			loadProtectedResources(longLogin);
			return;
		}
		logger.error("Login failed for user: " + login);
		throw new Exception("Login failed!");
	}

	/**
	 * Launched after successful authentication. Sets the content of the Main Window according to
	 * the user's settings, rights and cookies.
	 *
	 * @param login the login of the logged user
	 *
	 * @throws Exception setting the content failed
	 *
	 */
	private void loadProtectedResources(String login) throws Exception {
		try {
			mainWindow.init(login, cookies);
			this.getApplication().setMainWindow(mainWindow);
		} catch (Exception e) {
			AccessListReader.removeAll();
			ViewInformation.removeAll();
			e.printStackTrace();
			throw new Exception("Something is wrong");
		}
	}

	/**
	 * Loads properties from the prop.file set in web.xml.
	 */
	private void initConstants() {
		ServletContext context = ((WebApplicationContext) this.getApplication()
				.getContext()).getHttpSession().getServletContext();

		logger.info("Setting properties file to: "
				+ context.getInitParameter("properties"));
		Constants.setPROP_FILE(context.getInitParameter("properties"));
		logger.info("Setting modify Access List path to: "
				+ context.getInitParameter("modifyList"));
		Constants.setACCESS_LIST_MODIFY_PATH(context
				.getInitParameter("modifyList"));

		Constants.setLDAP_SETTINGS_PATH(context.getInitParameter("ldapsettings"));

		Constants.setUSERNAME_SVN(context.getInitParameter("loginSVN"));
		Constants.setPASSWORD_SVN(context.getInitParameter("passwordSVN"));

		Constants.loadConstants();

		MailProperties.setPROP_FILE(Constants.getProperties().getProperty("mail_folder"));
		MailProperties.loadConstants();
	}

	/**
	 * Initializes the login window.
	 */
	public void init() {
		logger.info("Start");

		setName(I18NSupport.getMessage(LOGIN_WINDOW_NAME));
		this.cookies = new CookieFactory();
		this.addComponent(cookies.getBrowserCookies());

		CustomLoginForm loginForm = new CustomLoginForm(
				I18NSupport.getMessage(LOGIN_USERNAME_CAPTION),
				I18NSupport.getMessage(LOGIN_PASSWORD_CAPTION),
				I18NSupport.getMessage(LOGIN_SUBMIT_BUTTON_CAPTION));
		loginForm.addListener(new LoginListener() {

			public void onLogin(LoginEvent event) {

				try {
					authenticate(event.getLoginParameter("username"),
							event.getLoginParameter("password"), event.getLoginParameter("domena"));
					open(new ExternalResource(thisWindow.getApplication()
							.getURL()));
				} catch (Exception e) {

					showNotification("Login Failed!", Notification.TYPE_WARNING_MESSAGE);
					e.printStackTrace();
				}

			}
		});
		VerticalLayout layout = (VerticalLayout) this.getContent();
		this.setStyleName("bodyLogin");
		this.setHeight(100, UNITS_PERCENTAGE);
		layout.setSizeFull();
		HorizontalLayout jl = new HorizontalLayout();
		jl.setWidth(99, UNITS_PERCENTAGE);
		Label jl3 = new Label("SVN Access Tool");
		final LoginWindow main = this;
		Button help = new Button("Help");
		help.setStyleName("link");
		help.addStyleName("Button2");
		help.addListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				ExternalResource ext = new ExternalResource(
						"http://wiki.es.gk-software.com/index.php/SVN_Access_Tool");
				main.open(ext, "_blank");
			}
		});
		help.addStyleName("ButtonLink");

		jl.addComponent(help);

		jl.setComponentAlignment(help, Alignment.MIDDLE_LEFT);

		jl3.addStyleName("h3");

		jl.setHeight(40, UNITS_PIXELS);
		jl.addStyleName("headerItem");

		Label jl2 = new Label(I18NSupport.getMessage(FOOTER_TEXT_ABOUT));
		jl2.addStyleName("footerItem");
		jl2.setHeight(37, UNITS_PIXELS);
		jl2.setWidth(99, UNITS_PERCENTAGE);

		layout.addComponent(jl);
		layout.addComponent(jl3);
		loginForm.setWidth(300, UNITS_PIXELS);
		loginForm.setHeight(300, UNITS_PIXELS);

		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(loginForm);
		hl.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

		layout.addComponent(hl);
		layout.addComponent(jl2);

		layout.setExpandRatio(jl, 0);
		layout.setExpandRatio(jl3, 0);
		layout.setExpandRatio(hl, 1);
		layout.setExpandRatio(jl2, 0);
		layout.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
	}
}
