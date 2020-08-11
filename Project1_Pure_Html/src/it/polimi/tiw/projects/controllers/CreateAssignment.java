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

@WebServlet("/CreateAssignment")
public class CreateAssignment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateAssignment() {
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
			if (!u.getRole().equals("admin")) {
				response.sendRedirect(loginpath);
				return;
			}
		}
		String prjId = request.getParameter("projectid");
		String wrkId = request.getParameter("workerid");
		String mhr = request.getParameter("maxhours");
		if (prjId == null || wrkId == null || mhr == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing assignment data");
			return;
		}
		int projectID = 0;
		int workerID = 0;
		int maxhours = 0;
		try {
			projectID = Integer.parseInt(prjId);
			workerID = Integer.parseInt(wrkId);
			maxhours = Integer.parseInt(mhr);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad assignment data");
			return;
		}
		if (maxhours <= 0) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Maximum amount of hours must be greater than zero");
			return;
		}
		ProjectDAO pDAO = new ProjectDAO(connection, projectID);
		try {
			if (pDAO.findOwner().getId() != u.getId()) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Trying to assign worker to non-owned project");
				return;
			}
			pDAO.createAssignment(workerID, maxhours);
		} catch (SQLException e) {
			// throw new ServletException(e);
			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Database failure");

		}
		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GoToProjectManagement?projectid=" + prjId;
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
