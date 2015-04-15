package com.gk_software.tools.svnaccess.main;

import gk.ee_common.i18n.I18NResource;
import gk.ee_common.util.PropertyKeyTypes.PropertyKeyString;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.core.i18n.I18NSupport;
import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.bussiness.impl.Control;
import com.gk_software.tools.svnaccess.bussiness.impl.CookieFactory;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.view.components.ControlButtons;
import com.gk_software.tools.svnaccess.view.components.CurrentPath;
import com.gk_software.tools.svnaccess.view.components.EffectiveRightsTable;
import com.gk_software.tools.svnaccess.view.components.GroupsTable;
import com.gk_software.tools.svnaccess.view.components.InheritanceCheckBox;
import com.gk_software.tools.svnaccess.view.components.Repositories;
import com.gk_software.tools.svnaccess.view.components.UsersTable;
import com.gk_software.tools.svnaccess.view.components.DirectoriesTree.DirectoriesTreeTable;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalCleaner;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalClearFunction;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalCookiesSettings;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalLocks;
import com.gk_software.tools.svnaccess.view.modalWindows.ModalMail;
import com.vaadin.Application;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.Window;

/**
 * The main application window.
 */
public class MainApplicationWindow extends Window {

	/** The serial id. */
	private static final long serialVersionUID = -970352999864811659L;

	/** The default name of the property file. */
	public static final PropertyKeyString PROP_APPLICATION_VERSION = new PropertyKeyString(
			"application.version", "N/A");

	// private static final Log logger = LogFactory
	// .getLog(MainApplicationWindow.class);

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(MainApplicationWindow.class);

	/** The logout button text. */
	public static final I18NResource LOGOUT_BUTTON_CAPTION = new I18NResource(
			MainApplicationWindow.class, "LOGOUT_BUTTON_CAPTION", "Logout");

	/** The access list label text. */
	public static final I18NResource ACCESS_LIST_LABEL_NAME = new I18NResource(
			MainApplicationWindow.class, "ACCESS_LIST_LABEL_NAME",
			"ACCESS LIST - CURRENT RIGHTS");

	/** The efective rights label text. */
	public static final I18NResource EFFECTIVE_RIGHTS_LABEL_NAME = new I18NResource(
			MainApplicationWindow.class, "ACCESS_LIST_LABEL_NAME",
			"INHERITED RIGHTS");

	/** The cookie factory. */
	private CookieFactory cookies;
	
	/** The main panel. */
	private HorizontalSplitPanel mainPanel;
	
	/** The left panel. */
	private VerticalLayout leftPanel;
	
	/** The right panel. */
	private VerticalLayout rightPanel;
	
	/** The repositories. */
	private Repositories repositories;
	
	/** The current path. */
	private CurrentPath currentPath;
	
	/** The directories table. */
	private DirectoriesTreeTable directories;
	
	/** The logout button. */
	private Button logoutButton;
	
	/** The clear rights button. */
	private Button clearRights;
	
	/** The efective rights label. */
	private Label effectiveRightsLabel;
	
	/** The efective rights table. */
	private EffectiveRightsTable effectiveRightsTable;
	
	/** The acccess list label. */
	private Label accessListLabel;
	
	/** The groups table. */
	private GroupsTable groupsTable;
	
	/** The users table. */
	private UsersTable usersTable;
	
	/** The inheritance checkbox. */
	private InheritanceCheckBox inheritance;
	
	/** The control buttons. */
	private ControlButtons buttons;
	
	/** The instance of this class. */
	private MainApplicationWindow thisWindow = this;
	
	/** The control class. */
	private IControl control;

	/**
	 * Creates a new instance and sets the window title.
	 */
	public MainApplicationWindow() {
		super("SVN Access Tool");
		// setName("SVN Access Tool");
	}

