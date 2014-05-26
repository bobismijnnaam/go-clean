package nl.plusminos.alcleantraz.gui;

import javax.swing.JFrame;

public class AlcleantrazGui {
	// Mainframe
	public final JFrame screen;
	
	// Panels
	public static final AlcleantrazGui inst = new AlcleantrazGui();
	public final Main mainPanel = new Main();
	public final PhaseOne phaseOnePanel = new PhaseOne();
	public final Schedule schedulePanel = new Schedule();
	
	public AlcleantrazGui() {
		screen = new JFrame("Alcleantraz{Peach}");
		screen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.setSize(320, 360);
		screen.setVisible(true);
		
		screen.add(mainPanel.panel);
	}
	
	public static void main(String[] args) {
	}
}
