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

	public void createRegistrationOfUser(String username, String email, String password) throws SQLException {
		String query = "INSERT into user (username, email, password)   VALUES(?, ?, ?)";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, username);
			pstatement.setString(2, email);
			pstatement.setString(3, password);	
			pstatement.executeUpdate();
		}
	}

	public boolean controlRegistrationOfUserName(String name) throws SQLException {
		String query = "SELECT * FROM user WHERE username= ?";
		
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
	public boolean controlRegistrationOfUserEmail(String email) throws SQLException {
		String query = "SELECT * FROM user WHERE email= ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, email);
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
