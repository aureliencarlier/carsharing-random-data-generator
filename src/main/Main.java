package main;

import java.awt.EventQueue;

import javax.swing.UIManager;

import view.RandomGenerator_frame;
import view.RandomGenerator_frame.GeneratorFrameStatus;

public class Main {

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
			}
		});

	}

}
