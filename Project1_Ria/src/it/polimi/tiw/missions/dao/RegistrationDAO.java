package it.polimi.tiw.missions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class RegistrationDAO {
	private Connection con;

	public RegistrationDAO(Connection connection) {
		this.con = connection;

	}

	public void createRegistrationOfUser(String username, String password) throws SQLException {
		String query = "INSERT into project1_pure_html.user (username, password)   VALUES(?, ?)";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, username);
			pstatement.setString(2, password);
			pstatement.executeUpdate();
		}
	}

	public boolean controlRegistrationOfUser(String name) throws SQLException {
		String query = "SELECT * FROM project1_pure_html.user WHERE username= ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, name);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, the registration is possible
					return true;
				else {
					
					return false; // la registrazione dell'utente non può essere effettuata perchè 
					              //username già esistente nel database
				}
			}
		}
	}
	

}
