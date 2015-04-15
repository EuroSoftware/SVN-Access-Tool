package com.gk_software.tools.svnaccess.bussiness.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.gk_software.core.client.web_vaadin.widget.BrowserCookies;
import com.gk_software.core.client.web_vaadin.widget.BrowserCookies.BrowserCookiesListener;
import com.gk_software.core.client.web_vaadin.widget.BrowserCookies.CookiesLoadedEvent;
import com.gk_software.tools.svnaccess.utils.Constants;
import com.gk_software.tools.svnaccess.view.components.FavoriteBean;

/**
 * Provides methods to store and manage cookies.
 */
public class CookieFactory {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(CookieFactory.class);

	/** The cookies taken from the browser. */
	BrowserCookies browserCookies;

	/**
	 * Creates a new instance and initializes the variables.
	 */
	public CookieFactory() {
		browserCookies = new BrowserCookies();
		browserCookies.setImmediate(true);

		browserCookies.loadCookies();
		browserCookies.attach();

		browserCookies.addListener(new BrowserCookiesListener() {
			private static final long serialVersionUID = -2905155218365650739L;

			public void cookiesLoaded(CookiesLoadedEvent event) {
				logger.info("Cookies load from client side.");
			}
		});
	}

	/**
	 * Adds cookie for the last visited node in a treeTable. This cookie is used for
	 * expanding the path from the previous login.
	 *
	 * @param name the name of the cookie
	 * @param value the value of the cookie
	 *
	 */
	public void addLastViewPath(String name, String value) {
		Date expires = new Date(System.currentTimeMillis()
				+ Long.parseLong(Constants.getProperties().getProperty(
						"cookieExp")) * 60 * 60 * 1000);
		browserCookies.setCookie(name, value, expires);
		logger.info("Add cookie: svn: " + name + " expiration: " + expires);
		logger.info("Add cookie: fullpath: " + value + " expiration: "
				+ expires);
	}

	/**
	 * Adds a user cookie for the selected node to the favorites path.
	 *
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 */
	public void addUserCookie(String svn, String fullpath) {
		Date expires = new Date(System.currentTimeMillis()
				+ Long.parseLong(Constants.getProperties().getProperty(
						"cookieExp")) * 60 * 60 * 1000);
		String name = "FAV##" + svn + "##" + fullpath;

//		Map<String, String> cookies = new HashMap<String, String>(
//				browserCookies.getCookies());

		browserCookies.setCookie(name,
				Long.toString(System.currentTimeMillis()), expires);
		logger.info("Add user cookie: " + name + ", value: "
				+ System.currentTimeMillis() + ", expiration: " + expires);
	}

	/**
	 * Adds an automatic cookie after saving. If the cookie already exists the count
	 * (value of the cookie) is raised.
	 *
	 * @param svn the path to the repository
	 * @param fullpath the path to the node in the repository
	 *
	 */
	public void addSaveCookie(String svn, String fullpath) {
		Date expires = new Date(System.currentTimeMillis()
				+ Long.parseLong(Constants.getProperties().getProperty(
						"cookieExp")) * 60 * 60 * 1000);
		String name = "AUT##" + svn + "##" + fullpath;

		Map<String, String> cookies = new HashMap<String, String>(
				browserCookies.getCookies());
		int usage = 1;
		if (cookies.containsKey(name)) {
			usage = Integer.parseInt(cookies.get(name)) + 1;
		}
		browserCookies.setCookie(name, Integer.toString(usage), expires);
		logger.info("Add save cookie: " + name + ", value: " + usage
				+ ", expiration: " + expires);
	}

	/**
	 * Returns the automatically added cookie.
	 *
	 * @return the map of the cookies sorted by the count
	 *
	 */
	public Map<String, Integer> getSaveCookies() {
		Map<String, String> cookies = new HashMap<String, String>(
				browserCookies.getCookies());

		Map<String, Integer> cookiesFiltered = new HashMap<String, Integer>();

		for (String cookieName : cookies.keySet()) {
			if (cookieName.startsWith("AUT")) {
				cookiesFiltered.put(cookieName,
						Integer.parseInt(cookies.get(cookieName)));
			}
		}
		return sortByValueInt(cookiesFiltered);
	}

	/**
	 * Returns the cookies of the logged user.
	 *
	 * @return the map of the cookies sorted by add date
	 *
	 */
	public Map<String, Long> getUserCookies() {
		Map<String, String> cookies = new HashMap<String, String>(
				browserCookies.getCookies());

		Map<String, Long> cookiesFiltered = new HashMap<String, Long>();

		for (String cookieName : cookies.keySet()) {
			if (cookieName.startsWith("FAV")) {
				cookiesFiltered.put(cookieName,
						Long.parseLong(cookies.get(cookieName)));
			}
		}
		return sortByValueLong(cookiesFiltered);
	}

	/**
	 * Removes the given cookie of the logged user.
	 *
	 * @param f the cookie to be removed
	 *
	 */
	public void removeUserCookie(FavoriteBean f) {
		String cookieName = "FAV##" + f.getSvn() + "##" + f.getFullpath();
		browserCookies.removeCookie(cookieName);
		logger.info("Remove user cookie: " + cookieName);

	}

	/**
	 * Removes the given automatically saved cookie.
	 *
	 * @param f the cookie to be removed
	 *
	 */
	public void removeSaveCookie(FavoriteBean f) {
		String cookieName = "AUT##" + f.getSvn() + "##" + f.getFullpath();
		browserCookies.removeCookie(cookieName);
		logger.info("Remove save cookie: " + cookieName);
	}

	/**
	 * Sorts the given map by timestamps.
	 *
	 * @param map the map to be sorted
	 *
	 * @return the sorted map
	 *
	 */
	private Map<String, Long> sortByValueLong(Map<String, Long> map) {
		List<Entry<String, Long>> list = new LinkedList<Entry<String, Long>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Long>>() {
			@Override
			public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<String, Long> result = new LinkedHashMap<String, Long>();
		for (Map.Entry<String, Long> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * Sorts the given map by usage.
	 *
	 * @param map the map to be sorted
	 *
	 * @return the sorted map
	 *
	 */
	private Map<String, Integer> sortByValueInt(Map<String, Integer> map) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(
				map.entrySet());
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});

		Map<String, Integer> result = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}

	/**
	 * Returns the last viewed svn
	 *
	 * @return the last viewed svn
	 *
	 */
	public String getLastViewSvn() {
		Map<String, String> cookies = new HashMap<String, String>(
				browserCookies.getCookies());
		logger.info("Cookie SVN retrived from client side");
		return cookies.get("svn");
	}

	/**
	 * Returns the last viewed path in the svn
	 *
	 * @return the last viewed path in the svn
	 *
	 */
	public String getlastViewPath() {
		Map<String, String> cookies = new HashMap<String, String>(
				browserCookies.getCookies());
		logger.info("Cookie Fullpath retrived from client side");
		return cookies.get("fullpath");
	}

	/**
	 * Sets the new browser cookie.
	 *
	 * @param browserCookies the new browser cookie
	 *
	 */
	public void setBrowserCookies(BrowserCookies browserCookies) {
		this.browserCookies = browserCookies;
	}

	/**
	 * Returns the browser cookie
	 *
	 * @return the browser cookie
	 *
	 */
	public BrowserCookies getBrowserCookies() {
		return browserCookies;
	}
}