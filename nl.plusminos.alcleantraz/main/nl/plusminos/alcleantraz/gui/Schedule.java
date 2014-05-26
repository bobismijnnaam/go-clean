package nl.plusminos.alcleantraz.gui;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import nl.plusminos.alcleantraz.peach.Info;
import nl.plusminos.alcleantraz.peach.Scheduler;

public class Schedule implements Runnable {
	public final JPanel panel;
	
	private JLabel status;
	private JLabel generationLabel;
	private JLabel fitnessLabel;
	
	private String[] names;
	private Scheduler scheduler;
	
	public Schedule() {
		// Initialize main panel
		panel = new JPanel(new MigLayout("fill", // , debug
				"",
				"[10][10][10]"));
		
		// Initialize 
		status = new JLabel("Starting GA.", SwingConstants.CENTER);
		generationLabel = new JLabel("No generation yet", SwingConstants.CENTER);
		fitnessLabel = new JLabel("No fitness yet", SwingConstants.CENTER);
		
		// Set central alignment of labels
		status.setAlignmentX(0.5f);
		
		// Add to panel
		panel.add(status, "wrap, growx");
		panel.add(generationLabel, "wrap, growx");
		panel.add(fitnessLabel, "growx");
	}
	
	public void start(String[] names, int threeWeeks) {
		this.names = names;
		
		scheduler = new Scheduler(names.length, threeWeeks);
		new Thread(scheduler).start();
		
		new Thread(this).start();
	}

	@Override
	public void run() {
		while (scheduler.foundIndividual == null) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			int generation = scheduler.generation;
			int fit = scheduler.foundFitness;
			
			if (generation != 0) {
				int genPercent = (int) (generation / ((float) Info.MAXGENERATION) * 100);
				generationLabel.setText("Generation: " + generation + "(" + (genPercent) + ")%");
				
				status.setText("Looking for initial schedule"); // animation?
			}
			
			if (fit != 9999) {
				fitnessLabel.setText("Best fitness discovered: " + fit);
			}
		}
	}
}
