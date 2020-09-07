package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.projects.beans.Album;

public class AlbumDAO {
	private Connection con;

	public AlbumDAO(Connection connection) {
		this.con = connection;
	}

	public List<Album> findAllAlbums() throws SQLException {
		List<Album> albums = new ArrayList<Album>();
		String query = "SELECT * FROM album ORDER BY creationDate DESC";
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
				a.setIconPath(result.getString("iconPath"));
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
		Album album = new Album();
		String query = "SELECT * FROM album WHERE id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, albumId);
			result = pstatement.executeQuery();
			while (result.next()) {
				album.setId(result.getInt("id"));
				album.setTitle(result.getString("title"));
				album.setDate(result.getDate("creationDate"));
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
		return album;
	}

	public String findAlbumTitleById(int albumId) throws SQLException {
		String albumTitle=null;
		String query = "SELECT title FROM album WHERE id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, albumId);
			result = pstatement.executeQuery();
			while (result.next()) {
				albumTitle=result.getString("title");
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
	return albumTitle;

	}
}
