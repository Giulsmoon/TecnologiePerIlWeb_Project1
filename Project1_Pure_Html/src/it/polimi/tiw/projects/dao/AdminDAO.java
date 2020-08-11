package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Project;

public class AdminDAO {
	private Connection con;
	private int id;

	public AdminDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

	public List<Project> findProjects() throws SQLException {
		List<Project> projects = new ArrayList<Project>();
		String query = "SELECT id, name FROM project WHERE owner = ? ORDER BY name ASC";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Project project = new Project();
					project.setId(result.getInt("id"));
					project.setName(result.getString("name"));
					projects.add(project);
				}
			}
		}
		return projects;
	}

	public int findDefaultProject() throws SQLException {
		String query = "SELECT id FROM project WHERE owner = ? ORDER BY name ASC LIMIT 1";
		int r = 0;
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				result.next();
				r = result.getInt("id");
			}
		}
		return r;
	}

	public void createProject(String pname) throws SQLException {
		String query = "INSERT into project (name, owner)   VALUES(?, ?)";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, pname);
			pstatement.setInt(2, id);
			pstatement.executeUpdate();
		}
	}
}
