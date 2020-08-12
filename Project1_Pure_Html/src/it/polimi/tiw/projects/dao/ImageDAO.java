package it.polimi.tiw.projects.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import it.polimi.tiw.projects.beans.Image;


public class ImageDAO {
	private Connection con;
	

	public ImageDAO(Connection connection) {
		this.con = connection;
	}

	public List<Image> findImagesByAlbum(int albumId) throws SQLException {
		List<Image> images = new ArrayList<Image>();
		String query = "SELECT i.id, i.title, i.description, i.date, i.filePath FROM project1_pure_html.image as i,project1_pure_html.albumimage as ai  WHERE i.id= ai.idImage and ai.idAlbum = ?";
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

	
	//System.out.println(findImagesByAlbum(1));
	
	public void createComment(String t, int img, int u) throws SQLException {
		String query = "INSERT into project1_pure_html.comment (text, idImage, idUser)   VALUES(?, ?, ?)";
		
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, t);
			pstatement.setInt(2, img);
			pstatement.setInt(3, u);
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

}