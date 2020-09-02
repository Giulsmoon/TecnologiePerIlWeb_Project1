package it.polimi.tiw.missions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.missions.beans.Image;
import it.polimi.tiw.missions.dao.AlbumDAO;
import it.polimi.tiw.missions.dao.ImageDAO;
import it.polimi.tiw.missions.utils.ConnectionHandler;

/**
 * Servlet implementation class GetImagesOfalbun
 */
@WebServlet("/GetImagesOfAlbum")
public class GetImagesOfAlbum extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetImagesOfAlbum() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	public boolean checkALbumId(int albumId) {
		boolean correct = false;
		AlbumDAO albumDao = new AlbumDAO(connection);
		try {
			if (albumDao.findAlbumById(albumId).getId() != 0) {
				correct = true;
			} else {
				correct = false;
			}
		} catch (SQLException e) {
			correct = false;
		}

		return correct;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		List<Image> images = null;
		String urlAlbumId = request.getParameter("albumId");
		Integer albumId = null;
		try {
			albumId = Integer.parseInt(urlAlbumId);
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}

		if (checkALbumId(albumId)) {

			ImageDAO imgDao = new ImageDAO(connection);

			try {
				images = imgDao.findImagesByAlbum(albumId);
			} catch (

			SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Database access failed");
			}
		}
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd").create();
		String json = gson.toJson(images);
	
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
