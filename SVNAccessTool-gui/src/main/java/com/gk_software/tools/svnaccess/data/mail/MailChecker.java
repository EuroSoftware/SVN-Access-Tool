package com.gk_software.tools.svnaccess.data.mail;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.gk_software.tools.svnaccess.utils.Constants;

/**
 * Represents a thread that periodically sends the mails added to it.
 */
public class MailChecker extends Thread {
	
	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(MailChecker.class);
	
	/** The indicator whether the thread has been interupted. */
	private boolean interupt = false;

	/**
	 * Returns the indicator whether the thread has been interupted.
	 *
	 * @return the indicator whether the thread has been interupted
	 *
	 */
	public boolean isInterupt() {
		return interupt;
	}

	/**
	 * Sets the new indicator whether the thread has been interupted.
	 *
	 * @param interupt the new indicator whether the thread has been interupted
	 *
	 */
	public void setInterupt(boolean interupt) {
		this.interupt = interupt;
	}

	/** The lock object for thread synchronization. */
	private Object lock = new Object();
	
	/**
	 * Returns the lock object for thread synchronization.
	 *
	 * @return the lock object for thread synchronization
	 *
	 */
	public Object getLock() {
		return lock;
	}

	/** The instance of this class. */
	private static MailChecker mailChecker;
	
	/** The indicator whether the checker is ready to commit. */
	private boolean readyToCommit = true;
	
	/** The mail notification. */
	private MailNotification mailNotification = null;
	
	/** The mail writer. */
	private MailWriter mailWriter = null;

	/**
	 * Returns the mail writer.
	 *
	 * @return the mail writer
	 *
	 */
	public MailWriter getMailWriter() {
		return mailWriter;
	}

	/**
	 * Sets the new mail writer.
	 *
	 * @param mailWriter the new mail writer
	 *
	 */
	public void setMailWriter(MailWriter mailWriter) {
		this.mailWriter = mailWriter;
	}

	/**
	 * Creates a new instance and initializes the variables.
	 */
	private MailChecker() {
		this.mailWriter = new MailWriter();
		this.mailNotification = new MailNotification();
	}

	/**
	 * Singleton class. This method provides only one instance of this class.
	 *
	 * @return the instance of this class
	 *
	 */
	public static MailChecker getInstance() {
		if (mailChecker == null) {
			mailChecker = new MailChecker();
		}
		return mailChecker;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public synchronized void run() {
		while (true) {
			synchronized (this) {
				try {
					logger.info("wait properties time");
					this.wait(Integer.parseInt(MailProperties.getProperties()
							.getProperty("mail_check")) * 60000);
//					this.wait(Integer.parseInt(MailProperties.getProperties()
//							.getProperty("mail_check")));
				} catch (InterruptedException e) {
					logger.error("Interrupted Exception.");
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String stacktrace = sw.toString();
					logger.error(stacktrace);
				}
			}

			synchronized (this) {
				// cekej dokud neni ulozeni dokonceno (ready to commit)
				while (!readyToCommit)
					try {
						logger.info("wait until ready to commit");
						this.wait(10000);

					} catch (InterruptedException e) {
						logger.error("Interrupted Exception.");
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						String stacktrace = sw.toString();
						logger.error(stacktrace);
					}
			}

			synchronized (this) {
				if (interupt) {
					logger.info("interupted - possible cause: disable mailing in settings");
					try {
						this.wait();
					} catch (InterruptedException e) {
						logger.error("Interrupted Exception.");
						StringWriter sw = new StringWriter();
						e.printStackTrace(new PrintWriter(sw));
						String stacktrace = sw.toString();
						logger.error(stacktrace);
					}
				}

			}
			logger.info("Sending mails");
			
			//Vysledky odeslaní jednotlivých souborů <JmenoSoubotu, Boolean>
			Map<String, Boolean> sentMails = commit();
			
			//Adresy, na které se povedlo odeslat maily.
			List<String> sentMailsAdreses = new LinkedList<String>();
			
			if (sentMails.containsValue(true)) {
				
				for (Entry<String, Boolean> mail : sentMails.entrySet()) {
					if(mail.getValue()==true){
						sentMailsAdreses.add(mail.getKey());
					}
				}
				logger.info("Mails to "+ sentMailsAdreses +" succesfully send");
				mailWriter.clearMessages(sentMailsAdreses);
			} 
			if (sentMails.containsValue(false)){
				//Adresy, na které se nepodařilo odeslat maily (všechny - odeslane)
				List<String> unsentMailsAdreses = new LinkedList<String>(sentMails.keySet());
				unsentMailsAdreses.removeAll(sentMailsAdreses);
				
				logger.info("Mails to "+unsentMailsAdreses+" unsuccesfully send");
			}
		}
	}

	/**
	 * Returns the indicator whether the checker is ready to commit.
	 *
	 * @return the indicator whether the checker is ready to commit
	 *
	 */
	public boolean isReadyToCommit() {
		return readyToCommit;
	}

	/**
	 * Sets the new indicator whether the checker is ready to commit.
	 *
	 * @param readyToCommit the new indicator whether the checker is ready to commit
	 *
	 */
	public void setReadyToCommit(boolean readyToCommit) {
		this.readyToCommit = readyToCommit;
	}

	/**
	 * Notifies this thread.
	 */
	public void notifyMy() {
		synchronized (this) {
			this.notify();
		}
	}

/*	private boolean commit() {
		synchronized (lock) {
			String path = Constants.getProperties().getProperty("mail_folder");
			boolean status = true;
			File f = new File(path);
			for (File file : f.listFiles()) {
				if (file.isFile()
						&& !file.getName().equals("properties.properties")) {
					try {
						status = mailNotification.sendMail(file.getName());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
			return status;
		}
	}
*/	
	
	/**
	 * Sends the e-mails from the mail folder.
	 * 
	 * @return a map containing e-mail name as key and its send status as value
	 * 
	 */
	private Map<String, Boolean> commit() {
		Map<String, Boolean> sentMails = new HashMap<String, Boolean>();
		synchronized (lock) {
			String path = Constants.getProperties().getProperty("mail_folder");
			boolean status = true;
			File f = new File(path);
			for (File file : f.listFiles()) {
				if (file.isFile()
						&& !file.getName().equals("properties.properties")) {
					try {
						status = mailNotification.sendMail(file.getName());
						if (status) {
							sentMails.put(file.getName(), true);
						} else {
							sentMails.put(file.getName(), false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			}
			return sentMails;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#interrupt()
	 */
	public void interrupt() {
		this.interupt = true;
	}
}
