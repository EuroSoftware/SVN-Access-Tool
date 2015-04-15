package com.gk_software.tools.svnaccess.view.components;

import java.io.File;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.gk_software.tools.svnaccess.data.ldap.LDAPSettings;
import com.gk_software.tools.svnaccess.data.ldap.Server;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.LoginForm;

/**
 * The login form.
 */
public class CustomLoginForm extends LoginForm {

	/** The username caption. */
	String usernameCaption;

	/** The password caption. */
	String passwordCaption;

	/** The submit caption. */
	String submitCaption;

	/**
	 * Creates a new instance and sets the variables according to the given values.
	 *
	 * @param usernameCaption the username caption
	 * @param passwordCaption the password caption
	 * @param submitCaption the submit caption
	 *
	 */
	public CustomLoginForm(String usernameCaption, String passwordCaption,
			String submitCaption) {
		this.usernameCaption = usernameCaption;
		this.passwordCaption = passwordCaption;
		this.submitCaption = submitCaption;
	}

	/**
	 * Returns the HTML code array of the login form.
	 *
	 * @return the HTML code array of the login form
	 *
	 */
	@Override
	protected byte[] getLoginHTML() {
		// Application URI needed for submitting form
		String appUri = getApplication().getURL().toString()
				+ getWindow().getName() + "/";

		String x, h, b; // XML header, HTML head and body

		x = "<!DOCTYPE html PUBLIC \"-//W3C//DTD "
				+ "XHTML 1.0 Transitional//EN\" " + "\"http://www.w3.org/TR/xhtml1/"
				+ "DTD/xhtml1-transitional.dtd\">\n";

		h = "<head><script type='text/javascript'>"
				+ "var setTarget = function() {" + "  var uri = '"
				+ appUri
				+ "loginHandler';"
				+ "  var f = document.getElementById('loginf');"
				+ "  document.forms[0].action = uri;"
				+ "  document.forms[0].username.focus();"
				+ "};"
				+ ""
				+ "var styles = window.parent.document.styleSheets;"
				+ "for(var j = 0; j < styles.length; j++) {\n"
				+ "  if(styles[j].href) {"
				+ "    var stylesheet = document.createElement('link');\n"
				+ "    stylesheet.setAttribute('rel', 'stylesheet');\n"
				+ "    stylesheet.setAttribute('type', 'text/css');\n"
				+ "    stylesheet.setAttribute('href', styles[j].href);\n"
				+ "    document.getElementsByTagName('head')[0]"
				+ "                .appendChild(stylesheet);\n"
				+ "  }"
				+ "}\n"
				+ "function submitOnEnter(e) {"
				+ "  var keycode = e.keyCode || e.which;"
				+ "  if (keycode == 13) {document.forms[0].submit();}"
				+ "}\n"
				+ "</script>" + "</head>";

		b = "<body onload='setTarget();'"
				+ "  style='margin:20;padding:20; background:transparent;'"
				+ "  class='"
				+ ApplicationConnection.GENERATED_BODY_CLASSNAME
				+ "'>  <div id='content'>"

				+ "<div class='v-app v-app-loginpage'"
				+ "     style='background:transparent;'>"
				+ "<iframe name='logintarget' style='width:0;height:0;"
				+ "border:0;margin:20;padding:20;'></iframe>"
				+ "<form id='loginf' target='logintarget'"
				+ "      onkeypress='submitOnEnter(event)'"
				+ "      method='post'>"
				+ "<table class='loginItem2'>"
				+ "<tr><td class='buttonText'>"
				+ usernameCaption
				+ "</td>"
				+ "<td><input class='v-textfield' style='display:block;'"
				+ "           type='text' name='username'></td></tr>"
				+ "<tr><td class='buttonText'>"
				+ passwordCaption
				+ "</td>"
				+ "    <td><input class='v-textfield'"
				+ "          style='display:block;' type='password'"
				+ "          name='password'></td></tr>"
				+ "<tr><td class='buttonText'>"
				+ "Domain:"
				+ "</td>"
				+ "    <td> <select style='display:block;' name='domena'>"
				//+ "<option value='GK-DOMAIN'>GK-DOMAIN<option value='HKR.LOCAL'>HKR.LOCAL"
				+ getAvailableDomains()
				+ "</select>"
				+ "</td></tr></table>"

				+ "<div id='center_div'>"
				+ "<div onclick='document.forms[0].submit();'"
				+ "     tabindex='0' role='button' >"
				+ "<span class='loginButton'>"
				+ submitCaption
				+ "</span></div></div>"
				+ ""
				+ "</form></div></div></body>";
		return (x + "<html>" + h + b + "</html>").getBytes();
	}

	/**
	 * Returns the string containing the option values for the domain combobox.
	 *
	 * @return the string containing the option values for the domain combobox
	 *
	 */
	private String getAvailableDomains() {
		ServletContext context = ((WebApplicationContext) this.getApplication()
				.getContext()).getHttpSession().getServletContext();

		JAXBContext jc;
		LDAPSettings ls = null;

		File LDAPSettingsFile = new File(context.getInitParameter("ldapsettings"));

		try {
			jc = JAXBContext.newInstance(LDAPSettings.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			ls = (LDAPSettings) unmarshaller.unmarshal(LDAPSettingsFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		String result = "";
		for (Server server : ls.getServer()) {
			result += "<option value='";
			result += server.getDomainName().toUpperCase();
			result += "'>";
			result += server.getDomainName().toUpperCase();
		}

		return result;

	}
}
