package com.gk_software.tools.svnaccess.theme;

import com.gk_software.core.client.web_vaadin.component.theme.DefaultTheme;

/**
 * Represents the Vaadin theme for the SVN Access Tool.
 */
public class SvnAccessTheme extends DefaultTheme {

	/** The serial id. */
	private static final long serialVersionUID = 7727522593316914766L;

	/** The name of the theme. */
	public static final String THEME_NAME = "com.gk-software.tools.svnaccess";

	/**
	 * Returns the name of the theme.
	 *
	 * @return the name of the theme
	 *
	 */
	@Override
	public String getName() {
		return THEME_NAME;
	}

}
