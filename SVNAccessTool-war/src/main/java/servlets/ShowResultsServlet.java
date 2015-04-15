package servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import parser.Parser;

/**
 * Shows the results page for the log search.
 */
public class ShowResultsServlet extends HttpServlet {

	/** The serial id. */
	private static final long serialVersionUID = 1L;

	/** The log parser. */
	private Parser parser;

	/** The instance of the {@code Logger} class. */
	private static final Logger logger = Logger.getLogger(ShowResultsServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ShowResultsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * Parses the logs with the given parameters and redirects the user to the results
	 * page.
	 *
	 * @param request the client request
	 * @param response the server response
	 *
	 * @throws ServletException, IOException processing failed
	 *
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String errorMes = "";
		try {
			String from = request.getParameter("from");
			String to = request.getParameter("to");
			String search = request.getParameter("search");
			String radio = request.getParameter("radio_button");
			String daysAgo = request.getParameter("daysAgo");
			String repos = request.getParameter("repos");

			HttpSession session = request.getSession(false);

			Set<String> modificationRepos = null;

			if (repos.equals("#allRepos")) {
				modificationRepos = (Set<String>) session
						.getAttribute("modificationRepos");
			}

			logger.info(from + " " + to + " control");

			Logger lo = Logger.getLogger("ACL_changes");
			FileAppender f = (FileAppender) lo.getAppender("aclLogger");

			if (radio.equals("from"))
				parser = new Parser(f.getFile(), from, to, search);
			else
				parser = new Parser(f.getFile(), from, to, search,
						Integer.parseInt(daysAgo));
			parser.setRadio(radio);
			parser.setRepo(repos);
			if (modificationRepos != null)
				parser.setRepos(modificationRepos);

			parser.setAdmin(Boolean.parseBoolean(session
					.getAttribute("isAdmin").toString()));
			parser.reader();
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			request.setAttribute("from", dateFormat.format(parser.getFromD()));
			request.setAttribute("to", dateFormat.format(parser.getToD()));
			request.setAttribute("logs", parser.getLogs());

			RequestDispatcher rd = request
					.getRequestDispatcher("WEB-INF/jsp/results.jsp");
			rd.forward(request, response);
		} catch (IOException e) {
			logger.error(e);
			errorMes += e;
		} catch (ParseException e) {
			logger.error("Not completely/incorrect filled form. Check the values. ");
			logger.error(e);
			errorMes += "Not completely/incorrect filled form. Check the values. ";
		} catch (NumberFormatException e) {
			logger.error("Not completely/incorrect filled form. Check the values. ");
			logger.error(e);
			errorMes += "Not completely/incorrect filled form. Check the values. ";
		}catch(NullPointerException e){
			logger.error("You are probably not logged anymore.");
			logger.error(e);
			errorMes += "You are probably not logged anymore. ";

		}
//		catch (EmptySessionException e) {
//			e.printStackTrace();
//			logger.error("Empty session exception, you are not logged in.");
//			errorMes += "Empty session exception, you are not logged in.";
//		}
		if (errorMes.length() != 0) {
			request.setAttribute("error", errorMes);
			RequestDispatcher rd = request
					.getRequestDispatcher("WEB-INF/jsp/error.jsp");
			rd.forward(request, response);
		}
	}

//	class EmptySessionException extends Exception {
//
//	}

}
