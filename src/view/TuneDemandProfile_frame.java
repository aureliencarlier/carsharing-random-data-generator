package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

public class TuneDemandProfile_frame extends JDialog implements ActionListener, ChangeListener {
	
	private static final long serialVersionUID = 1L;
	
	// GUI components
	private JButton btn_OK;
	private JButton btn_Cancel;
	
	// other components
	private HashMap<Integer, JSlider> mapDemand;
	private int returnState; 
	public static final int APPROVE_OPTION = 1;
	public static final int CANCEL_OPTION = 0;
	
	public TuneDemandProfile_frame(Integer ... currentValues) {
		this.setTitle("Tune Demand Profile");
		mapDemand = new HashMap<Integer, JSlider>();
		returnState = -1;
		
		JPanel contentPanel = new JPanel(new BorderLayout());
		this.setContentPane(contentPanel);
		
		JPanel panel_demandDistribution = new JPanel(new MigLayout());
		contentPanel.add(panel_demandDistribution, BorderLayout.CENTER);
		panel_demandDistribution.setBorder(new TitledBorder(null, "Demand distribution", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		for(int i=0 ; i < currentValues.length ; i++){
			JSlider slider = new JSlider(SwingConstants.VERTICAL, 0, 100, currentValues[i]);
			slider.setFocusable(false);
			slider.addChangeListener(this);
			panel_demandDistribution.add(slider, "cell " + i + " " + 0 + ", center, wmin 25");
			JLabel jl = new JLabel("" + i);
			jl.setHorizontalAlignment(SwingConstants.CENTER);
			panel_demandDistribution.add(jl, "cell " + i + " " + 1 + ", center, wmin 25");
			mapDemand.put(i, slider);
		}
		panel_demandDistribution.add(new JLabel("time (hours)"), "cell " + currentValues.length + " " + 1);
		
		JPanel panel_buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		contentPanel.add(panel_buttons, BorderLayout.SOUTH);
		btn_OK = new JButton("OK");
		btn_Cancel = new JButton("Cancel");
		panel_buttons.add(btn_OK);
		panel_buttons.add(btn_Cancel);
		btn_Cancel.addActionListener(this);
		btn_OK.addActionListener(this);
		
		this.pack();
	}
	
	public int openAndTune(Component parent){
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
		return returnState;
	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if(ae.getSource() == btn_OK){
			returnState = APPROVE_OPTION;
		}
		else if(ae.getSource() == btn_Cancel){
			returnState = CANCEL_OPTION;
		}
		this.setVisible(false);
	}

	public HashMap<Integer, JSlider> getMapDemand() {
		return mapDemand;
	}

	@Override
	public void stateChanged(ChangeEvent ce) {
		JSlider js = (JSlider) ce.getSource();
		if(js.getValueIsAdjusting()){
			js.setToolTipText("" + js.getValue());
		}
	}

}
