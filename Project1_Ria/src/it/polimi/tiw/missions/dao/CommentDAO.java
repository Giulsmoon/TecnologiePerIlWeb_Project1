package it.polimi.tiw.missions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.missions.beans.Comment;

public class CommentDAO {
	private Connection con;

	public CommentDAO(Connection connection) {
		this.con = connection;

	}

	public void findUsernameOfComments(List<Comment> comments) throws SQLException {
		if (!comments.isEmpty()) {
			String query = "SELECT  u.username, c.id from user u JOIN comment c ON u.id=c.idUser";
			ResultSet result = null;
			PreparedStatement pstatement = null;
			try {
				pstatement = con.prepareStatement(query);
				// pstatement.setInt(1, comments.get(i).getIdUser());
				result = pstatement.executeQuery();
				while (result.next()) {
					for (int i = 0; i < comments.size(); i++) {
						if (comments.get(i).getId() == result.getInt("id")) {
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

	public void findUsernameOfComment(Comment comment) throws SQLException {
		if (comment != null) {
			String query = "SELECT  u.username, c.id from user u JOIN comment c ON u.id=c.idUser";
			ResultSet result = null;
			PreparedStatement pstatement = null;
			try {
				pstatement = con.prepareStatement(query);
				// pstatement.setInt(1, comments.get(i).getIdUser());
				result = pstatement.executeQuery();
				while (result.next()) {
					if (comment.getId() == result.getInt("id")) {
						comment.setUsername(result.getString("username"));
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

	public Comment findLastInsertedComment() throws SQLException {
		Comment comment = new Comment();
		String query = "SELECT * FROM comment  WHERE id= (SELECT MAX(id) FROM comment) ";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {

				comment.setId(result.getInt("id"));
				comment.setText(result.getString("text"));
				comment.setIdImage(result.getInt("idImage"));
				comment.setIdUser(result.getInt("idUser"));
				comment.setDate(result.getDate("date"));

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
		return comment;
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
