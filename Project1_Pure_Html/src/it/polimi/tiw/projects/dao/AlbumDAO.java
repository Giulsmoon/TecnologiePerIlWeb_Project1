package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Album;
import it.polimi.tiw.projects.beans.Image;

public class AlbumDAO {
	private Connection con;

	public AlbumDAO(Connection connection) {
		this.con = connection;
	}

	public List<Album> findAllAlbums() throws SQLException {
		List<Album> albums = new ArrayList<Album>();
		String query = "SELECT * FROM project1_pure_html.album ORDER BY creationDate DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			result = pstatement.executeQuery();
			while (result.next()) {
				Album a = new Album();
				a.setId(result.getInt("id"));
				a.setTitle(result.getString("title"));
				a.setDate(result.getDate("creationDate"));
				albums.add(a);
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
		return albums;
	}

	public Album findAlbumById(int albumId) throws SQLException {
		Album a = new Album();
		String query = "SELECT * project1_pure_html.album where id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, albumId);
			result = pstatement.executeQuery();
			while (result.next()) {
				a.setId(result.getInt("id"));
				a.setTitle(result.getString("title"));
				a.setDate(result.getDate("creationDate"));
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
		return a;
	}

	public Image findDefaultImage(int albumId) throws SQLException {
		String query = "SELECT * FROM project1_pure_html.image as i, project1_pure_html.albumimage as ai WHERE i.id=ai.idImage and ai.idALbum= ? ORDER BY i.date DESC LIMIT 1";
		Image image = new Image();
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, albumId);
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					image.setId(result.getInt("id"));
					image.setTitle(result.getString("title"));
					image.setDate(result.getDate("date"));
					image.setDescription(result.getString("description"));
					image.setFilePath(result.getString("filePath"));
				}

			}
		}
		return image;
	}

}
