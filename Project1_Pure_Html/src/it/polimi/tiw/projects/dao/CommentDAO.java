package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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


	public void  findUsernameOfComment(List<Comment> comments) throws SQLException {
		if (!comments.isEmpty()) {
				String query2 = "SELECT  u.username, c.id from user u JOIN comment c ON u.id=c.idUser";
				//String query = "SELECT username FROM project1_pure_html.user  WHERE id = ?";
				ResultSet result = null;
				PreparedStatement pstatement = null;
				try {
					pstatement = con.prepareStatement(query2);
					//pstatement.setInt(1, comments.get(i).getIdUser());
					result = pstatement.executeQuery();
					while (result.next()) {
						
							for (int i = 0; i < comments.size(); i++) {
								if(comments.get(i).getId()==result.getInt("id")) {
									comments.get(i).setUsername(result.getString("username"));
								}
								
							
						}
						
						
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
			}
	}
	
	public void createComment(String text, int imageId, int userId) throws SQLException {
		String query = "INSERT into comment (text, idImage, idUser, date)   VALUES(?, ?, ?, ?)";

		try (PreparedStatement pstatement = con.prepareStatement(query);) {

			pstatement.setString(1, text);
			pstatement.setInt(2, imageId);
			pstatement.setInt(3, userId);
			pstatement.setObject(4, DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDate.now()));
			pstatement.executeUpdate();
		}
	}

	public List<Comment> findCommentsOfImage(int imageId) throws SQLException {
		List<Comment> comments = new ArrayList<Comment>();
		String query = "SELECT * FROM comment  WHERE idImage= ?";
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
