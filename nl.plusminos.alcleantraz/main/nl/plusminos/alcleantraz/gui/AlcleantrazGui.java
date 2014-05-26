package nl.plusminos.alcleantraz.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import nl.plusminos.alcleantraz.gui.Main;
import nl.plusminos.alcleantraz.gui.Schedule;
import nl.plusminos.alcleantraz.gui.Search;

public class AlcleantrazGui {
	// Mainframe
	public final JFrame screen;
	
	// Panels
	public static final AlcleantrazGui inst = new AlcleantrazGui();
	public final Main mainPanel = new Main();
	public final Search searchPanel = new Search();
	public final Schedule schedulePanel = new Schedule();
	
	public AlcleantrazGui() {
//		try {
//			UIManager.setLookAndFeel(
//			        UIManager.getSystemLookAndFeelClassName());
//		} catch (ClassNotFoundException | InstantiationException
//				| IllegalAccessException | UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
		
		screen = new JFrame("Alcleantraz{Ginger}");
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.setSize(320, 360);
		screen.setVisible(true);
		
//		screen.setLayout(new BorderLayout());
		screen.add(mainPanel.panel);
	}
	
	public static void main(String[] args) {
	}
}
