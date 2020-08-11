package it.polimi.tiw.projects.beans;

public class ProjectStats {
	private int id;
	private String name;
	private int workerId;
	private int maxHours;
	private int workedHours;
	

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}


	public int getWorkerId() {
		return workerId;
	}


	public int getMaxHours() {
		return maxHours;
	}


	public int getWorkedHours() {
		return workedHours;
	}

	public void setId(int i) {
		id = i;
	}

	public void setName(String un) {
		name = un;
	}

	public void setWorkerId(int i) {
		workerId = i;
	}


	public void setMaxHours(int h) {
		maxHours = h;
	}


	public void setWorkedHours(int h) {
		workedHours = h;
	}

}