	/**
	 * Initializes the window and sets the variables according to the given values.
	 * 
	 * @param user the logged user
	 * @param cookies2 the cookie factory for the logged user
	 * 
	 * @throws Exception initialization failed
	 * 
	 */
	public void init(String user, CookieFactory cookies2) throws Exception {
		logger.info("Parameters: user=" + user);
		// Constants.loadConstants();
		// this.cookies =new CookieFactory();
		this.cookies = cookies2;

		logger.info("Creating control");
		try {
			control = new Control(this, user, cookies);
		} catch (Exception e) {
			throw e;
		}

		this.setWidth(100, UNITS_PERCENTAGE);
		logger.info("Creating top panel with help");

		this.setStyleName("bodyLogin");
		final VerticalSplitPanel vert = new VerticalSplitPanel();
		vert.setHeight(100, UNITS_PERCENTAGE);
		vert.setWidth(100, UNITS_PERCENTAGE);

		vert.setSplitPosition(50, Sizeable.UNITS_PIXELS);
		vert.setLocked(true);
		vert.setImmediate(true);
		this.setImmediate(true);
		// vert.addStyleName("splitPane");
		setContent(vert);
		initializeTopPanel(vert);
		logger.info("Creating main panel");
		mainPanel = new HorizontalSplitPanel();
		mainPanel.setWidth(100, Sizeable.UNITS_PERCENTAGE);
		// mainPanel.setImmediate(true);
		logger.info("Creating left panel");
		leftPanel = new VerticalLayout();
		leftPanel.setSizeFull();
		leftPanel.addStyleName("leftPanelStyle");

		leftPanel.addComponent(cookies.getBrowserCookies());

		logger.info("Creating right panel");
		rightPanel = new VerticalLayout();
		// rightPanel.setSizeFull();

		logger.info("Initializing left panel");
		initializeLeftPanel();

		logger.info("Initializing right panel");
		initializeRightPanel();

		mainPanel.setSplitPosition(325, Sizeable.UNITS_PIXELS, true);
		mainPanel.setLocked(false);
		vert.addComponent(mainPanel);

		getButtons().disableAll();
		getButtons().getLock().setEnabled(false);
		getButtons().getUnlock().setEnabled(false);

		getInheritance().setEnabled(false);
		if (control.getUser().isAdmin()) {
			logger.info("Logged user is admin");
			getButtons().getSettings().setEnabled(true);
		}

		control.setPathFromCookies();
	}

	/**
	 * Returns the cookie factory.
	 *
	 * @return the cookie factory
	 *
	 */
	public CookieFactory getCookies() {
		return cookies;
	}

