package it.polimi.tiw.missions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.missions.dao.RegistrationDAO;
import it.polimi.tiw.missions.utils.ConnectionHandler;

/**
 * Servlet implementation class Registration
 */
@WebServlet("/Registration")
@MultipartConfig
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
		connection = ConnectionHandler.getConnection(getServletContext());

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$",
			Pattern.CASE_INSENSITIVE);

	public static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		boolean isBadRequest = false;

		String username = null;
		String email = null;
		String password = null;
		String passwordReinserted = null;
		RegistrationDAO registrationDAO = new RegistrationDAO(connection);

		try {
			username = request.getParameter("username");
			email = request.getParameter("email");
			password = request.getParameter("password");
			passwordReinserted = request.getParameter("passwordReinserted");
			//controllo che i valori inseriti siano corretti anche a lato server
			isBadRequest = username.isEmpty() || email.isEmpty() || password.isEmpty() || passwordReinserted.isEmpty()
					|| !password.equals(passwordReinserted) || !username.matches("[a-zA-Z0-9]*")
					|| !password.matches("[a-zA-Z0-9]*") || !passwordReinserted.matches("[a-zA-Z0-9]*")
					|| !validate(email);
		} catch (NumberFormatException | NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		} else {
			try {
				//controllo se il nome non esiste già
				if (registrationDAO.controlRegistrationOfUserName(username)) {
					//controllo se l'email non esiste già
					if (registrationDAO.controlRegistrationOfUserEmail(email)) {
						//se sia l'utente che l'email sono nuovi allora inserisco la nuova registrazione nel database
						registrationDAO.createRegistrationOfUser(username, email, password);
						response.setStatus(HttpServletResponse.SC_OK);
						return;
					} else {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().println("Email Already Exist");
						return;
					}
				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Username Already Exist");
					return;
				}
			} catch (Exception e) {

				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Failed to access to DB");

				return;
			}
		}

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
