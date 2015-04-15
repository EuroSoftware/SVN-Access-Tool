package com.gk_software.tools.svnaccess.data.ldap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.data.accessList.Node;
import com.gk_software.tools.svnaccess.data.accessList.ViewInformation;
import com.gk_software.tools.svnaccess.data.mail.MailChecker;
import com.gk_software.tools.svnaccess.data.mail.MailWriter;
import com.gk_software.tools.svnaccess.utils.Constants;


/**
 * Controls changes in LDAP and if some change happens sends a mail.
 */
public class LDAPChangeControl extends Thread{

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(LDAPChangeControl.class);

	/** The instance of this class. */
	private static LDAPChangeControl ldapChangeControl;

	/** The group rights. */
	private String groupsRigrhts = "";

	/**
	 * Creates a new instance.
	 */
	private LDAPChangeControl(){}

	/**
	 * Singleton class. This method provides only one instance of this class.
	 *
	 * @return the instance of this class
	 *
	 */
	public static LDAPChangeControl getInstance(){
		if(ldapChangeControl == null){
			return new LDAPChangeControl();
		} else {
			return ldapChangeControl;
		}
	}

	/**
	 * Adds the rights of the given group for the given list of repos.
	 *
	 * @param group the group whose rights we search
	 * @param repos the list of repositories
	 *
	 */
	private void rightsOfGroup(String group, List<Node> repos){
		try {
			for (Node node : repos) {
				rightsOfGroup(group, node.getChildren());

				if (node.getGroups().containsKey(group)) {
					groupsRigrhts+="\tIn directory: "+node.getFullPath()+ " with rights: " + node.getGroups().get(group)+"\n";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the rights of the given group for the given list of repos and returns the
	 * result.
	 *
	 * @param group group the group whose rights we search
	 * @param repos the list of repositories
	 *
	 * @return the
	 *
	 */
	private String rightsOfGroupWrap(String group, List<Node> repos){
		groupsRigrhts = "";
		group="@"+group;
		rightsOfGroup(group, repos);

		if(groupsRigrhts.length()==0){
			return null;
		}

		return groupsRigrhts;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Thread#run()
	 */
	@Override
	public synchronized void run(){

		LdapProvider.getInstance().reloadData();
		Map<String, Set<String>> usersOld = LdapProvider.getInstance().getGroupsUsers();
		Map<String, Set<String>> usersActual;


		while(true){
			synchronized (this) {
				try {
					this.wait(Integer.parseInt(Constants.getProperties()
							.getProperty("LDAPChangeControlTimer").toString()));

					LdapProvider.getInstance().reloadData();
					usersActual = LdapProvider.getInstance().getGroupsUsers();

					logger.info("LDAP change control started.");
					if(usersOld.equals(usersActual)){
					} else{

						//e.g.: DFS_Planung_KT_Spec_GUI=[hkirchhoff@GK-DOMAIN, aclaussner@GK-DOMAIN, kwilhelm@GK-DOMAIN]
						for (Entry<String, Set<String>> element : LdapProvider.getInstance().getGroupsUsers().entrySet()) {
							if(usersOld.containsKey(element.getKey())&&usersActual.containsKey(element.getKey())){
								//both maps have same groups with the same content?
								if(!usersOld.get(element.getKey()).equals(usersActual.get(element.getKey()))){
									//if not check if the new group has all the old users
									for (String user : usersOld.get(element.getKey())) {
										if(!usersActual.get(element.getKey()).contains(user)){
											logger.info("FROM GROUP: "+element.getKey()+" DELETED: "+user);
											MailChecker mailChecker = MailChecker.getInstance();

											String rights = "";
											try {
												List<Node> repos = ViewInformation.getInstance().getRepositories();
												rights = rightsOfGroupWrap(element.getKey(), repos);
											} catch (Exception e) {
												e.printStackTrace();
											}

											if(rights != null){
												synchronized (mailChecker.getLock()) {
													mailChecker.getMailWriter().appendMessage(user, MailWriter.REMOVED_LDAP, element.getKey(), rights);
												}
											}
										}
									}
									//and if it has some new
									for (String user : usersActual.get(element.getKey())) {
										if(!usersOld.get(element.getKey()).contains(user)){
											logger.info("TO GROUP: "+element.getKey()+" ADDED: "+user);

											MailChecker mailChecker = MailChecker.getInstance();

											String rights = "";
											try {
												List<Node> repos = ViewInformation.getInstance().getRepositories();
												rights = rightsOfGroupWrap(element.getKey(), repos);
											} catch (Exception e) {
												e.printStackTrace();
											}
											if (rights != null) {
												synchronized (mailChecker.getLock()) {
													mailChecker.getMailWriter().appendMessage(user, MailWriter.ADDED_LDAP, element.getKey(), rights);
												}
											}
										}
									}
								}
							}
						}
						usersOld = usersActual;
					}

				} catch (InterruptedException e) {
					logger.error("Interrupted Exception.");
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String stacktrace = sw.toString();
					logger.error(stacktrace);
					e.printStackTrace();
				}
			}
		}
	}
}
