package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import core.Generator_StationsAndDemand;

public class RandomGenerator_frame extends JDialog implements ActionListener {

	private static final long serialVersionUID = 135L;

	// GUI parameters
	private JPanel contentPanel;
	private JPanel generalParameters_panel;
	private JPanel footer_panel;
	private JButton btn_browse_workingDirectory;
	private JButton btn_run;
	private JButton btn_ok;
	private JButton btn_cancel;
	private JSpinner spi_NB_FILES;
	private JTextField tf_FileName;
	private JTextField tf_generalParam_saveDir;
	private JCheckBox chckbx_MakeStatistics;
	private RandomGeneratorParameters_panel randomGeneratorParameters_panel;

	// Other parameters
	private String _frameTitle;
	private String _dirName;
	private String _fileName;
	private Path path_saveDir;
	private GeneratorFrameStatus frameStatus;
	private int returnState;

	// constants
	public enum GeneratorFrameStatus {standAloneFrame, onlyTuneParametersFrame};
	public static final int APPROVE_OPTION = 1;
	public static final int CANCEL_OPTION = 0;
	
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {

				/*********************
				 * SYSTEM PARAMETERS *
				 *********************/
				try {UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");}
				catch (Exception e) {e.printStackTrace();}

				/********************
				 * Display the view *
				 ********************/	
				RandomGenerator_frame view = new RandomGenerator_frame(GeneratorFrameStatus.standAloneFrame);
				view.setVisible(true);
				
				// for debug :
				Path p = Paths.get("D:", "workspace eclipse", "carsharing_demand_generator_solver", "defaultParams", "testInputs.inputs");
				try {
					Element generatorParameters = new SAXBuilder().build(p.toFile()).getRootElement().getChild("generatorParameters");
					view.setGeneratorParameters(generatorParameters);
				} catch (JDOMException | IOException exception) {exception.printStackTrace();}
				
			}
		});
	}

	public RandomGenerator_frame(GeneratorFrameStatus frameStatus){

		// set default values
		_frameTitle = "Random Data Generator For One-Way Carsharing Systems";
		_dirName = "Carsharing Data";
		_fileName = "randomDataCS";
		path_saveDir = Paths.get(System.getProperty("user.home"), _dirName);
		this.frameStatus = frameStatus;
		this.setTitle(_frameTitle);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		initComponents();
		initSizeAndLocation();
	}

	private void initSizeAndLocation(){
		// Sizing
		this.pack();
		//		this.setMinimumSize(new Dimension(550, 650));
		//		this.setExtendedState(JFrame.MAXIMIZED_BOTH); // maximize the frame

		// Locating
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); // Place the frame at the center of the screen
	}

	private void initComponents(){
		contentPanel = new JPanel(new BorderLayout());
		this.setContentPane(contentPanel);

		switch(frameStatus){
		case standAloneFrame : {
			initHeader();
			initCenter();
			initFooter();
		}; break;
		case onlyTuneParametersFrame : {
			initCenter();
			initOKCancelButtons();
		}; break;
		}
	}

	private void initHeader() {
		generalParameters_panel = new JPanel(new MigLayout("", "[][120px:n,grow,fill][right][40px:n][50px:n][]", "[][]"));
		generalParameters_panel.setBorder(new TitledBorder(null, "General Parameters", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JLabel lbl_SaveDirectory = new JLabel("Save directory :");
		generalParameters_panel.add(lbl_SaveDirectory, "cell 0 0");

		tf_generalParam_saveDir = new JTextField(path_saveDir.toFile().toString());
		tf_generalParam_saveDir.setHorizontalAlignment(SwingConstants.CENTER);
		generalParameters_panel.add(tf_generalParam_saveDir, "cell 1 0 4 1,growx");

		btn_browse_workingDirectory = new JButton("...");
		generalParameters_panel.add(btn_browse_workingDirectory, "cell 5 0,alignx right");
		btn_browse_workingDirectory.addActionListener(this);

		JLabel lbl_FileNames = new JLabel("File name :");
		generalParameters_panel.add(lbl_FileNames, "cell 0 1");

		tf_FileName = new JTextField(_fileName);
		tf_FileName.setHorizontalAlignment(SwingConstants.CENTER);
		generalParameters_panel.add(tf_FileName, "cell 1 1");

		contentPanel.add(generalParameters_panel, BorderLayout.NORTH);

		JLabel lbl_NumberOfFiles = new JLabel("# Files :");
		generalParameters_panel.add(lbl_NumberOfFiles, "cell 2 1");

		spi_NB_FILES = new JSpinner();
		generalParameters_panel.add(spi_NB_FILES, "cell 3 1,growx");
		spi_NB_FILES.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));

		chckbx_MakeStatistics = new JCheckBox("make statistics");
		generalParameters_panel.add(chckbx_MakeStatistics, "cell 4 1 2 1,alignx right");
		chckbx_MakeStatistics.setSelected(true);
	}

	private void initCenter() {
		randomGeneratorParameters_panel = new RandomGeneratorParameters_panel();
		contentPanel.add(randomGeneratorParameters_panel, BorderLayout.CENTER);
	}

	private void initFooter() {

		footer_panel = new JPanel();
		btn_run = new JButton("Run !");
		btn_run.addActionListener(this);
		footer_panel.setLayout(new MigLayout("", "[grow][70px:n]", "[40px:n]"));
		footer_panel.add(btn_run, "cell 1 0,grow");
		contentPanel.add(footer_panel, BorderLayout.SOUTH);
	}

	private void initOKCancelButtons() {

		footer_panel = new JPanel();
		btn_ok = new JButton("OK");
		btn_cancel = new JButton("Cancel");
		btn_ok.addActionListener(this);
		btn_cancel.addActionListener(this);
		footer_panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		footer_panel.add(btn_ok);
		footer_panel.add(btn_cancel);
		contentPanel.add(footer_panel, BorderLayout.SOUTH);
	}

	public int openAndTune(Component parent){
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
		return returnState;
	}

	public Element getGeneratorParameters(){
		return randomGeneratorParameters_panel.getParameters();
	}
	
	public void setGeneratorParameters(Element params){
		randomGeneratorParameters_panel.setParameters(params);
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource(); 
		if(source == btn_browse_workingDirectory){
			final JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setCurrentDirectory(path_saveDir.getParent().toFile());
			int returnVal = fc.showOpenDialog(fc);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				path_saveDir = Paths.get(fc.getSelectedFile().getAbsolutePath()); 
				tf_generalParam_saveDir.setText(path_saveDir.toString());
			}
		}
		else if (source == btn_run){
			try {
				
				_fileName = tf_FileName.getText();
				if(!Files.exists(path_saveDir.resolve(_fileName + "-1.xml"))){

					Files.createDirectories(path_saveDir);
					Generator_StationsAndDemand generator = new Generator_StationsAndDemand(randomGeneratorParameters_panel.getParameters());

					for (int i=1 ; i <= (int)spi_NB_FILES.getValue() ; i++){
						generator.generate();
						generator.exportDataToXML(path_saveDir.resolve(_fileName + "-" + i + ".xml"));
						generator.reset();
					}
					
					JOptionPane.showMessageDialog(null, "Instance(s) generated !", "Info", JOptionPane.INFORMATION_MESSAGE);
				}				
				else {
					JOptionPane.showMessageDialog(null, "Error ! Some files with the same name have been already generated.", "Error", JOptionPane.ERROR_MESSAGE);
				}


			} catch (Exception e) {e.printStackTrace();}
		}
		else if (source == btn_ok){
			returnState = APPROVE_OPTION;
			this.setVisible(false);
		}
		else if (source == btn_cancel){
			returnState = CANCEL_OPTION;
			this.setVisible(false);
		}
	}

}
