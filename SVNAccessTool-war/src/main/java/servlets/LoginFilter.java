package servlets;


import java.io.IOException;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;


@WebFilter("/LogReader/*")
public class LoginFilter implements Filter {

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(LoginFilter.class);

	/**
	 * Initializes the filter with the given configuration.
	 *
	 * @param config the filter configuration
	 *
	 */
	public void init(FilterConfig config) throws ServletException {
		// If you have any <init-param> in web.xml, then you could get them
		// here by config.getInitParameter("name") and assign it as field.
	}

	/**
	 * Filters the users trying to access the assigned pages.
	 *
	 * @param request the client request
	 * @param response the server response
	 * @param chain the filter chain
	 *
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("user") == null) {
			// No logged-in user found, so redirect to login page.
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/WEB-INF/jsp/error.jsp");
			request.setAttribute("error",
					"You have no rights to see this content!");
			logger.info("User has no rights to see log viewer");
			dispatcher.forward(request, response);
			// response.sendRedirect(request.getContextPath() + "/error");
		} else {
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			Date date = new Date();
			List<String> modificationRepos = new ArrayList<String>(
					(Set<String>) session.getAttribute("modificationRepos"));
			Collator collator = Collator.getInstance(new Locale("Czech"));
			Collections.sort(modificationRepos, collator);

			request.setAttribute("from", dateFormat.format(date));
			request.setAttribute("to", dateFormat.format(date));
			request.setAttribute("repository", modificationRepos);
			request.setAttribute("daysAgo", 10);
			RequestDispatcher dispatcher = request
					.getRequestDispatcher("/WEB-INF/jsp/index.jsp");
			dispatcher.forward(request, response);

			// chain.doFilter(req, res); // Logged-in user found, so just
			// continue request.
		}
	}

	/**
	 * Destroys the user resources.
	 */
	public void destroy() {
		// If you have assigned any expensive resources as field of
		// this Filter class, then you could clean/close them here.
	}
}
