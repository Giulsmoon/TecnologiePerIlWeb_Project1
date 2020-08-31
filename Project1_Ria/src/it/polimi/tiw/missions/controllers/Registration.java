package it.polimi.tiw.missions.controllers;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		boolean isBadRequest = false;

		String username = null;
		String password = null;
		String passwordReinserted = null;
		RegistrationDAO registrationDAO = new RegistrationDAO(connection);

		try {
			username = StringEscapeUtils.escapeJava(request.getParameter("username"));
			password = StringEscapeUtils.escapeJava(request.getParameter("password"));
			passwordReinserted = StringEscapeUtils.escapeJava(request.getParameter("passwordReinserted"));

			isBadRequest = username.isEmpty() || password.isEmpty() || passwordReinserted.isEmpty()
					|| !password.equals(passwordReinserted);
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
				if (registrationDAO.controlRegistrationOfUser(username)) {
					registrationDAO.createRegistrationOfUser(username, password);
					response.setStatus(HttpServletResponse.SC_OK);

				} else {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					response.getWriter().println("Username Already Exist");
				}
			} catch (Exception e) {

				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Failed to access to DB");

				return;
			}
		}

	}
}
