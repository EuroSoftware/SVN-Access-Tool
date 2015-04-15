package com.gk_software.tools.svnaccess.view.components;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.bussiness.IControl;
import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/**
 * Represents the list of all the available repositories.
 */
public class Repositories extends Panel implements Button.ClickListener {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(Repositories.class);

	/** The control class. */
	private IControl control;

	/** The currently selected repository button. */
	private Button chosenRepo = null;

	/** The map with repository as key and its nodes as value. */
	private HashMap<String, Node> reposMap;

	/**
	 * Creates a new instance, sets the variables according to the given values and
	 * initializes all the other variables.
	 *
	 * @param repos the list of all the available repositories
	 * @param control the control class
	 *
	 */
	public Repositories(List<Node> repos, IControl control) {
		super();
		this.control = control;
		reposMap = new HashMap<String, Node>();
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeUndefined();

		this.setWidth(100, UNITS_PERCENTAGE);
		this.setHeight(35, UNITS_PIXELS);
		this.setScrollable(true);
		Button button;
		// here we have to add the right strings when we have Node implemented
		if (repos != null && repos.size() > 0) {
			for (int i = 0; i < repos.size(); i++) {
				Node but = repos.get(i);
				button = new Button(but.getSvn());
				button.setData(but.getSvn());
				button.addListener(this);
				button.addStyleName("link");
				button.addStyleName("repo");
				button.setDescription(but.getRepositoryDescription());
				hl.addComponent(button);
				reposMap.put(but.getSvn(), but);
			}
		}
		this.setContent(hl);
	}

	/**
	 * Sets the new currently selected repository button.
	 *
	 * @param chosenRepo the new currently selected repository button
	 *
	 */
	public void setChosenRepo(Button chosenRepo) {
		this.chosenRepo = chosenRepo;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.vaadin.ui.Button.ClickListener#buttonClick(com.vaadin.ui.Button.
	 * ClickEvent)
	 */
	public void buttonClick(ClickEvent event) {
		// remove style of selected repository
		if (chosenRepo != null) {
			chosenRepo.removeStyleName("selectedRepository");
		}
		chosenRepo = event.getButton();

		// selected repository should be highlighted
		chosenRepo.addStyleName("selectedRepository");
		control.repositorySelected((String) event.getButton().getData());

		checkRepositories(control.getRepositories());
	}

	/**
	 * Returns the name of the currently selected repository.
	 *
	 * @return empty string if no repository is selected otherwise the name of
	 *         the repository.
	 *
	 */
	public String getChosenRepo() {
		if (chosenRepo == null)
			return "";
		String s = (String) chosenRepo.getData();

		return s;
	}

	/**
	 * Returns the currently selected repository as a node.
	 *
	 * @return null if no repository is selected otherwise the Node of the
	 *         repository.
	 *
	 */
	public Node getChosenRepoNode() {
		if (chosenRepo != null) {
			return reposMap.get((String) chosenRepo.getData());
		} else {
			return null;
		}
	}

