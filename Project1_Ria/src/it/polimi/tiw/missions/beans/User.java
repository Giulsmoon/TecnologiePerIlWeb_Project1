package it.polimi.tiw.missions.beans;


public class User {
	private int id;
	private String username;
	private String email;
	private int[] prefAlbumOrder  =null;

	public int getId() {
		return id;
	}

		
	public void setId(int i) {
		id = i;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String un) {
		username = un;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String emailUser) {
		email = emailUser;
	} 
	
	public int[] getPrefAlbumOrder() {
		return prefAlbumOrder;
	}


	public void setPrefAlbumOrder(int[] prefAlbumOrder) {
		this.prefAlbumOrder = prefAlbumOrder;
	}

}
