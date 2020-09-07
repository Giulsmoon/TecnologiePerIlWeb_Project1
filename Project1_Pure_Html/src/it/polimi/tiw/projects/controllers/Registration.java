package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.projects.dao.RegistrationDAO;

/**
 * Servlet implementation class Registration
 */
@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Registration() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		try {
			ServletContext context = getServletContext();
			String driver = context.getInitParameter("dbDriver");
			String url = context.getInitParameter("dbUrl");
			String user = context.getInitParameter("dbUser");
			String password = context.getInitParameter("dbPassword");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			throw new UnavailableException("Can't load database driver");
		} catch (SQLException e) {
			throw new UnavailableException("Couldn't get db connection");
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String passwordReinserted = request.getParameter("passwordReinserted");
		String stringError = "";
		boolean usernameMatch;
		boolean passwordMatch;
		boolean passwordReinsertedMatch;

		if (username == null || password == null || passwordReinserted == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter in registration creation");
			return;
		}
		usernameMatch = username.matches("[a-zA-Z0-9]*");
		passwordMatch = password.matches("[a-zA-Z0-9]*");
		passwordReinsertedMatch = passwordReinserted.matches("[a-zA-Z0-9]*");
		;

		RegistrationDAO registrationDAO = new RegistrationDAO(connection);
		try {
			if (!usernameMatch || !passwordMatch || !passwordReinsertedMatch) {
				stringError=stringError.concat("&characterDosentMatch=true");
			} else {

				if (registrationDAO.controlRegistrationOfUser(username) && password.equals(passwordReinserted)) {

					try {
						// lo username non è presente nel database, provo a creare un nuovo utente
						registrationDAO.createRegistrationOfUser(username, password);
						response.sendRedirect(getServletContext().getContextPath() + "/GoLogin?registrationDone=" + true);
						return;
					} catch (SQLException e) {

						response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure of user creation in database");
						return;
					}
				} else {

					if (!password.equals(passwordReinserted)) {
						stringError=stringError.concat("&wrongPasswords=true");

					}
					if (!registrationDAO.controlRegistrationOfUser(username)) {
						stringError=stringError.concat("&usernameExist=true");
					}

				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} 

		response.sendRedirect(getServletContext().getContextPath() + "/OpenRegistrationForm?" + stringError);

	}
	
	public void destroy() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException sqle) {
		}
	}

}
