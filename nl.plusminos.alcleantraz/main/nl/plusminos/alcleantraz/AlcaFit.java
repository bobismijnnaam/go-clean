package nl.plusminos.alcleantraz;

import jenes.chromosome.IntegerChromosome;
import jenes.population.Fitness;
import jenes.population.Individual;

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
	
	/**
	 * Initializes the class.
	 * @param persons The amount of persons you want to roster
	 * @param jobs The amount of jobs you have
	 * @param weeks The amount of weeks you want to plan ahead
	 * @param lastWeek The last week of previous schedule. Leave null if you don't want to consider it.
	 */
	public AlcaFit(int persons, int jobs, int weeks, int[] lastWeek) {
		super(true);
		
		PERSONS = persons;
		JOBS = jobs;
		WEEKS = weeks;
		SLOTS = jobs * weeks;
		LAST_WEEK = lastWeek;
	}
	
	/**
	 * Gets the job position in the current chromosome. Does not check bounds
	 * @param job The job in the week
	 * @param week The week in the schedule
	 * @return The position
	 */
	public int getJob(int job, int week) {
		return week * JOBS + job;
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
		inWeek = new boolean[PERSONS]; // The array to store whether or not a person has a job that week
		
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
			if (LAST_WEEK != null) {
				// TODO
				return NO_JOB;
			} else {
				return NO_JOB;
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
	
	/**
	 * Counts all the jobs where the person doing the job the previous week is the same as this week.
	 * @param chrom The chromosome to evaluate
	 * @return The cases where last week's person was the same as this week
	 */
	public int countOverlap(IntegerChromosome chrom) {
		int person;
		int pos;
		int overlaps = 0;
		
		for (int w = 0; w < WEEKS; w++) {
			for (int j = 0; j < JOBS; j++) {
				pos = getJob(j, w);
				person = chrom.getValue(pos);
				if (j == getPreviousJob(chrom, w, person)) {
					overlaps++;
				}
			}
		}
		
		return overlaps;
	}
	
	/**
	 * Checks whether or not each person is assigned each job at least once
	 * @param chrom The chromosome to evaluate
	 * @return Return true if every person is assigned every job at least once.
	 */
	public boolean hasPerfectAssignment(IntegerChromosome chrom) {
		boolean[][] hasDone = new boolean[PERSONS][JOBS]; 
		for (int p = 0; p < PERSONS; p++) {
			hasDone[p] = new boolean[JOBS];
		}
		
		int job;
		int person;
		int sum = 0; // TODO: Think of better name
		for (int w = 0; w < WEEKS; w++) {
			for (int j = 0; j < JOBS; j++) {
				job = getJob(j, w);
				person = chrom.getValue(job);
				
				if (!hasDone[person][j]) {
					hasDone[person][j] = true;
					sum++;
				}
			}
		}
		
		if (sum == JOBS * PERSONS) {
			return true;
		}
		
		return false;
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
			
			boolean hasEachJobOnce = hasPerfectAssignment(chrom);
			
			int score = 0;
			if (noDoubles) {
				score++;
			}
			if (noOverlap) {
				score++;
			}
			if (hasEachJobOnce) {
				score++;
			}
			
			individual.setScore(score);
		}
	}

}
