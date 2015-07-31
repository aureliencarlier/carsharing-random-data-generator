package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.jdom2.Element;



public class RandomGeneratorParameters_panel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	enum GenerationMethod {Uniform,	Centroid};
	
	// GUI components
	private JPanel panel_centroidMethodParameters;
	private JSpinner spi_NB_STATIONS;
	private JSpinner spi_STATION_SIZE_LB;
	private JSpinner spi_STATION_SIZE_UB;
	private JSpinner spi_NB_DEMAND;
	private JSpinner spi_TIME_STEP_MIN;
	private JSpinner spi_SIDE_OF_THE_WHOLE_GRAPH_M;
	private JSpinner spi_AVERAGE_CARSPEED_KMH;
	private JSpinner spi_MAX_DISTANCE_FOR_A_TRAVEL_M;
	private JSpinner spi_PROPORTION_CENTROID_AREA_P;
	private JSpinner spi_CONCENTRATION_CENTER_P;
	private JSpinner spi_MORNING_RUSH_LB_H;
	private JSpinner spi_MORNING_RUSH_UB_H;
	private JSpinner spi_DEMAND_PROP_DURING_MORNING_RUSH_P;
	private JSpinner spi_EVENING_RUSH_LB_H;
	private JSpinner spi_EVENING_RUSH_UB_H;
	private JSpinner spi_DEMAND_PROP_DURING_EVENING_RUSH_P;
	private JSpinner spi_PENALTY_COEF_RUSHHOURS_P;
	
	private JComboBox <GenerationMethod> cb_generationMethod;
	
	private JButton btn_tuneDemandProfile;
	
	private Integer[] demandProfileValues = new Integer[]{5, 5, 5, 7, 10, 15, 20, 50, 80, 70, 60, 65, 70, 63, 55, 73, 90, 85, 80, 53, 25, 18, 10, 8};
	
	/**
	 * Create a new panel
	 */
	public RandomGeneratorParameters_panel(){
		init_panel();
		init_header();
		init_sharedParameters();
		init_centroidMethodParameters();
		init_footer();
	}

	private void init_panel() {
		this.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		this.setLayout(new MigLayout("", "[grow,fill]", "[][][]"));
	}
	
	private void init_header() {
		JPanel panel_generationMethod = new JPanel();
		this.add(panel_generationMethod, "cell 0 0,grow"); // "cell column row"
		panel_generationMethod.setLayout(new MigLayout("", "[250.00:n,grow][190px:n,grow,fill]", "[]"));

		JLabel lbl_generationMethod = new JLabel("Generation method  :");
		panel_generationMethod.add(lbl_generationMethod, "cell 0 0");
		
		cb_generationMethod = new JComboBox <GenerationMethod> ();
		cb_generationMethod.setModel(new DefaultComboBoxModel <GenerationMethod> (GenerationMethod.values()));
		cb_generationMethod.setSelectedIndex(1);
		panel_generationMethod.add(cb_generationMethod, "cell 1 0");
		cb_generationMethod.addActionListener(this);
		cb_generationMethod.setFocusable(false);
	}
	
	private void init_sharedParameters() {
		JPanel panel_sharedParameters = new JPanel();
		this.add(panel_sharedParameters, "cell 0 1,grow");
		panel_sharedParameters.setLayout(new MigLayout("", "[250px:n,grow][80px:n,fill][30px:n,center][80px:n,fill]", "[][][][][][][]"));

		JLabel lbl_NumberOfStations = new JLabel("Number of stations :");
		panel_sharedParameters.add(lbl_NumberOfStations, "cell 0 0");

		spi_NB_STATIONS = new JSpinner();
		spi_NB_STATIONS.setModel(new SpinnerNumberModel(new Integer(50), new Integer(1), null, new Integer(1)));
		panel_sharedParameters.add(spi_NB_STATIONS, "cell 1 0 3 1");

		JLabel lbl_RangeStationSize = new JLabel("Parking range (# parking slots) :");
		panel_sharedParameters.add(lbl_RangeStationSize, "cell 0 1");

		spi_STATION_SIZE_LB = new JSpinner();
		spi_STATION_SIZE_LB.setModel(new SpinnerNumberModel(new Integer(6), new Integer(1), null, new Integer(1)));
		panel_sharedParameters.add(spi_STATION_SIZE_LB, "cell 1 1");

		JLabel lbl_and1 = new JLabel("and");
		panel_sharedParameters.add(lbl_and1, "cell 2 1");

		spi_STATION_SIZE_UB = new JSpinner();
		spi_STATION_SIZE_UB.setModel(new SpinnerNumberModel(new Integer(10), new Integer(1), null, new Integer(1)));
		panel_sharedParameters.add(spi_STATION_SIZE_UB, "cell 3 1");

		JLabel lbl_NumberOfDemand = new JLabel("Number of demand (# requests) :");
		panel_sharedParameters.add(lbl_NumberOfDemand, "cell 0 2");

		spi_NB_DEMAND = new JSpinner();
		spi_NB_DEMAND.setModel(new SpinnerNumberModel(new Integer(500), new Integer(1), null, new Integer(1)));
		panel_sharedParameters.add(spi_NB_DEMAND, "cell 1 2 3 1");

		JLabel lbl_TimeStep = new JLabel("Time step (min) :");
		panel_sharedParameters.add(lbl_TimeStep, "cell 0 3");

		spi_TIME_STEP_MIN = new JSpinner();
		spi_TIME_STEP_MIN.setModel(new SpinnerListModel(new String[] {"1", "2", "3", "4", "5", "6", "8", "9", "10", "12", "15", "16", "18", "20", "24", "30", "32", "36", "40", "45", "48", "60", "72", "80", "90", "96", "120", "144", "160", "180", "240", "288", "360", "480", "720", "1440"}));
		panel_sharedParameters.add(spi_TIME_STEP_MIN, "cell 1 3 3 1");
		spi_TIME_STEP_MIN.setValue("10");
		((DefaultEditor) spi_TIME_STEP_MIN.getEditor()).getTextField().setHorizontalAlignment(JTextField.RIGHT);

		JLabel lbl_SideOfTheGraph = new JLabel("Area length side (m) :");
		panel_sharedParameters.add(lbl_SideOfTheGraph, "cell 0 4");

		spi_SIDE_OF_THE_WHOLE_GRAPH_M = new JSpinner();
		spi_SIDE_OF_THE_WHOLE_GRAPH_M.setModel(new SpinnerNumberModel(new Integer(100000), new Integer(1), null, new Integer(1)));
		panel_sharedParameters.add(spi_SIDE_OF_THE_WHOLE_GRAPH_M, "cell 1 4 3 1");

		JLabel lbl_AverageCarSpeed = new JLabel("Average car speed (km/h) :");
		panel_sharedParameters.add(lbl_AverageCarSpeed, "cell 0 5");

		spi_AVERAGE_CARSPEED_KMH = new JSpinner();
		spi_AVERAGE_CARSPEED_KMH.setModel(new SpinnerNumberModel(new Integer(70), new Integer(10), null, new Integer(1)));
		panel_sharedParameters.add(spi_AVERAGE_CARSPEED_KMH, "cell 1 5 3 1");

		JLabel lbl_MaximumDistanceForATravel = new JLabel("Maximum trip distance (m) :");
		panel_sharedParameters.add(lbl_MaximumDistanceForATravel, "cell 0 6");

		spi_MAX_DISTANCE_FOR_A_TRAVEL_M = new JSpinner();
		spi_MAX_DISTANCE_FOR_A_TRAVEL_M.setModel(new SpinnerNumberModel(new Integer(60000), new Integer(1), null, new Integer(1)));
		panel_sharedParameters.add(spi_MAX_DISTANCE_FOR_A_TRAVEL_M, "cell 1 6 3 1");
	}

	private void init_centroidMethodParameters() {
		panel_centroidMethodParameters = new JPanel();
		this.add(panel_centroidMethodParameters, "cell 0 2,grow");
		panel_centroidMethodParameters.setLayout(new MigLayout("", "[250px:n,grow][80px:n,fill][30px:n,center][80px:n,fill]", "[][][][][][][][]"));

		JLabel lbl_proportionOfTheCentroidArea = new JLabel("Centroid area dimension (% of total area) :");
		panel_centroidMethodParameters.add(lbl_proportionOfTheCentroidArea, "cell 0 0");

		spi_PROPORTION_CENTROID_AREA_P = new JSpinner();
		spi_PROPORTION_CENTROID_AREA_P.setModel(new SpinnerNumberModel(10, 0, 100, 1));
		panel_centroidMethodParameters.add(spi_PROPORTION_CENTROID_AREA_P, "cell 1 0 3 1");

		JLabel lbl_concentrationInTheCenter = new JLabel("Density in the centroid area (%) :");
		panel_centroidMethodParameters.add(lbl_concentrationInTheCenter, "cell 0 1");

		spi_CONCENTRATION_CENTER_P = new JSpinner();
		spi_CONCENTRATION_CENTER_P.setModel(new SpinnerNumberModel(35, 0, 100, 1));
		panel_centroidMethodParameters.add(spi_CONCENTRATION_CENTER_P, "cell 1 1 3 1");
		
		JLabel lbl_demandDistribution = new JLabel("Demand distribution :");
		panel_centroidMethodParameters.add(lbl_demandDistribution, "cell 0 2");
		
		btn_tuneDemandProfile = new JButton("Tune");
		panel_centroidMethodParameters.add(btn_tuneDemandProfile, "cell 3 2");
		btn_tuneDemandProfile.addActionListener(this);
				
		JLabel lbl_MorningRushTime = new JLabel("Morning rush (sub -> centre) time slot (h) :");
		panel_centroidMethodParameters.add(lbl_MorningRushTime, "cell 0 3");

		spi_MORNING_RUSH_LB_H = new JSpinner();
		spi_MORNING_RUSH_LB_H.setModel(new SpinnerNumberModel(7, 0, 23, 1));
		panel_centroidMethodParameters.add(spi_MORNING_RUSH_LB_H, "cell 1 3");

		JLabel label_4 = new JLabel("and");
		panel_centroidMethodParameters.add(label_4, "cell 2 3");

		spi_MORNING_RUSH_UB_H = new JSpinner();
		spi_MORNING_RUSH_UB_H.setModel(new SpinnerNumberModel(9, 0, 23, 1));
		panel_centroidMethodParameters.add(spi_MORNING_RUSH_UB_H, "cell 3 3");

		JLabel lbl_DemandProportionDuringMorningRush = new JLabel("Demand proportion during morning rush (%) :");
		panel_centroidMethodParameters.add(lbl_DemandProportionDuringMorningRush, "cell 0 4");

		spi_DEMAND_PROP_DURING_MORNING_RUSH_P = new JSpinner();
		spi_DEMAND_PROP_DURING_MORNING_RUSH_P.setModel(new SpinnerNumberModel(80, 0, 100, 1));
		panel_centroidMethodParameters.add(spi_DEMAND_PROP_DURING_MORNING_RUSH_P, "cell 1 4 3 1");

		JLabel lbl_EveningRushTime = new JLabel("Evening rush (centre -> sub) time slot (h) :");
		panel_centroidMethodParameters.add(lbl_EveningRushTime, "cell 0 5");

		spi_EVENING_RUSH_LB_H = new JSpinner();
		spi_EVENING_RUSH_LB_H.setModel(new SpinnerNumberModel(17, 0, 23, 1));
		panel_centroidMethodParameters.add(spi_EVENING_RUSH_LB_H, "cell 1 5");

		JLabel label_5 = new JLabel("and");
		panel_centroidMethodParameters.add(label_5, "cell 2 5");

		spi_EVENING_RUSH_UB_H = new JSpinner();
		spi_EVENING_RUSH_UB_H.setModel(new SpinnerNumberModel(20, 0, 23, 1));
		panel_centroidMethodParameters.add(spi_EVENING_RUSH_UB_H, "cell 3 5");

		JLabel DemandProportionDuringEveningRush = new JLabel("Demand proportion during evening rush (%) :");
		panel_centroidMethodParameters.add(DemandProportionDuringEveningRush, "cell 0 6");

		spi_DEMAND_PROP_DURING_EVENING_RUSH_P = new JSpinner();
		spi_DEMAND_PROP_DURING_EVENING_RUSH_P.setModel(new SpinnerNumberModel(60, 0, 100, 1));
		panel_centroidMethodParameters.add(spi_DEMAND_PROP_DURING_EVENING_RUSH_P, "cell 1 6 3 1");

		JLabel lbl_PenaltyCoeficient = new JLabel("Time penalty during rush hours (%) :");
		panel_centroidMethodParameters.add(lbl_PenaltyCoeficient, "cell 0 7");

		spi_PENALTY_COEF_RUSHHOURS_P = new JSpinner();
		spi_PENALTY_COEF_RUSHHOURS_P.setModel(new SpinnerNumberModel(new Integer(160), new Integer(0), null, new Integer(1)));
		panel_centroidMethodParameters.add(spi_PENALTY_COEF_RUSHHOURS_P, "cell 1 7 3 1");
	}
	
	private void init_footer() {
		
		
	}
	
	public Element getParameters(){
		Element generatorParameters = new Element("generatorparameters");

		generatorParameters.addContent(new Element("generationmethod").
				setAttribute("value", cb_generationMethod.getSelectedItem().toString()));
		generatorParameters.addContent(new Element("nbstations").
				setAttribute("value", Integer.toString((int)spi_NB_STATIONS.getValue())));
		generatorParameters.addContent(new Element("nbdemands").
				setAttribute("value", Integer.toString((int)spi_NB_DEMAND.getValue())));
		generatorParameters.addContent(new Element("stationsize").
				setAttribute("lb", 	Integer.toString((int)spi_STATION_SIZE_LB.getValue())).
				setAttribute("ub", 	Integer.toString((int)spi_STATION_SIZE_UB.getValue())));
		generatorParameters.addContent(new Element("time").
				setAttribute("timestepmin",		(String)spi_TIME_STEP_MIN.getValue()).
				setAttribute("nbtsinoneday",	Integer.toString(1440 / Integer.parseInt((String)spi_TIME_STEP_MIN.getValue()))).
				setAttribute("nbtsinonehour",	Integer.toString(60 / Integer.parseInt((String)spi_TIME_STEP_MIN.getValue()))));
		generatorParameters.addContent(new Element("sideofthewholegraphm").
				setAttribute("value", Integer.toString((int)spi_SIDE_OF_THE_WHOLE_GRAPH_M.getValue())));
		generatorParameters.addContent(new Element("averagecarspeedkmh").
				setAttribute("value", Integer.toString((int)spi_AVERAGE_CARSPEED_KMH.getValue())));
		generatorParameters.addContent(new Element("maxdistanceforatravelm").
				setAttribute("value", Integer.toString((int)spi_MAX_DISTANCE_FOR_A_TRAVEL_M.getValue())));
		generatorParameters.addContent(new Element("proportioncentroidareap").
				setAttribute("value", Double.toString((double) (int)spi_PROPORTION_CENTROID_AREA_P.getValue() / 100)));
		generatorParameters.addContent(new Element("concentrationcenterp").
				setAttribute("value", Double.toString((double) (int)spi_CONCENTRATION_CENTER_P.getValue() / 100)));
		generatorParameters.addContent(new Element("morningrush").
				setAttribute("lb", Integer.toString((int)spi_MORNING_RUSH_LB_H.getValue())).
				setAttribute("ub", Integer.toString((int)spi_MORNING_RUSH_UB_H.getValue())).
				setAttribute("demandprop", Double.toString((double) (int)spi_DEMAND_PROP_DURING_MORNING_RUSH_P.getValue() / 100)));
		generatorParameters.addContent(new Element("eveningrush").
				setAttribute("lb", Integer.toString((int)spi_EVENING_RUSH_LB_H.getValue())).
				setAttribute("ub", Integer.toString((int)spi_EVENING_RUSH_UB_H.getValue())).
				setAttribute("demandprop", Double.toString((double) (int)spi_DEMAND_PROP_DURING_EVENING_RUSH_P.getValue() / 100)));
		generatorParameters.addContent(new Element("penaltycoefrushhours").
				setAttribute("value", Double.toString((double) (int)spi_PENALTY_COEF_RUSHHOURS_P.getValue() / 100)));

		// demand over time
		Element demandsOverTime = new Element("demandsovertime");
		for(int hour = 0 ; hour < demandProfileValues.length ; hour++){
			String value = Integer.toString(demandProfileValues[hour]);
			demandsOverTime.addContent(new Element("demand").setAttribute("hour", Integer.toString(hour)).setAttribute("value", value));
		}
		generatorParameters.addContent(demandsOverTime);
		
		return generatorParameters;
	}
	
	public void setParameters(Element params){ // TODO
		for(Element e : params.getChildren()){
			switch(e.getName()){
			case "generationmethod" :{
				cb_generationMethod.setSelectedItem((e.getAttributeValue("value").equals("centroid"))? GenerationMethod.Centroid : null);
				cb_generationMethod.setSelectedItem((e.getAttributeValue("value").equals("uniform"))? GenerationMethod.Uniform : null);
			}; break;
			case "nbstations" :{

			}; break;
			case "nbdemands" :{

			}; break;
			case "stationsize" :{

			}; break;
			case "time" :{

			}; break;
			case "sideofthewholegraphm" :{

			}; break;
			case "averagecarspeedkmh" :{

			}; break;
			case "maxdistanceforatravelm" :{

			}; break;
			case "proportioncentroidareap" :{

			}; break;
			case "concentrationcenterp" :{

			}; break;
			case "morningrush" :{

			}; break;
			case "eveningrush" :{

			}; break;
			case "penaltycoefrushhours" :{

			}; break;
			case "demandsovertime" :{

			}; break;
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == btn_tuneDemandProfile){
			final TuneDemandProfile_frame tdp = new TuneDemandProfile_frame(demandProfileValues);
			int returnVal = tdp.openAndTune(this);
			if(returnVal == TuneDemandProfile_frame.APPROVE_OPTION){ // update values
				for (int i=0 ; i < demandProfileValues.length ; i++){
					demandProfileValues[i] = ((JSlider)tdp.getMapDemand().get(i)).getValue();
				}
			}			
		}
	}

}
