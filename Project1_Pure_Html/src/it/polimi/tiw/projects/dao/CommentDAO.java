package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Comment;
import it.polimi.tiw.projects.beans.Image;


public class CommentDAO {
	private Connection con;
	
	

	public CommentDAO(Connection connection) {
		this.con = connection;
	}


	
	public void createComment(String text, int imageId, int userId) throws SQLException {
		String query = "INSERT into project1_pure_html.comment (text, idImage, idUser)   VALUES(?, ?, ?)";
		
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, text);
			pstatement.setInt(2, imageId);
			pstatement.setInt(3, userId);
			pstatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			try {
				pstatement.close();
			} catch (Exception e1) {

			}
		}
	}
	
	public List<Comment> findCommentsOfImage(int imageId) {
		return null;
	}
	
	
}
