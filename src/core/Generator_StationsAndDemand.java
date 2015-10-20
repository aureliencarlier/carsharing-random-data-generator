package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Generator_StationsAndDemand {
	
	private Element params;

	// internal parameters
	public enum GenerationMethod {Uniform, Centroid};
	private GenerationMethod _generationMethod;
	private int _nbStations;
	private int _nbDemands;
	private int _stationSizeLB;
	private int _stationSizeUB;
	private int _sizeOfTheWholeGraph_m;
	private int _morningRush_lb_h;
	private int _morningRush_ub_h;
	private int _eveningRush_lb_h;
	private int _eveningRush_ub_h;
	private int _TS_min;
	private int _nbTSInOneDay;
	private int _nbTSInOneHour;
	private int _maxDistanceForATravel_m;
	private double _proportionCentroidArea;
	private double _morningRush_demandProportion;
	private double _eveningRush_demandProportion;
	private double _penaltyCoefRushHour;
	private double _concentrationCenter;
	private double _averagecarspeed_kmh;
	private HashMap<Integer, Integer> _demandsOverTime;
	private HashMap<Integer, Integer> _demandsOverTime_expanded;

	// induced parameters
	private int _sideOfTheCentroidArea;
	private int _minInCentroid;
	private int _maxInCentroid;
	private int _morningRush_lb_ts;
	private int _morningRush_ub_ts;
	private int _eveningRush_lb_ts;
	private int _eveningRush_ub_ts;

	private ArrayList<Station> listStations;
	private ArrayList<Station> listStations_insideCentroid;
	private ArrayList<Station> listStations_outsideCentroid;
	private ArrayList<CarsharingDemand> listDemand;

	private HashMap<Integer, Double> mapCoefTravelPenalties;
	private HashMap<Integer, Double> demandOverTime_CumulatedAndNormalized;

	public Generator_StationsAndDemand(){}
	
	public Generator_StationsAndDemand(Element params) {
		this.params = params;
		init();
	}

	public void init(){

		_generationMethod				 = GenerationMethod.valueOf(	params.getChild("generationmethod").getAttributeValue("value"));
		_nbStations						 = Integer.parseInt(	params.getChild("nbstations").getAttributeValue("value"));
		_nbDemands						 = Integer.parseInt(	params.getChild("nbdemands").getAttributeValue("value"));
		_stationSizeLB					 = Integer.parseInt(	params.getChild("stationsize").getAttributeValue("lb"));
		_stationSizeUB					 = Integer.parseInt(	params.getChild("stationsize").getAttributeValue("ub"));
		_sizeOfTheWholeGraph_m			 = Integer.parseInt(	params.getChild("sideofthewholegraphm").getAttributeValue("value"));
		_proportionCentroidArea			 = Double.parseDouble(	params.getChild("proportioncentroidareap").getAttributeValue("value"));
		_morningRush_lb_h				 = Integer.parseInt(	params.getChild("morningrush").getAttributeValue("lb"));
		_morningRush_ub_h				 = Integer.parseInt(	params.getChild("morningrush").getAttributeValue("ub"));
		_morningRush_demandProportion	 = Double.parseDouble(	params.getChild("morningrush").getAttributeValue("demandprop"));
		_eveningRush_lb_h				 = Integer.parseInt(	params.getChild("eveningrush").getAttributeValue("lb"));
		_eveningRush_ub_h				 = Integer.parseInt(	params.getChild("eveningrush").getAttributeValue("ub"));
		_eveningRush_demandProportion	 = Double.parseDouble(	params.getChild("eveningrush").getAttributeValue("demandprop"));
		_TS_min							 = Integer.parseInt(	params.getChild("time").getAttributeValue("timestepmin"));
		_nbTSInOneDay					 = Integer.parseInt(	params.getChild("time").getAttributeValue("nbtsinoneday"));
		_nbTSInOneHour					 = Integer.parseInt(	params.getChild("time").getAttributeValue("nbtsinonehour"));
		_penaltyCoefRushHour			 = Double.parseDouble(	params.getChild("penaltycoefrushhours").getAttributeValue("value"));
		_concentrationCenter			 = Double.parseDouble(	params.getChild("concentrationcenterp").getAttributeValue("value"));
		_averagecarspeed_kmh			 = Double.parseDouble(	params.getChild("averagecarspeedkmh").getAttributeValue("value"));
		_maxDistanceForATravel_m		 = Integer.parseInt(	params.getChild("maxdistanceforatravelm").getAttributeValue("value"));
		_demandsOverTime 				 = initDemandsOverTime(	params.getChild("demandsovertime"));

		// induced parameters
		_sideOfTheCentroidArea 			 = (int) (_sizeOfTheWholeGraph_m * Math.sqrt(_proportionCentroidArea));
		_minInCentroid 					 = (int) ((int)(_sizeOfTheWholeGraph_m/2)) - (_sideOfTheCentroidArea/2);
		_maxInCentroid					 = (int) ((int)(_sizeOfTheWholeGraph_m/2)) + (_sideOfTheCentroidArea/2);
		_demandsOverTime_expanded		 = expandDemandsOverAllTS();
		_morningRush_lb_ts				 = convertHour2Ts(_morningRush_lb_h, _nbTSInOneDay);
		_morningRush_ub_ts				 = convertHour2Ts(_morningRush_ub_h, _nbTSInOneDay);
		_eveningRush_lb_ts				 = convertHour2Ts(_eveningRush_lb_h, _nbTSInOneDay);
		_eveningRush_ub_ts				 = convertHour2Ts(_eveningRush_ub_h, _nbTSInOneDay);
		
		// init other parameters
		mapCoefTravelPenalties = init_mapCoefTravelPenalties();
		demandOverTime_CumulatedAndNormalized = init_demandOverTime_CumulatedAndNormalized();
		listStations = new ArrayList<Station>();
		listStations_insideCentroid = new ArrayList<Station>();
		listStations_outsideCentroid = new ArrayList<Station>();
		listDemand = new ArrayList<CarsharingDemand>();
		
	}

	private HashMap<Integer, Integer> initDemandsOverTime(Element demandOverTime) {
		HashMap<Integer /*hour*/, Integer /*demand value*/> map = new HashMap<Integer, Integer>();
		for(Element demand : demandOverTime.getChildren("demand")){
			map.put(Integer.parseInt(demand.getAttributeValue("hour")), Integer.parseInt(demand.getAttributeValue("value")));
		}
		return map;
	}

	public void generate(){
		generateStations();
		generateDemand();
	}

	/**
	 * Generate stations over an area depending on the generation method given in parameter
	 * @param generationMethod
	 */
	private void generateStations() {

		switch (_generationMethod){
		case Centroid:{

			/**********************
			 * STATION GENERATION *
			 **********************/
			int nbStationsInsideCentroid = (int) (_nbStations * _concentrationCenter);
			for(int idStation = 1 ; idStation <= _nbStations ; idStation++){

				int xPos = (idStation <= nbStationsInsideCentroid)?	generate_x_InsideCentroid() : generate_x_OutsideCentroid();
				int yPos = (idStation <= nbStationsInsideCentroid)? generate_x_InsideCentroid() : generate_x_OutsideCentroid();
				int maxSize = randomIntegerValue(_stationSizeLB, _stationSizeUB);

				Station station = new Station("S" + idStation, xPos, yPos, maxSize);
				boolean insideCentroid = (idStation <= nbStationsInsideCentroid)? listStations_insideCentroid.add(station) : !listStations_outsideCentroid.add(station);
				station.setInsideCentroid(insideCentroid);
				listStations.add(station);
			}
			break;
		}

		case Uniform:{

			// TODO

			break;
		} 

		default:{
			System.out.println("> ERROR : No Generation Method");
			Thread.currentThread().interrupt();
			break;
		}
		}
	}

	private void generateDemand() {

		switch (_generationMethod){
		case Centroid:{

			int NBCreatedDemands = 1;
			for(int NoDemand = 1 ; NoDemand <= _nbDemands ; NoDemand++){

				/***************
				 * FIND A TIME *
				 ***************/
				int randomTime_TS = generate_randomTime_TS();

				/******************************************
				 * FIND TWO STATIONS FOR THAT RANDOM TIME *
				 ******************************************/
				Station [] stations = pickTwoStations(randomTime_TS);

				/*******************
				 * DEMAND CREATION *
				 *******************/

				Station sO = stations[0];
				Station sD = stations[1];
				int tO = randomTime_TS;
				int tD = arrivalTS(randomTime_TS, calculate_TravelTime_TS(stations[0], stations[1], randomTime_TS), _nbTSInOneDay);

				boolean demandAlreadyExists = false;
				for(CarsharingDemand csd : listDemand){
					if(csd.getsOrigin() == sO && csd.getsDestination() == sD && csd.getDepartureTime_TS() == tO){
						csd.increaseDemandBy(1);
						demandAlreadyExists = true;
						break;
					}
				}
				if(!demandAlreadyExists){
					listDemand.add(new CarsharingDemand("D" + NBCreatedDemands, sO, sD, tO, tD));
					NBCreatedDemands++;
				}
			}
		} break;

		case Uniform:{
			// TODO
		} break;

		default:{
			// TODO
		} break;
		}

	}
	
	public void reset(){
		// TODO
		init();
	}

	public void exportDataToXML(Path xmlFile) throws IOException{

		Element root = new Element("randomGeneratedData");

		// parameters
		Element parameters = new Element("parameters");
		root.addContent(parameters);
		parameters.setAttribute("nbStations", Integer.toString(listStations.size()));
		parameters.setAttribute("nbDemands", Integer.toString(listDemand.size()));

		// stations
		Element stations = new Element("stations");
		root.addContent(stations);
		for(Station s : listStations){
			Element station = new Element("station");
			stations.addContent(station);
			station.setAttribute("id", s.getId());
			station.setAttribute("xPos", Integer.toString(s.getxPos()));
			station.setAttribute("yPos", Integer.toString(s.getyPos()));
			station.setAttribute("maxSize", Integer.toString(s.getMaxSize()));
		}

		// demands
		Element demands = new Element("demands");
		root.addContent(demands);
		for(CarsharingDemand d : listDemand){
			Element demand = new Element("demand");
			demands.addContent(demand);
			demand.setAttribute("id", d.getId());
			demand.setAttribute("idsOrigin", d.getsOrigin().getId());
			demand.setAttribute("idsDestination", d.getsDestination().getId());
			demand.setAttribute("nbDemand", Integer.toString(d.getNbDemand()));
			demand.setAttribute("departureTime", Integer.toString(d.getDepartureTime_TS()));
			demand.setAttribute("arrivalTime", Integer.toString(d.getArrivalTime_TS()));
		}
		
		// export
		Document doc = new Document(root);
		XMLOutputter xmlOut = new XMLOutputter(Format.getPrettyFormat());
		xmlOut.output(doc, Files.newOutputStream(xmlFile));
	}
	
	/**
	 * convert the hour value into a number of time steps when there is NB_TIMESTEP_IN_ONE_DAY time-steps in one day
	 * @param HOUR
	 * @param NB_TIMESTEP_IN_ONE_DAY
	 * @return
	 */
	public int convertHour2Ts(double HOUR, int NB_TIMESTEP_IN_ONE_DAY){
		return  (int) (((HOUR * 60 ) * NB_TIMESTEP_IN_ONE_DAY) / 1440);
	}

	/**
	 * convert the number of minutes into a number of time steps when there is NB_TIMESTEP_IN_ONE_DAY time-steps in one day
	 * @param MINUTES
	 * @param NB_TIMESTEP_IN_ONE_DAY
	 * @return
	 */
	public int convertMinutes2Ts(int MINUTES, int NB_TIMESTEP_IN_ONE_DAY){
		return  (int) ((MINUTES * NB_TIMESTEP_IN_ONE_DAY) / 1440);
	}


	private int generate_x_InsideCentroid() {
		return ((int) (Math.random() * _sideOfTheCentroidArea)) + _minInCentroid;
	}

	private int generate_x_OutsideCentroid() {

		boolean findCoordOutsideCentroid = false;
		int randomX = -1;

		while(!findCoordOutsideCentroid){
			randomX = (int) (Math.random() * (_sizeOfTheWholeGraph_m + 1));
			findCoordOutsideCentroid = (xOutsideCentroid(randomX))? true : false;
		}

		return randomX;
	}

	private boolean xInsideCentroid(int randomX){
		return (_minInCentroid <= randomX && randomX <= _maxInCentroid);
	}

	private boolean xOutsideCentroid(int randomX) {
		return !xInsideCentroid(randomX);
	}

	public int calculate_TravelTime_TS(CarsharingDemand demand){
		return calculate_TravelTime_TS(demand.getsOrigin(), demand.getsDestination(), demand.getDepartureTime_TS());
	}

	public int calculate_TravelTime_TS(Station SOrigin, Station SDestination, int departureTime_TS){

		int travelTime_TS = 0;
		int time_TS = departureTime_TS;

		double traveledDistance = 0;
		double totalDistance = calculate_distanceBetweenStations(SOrigin, SDestination);

		while (traveledDistance < totalDistance){
			traveledDistance += (_averagecarspeed_kmh * 1000 / _nbTSInOneHour) * (1 / mapCoefTravelPenalties.get(time_TS));
			time_TS = (time_TS + 1) % _nbTSInOneDay;
			travelTime_TS++;
		}

		return travelTime_TS;
	}

	public int calculate_TravelTime_TS(double distanceBetweenStations, int departureTime_TS){

		int travelTime_TS = 0;
		int time_TS = departureTime_TS;
		double traveledDistance = 0;

		while (traveledDistance < distanceBetweenStations){
			traveledDistance += (_averagecarspeed_kmh * 1000 / _nbTSInOneHour) * (1 / mapCoefTravelPenalties.get(time_TS));
			time_TS = (time_TS + 1) % _nbTSInOneDay;
			travelTime_TS++;
		}

		return travelTime_TS;
	}

	public HashMap<Integer, Double> init_mapCoefTravelPenalties(){
		
		HashMap<Integer, Double> mapToReturn = new HashMap<Integer, Double>();
		for (int ts = 0 ; ts < _nbTSInOneDay; ts++){
			if(_morningRush_lb_ts <= ts &&  ts <= _morningRush_ub_ts){ // morning rush
				mapToReturn.put(ts, _penaltyCoefRushHour);
			}
			else if(_eveningRush_lb_ts <= ts &&  ts <= _eveningRush_ub_ts){ // evening rush
				mapToReturn.put(ts, _penaltyCoefRushHour);
			}
			else{
				mapToReturn.put(ts, new Double(1));
			}
		}
		return mapToReturn;
	}

	private HashMap<Integer, Double> init_demandOverTime_CumulatedAndNormalized() {

		HashMap<Integer, Double> mapToReturn = new HashMap<Integer, Double>();
		double sum_values = 0;
		for (int i : _demandsOverTime_expanded.values()){
			sum_values += i;
		}
		
		for (int hour = 0 ; hour < _demandsOverTime_expanded.size() ; hour++){ // normalisation + cumulated sum

			if(hour != 0){
				mapToReturn.put(hour, mapToReturn.get(hour - 1) + _demandsOverTime_expanded.get(hour)/sum_values);
			}
			else {
				mapToReturn.put(hour, _demandsOverTime_expanded.get(hour)/sum_values);
			}
		}
		return mapToReturn;
	}

	private HashMap<Integer, Integer> expandDemandsOverAllTS() {

		HashMap<Integer, Integer> mapToReturn = new HashMap<Integer, Integer>();
		int NB_TS_BETWEEN_TWO_VALUES = (int) (_nbTSInOneDay / _demandsOverTime.size());

		int index = 0;
		for(int ts = 0 ; ts < _nbTSInOneDay ; ts++){
			if(ts != 0 && ts % NB_TS_BETWEEN_TWO_VALUES == 0){
				index++;
			}
			mapToReturn.put(ts, _demandsOverTime.get(index));
		}		
		return mapToReturn;
	}

	private int generate_randomTime_TS() {

		double randomNumber01 = Math.random();
		int randomTime_TS = -1; // this is an index (a stepTime)

		for(int ts = 0 ; ts < demandOverTime_CumulatedAndNormalized.size() ; ts++){
			randomTime_TS = (demandOverTime_CumulatedAndNormalized.get(ts) < randomNumber01)? randomTime_TS : ts ;
			if(randomTime_TS != -1){
				return randomTime_TS;
			}
		}

		return -1;
	}

	private boolean isInMorningRush(int randomTime_TS) {
		return _morningRush_lb_h <= randomTime_TS && randomTime_TS <= _morningRush_ub_h;
	}

	private boolean isInEveningRush(int randomTime_TS) {
		return _eveningRush_lb_h <= randomTime_TS && randomTime_TS <= _eveningRush_ub_h;
	}

	enum RushSituation {morningRush, eveningRush, outsideRushPeriod};
	private Station[] pickTwoStations(int randomTime_TS) {

		Station SO = null, SD = null;

		/* 2 constraints of selection :
		 * - rush period determines the orientation
		 * - max distance ensures realistic demand */

		RushSituation rushSituation = null;
		boolean distanceOK = false;
		if(isInMorningRush(randomTime_TS) && Math.random() <= _morningRush_demandProportion){
			rushSituation = RushSituation.morningRush;
		}
		else if(isInEveningRush(randomTime_TS) && Math.random() <= _eveningRush_demandProportion){
			rushSituation = RushSituation.eveningRush;
		}
		else{
			rushSituation = RushSituation.outsideRushPeriod;
		}

		while (!distanceOK){

			Collections.shuffle(listStations_insideCentroid);
			Collections.shuffle(listStations_outsideCentroid);
			Collections.shuffle(listStations);

			switch(rushSituation){
			case morningRush:{ // morning rush (OutsideCentroid -> InsideCentroid)
				SO = listStations_outsideCentroid.get(0);
				SD = listStations_insideCentroid.get(0);
			} break;

			case eveningRush:{ // evening rush (InsideCentroid -> OutsideCentroid)
				SO = listStations_insideCentroid.get(0);
				SD = listStations_outsideCentroid.get(0);
			} break;

			case outsideRushPeriod:{ // others periods or if there are a random demand
				SO = listStations.get(0);
				SD = listStations.get(1);
			} break;
			}

			distanceOK = (calculate_distanceBetweenStations(SO, SD) <= _maxDistanceForATravel_m)? true : false;
		}

		return new Station [] {SO, SD};
	}

	/**
	 * The nodes must have the attributes "x" and "y"
	 * @param stationOrigin
	 * @param stationDestination
	 * @return The euclidian distance between the two nodes.
	 */
	public static double calculate_distanceBetweenStations(Station stationOrigin, Station stationDestination) {

		int xO = (int) stationOrigin.getxPos();
		int yO = (int) stationOrigin.getyPos();

		int xD = (int) stationDestination.getxPos();
		int yD = (int) stationDestination.getyPos();

		return Math.sqrt(Math.pow((xD - xO), 2) + Math.pow((yD - yO), 2));
	}

	public static double calculate_distanceBetweenStations(int xO, int yO, int xD, int yD) {
		return Math.sqrt(Math.pow((xD - xO), 2) + Math.pow((yD - yO), 2));
	}

	public int calculate_arrivalTS(int departureTS, int travelTimeTS) {
		return ((departureTS + travelTimeTS) % _nbTSInOneDay);
	}
	
	public static int arrivalTS(int departureTS, int travelTimeTS, int nbTSInOneDay) {
		return ((departureTS + travelTimeTS) % nbTSInOneDay);
	}

	public ArrayList<Station> getListStations() {
		return listStations;
	}

	public ArrayList<Station> getListStations_insideCentroid() {
		return listStations_insideCentroid;
	}

	public ArrayList<Station> getListStations_outsideCentroid() {
		return listStations_outsideCentroid;
	}

	public ArrayList<CarsharingDemand> getListDemand() {
		return listDemand;
	}

	public HashMap<Integer, Double> getMapCoefTravelPenalties() {
		return mapCoefTravelPenalties;
	}

	public HashMap<Integer, Double> getDemandOverTime_CumulatedAndNormalized() {
		return demandOverTime_CumulatedAndNormalized;
	}

	public void setListStations(ArrayList<Station> listStations) {
		this.listStations = listStations;
	}

	public void setListStations_insideCentroid(
			ArrayList<Station> listStations_insideCentroid) {
		this.listStations_insideCentroid = listStations_insideCentroid;
	}

	public void setListStations_outsideCentroid(
			ArrayList<Station> listStations_outsideCentroid) {
		this.listStations_outsideCentroid = listStations_outsideCentroid;
	}

	public void setListDemand(ArrayList<CarsharingDemand> listDemand) {
		this.listDemand = listDemand;
	}

	public void setMapCoefTravelPenalties(
			HashMap<Integer, Double> mapCoefTravelPenalties) {
		this.mapCoefTravelPenalties = mapCoefTravelPenalties;
	}

	public void setDemandOverTime_CumulatedAndNormalized(
			HashMap<Integer, Double> demandOverTime_CumulatedAndNormalized) {
		this.demandOverTime_CumulatedAndNormalized = demandOverTime_CumulatedAndNormalized;
	}
	
	public int get_nbStations() {
		return _nbStations;
	}

	public int get_nbTSInOneDay() {
		return _nbTSInOneDay;
	}
	
	public int get_TS_min() {
		return _TS_min;
	}

	public int get_nbTSInOneHour() {
		return _nbTSInOneHour;
	}

	public void set_nbTSInOneHour(int _nbTSInOneHour) {
		this._nbTSInOneHour = _nbTSInOneHour;
	}

	public double get_averagecarspeed_kmh() {
		return _averagecarspeed_kmh;
	}

	public void set_averagecarspeed_kmh(double _averagecarspeed_kmh) {
		this._averagecarspeed_kmh = _averagecarspeed_kmh;
	}

	/**
	 * @param lb
	 * @param ub
	 * @return a random integer value in the inverval [lb ; ub]
	 */
	public static int randomIntegerValue(int lb, int ub){
		return lb + ((int) ((ub + 1 - lb) * Math.random()));
	}

}
