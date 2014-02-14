package nl.plusminos.alcleantraz;

import jenes.chromosome.IntegerChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;

// TODO: als rooster 11 lang is iedereen alles 1 x, als rooster 22 lang is iedereen alles 2 x, etc.
public class AlcaFit extends Fitness<IntegerChromosome> {
	
	public static enum Job {
		Kitchen,
		Hallway,
		Toilet,
		Douche,
		None;
		
		public static Job toJob(int j) {
			if (j >= 0 && j < 3) {
				return Kitchen;
			} else if (j >= 3 && j < 5) {
				return Hallway;
			} else if (j == 5) {
				return Toilet;
			} else if (j == 6) {
				return Douche;
			}
			
			return None;
		}
		
		public static int toInt(Job j) {
			switch (j) {
				case Kitchen:
					return 0;
				case Hallway:
					return 3;
				case Toilet:
					return 5;
				case Douche:
					return 6;
			}
			
			return 9999;
		}
	}
	
	// Make the class
	public static final AlcaFit fitness = new AlcaFit();
	
	// Settings
	private static int PERSONS = 0;
	private static int JOBS = 0;
	private static int WEEKS = 0;
	private static int SLOTS = 0;
	private static boolean CHECK_PREVIOUS_SCHEDULE = false;
	
	// Preset variables
	private boolean[] inWeek;
	
	private AlcaFit() {
		super(true);
	}
	
	public static void initialize(int persons, int jobs, int weeks) {
		AlcaFit.PERSONS = persons;
		AlcaFit.JOBS = jobs;
		AlcaFit.WEEKS = weeks;
		AlcaFit.SLOTS = JOBS * WEEKS;
	}
	
	/**
	 * Gets the job position in the current chromosome. Does not check bounds
	 * @param job The job in the week
	 * @param week The week in the schedule
	 * @return The position
	 */
	public static int getJob(int job, int week) {
		return week * AlcaFit.JOBS + job;
	}
	
	/**
	 * Resets the internal array inWeek[PERSONS] used for keeping track of persons
	 * present in this week.
	 */
	private void resetInWeekArray() {
		for (int i = 0; i < PERSONS; i++) {
			inWeek[i] = false;
		}
	}
	
	/**
	 * Checks if there are any double persons in any week.
	 * @param chrom The chromosome of an individual
	 * @return Whether or not it has a week with doubles.
	 */
	public boolean hasNoDoublesPerWeek(IntegerChromosome chrom) {
		int pos; // The position of a job in the chromosome
		int person; // The person on a job
		inWeek = new boolean[AlcaFit.PERSONS]; // The array to store whether or not a person has a job that week
		
		resetInWeekArray();
		
		// Loop through the weeks
		for (int w = 0; w < WEEKS; w++) {
			// Loop through the jobs
			for (int j = 0; j < JOBS; j++) {
				// Get the position of the job in the chromosome
				pos = getJob(j, w);
				// Get the person on the job
				person = chrom.getValue(pos);
				
				if (inWeek[person]) { // Return false if this person already appears in the week
					return false;
				} else { // Set the slot to true because this person has at least a job this week
					inWeek[person] = true;
				}
			}
			
			// Reset the inWeek[PERSONS] array for the next week
			resetInWeekArray();
		}
		
		return true;
	}
	
	/**
	 * Returns the job the person had the previous week. The function will keep week 0 in mind.
	 * @param chrom The chromosome of the individual
	 * @param week The week you are currently looking at. The function will look at the week before that.
	 * @param person The person you want to know the previous job of
	 * @return The previous job
	 */
	public int getPreviousJob(IntegerChromosome chrom, int week, int person) {
		if (week == 0) {
			if (CHECK_PREVIOUS_SCHEDULE) {
				// TODO
				return -1;
			} else {
				return -1;
			}
		} else {
			int pos;
			int prevPerson;
			
			for (int i = 0; i < JOBS; i++) {
				pos = getJob(i, week - 1);
				prevPerson = chrom.getValue(pos);
				if (prevPerson == person) {
					return i;
				}
			}
			
			return -1;
		}
	}
	
	/**
	 * Checks whether there are any people who have the same job as the previous week
	 * @param chrom The chromosome of the individual
	 */
	public boolean hasNoOverlap(IntegerChromosome chrom) {
		int person;
		int pos;
		
		for (int w = 0; w < WEEKS; w++) {
			for (int j = 0; j < JOBS; j++) {
				pos = getJob(j, w);
				person = chrom.getValue(pos);
				if (j == getPreviousJob(chrom, w, person)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	// TODO: Extra objective by checking spread of everyones task?
	/**
	 * Evaluates a given individual and sets its score.
	 */
	public void evaluate(Individual<IntegerChromosome> individual) {
		if (PERSONS == 0 || JOBS == 0 || WEEKS == 0) { // Check for initialization
			return;
		} else { // Go ahead!
			IntegerChromosome chrom = individual.getChromosome();
			
			// Test if this schedule has doubles in a week. False if it has, true if it doesn't
			boolean noDoubles = hasNoDoublesPerWeek(chrom);
			
			boolean noOverlap = hasNoOverlap(chrom);
			
			int score = 0;
			if (noDoubles) {
				score++;
			}
			if (noOverlap) {
				score++;
			}
			
			individual.setScore(score);
		}
	}

}
