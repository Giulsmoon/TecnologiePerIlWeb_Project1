package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.User;

public class ProjectDAO {
	private Connection con;
	private int id;

	public ProjectDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

	public User findOwner() throws SQLException {
		User owner = null;
		String query = "SELECT U.id, U.username from user U JOIN project P ON P.owner=U.id where P.id = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					owner = new User();
					owner.setId(result.getInt("id"));
					owner.setUsername(result.getString("username"));
				}
			}
		}
		return owner;
	}

	public boolean worksFor(int workerId) throws SQLException {
		String query = "SELECT * from assignment where prjid = ? and usrid = ?";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			pstatement.setInt(2, workerId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					return true;
				}
			}
		}
		return false;
	}

	public List<User> findWorkers() throws SQLException {
		List<User> workers = new ArrayList<User>();
		String query = "SELECT id, username from user U where U.role='worker' AND  U.id NOT IN (SELECT usrid FROM assignment WHERE prjid = ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					User worker = new User();
					worker.setId(result.getInt("id"));
					worker.setUsername(result.getString("username"));
					workers.add(worker);
				}
			}
		}
		return workers;
	}

	public void createAssignment(int workerid, int maxhours) throws SQLException {
		String query = "INSERT into assignment (usrid, prjid, maxhours)   VALUES(?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, workerid);
			pstatement.setInt(2, id);
			pstatement.setInt(3, maxhours);
			pstatement.executeUpdate();
		}
	}

	public void createReport(int workerid, int workedhours, int day) throws SQLException {
		String query = "INSERT into report (usrid, prjid, hours, date)   VALUES(?, ?, ?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, workerid);
			pstatement.setInt(2, id);
			pstatement.setInt(3, workedhours);
			pstatement.setInt(4, day);
			pstatement.executeUpdate();
		}
	}
}
