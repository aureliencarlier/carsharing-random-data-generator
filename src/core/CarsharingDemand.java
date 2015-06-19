package core;


public class CarsharingDemand {
	
	private String id;
	private Station sOrigin;
	private Station sDestination;
	private int departureTime_TS;
	private int arrivalTime_TS;
	private int nbDemand;
	
	public CarsharingDemand(String id, Station sOrigin, Station sDestination,	int departureTime_TS, int arrivalTime_TS) {
		this.id = id;
		this.sOrigin = sOrigin;
		this.sDestination = sDestination;
		this.departureTime_TS = departureTime_TS;
		this.arrivalTime_TS = arrivalTime_TS;
		nbDemand = 1;
	}
	
	public void increaseDemandBy(int nb){
		nbDemand += nb;
	}

	public String getId() {
		return id;
	}
	
	public int getDepartureTime_TS() {
		return departureTime_TS;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setDepartureTime_TS(int departureTime_TS) {
		this.departureTime_TS = departureTime_TS;
	}

	public Station getsOrigin() {
		return sOrigin;
	}

	public Station getsDestination() {
		return sDestination;
	}

	public void setsOrigin(Station sOrigin) {
		this.sOrigin = sOrigin;
	}

	public void setsDestination(Station sDestination) {
		this.sDestination = sDestination;
	}

	public int getArrivalTime_TS() {
		return arrivalTime_TS;
	}

	public void setArrivalTime_TS(int arrivalTime_TS) {
		this.arrivalTime_TS = arrivalTime_TS;
	}

	public int getNbDemand() {
		return nbDemand;
	}

	public void setNbDemand(int nbDemand) {
		this.nbDemand = nbDemand;
	}
	
}