	/**
	 * Initializes the top panel and adds id to the given vertical panel.
	 *
	 * @param vert the vertical panel to which is the top panel added
	 *
	 */
	private void initializeTopPanel(VerticalSplitPanel vert) {

		Button help = new Button("Help");
		help.setStyleName("link");
		help.addStyleName("Button2");
		help.addListener(new ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				// System.out.println("click");
				ServletContext context = ((WebApplicationContext) thisWindow
						.getApplication().getContext()).getHttpSession()
						.getServletContext();
				String path = context.getContextPath();

				// ExternalResource ext = new ExternalResource(path
				// + "/SVN_Access_Tool-updated.pdf");

				ExternalResource ext = new ExternalResource(
						"http://wiki.es.gk-software.com/index.php/SVN_Access_Tool");
				thisWindow.open(ext, "_blank");
			}
		});

		Button logViewer = new Button("Log Viewer");
		logViewer.setStyleName("link");
		logViewer.addStyleName("Button2");
		logViewer.addListener(new ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				logger.info("User: " + control.getUser().getUserName()
						+ " click on LogViewer.");
				ServletContext context = ((WebApplicationContext) thisWindow
						.getApplication().getContext()).getHttpSession()
						.getServletContext();
				String path = context.getContextPath();

				WebApplicationContext ctx = ((WebApplicationContext) thisWindow
						.getApplication().getContext());
				HttpSession session = ctx.getHttpSession();

				Set<String> modificationRepos = new HashSet<String>();

				for (Node r : control.getRepositories()) {

					if (control.canModifyCheckBoxes(r.getSvn(), "/",
							control.getUser())) {
						modificationRepos.add(r.getSvn());
					}
					String responsiblePerson = null;
					if (r.getResponsiblePerson() != null) {
						responsiblePerson = r.getResponsiblePerson();
						if (!responsiblePerson.contains("@")) {
							responsiblePerson += "@gk-domain";
						}
						responsiblePerson = responsiblePerson.toLowerCase();
					}

					if (responsiblePerson != null
							&& responsiblePerson.equals(control.getUser()
									.getUserName().toLowerCase())) {
						modificationRepos.add(r.getSvn());
					}
				}
				if (control.getUser().isAdmin()
						|| modificationRepos.size() != 0) {
					session.setAttribute("user", control.getUser()
							.getUserName());
					session.setAttribute("modificationRepos", modificationRepos);
					session.setAttribute("isAdmin", control.getUser().isAdmin());
					logger.info("User has rights to see log from following groups: "
							+ modificationRepos);
				} else {
					logger.info("User has no rights to see log viewer");
					session.setAttribute("user", null);
				}
				ExternalResource ext = new ExternalResource(path + "/LogReader");
				// ExternalResource ext = new ExternalResource(path+"/"+context
				// .getInitParameter("documentation"));
				thisWindow.open(ext, "_blank");
			}

		});

		Button locks = new Button("Locks");
		locks.setStyleName("link");
		locks.addStyleName("Button2");
		locks.addListener(new ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				final ModalLocks ml = new ModalLocks(control);
				getWindow().addWindow(ml.getSubwindow());
			}

		});

		Button clean = new Button("Clean");
		clean.setStyleName("link");
		clean.addStyleName("Button2");
		clean.addListener(new ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				if (control.getUser().isAdmin()) {
					final ModalCleaner ml = new ModalCleaner(control);
					getWindow().addWindow(ml.getSubwindow());
				} else {
					thisWindow
							.showNotification("You do not have permission to perform this action.");
				}
			}

		});


		clearRights = new Button("Clean rights");
		clearRights.setStyleName("link");
		clearRights.addStyleName("Button2");

		clearRights.setEnabled(false);

		clearRights.addListener(new ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				ModalClearFunction mcf = new ModalClearFunction(control);
				getWindow().addWindow(mcf.getSubwindow());
			}

		});

		Button mail = new Button("Mail");
		mail.setStyleName("link");
		mail.addStyleName("Button2");
		mail.addListener(new ClickListener() {
			@Override
			public void buttonClick(Button.ClickEvent event) {
				Set<String> modificationRepos = new HashSet<String>();

				for (Node r : control.getRepositories()) {

					if (control.canModifyCheckBoxes(r.getSvn(), "/",
							control.getUser())) {
						modificationRepos.add(r.getSvn());
					}
					String responsiblePerson = null;
					if (r.getResponsiblePerson() != null) {
						responsiblePerson = r.getResponsiblePerson();
						if (!responsiblePerson.contains("@")) {
							responsiblePerson += "@gk-domain";
						}
						responsiblePerson = responsiblePerson.toLowerCase();
					}

					if (responsiblePerson != null
							&& responsiblePerson.equals(control.getUser()
									.getUserName().toLowerCase())) {
						modificationRepos.add(r.getSvn());
					}
				}

				final ModalMail ml = new ModalMail(control);
				getWindow().addWindow(ml.getSubwindow(modificationRepos));

				/*if (control.getUser().isAdmin()
						|| modificationRepos.size() != 0) {
					final ModalMail ml = new ModalMail(control);
					getWindow().addWindow(ml.getSubwindow(modificationRepos));
				} else {
					thisWindow
							.showNotification("You do not have permission to perform this action.");
				}*/
			}

		});

		HorizontalLayout buttonsLayout = new HorizontalLayout();
		buttonsLayout.setHeight(100, UNITS_PERCENTAGE);

		buttonsLayout.addComponent(help);
		buttonsLayout.addComponent(logViewer);
		buttonsLayout.addComponent(locks);
		buttonsLayout.addComponent(clean);
		buttonsLayout.addComponent(mail);
		buttonsLayout.addComponent(clearRights);

		buttonsLayout.setComponentAlignment(help, Alignment.MIDDLE_LEFT);
		buttonsLayout.setComponentAlignment(logViewer, Alignment.MIDDLE_LEFT);
		buttonsLayout.setComponentAlignment(locks, Alignment.MIDDLE_LEFT);
		buttonsLayout.setComponentAlignment(clean, Alignment.MIDDLE_LEFT);
		buttonsLayout.setComponentAlignment(mail, Alignment.MIDDLE_LEFT);
		buttonsLayout.setComponentAlignment(clearRights, Alignment.MIDDLE_LEFT);

		HorizontalLayout usernameWrap = new HorizontalLayout();
		usernameWrap.setHeight(100, UNITS_PERCENTAGE);

		Label userName = new Label("Username: " + control.getUser().getUserName());
		userName.setWidth("100%");
		userName.addStyleName("boldCaption");
		usernameWrap.addComponent(userName);
		usernameWrap.setExpandRatio(userName, 1.0f);
		usernameWrap.setComponentAlignment(userName, Alignment.MIDDLE_CENTER);

		HorizontalLayout logoutWrap = new HorizontalLayout();
		logoutWrap.setHeight(100, UNITS_PERCENTAGE);
		logoutButton = new Button(I18NSupport.getMessage(LOGOUT_BUTTON_CAPTION));
		logoutButton.addListener(new ClickListener() {
			public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
				WebApplicationContext ctx = ((WebApplicationContext) thisWindow
						.getApplication().getContext());
				HttpSession session = ctx.getHttpSession();
				session.invalidate();
				thisWindow.getApplication().close();

			}
		});
		logoutButton.setStyleName("link");
		logoutButton.addStyleName("Button2");
		logoutWrap.addComponent(logoutButton);
		logoutWrap.setWidth("220px");
		logoutWrap.setComponentAlignment(logoutButton, Alignment.MIDDLE_LEFT);

		HorizontalLayout jl = new HorizontalLayout();
		jl.setWidth(99, UNITS_PERCENTAGE);
		jl.setHeight(40, UNITS_PIXELS);
		jl.addStyleName("headerItem");
		jl.setSpacing(false);
		jl.addComponent(buttonsLayout);
		jl.addComponent(usernameWrap);
		jl.addComponent(logoutWrap);

		HorizontalLayout jl2 = new HorizontalLayout();
		jl2.setMargin(false, true, false, false);
		jl2.setHeight(50, UNITS_PIXELS);
		jl2.setWidth(100, UNITS_PERCENTAGE);
		jl2.addComponent(jl);
		jl2.setComponentAlignment(jl, Alignment.MIDDLE_CENTER);

		vert.addComponent(jl2);

	}

	/**
	 * Initializes the right panel.
	 */
	private void initializeRightPanel() {
		rightPanel.addStyleName("marginexample");
		rightPanel.setHeight(100, UNITS_PERCENTAGE);
		rightPanel.setMargin(false, true, false, true);
		rightPanel.setSpacing(true);

		effectiveRightsLabel = new Label(
				I18NSupport.getMessage(EFFECTIVE_RIGHTS_LABEL_NAME));
		effectiveRightsLabel.addStyleName("boldCaption");
		rightPanel.addComponent(effectiveRightsLabel);

		effectiveRightsTable = new EffectiveRightsTable(control);
		rightPanel.addComponent(effectiveRightsTable);

		accessListLabel = new Label(
				I18NSupport.getMessage(ACCESS_LIST_LABEL_NAME));
		accessListLabel.addStyleName("boldCaption");

		rightPanel.addComponent(accessListLabel);

		groupsTable = new GroupsTable(control);
		usersTable = new UsersTable(control);
		inheritance = new InheritanceCheckBox(control);
		inheritance.setImmediate(true);

		rightPanel.addComponent(groupsTable);
		rightPanel.addComponent(usersTable);

		buttons = new ControlButtons(control);

		VerticalLayout hl = new VerticalLayout();

		//CheckBox "disable inheritance" is shown just for Admins
		if(control.getUser().isAdmin()){
			hl.addComponent(inheritance);
			hl.setComponentAlignment(inheritance, Alignment.MIDDLE_LEFT);
		}

		hl.addComponent(buttons);
		hl.setComponentAlignment(buttons, Alignment.MIDDLE_LEFT);
		hl.setWidth(280, UNITS_PIXELS);
		rightPanel.addComponent(hl);
		rightPanel.setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);

		rightPanel.setExpandRatio(effectiveRightsTable, 1);
		rightPanel.setExpandRatio(accessListLabel, 0);
		rightPanel.setExpandRatio(groupsTable, 1);
		rightPanel.setExpandRatio(usersTable, 1);
		// rightPanel.setExpandRatio(inheritance, 0);
		// rightPanel.setExpandRatio(buttons, 0);

		mainPanel.addComponent(rightPanel);
	}

	/**
	 * Initializes the left panel.
	 */
	private void initializeLeftPanel() {
		this.setScrollable(true);
		repositories = new Repositories(control.getRepositories(), this.control);
		leftPanel.addComponent(repositories);

		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth(100, UNITS_PERCENTAGE);
		// hl.setHeight(20,UNITS_PIXELS);
		currentPath = new CurrentPath(control);
		currentPath.setWidth(100, UNITS_PERCENTAGE);
		Button favorites = new Button();
		favorites.setWidth(20, UNITS_PIXELS);
		favorites.setStyleName("link");
		favorites.addStyleName("favorites");
		favorites.setDescription("Add path to favorites");
		favorites.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					String svn = getRepositories().getChosenRepo();
					String fullpath = getCurrentPath().getValue().toString();
					cookies.addUserCookie(svn, fullpath);
					thisWindow.showNotification("Cookies", "Path " + fullpath
							+ " for " + svn + " svn was successfully added.",
							Notification.TYPE_WARNING_MESSAGE);
				} catch (Exception e) {
					thisWindow.showNotification("Cookies",
							"No directory selected.",
							Notification.TYPE_WARNING_MESSAGE);
				}
			}
		});

		Button settings = new Button();
		settings.setWidth(20, UNITS_PIXELS);
		settings.setStyleName("link");
		settings.addStyleName("settings");
		settings.setDescription("Remove favorites");

		settings.addListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {

				ModalCookiesSettings mcs = new ModalCookiesSettings(control);
				Window win2 = mcs.getSubwindow();
				if (win2 != null)
					if (win2.getParent() != null) {
						// window is already showing
						getWindow().showNotification("Window is already open");
					} else {
						// Open the subwindow by adding it to the parent
						// window
						// System.out.println("open window group");
						getWindow().addWindow(win2);
						// win2.focus();
					}
			}
		});

		hl.addComponent(currentPath);
		hl.setExpandRatio(currentPath, 1.0f);
		hl.addComponent(favorites);
		hl.addComponent(settings);
		leftPanel.addComponent(hl);

		directories = new DirectoriesTreeTable(control);
		// directories.setImmediate(true);

		directories.setCellStyleGenerator(new TreeTable.CellStyleGenerator() {
			@Override
			public String getStyle(Object itemId, Object propertyId) {
				if (propertyId == null) {
					// Styling for row
					BeanItem item = (BeanItem) directories.getItem(itemId);
					String svn = getRepositories().getChosenRepo();
					String s = "";
					try {
						s = svn + ":"
								+ item.getItemProperty("fullPath").toString();
					} catch (Exception e) {
						return null;
					}
					boolean locked = Boolean.parseBoolean(item.getItemProperty(
							"locked").toString());

					if (control.getChanges().containsKey(s)) {
						if (locked)
							return "highlight-red-locked";
						else
							return "highlight-red";
					} else {
						for (String key : control.getChanges().keySet()) {

							String[] split = key.split(":");

							if (split[0].equals(svn)
									&& split[1].startsWith(item
											.getItemProperty("fullPath")
											.toString())) {
								if (locked)
									return "highlight-green-locked";
								else
									return "highlight-green";

							}
						}
					}
					if (locked)
						return "highlight-locked";

				}
				return null;
			}
		});
		leftPanel.addComponent(directories);
		leftPanel.setImmediate(true);
		leftPanel.setExpandRatio(repositories, 0);
		leftPanel.setExpandRatio(hl, 0);
		leftPanel.setExpandRatio(directories, 1);
		// leftPanel.setMargin(false, false, false,true);
		mainPanel.addComponent(leftPanel);
	}

	/**
	 * Returns the left panel.
	 *
	 * @return the left panel
	 *
	 */
	public VerticalLayout getLeftPanel() {
		return leftPanel;
	}

	/**
	 * Returns the repositories.
	 *
	 * @return the repositories
	 *
	 */
	public Repositories getRepositories() {
		return repositories;
	}

	/**
	 * Sets the new repositories.
	 *
	 * @param repositories the new repositories
	 *
	 */
	public void setRepositories(Repositories repositories) {
		this.repositories = repositories;
	}

	/**
	 * Returns the current path.
	 *
	 * @return the current path
	 *
	 */
	public CurrentPath getCurrentPath() {
		return currentPath;
	}

	/**
	 * Sets the new current path.
	 *
	 * @param currentPath the new current path
	 *
	 */
	public void setCurrentPath(CurrentPath currentPath) {
		this.currentPath = currentPath;
	}

	/**
	 * Returns the directories table.
	 *
	 * @return the directories table
	 *
	 */
	public DirectoriesTreeTable getDirectories() {
		return directories;
	}

	/**
	 * Sets the new directories table.
	 *
	 * @param directories the new directories table
	 *
	 */
	public void setDirectories(DirectoriesTreeTable directories) {
		this.directories = directories;
	}

	/**
	 * Returns the groups table.
	 *
	 * @return the groups table
	 *
	 */
	public GroupsTable getGroupsTable() {
		return groupsTable;
	}

	/**
	 * Sets the new groups table.
	 *
	 * @param groupsTable the new groups table
	 *
	 */
	public void setGroupsTable(GroupsTable groupsTable) {
		this.groupsTable = groupsTable;
	}
	
	/**
	 * Returns the users table.
	 *
	 * @return the users table
	 *
	 */
	public UsersTable getUsersTable() {
		return usersTable;
	}

	/**
	 * Sets the new users table.
	 *
	 * @param usersTable the new users table
	 *
	 */
	public void setUsersTable(UsersTable usersTable) {
		this.usersTable = usersTable;
	}

	/**
	 * Returns the efective rights table.
	 *
	 * @return the efective rights table
	 *
	 */
	public EffectiveRightsTable getEffectiveRightsTable() {
		return effectiveRightsTable;
	}

	/**
	 * Sets the new efective rights table.
	 *
	 * @param effectiveRightsTable the new efective rights table
	 *
	 */
	public void setEffectiveRightsTable(EffectiveRightsTable effectiveRightsTable) {
		this.effectiveRightsTable = effectiveRightsTable;
	}

	/**
	 * Returns the inheritance checkbox.
	 *
	 * @return the inheritance checkbox
	 *
	 */
	public InheritanceCheckBox getInheritance() {
		return inheritance;
	}

	/**
	 * Returns the control buttons.
	 *
	 * @return the control buttons
	 *
	 */
	public ControlButtons getButtons() {
		return buttons;
	}

	/**
	 * Sets the new control buttons.
	 *
	 * @param buttons the new control buttons
	 *
	 */
	public void setButtons(ControlButtons buttons) {
		this.buttons = buttons;
	}

	/**
	 * Returns the clear rights button.
	 *
	 * @return the clear rights button
	 *
	 */
	public Button getClearRights() {
		return clearRights;
	}
}
