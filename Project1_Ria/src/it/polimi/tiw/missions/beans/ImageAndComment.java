package it.polimi.tiw.missions.beans;

import java.util.List;


public class ImageAndComment {
	private Image image = new Image();
	private List<Comment> comments = null;	
	
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}



}
