package it.polimi.tiw.missions.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import it.polimi.tiw.missions.beans.User;
import it.polimi.tiw.missions.dao.AlbumDAO;
import it.polimi.tiw.missions.dao.UserDAO;
import it.polimi.tiw.missions.utils.ConnectionHandler;

@WebServlet("/SaveAlbumOrder")
@MultipartConfig
public class SaveAlbumOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public SaveAlbumOrder() {
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
		User user = null;
		HttpSession s = request.getSession();

		StringBuilder buffer = new StringBuilder();
		BufferedReader reader = request.getReader();
		String line;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		// Ottengo lo stream dell'oggetto inviato dal client sotto forma di stringa e lo
		// parso
		String data = buffer.toString();
		Gson gson = new Gson();
		user = (User) s.getAttribute("user");
		int[] prefAlbum = gson.fromJson(data, int[].class);

		AlbumDAO albumDAO = new AlbumDAO(connection);
		int albumSize = -1;
		try {
			albumSize = albumDAO.findAlbumsOrderedById().size();

		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		// controllo che il numero di preferenze inserite sia uguale al numero degli
		// album
		isBadRequest = prefAlbum.length < albumSize || prefAlbum.length > albumSize;
		if (isBadRequest) {

			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Incorrect or missing param values");
			return;
		} else {
			UserDAO userDAO = new UserDAO(connection);
			try {
				//inserisco le nuove preferenze dell'ordine degli album nel database
				userDAO.updatePreferenceAlbum(user.getUsername(), gson.toJson(prefAlbum));
			} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Error in update preference");
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
