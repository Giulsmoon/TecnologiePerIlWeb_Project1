package it.polimi.tiw.missions.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.missions.beans.Image;

public class ImageDAO {
	private Connection con;

	public ImageDAO(Connection connection) {
		this.con = connection;

	}

	public List<Image> findImagesByAlbum(int albumId) throws SQLException {
		List<Image> images = new ArrayList<Image>();
		String query = "SELECT i.id, i.title, i.description, i.date, i.filePath FROM image as i, albumimage as ai  WHERE i.id= ai.idImage and ai.idAlbum = ? ORDER BY i.date DESC ";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, albumId);
			result = pstatement.executeQuery();
			while (result.next()) {
				Image image = new Image();
				image.setId(result.getInt("id"));
				image.setTitle(result.getString("title"));
				image.setDescription(result.getString("description"));
				image.setDate(result.getDate("date"));
				image.setFilePath(result.getString("filePath"));
				images.add(image);
			}
		} catch (SQLException e) {
			throw new SQLException(e);

		} finally {
			try {
				result.close();
			} catch (Exception e1) {
				throw new SQLException(e1);
			}
			try {
				pstatement.close();
			} catch (Exception e2) {
				throw new SQLException(e2);
			}
		}

		return images;
	}

	
	public boolean checkImage(int imageId) throws SQLException {
		String query = "SELECT * FROM image WHERE id = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, imageId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) // no results, return false
					return false;
				else {
					
					return true; // trovata corrispondenza dell'immagine
				}
			}
		}
	}
	
	
	public Image findImageById(int imageId) throws SQLException {
		Image image = new Image();
		String query = "SELECT * FROM image WHERE id = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, imageId);
			result = pstatement.executeQuery();
			while (result.next()) {
				image.setId(result.getInt("id"));
				image.setTitle(result.getString("title"));
				image.setDescription(result.getString("description"));
				image.setDate(result.getDate("date"));
				image.setFilePath(result.getString("filePath"));
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
		return image;
	}

}