	/**
	 * Checks whether the given repository exists.
	 *
	 * @param repos the list of all the available repositories
	 * @param svn the searched repository
	 *
	 * @return true if it exists otherwise false
	 *
	 */
	private boolean repositoryExists(List<Node> repos, String svn) {
		if (repos != null && repos.size() > 0) {
			for (int i = 0; i < repos.size(); i++) {
				if (repos.get(i).getSvn().equals(svn)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sets the given repository as the currently selected
	 *
	 * @param repos the list of all the available repositories
	 * @param svn the repository to be set as currently selected
	 *
	 */
	public String setChosenRepo(List<Node> repos, String svn) {
		if (repositoryExists(repos, svn)) {
			this.removeAllComponents();
			reposMap = new HashMap<String, Node>();
			HorizontalLayout hl = new HorizontalLayout();
			hl.setSizeUndefined();
			Button button;
			Label label;
			// here we have to add the right strings when we have Node
			// implemented
			if (repos != null && repos.size() > 0) {
				for (int i = 0; i < repos.size(); i++) {
					button = new Button(repos.get(i).getSvn());
					button.setData(repos.get(i).getSvn());
					button.addListener(this);
					button.addStyleName("link");
					button.addStyleName("repo");
					button.setDescription(repos.get(i)
							.getRepositoryDescription());
					hl.addComponent(button);
					reposMap.put(repos.get(i).getSvn(), repos.get(i));
					if (repos.get(i).getSvn().equals(svn)) {
						chosenRepo = button;
						chosenRepo.addStyleName("selectedRepository");
						button.focus();
					}
				}
			}
			this.setContent(hl);
			return null;
		}
		return "SVN: " + svn + " does not exist.";
	}

	/**
	 * Fills the repository list with the given list of repositories.
	 *
	 * @param repos the list repositories which are added to the repository list
	 *
	 */
	public void updateRepositories(List<Node> repos) {
		// System.out.println("Reloading");
		String chosenRepoSVN = null;
		// getting last chosen SVN
		if (chosenRepo != null) {
			chosenRepoSVN = (String) chosenRepo.getData();
			// chosenRepoSVN.addStyleName("selectedRepository");
		}
		this.removeAllComponents();

		reposMap = new HashMap<String, Node>();
		HorizontalLayout hl = new HorizontalLayout();
		hl.setSizeUndefined();
		Button button;
		Label label;
		// here we have to add the right strings when we have Node
		// implemented
		if (repos != null && repos.size() > 0) {
			for (int i = 0; i < repos.size(); i++) {
				button = new Button(repos.get(i).getSvn());
				button.setData(repos.get(i).getSvn());
				button.addListener(this);
				button.addStyleName("link");
				button.addStyleName("repo");
				button.setDescription(repos.get(i).getRepositoryDescription());
				hl.addComponent(button);
				reposMap.put(repos.get(i).getSvn(), repos.get(i));
				if (repos.get(i).getSvn().equals(chosenRepoSVN)) {
					chosenRepo = button;
					button.focus();
					chosenRepo.addStyleName("selectedRepository");
				}
				// if (i < repos.size() - 1) {
				// label = new Label(" | ");
				// this.addComponent(label);
				// }
			}
		}
		this.setContent(hl);
		// } else {
		// System.out.println("No need to reload");
		// }
	}

	/**
	 * Checks if all the repositories in the given list physically exists on the disk
	 * and reloads them if they do not.
	 *
	 * @param repos the list of all the available repositories
	 *
	 */
	public void checkRepositories(List<Node> repos) {
		if (ifRepoChanged(repos, reposMap)) {
			logger.info("Reloading SVN repositories in app.");
			String chosenRepoSVN = null;
			// getting last chosen SVN
			if (chosenRepo != null) {
				chosenRepoSVN = (String) chosenRepo.getData();
				// chosenRepoSVN.addStyleName("selectedRepository");
			}
			this.removeAllComponents();

			reposMap = new HashMap<String, Node>();
			HorizontalLayout hl = new HorizontalLayout();
			hl.setSizeUndefined();
			Button button;
			Label label;
			// here we have to add the right strings when we have Node
			// implemented
			if (repos != null && repos.size() > 0) {
				for (int i = 0; i < repos.size(); i++) {
					button = new Button(repos.get(i).getSvn());
					button.setData(repos.get(i).getSvn());
					button.addListener(this);
					button.addStyleName("link");
					button.addStyleName("repo");
					button.setDescription(repos.get(i)
							.getRepositoryDescription());
					hl.addComponent(button);
					reposMap.put(repos.get(i).getSvn(), repos.get(i));
					if (repos.get(i).getSvn().equals(chosenRepoSVN)) {
						chosenRepo = button;
						button.focus();
						chosenRepo.addStyleName("selectedRepository");
					}
				}
			}
			this.setContent(hl);
		} else {
			logger.info("There are no changes in repositories, no need to reload");
		}
	}

	/**
	 * Checks if the repositories in the given list were changed
	 *
	 * @param repos the list of all the available repositories
	 * @param reposMap2 the list of the currently loaded repositories
	 *
	 * @return true if there are changes otherwise false
	 *
	 */
	private boolean ifRepoChanged(List<Node> repos,
			HashMap<String, Node> reposMap2) {

		String reposA = "";
		for (int i = 0; i < repos.size(); i++) {
			reposA += repos.get(i).getSvn() + ", ";
		}
		// logger.info("Loaded repos: " + reposA);
		// logger.info("Repos on a disk: " + reposMap2.keySet());

		if (repos.size() != reposMap2.size()) {
			logger.info("There are changes in amount of repositories");
			return true; // different size, reloading
		}
		for (int i = 0; i < repos.size(); i++) {
			Node n = repos.get(i);
			String repoName = n.getSvn();
			// logger.info("Read repository from a disk: " + repoName);
			if (!reposMap2.containsKey(repoName)) {
				logger.info("Repository " + repoName + " is not loaded.");
				return true;
			}
		}

		return false;
	}

	/**
	 * Updates the timestamps of all the repositories in the given list.
	 *
	 * @param repos the list of all the available repositories
	 *
	 */
	public void updateRepositoryTimestamps(List<Node> repos) {
		if (repos != null && repos.size() > 0) {
			for (int i = 0; i < repos.size(); i++) {
				Node svn = repos.get(i);
				reposMap.get(svn.getSvn()).setTimestamp(svn.getTimestamp());
			}
		}
	}
}
