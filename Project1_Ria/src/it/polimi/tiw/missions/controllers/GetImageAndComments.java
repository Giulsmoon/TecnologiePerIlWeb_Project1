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
import it.polimi.tiw.missions.beans.ImageAndComment;
import it.polimi.tiw.missions.dao.AlbumDAO;
import it.polimi.tiw.missions.dao.CommentDAO;
import it.polimi.tiw.missions.dao.ImageDAO;
import it.polimi.tiw.missions.utils.ConnectionHandler;

/**
 * Servlet implementation class GetImagesOfalbun
 */
@WebServlet("/GetImageAndComments")
public class GetImageAndComments extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetImageAndComments() {
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
		// TODO Auto-generated method stub
		ImageAndComment imageAndComments = new ImageAndComment();
		String urlImageId = request.getParameter("imageId");
		Integer imageId = null;
		try {
			imageId = Integer.parseInt(urlImageId);
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect param values");
			return;
		}

		ImageDAO imageDAO = new ImageDAO(connection);
		CommentDAO commentDAO = new CommentDAO(connection);
		
		try {
			imageAndComments.setImage(imageDAO.findImageById(imageId));
			imageAndComments.setComments(commentDAO.findCommentsOfImage(imageId));
			commentDAO.findUsernameOfComment(imageAndComments.getComments());
		} catch (

		SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Database access failed");
		}
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy MM dd").create();
		String json = gson.toJson(imageAndComments);

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

}
