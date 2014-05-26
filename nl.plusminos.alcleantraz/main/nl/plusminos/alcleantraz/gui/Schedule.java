package nl.plusminos.alcleantraz.gui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import nl.plusminos.alcleantraz.peach.Info;
import nl.plusminos.alcleantraz.peach.Scheduler;

public class Schedule implements Runnable {
	public final JPanel panel = new JPanel(new MigLayout("fill",
			"",
			"[10][10][10]"));;
	
	private JPanel generationPanel = new JPanel(new MigLayout());
	
	private JLabel status;
	private JLabel generationLabel;
	private JLabel generationBar;
	private JLabel fitnessLabel;
	
	private String[] names;
	private Scheduler scheduler;
	
	private long searchStart;
	
	public Schedule() {
		// Initialize 
		status = new JLabel("Starting GA.");
		generationLabel = new JLabel("No generation yet", SwingConstants.CENTER);
		generationBar = new JLabel();
		fitnessLabel = new JLabel("No fitness yet", SwingConstants.CENTER);
		
		// Setup labels a bit
		generationBar.setBackground(Color.ORANGE);
		generationBar.setOpaque(true);
		
		// Prepare generationPanel
		generationPanel.add(generationBar, "pos 0% 0% 0% 100%");
		generationPanel.add(generationLabel, "pos 0% 0% 100% 100%");
		generationPanel.setComponentZOrder(generationLabel, 0);
		
		// Add to panel
		panel.add(status, "wrap, w 55%, align center");
		panel.add(generationPanel, "growx, wrap, h 20%");
		panel.add(fitnessLabel, "growx");
	}
	
	public void start(String[] names, int threeWeeks) {
		this.names = names;
		searchStart = System.currentTimeMillis();
		
		scheduler = new Scheduler(names.length, threeWeeks);
		new Thread(scheduler).start();
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (scheduler.foundIndividual == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			int generation = scheduler.generation;
			int fit = scheduler.foundFitness;
			
			if (generation != 0) {
				int genPercent = (int) (generation / ((float) Info.MAXGENERATION) * 100);
				generationLabel.setText("Generation: " + generation + " (" + (genPercent) + "%)");
				
				MigLayout ml = (MigLayout) generationPanel.getLayout();
				ml.setComponentConstraints(generationBar, "pos 0% 0% " + genPercent + "% 100%");
				
				int amountOfDots = (int) ((System.currentTimeMillis() - searchStart) / 800) % 4;
				status.setText("Looking for initial schedule"); // animation?
				for (int i = 0; i < amountOfDots; i++) {
					status.setText(status.getText() + ".");
				}
				
			}
			
			if (fit != 9999) {
				fitnessLabel.setText("Best fitness discovered: " + fit);
			}
		}
		
		// Setup finding schedule screen
		AlcleantrazGui.inst.screen.setVisible(false);
		AlcleantrazGui.inst.screen.remove(panel);
		AlcleantrazGui.inst.screen.add(AlcleantrazGui.inst.phaseOnePanel.panel);
		
		AlcleantrazGui.inst.phaseOnePanel.start(names, scheduler.foundIndividual);
		AlcleantrazGui.inst.screen.setVisible(true);
	}
}
