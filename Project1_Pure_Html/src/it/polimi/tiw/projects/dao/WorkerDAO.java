package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.ProjectStats;

public class WorkerDAO {
	private Connection con;
	private int id;

	public WorkerDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}

	public List<ProjectStats> findProjects() throws SQLException {
		List<ProjectStats> projects = new ArrayList<ProjectStats>();
		String query = "SELECT P.id as projectid, P.name,  A.usrid as userid, A.maxhours, COALESCE(sum(R.hours), 0) as workedhours FROM project P JOIN assignment A ON P.id=A.prjid LEFT JOIN report R ON R.prjid=P.id AND A.usrid=R.usrid where A.usrid = ? GROUP BY P.id, R.usrid;	";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, this.id);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					ProjectStats project = new ProjectStats();
					project.setId(result.getInt("projectid"));
					project.setName(result.getString("name"));
					project.setWorkerId(this.id);
					project.setMaxHours(result.getInt("maxhours"));
					project.setWorkedHours(result.getInt("workedhours"));
					projects.add(project);
				}
			}
		}
		return projects;
	}
}
