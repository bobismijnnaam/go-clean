package nl.plusminos.alcleantraz.gui;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import nl.plusminos.alcleantraz.peach.Individual;

public class PhaseOne {
	public final JPanel panel = new JPanel(new MigLayout());
	
	public PhaseOne() {
		
	}

	public void start(String[] names, Individual foundIndividual) {
		AlcleantrazGui.inst.screen.setSize(1024, 768);
		
	}
}
