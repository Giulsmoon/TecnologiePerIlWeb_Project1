package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Comment;

public class CommentDAO {
	private Connection con;

	public CommentDAO(Connection connection) {
		this.con = connection;

	}

	public void createComment(String text, int imageId, int userId) throws SQLException {
		String query = "INSERT into project1_pure_html.comment (text, idImage, idUser)   VALUES(?, ?, ?)";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, text);
			pstatement.setInt(2, imageId);
			pstatement.setInt(3, userId);
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
				comment.setId(result.getInt("idImage"));
				comment.setId(result.getInt("idUser"));
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
