package it.polimi.tiw.projects.beans;

public class Comment {
	private int id;
	private String text;
	private int idImage;
	private int idUser;

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

}
