package nl.plusminos.alcleantraz.ginger;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import nl.plusminos.alcleantraz.ginger.panels.Main;
import nl.plusminos.alcleantraz.ginger.panels.Schedule;
import nl.plusminos.alcleantraz.ginger.panels.Search;

public class Ginger {
	// Mainframe
	public final JFrame screen;
	
	// Panels
	public final Main mainPanel = new Main();
	public final Search searchPanel = new Search();
	public final Schedule schedulePanel = new Schedule();
	
	public Ginger() {
//		try {
//			UIManager.setLookAndFeel(
//			        UIManager.getSystemLookAndFeelClassName());
//		} catch (ClassNotFoundException | InstantiationException
//				| IllegalAccessException | UnsupportedLookAndFeelException e) {
//			e.printStackTrace();
//		}
		
		screen = new JFrame("Alcleantraz{Ginger}");
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.setSize(320, 300);
		screen.setVisible(true);
		
		screen.setLayout(new BorderLayout());
		screen.add(mainPanel.panel, BorderLayout.CENTER);
	}
	
	public static void main(String[] args) {
		new Ginger();
	}
}
