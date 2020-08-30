package it.polimi.tiw.missions.beans;

import java.util.ArrayList;
import java.util.List;

public class User {
	private int id;
	private String username;
	private String orderOFAlbum;
	private int[] prefAlbumOrder;

	public int getId() {
		return id;
	}


	public String getOrderOFAlbum() {
		return orderOFAlbum;
	}

	public String getUsername() {
		return username;
	}
	
	public void setId(int i) {
		id = i;
	}

	

	public void setUsername(String un) {
		username = un;
	}

	public void setOrderOFAlbum(String order) {
		orderOFAlbum = order;
	}

	public int[] getPrefAlbumOrder() {
		return prefAlbumOrder;
	}


	public void setPrefAlbumOrder(int[] prefAlbumOrder) {
		this.prefAlbumOrder = prefAlbumOrder;
	}

}
