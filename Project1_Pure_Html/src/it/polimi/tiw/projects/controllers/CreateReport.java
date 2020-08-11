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
import javax.servlet.http.HttpSession;

import it.polimi.tiw.projects.beans.User;
import it.polimi.tiw.projects.dao.ProjectDAO;

@WebServlet("/CreateReport")
public class CreateReport extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateReport() {
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String loginpath = getServletContext().getContextPath() + "/index.html";
		User u = null;
		HttpSession s = request.getSession();
		if (s.isNew() || s.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		} else {
			u = (User) s.getAttribute("user");
			if (!u.getRole().equals("worker")) {
				response.sendRedirect(loginpath);
				return;
			}
		}
		String prjId = request.getParameter("projectid");
		String whr = request.getParameter("workedhours");
		String date = request.getParameter("date");
		if (prjId == null || whr == null || date == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter in report creation");
			return;
		}
		int workedhours = 0;
		int projectID = 0;
		int workerID = 0;
		int day = 0;
		try {
			workerID = u.getId();
			workedhours = Integer.parseInt(whr);
			projectID = Integer.parseInt(prjId);
			day = Integer.parseInt(date);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter in report creation");
			return;
		}
		if (workedhours <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Worked hours must be greater than zero");
			return;
		}
		ProjectDAO pDAO = new ProjectDAO(connection, projectID);
		try {
			if (pDAO.worksFor(u.getId()) == false) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to report work in wrong project");
				return;
			}
			pDAO.createReport(workerID, workedhours, day);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure of report creation in database");
			return;
		}
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToHomeWorker";
		response.sendRedirect(path);
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
