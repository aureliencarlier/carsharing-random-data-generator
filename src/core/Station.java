package core;


public class Station {

	private String id;
	private int xPos;
	private int yPos;
	private int maxSize;
	private boolean insideCentroid;
	
	public Station(String id, int xPos, int yPos, int maxSize) {
		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
		this.maxSize = maxSize;
	}

	public String getId() {
		return id;
	}

	public int getxPos() {
		return xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	public boolean isInsideCentroid() {
		return insideCentroid;
	}

	public void setInsideCentroid(boolean insideCentroid) {
		this.insideCentroid = insideCentroid;
	}
	
}
