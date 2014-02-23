package nl.plusminos.alcleantraz.vanilla;

import jenes.chromosome.IntegerChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;

public class VanillaFit extends Fitness<IntegerChromosome> {

	// Constants
	private final int NO_JOB = -1;
	
	// Settings
	private final int PERSONS;
	private final int JOBS;
	private final int WEEKS;
	private final int SLOTS;
	private final int[] LAST_WEEK;
	
	// Preset variables
	private boolean[] inWeek;
	
	public VanillaFit(int persons, int jobs, int weeks, int[] lastWeek) {
		super(true);
		// super(false, false, false, true); // This comes later
		// The order is determined by the todo-list (in notes.txt)
		
		PERSONS = persons;
		JOBS = jobs;
		WEEKS = weeks;
		SLOTS = jobs * weeks;
		LAST_WEEK = lastWeek;
		
		inWeek = new boolean[PERSONS]; // The array to store whether or not a person has a job that week
	}
	
	@Override
	public void evaluate(Individual<IntegerChromosome> arg0) {
		// TODO Auto-generated method stub
		
	}

}
