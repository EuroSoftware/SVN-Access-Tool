package servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Shows the main page.
 */
public class Servlet extends HttpServlet {

	/** The serial id. */
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Servlet() {
		super();
	}

	/**
	 * Forwards the client to the main page.
	 *
	 * @param request the client request
	 * @param response the server response
	 *
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RequestDispatcher dispatcher = request
				.getRequestDispatcher("/WEB-INF/jsp/index.jsp");
		dispatcher.forward(request, response);
	}

	/**
	 * Redirects the client to the log reader page.
	 *
	 * @param request the client request
	 * @param response the server response
	 *
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect(request.getAttribute("baseUrl") + "ridici");
	}

	/**
	 * Initializes the servlet with the given configuration.
	 *
	 * @param config the servlet configuration
	 *
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
}
