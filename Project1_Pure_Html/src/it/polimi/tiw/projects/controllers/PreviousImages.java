package it.polimi.tiw.projects.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.projects.beans.Image;
import it.polimi.tiw.projects.dao.ImageDAO;

/**
 * Servlet implementation class CheckGuest
 */
@WebServlet("/PreviousImages")
public class PreviousImages extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PreviousImages() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
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
		String path = getServletContext().getContextPath() + "/GoToHomePage";
		response.sendRedirect(path);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String next = request.getParameter("nextImages");
		String previous = request.getParameter("previousImages");
		String albumId = request.getParameter("albumId");
		System.out.println(next + "   " + previous + "   " + albumId);

		if (next == null || previous == null) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameter ");
			return;
		}
		int nextNum = 0;
		int previousNum = 0;
		int alId = 0;
		try {
			nextNum = Integer.parseInt(next) + 1;
			previousNum = Integer.parseInt(previous) - 1;
			alId = Integer.parseInt(albumId);
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bad parameter ");
			return;
		}
		int numberOfBlocks;
		List<Image> images = null;
		ImageDAO imgDao = new ImageDAO(connection);
		try {
			images = imgDao.findImagesByAlbum(alId);
		} catch (

		SQLException e) {
			response.sendError(500, "Database access failed");
		}
		if (images.size() % 5 == 0) {
			numberOfBlocks = Math.floorDiv(images.size(), 5);
		} else {
			numberOfBlocks = Math.floorDiv(images.size(), 5) + 1;
		}
		if(previousNum<0) {
			String ctxpath = getServletContext().getContextPath();
			String path = ctxpath + "/GetImagesOfAlbum?albumId=" + alId;
			response.sendRedirect(path);
		}else {
			if(nextNum+previousNum!=(numberOfBlocks - 1)){
				response.sendError(500, "Bad Post Request");
			}
			else {
				String ctxpath = getServletContext().getContextPath();
				String path = ctxpath + "/GetImagesOfAlbum?albumId=" + alId + "&nextImages=" + nextNum + "&previousImages=" + previousNum;
				response.sendRedirect(path);
			}
		}
		
	}
}
