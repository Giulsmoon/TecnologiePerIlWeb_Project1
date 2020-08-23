package it.polimi.tiw.missions.beans;

import java.util.Date;

public class Image {
	private int id;
	private String title;
	private String description;
	private Date date;
	private String filePath;
	private Album album;

	public int getId() {
		return id;
	}

	public void setId(int iden) {
		this.id = iden;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String t) {
		this.title = t;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String d) {
		this.description = d;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date d) {
		this.date = d;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String fp) {
		this.filePath = fp;
	}

	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album a) {
		this.album = a;
	}
}
