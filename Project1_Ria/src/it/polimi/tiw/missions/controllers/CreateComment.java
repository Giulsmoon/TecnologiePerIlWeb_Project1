package it.polimi.tiw.missions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.missions.beans.User;
import it.polimi.tiw.missions.dao.CommentDAO;

@WebServlet("/CreateComment")
@MultipartConfig
public class CreateComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateComment() {
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
		User u = null;
		HttpSession s = request.getSession();
		String comment = request.getParameter("comment");
		String imageId = request.getParameter("imageId");
		String albumId = request.getParameter("albumId");
		String urlPreviousImages = request.getParameter("urlPreviousImages");
		String urlNextImages = request.getParameter("urlNextImages");

		u = (User) s.getAttribute("user");

		if (comment == null || imageId == null || albumId == null || 
				 urlPreviousImages == null || urlNextImages == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter in comment creation");
			return;
		}
		
		int imgId = 0;
		int userId = 0;
		int alId = 0;

		try {
			userId = u.getId();
			imgId = Integer.parseInt(imageId);
			alId = Integer.parseInt(albumId);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter in comment creation");
			return;
		}
		CommentDAO commentDAO = new CommentDAO(connection);
		try {
			commentDAO.createComment(comment, imgId, userId);

		} catch (SQLException e) {

			response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "Failure of comment creation in database");
			return;
		}

		String ctxpath = getServletContext().getContextPath();
		String path = ctxpath + "/GetImagesOfAlbum?albumId=" + alId + "&imageId=" + imgId + 
				"&nextImages=" + urlNextImages + "&previousImages=" + urlPreviousImages;
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
