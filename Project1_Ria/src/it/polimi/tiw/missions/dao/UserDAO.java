package it.polimi.tiw.missions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.polimi.tiw.missions.beans.User;


public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException {
		String query = "SELECT  id, username FROM user  WHERE username = ? AND password =?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, credential check failed
					return null;
				else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					return user;
				}
			}
		}
	}
	
	public User checkUserAlbumPreference(User user)throws SQLException {
		
		String query = "SELECT  albumOrder FROM preference as p  WHERE p.idUser= ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, user.getId());
			
			try (ResultSet result = pstatement.executeQuery();) {
				     
				
				Gson gson = new GsonBuilder().create();
				int[] order = gson.fromJson(result.getString("albumOrder"), int[].class);
				user.setPrefAlbumOrder(order);
			}
		}
		return user;
	}
	
	public List<User> findAllUsers() throws SQLException {
		List<User> users = new ArrayList<User>();
		String query = "SELECT * FROM project1_pure_html.user  ";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				User u = new User();
				u.setId(result.getInt("id"));
				u.setUsername(result.getString("username"));
				
				users.add(u);
			}
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close result");
			}
			try {
				pstatement.close();
			} catch (Exception e1) {
				throw new SQLException("Cannot close statement");
			}
		}
		return users;
	}
}
