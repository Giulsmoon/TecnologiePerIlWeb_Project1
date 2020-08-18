package it.polimi.tiw.projects.beans;

import java.util.Date;

public class Comment {
	private int id;
	private String text;
	private int idImage;
	private int idUser;
	private String username;
	private Date date;

	public int getId() {
		return id;
	}

	public void setId(int iden) {
		this.id = iden;
	}

	public String getText() {
		return text;
	}

	public void setText(String t) {
		this.text = t;
	}

	public int getIdImage() {
		return idImage;
	}

	public void setIdImage(int idImage) {
		this.idImage = idImage;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String u) {
		this.username = u;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date d) {
		this.date = d;
	}
}
