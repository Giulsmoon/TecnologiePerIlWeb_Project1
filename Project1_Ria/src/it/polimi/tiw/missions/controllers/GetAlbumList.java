package it.polimi.tiw.missions.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.lang.String;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

import it.polimi.tiw.missions.beans.Album;
import it.polimi.tiw.missions.beans.User;
import it.polimi.tiw.missions.dao.AlbumDAO;
import it.polimi.tiw.missions.dao.UserDAO;
import it.polimi.tiw.missions.utils.ConnectionHandler;

@WebServlet("/GetAlbumList")
public class GetAlbumList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetAlbumList() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}
	
	public List<Album> sortAlbumByUserPreference(int[] userPreference, List<Album> albums){
		Album albumSupport = null;

	
		for(int i=0; i<albums.size();i++) {
			int position=0;
			int j=0;	
			for (Album album : albums) {
				if(album.getId()==userPreference[i])
				{
					position=j;
					break;
				}
				else {
					j++;
				}
			}
			albumSupport= albums.get(i);
			albums.set(i, albums.get(position));
			albums.set(position, albumSupport);
			
		}
		return albums;
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		AlbumDAO albumDAO = new AlbumDAO(connection);
		UserDAO userDAO = new UserDAO(connection);
		
		List<Album> albums = new ArrayList<Album>();
		List<Album> orderedAlbumsById = new ArrayList<Album>();
		try {
			if(user!=null)
				user=userDAO.checkUserAlbumPreference(user);
			if(user!=null && user.getPrefAlbumOrder()!=null) {
				
				orderedAlbumsById = albumDAO.findAlbumsOrderedById();
				albums = sortAlbumByUserPreference(user.getPrefAlbumOrder(), orderedAlbumsById);
			}else {
				
				albums = albumDAO.findAlbumsOrderedByDate();
			}
			
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to get all the albums");
			return;
		}
		
		Gson gson = new GsonBuilder()
				   .setDateFormat("yyyy-MM-dd").create();
		String json = gson.toJson(albums);
	
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
		
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
