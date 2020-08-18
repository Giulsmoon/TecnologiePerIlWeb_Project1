package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.polimi.tiw.projects.beans.Comment;
import it.polimi.tiw.projects.beans.Image;
import sun.java2d.d3d.D3DScreenUpdateManager;

public class CommentDAO {
	private Connection con;

	public CommentDAO(Connection connection) {
		this.con = connection;

	}


	public void  findUsernameOfComment(Comment comment) throws SQLException {
		
		String query = "SELECT username FROM project1_pure_html.user  WHERE id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, comment.getIdUser());
			result = pstatement.executeQuery();
			while (result.next()) {
				comment.setUsername(result.getString("username"));
				
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
		return;
	}
	
	public void createComment(String text, int imageId, int userId, Date date) throws SQLException {
		String query = "INSERT into project1_pure_html.comment (text, idImage, idUser, date)   VALUES(?, ?, ?, ?)";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, text);
			pstatement.setInt(2, imageId);
			pstatement.setInt(3, userId);
			pstatement.setObject(4, date.toInstant().atZone(ZoneId.of("Europe/Rome")).toLocalDate());
			pstatement.executeUpdate();
		}
	}

	public List<Comment> findCommentsOfImage(int imageId) throws SQLException {
		List<Comment> comments = new ArrayList<Comment>();
		String query = "SELECT * FROM project1_pure_html.comment  WHERE idImage= ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, imageId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Comment comment = new Comment();
				comment.setId(result.getInt("id"));
				comment.setText(result.getString("text"));
				comment.setIdImage(result.getInt("idImage"));
				comment.setIdUser(result.getInt("idUser"));
				comment.setDate(result.getDate("date"));
				comments.add(comment);
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
		return comments;
	}

}
