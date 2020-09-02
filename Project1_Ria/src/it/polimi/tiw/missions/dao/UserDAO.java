package it.polimi.tiw.missions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
		String query = "SELECT  u.id, u.username FROM user u WHERE u.username = ? AND BINARY u.password = BINARY ?";
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

	public User checkUserAlbumPreference(User user) throws SQLException {

		String query = "SELECT  p.albumOrder FROM preference as p  WHERE p.idUser= ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, user.getId());

			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Gson gson = new GsonBuilder().create();

					int[] order = gson.fromJson(result.getString("albumOrder"), int[].class);
					user.setPrefAlbumOrder(order);
				}
			}
		}
		return user;
	}

	public User findUserByUsername(String username) throws SQLException {
		String query = "SELECT  u.id, u.username FROM user u WHERE u.username = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, username);
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

	public void updatePreferenceAlbum(String username, String albPref) throws SQLException {

		User user = findUserByUsername(username);
		String query = "SELECT  * FROM preference p WHERE p.idUser = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, user.getId());
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, insert new user preference
				{
					String query2 = "INSERT into preference (idUser, albumOrder)   VALUES(?,?)";
					PreparedStatement pstatement2 = con.prepareStatement(query2);
					pstatement2.setInt(1, user.getId());
					pstatement2.setString(2, albPref);
					pstatement2.executeUpdate();
				} else {
					result.next();	 // founded result, update user preference
					String query3 = "UPDATE preference SET albumOrder = ? where idUser = ?";
					PreparedStatement pstatement3 = con.prepareStatement(query3);
					pstatement3.setString(1, albPref);
					pstatement3.setInt(2, user.getId());
					pstatement3.executeUpdate();
				}
			}
		}
		

		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			
		}
	}

	public List<User> findAllUsers() throws SQLException {
		List<User> users = new ArrayList<User>();
		String query = "SELECT * FROM user  ";
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
