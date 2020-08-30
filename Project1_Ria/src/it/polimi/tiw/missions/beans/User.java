package it.polimi.tiw.missions.beans;

import java.util.ArrayList;
import java.util.List;

public class User {
	private int id;
	private String username;
	private int[] prefAlbumOrder  =null;

	public int getId() {
		return id;
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

	public int[] getPrefAlbumOrder() {
		return prefAlbumOrder;
	}


	public void setPrefAlbumOrder(int[] prefAlbumOrder) {
		this.prefAlbumOrder = prefAlbumOrder;
	}

}
